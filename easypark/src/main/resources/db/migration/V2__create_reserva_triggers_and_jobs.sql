--------------------------------------------------------------------------------
-- EasyPark - Triggers & Procedures (Oracle) - Sprint 1
--   A) TRIGGER  : trg_sensor_evento_after_insert
--   B) PROCEDURE: user_eta_update_process
--   C) PROCEDURE: reserva_timeouts (usa espera/tolerÃ¢ncia do ESTACIONAMENTO)
--                 + wrapper + job (opcionais)
--   D) PROCEDURE: reserva_prereserva_timeouts (OpÃ§Ã£o A) + wrapper + job (opcionais)
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- A) TRIGGER: INSERT em SENSOR_EVENTO
--     - Upsert em VAGA_STATUS (cache do status atual da vaga)
--     - Se status='OCUPADA' e houver RESERVA para a vaga, transita RESERVA->OCUPADA
--     - Registra em RESERVA_HIST a transiÃ§Ã£o (origem_evento='SENSOR')
--------------------------------------------------------------------------------
CREATE OR REPLACE TRIGGER trg_sensor_evento_after_insert
AFTER INSERT ON sensor_evento
FOR EACH ROW
DECLARE
  v_reserva_id NUMBER;
BEGIN
  -- Upsert do status atual
  MERGE INTO vaga_status tgt
  USING (
    SELECT :NEW.vaga_id      AS vaga_id,
           :NEW.status       AS st,
           :NEW.ocorrido_em  AS ts,
           :NEW.sensor_id    AS sid
    FROM dual
  ) src
  ON (tgt.vaga_id = src.vaga_id)
  WHEN MATCHED THEN
    UPDATE SET tgt.status_ocupacao = src.st,
               tgt.ultimo_ocorrido = src.ts,
               tgt.sensor_id       = src.sid
  WHEN NOT MATCHED THEN
    INSERT (vaga_id, status_ocupacao, ultimo_ocorrido, sensor_id)
    VALUES (src.vaga_id, src.st, src.ts, src.sid);

  -- Se ocupou fisicamente, tentar RESERVA -> OCUPADA
  IF :NEW.status = 'OCUPADA' THEN
    BEGIN
      SELECT id
        INTO v_reserva_id
        FROM reserva
       WHERE vaga_id = :NEW.vaga_id
         AND estado  = 'RESERVA'
         AND ROWNUM = 1
       FOR UPDATE;

      UPDATE reserva
         SET estado     = 'OCUPADA',
             ocupado_em = :NEW.ocorrido_em
       WHERE id = v_reserva_id;

      INSERT INTO reserva_hist
            (reserva_id, from_estado, to_estado, origem_evento, referencia_id, observacao)
      VALUES (v_reserva_id, 'RESERVA', 'OCUPADA', 'SENSOR', :NEW.id, 'sensor_evento');
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        NULL; -- nenhuma reserva em RESERVA para a vaga
      WHEN OTHERS THEN
        NULL; -- evitar falha em cascata; alternativa: log tÃ©cnico
    END;
  END IF;
END;
/
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- B) PROCEDURE: user_eta_update_process
--     - Atualiza ETA (origem, minutos, timestamp)
--     - Se estado='PRE_RESERVA' e ETA <= antecedencia_minutos, faz PRE_RESERVA->RESERVA
--       (marca confirmado_em e vaga_bloqueada='Y' + insere RESERVA_HIST)
-- ObservaÃ§Ã£o: sem COMMIT/ROLLBACK (transaÃ§Ã£o do chamador).
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE user_eta_update_process(
  p_reserva_id   IN  NUMBER,
  p_eta_minutos  IN  NUMBER,
  p_status       OUT VARCHAR2,  -- 'TRANSITIONED' | 'NO_OP' | 'NOT_FOUND' | 'ERROR'
  p_msg          OUT VARCHAR2
) AS
  v_estado VARCHAR2(20);
  v_ant    NUMBER;
BEGIN
  p_status := NULL;
  p_msg    := NULL;

  -- Lock da reserva para consistencia
  BEGIN
    SELECT estado, antecedencia_minutos
      INTO v_estado, v_ant
      FROM reserva
     WHERE id = p_reserva_id
     FOR UPDATE;
  EXCEPTION
    WHEN NO_DATA_FOUND THEN
      p_status := 'NOT_FOUND';
      p_msg    := 'reserva inexistente';
      RETURN;
  END;

  -- Atualiza ETA
  UPDATE reserva
     SET eta_origem        = 'google_maps',
         eta_minutos       = p_eta_minutos,
         eta_atualizado_em = SYSTIMESTAMP
   WHERE id = p_reserva_id;

  -- PRE_RESERVA -> RESERVA se ETA <= antecedencia
  IF v_estado = 'PRE_RESERVA' AND p_eta_minutos <= NVL(v_ant, 0) THEN
    UPDATE reserva
       SET estado         = 'RESERVA',
           confirmado_em  = SYSTIMESTAMP,
           vaga_bloqueada = 'Y'
     WHERE id = p_reserva_id;

    INSERT INTO reserva_hist
          (reserva_id, from_estado, to_estado, origem_evento, observacao)
    VALUES (p_reserva_id, 'PRE_RESERVA', 'RESERVA', 'ETA', 'eta <= antecedencia');

    p_status := 'TRANSITIONED';
    p_msg    := 'PRE_RESERVA -> RESERVA';
  ELSE
    p_status := 'NO_OP';
    p_msg    := 'sem transiÃ§Ã£o';
  END IF;

EXCEPTION
  WHEN OTHERS THEN
    p_status := 'ERROR';
    p_msg    := SQLERRM;
END user_eta_update_process;
/
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- C) PROCEDURE: reserva_timeouts (USANDO VALORES DO ESTACIONAMENTO)
--     - Cancela reservas em 'RESERVA' que excederam:
--          confirmado_em + (espera_minutos + tolerancia_minutos) DO SEU ESTACIONAMENTO
--     - Libera vaga_bloqueada e registra histÃ³rico (origem_evento='TIMEOUT')
-- ObservaÃ§Ã£o: esta procedure faz COMMIT; ideal para rodar via DBMS_SCHEDULER.
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE reserva_timeouts(
  p_out_canceladas OUT NUMBER
) AS
  CURSOR c_res IS
    SELECT r.id,
           r.confirmado_em,
           NVL(e.espera_minutos, 10)      AS espera_min,
           NVL(e.tolerancia_minutos, 5)   AS tolerancia_min
      FROM reserva r
      JOIN vaga v   ON v.id = r.vaga_id
      JOIN nivel n  ON n.id = v.nivel_id
      JOIN estacionamento e ON e.id = n.estacionamento_id
     WHERE r.estado = 'RESERVA'
       AND r.confirmado_em IS NOT NULL
     FOR UPDATE SKIP LOCKED;

  v_cnt NUMBER := 0;
BEGIN
  FOR r IN c_res LOOP
    IF r.confirmado_em
       + NUMTODSINTERVAL(r.espera_min + r.tolerancia_min, 'MINUTE')
       <= SYSTIMESTAMP
    THEN
      UPDATE reserva
         SET estado = 'CANCELADA',
             motivo_cancelamento = 'TIMEOUT_NO_SHOW',
             vaga_bloqueada = 'N'
       WHERE id = r.id;

      INSERT INTO reserva_hist
            (reserva_id, from_estado, to_estado, origem_evento, observacao)
      VALUES (r.id, 'RESERVA', 'CANCELADA', 'TIMEOUT', 'prazo + tolerancia (por estacionamento)');

      v_cnt := v_cnt + 1;
    END IF;
  END LOOP;

  COMMIT;
  p_out_canceladas := v_cnt;

EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    p_out_canceladas := -1;
    RAISE;
END reserva_timeouts;
/
--------------------------------------------------------------------------------

--------------------------------------------------------------------------------
-- D) PROCEDURE: reserva_prereserva_timeouts (OpÃ§Ã£o A)
--     - Cancela PRE_RESERVA se now > (inicio_previsto + tolerancia do estacionamento)
--     - Registra histÃ³rico (origem_evento='TIMEOUT', observaÃ§Ã£o 'pre-reserva expirada')
-- ObservaÃ§Ã£o: usa a mesma tolerÃ¢ncia do estacionamento; faz COMMIT.
--------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE reserva_prereserva_timeouts(
  p_out_canceladas OUT NUMBER
) AS
  CURSOR c_res IS
    SELECT r.id,
           r.inicio_previsto,
           NVL(e.tolerancia_minutos, 5) AS tolerancia_min
      FROM reserva r
      JOIN vaga v   ON v.id = r.vaga_id
      JOIN nivel n  ON n.id = v.nivel_id
      JOIN estacionamento e ON e.id = n.estacionamento_id
     WHERE r.estado = 'PRE_RESERVA'
       AND r.inicio_previsto IS NOT NULL
     FOR UPDATE OF r.id SKIP LOCKED;

  v_cnt NUMBER := 0;
BEGIN
  FOR r IN c_res LOOP
    IF r.inicio_previsto
       + NUMTODSINTERVAL(r.tolerancia_min, 'MINUTE')
       <= SYSTIMESTAMP
    THEN
      UPDATE reserva
         SET estado = 'CANCELADA',
             motivo_cancelamento = 'PRERESERVA_TIMEOUT',
             vaga_bloqueada = 'N'
       WHERE id = r.id;

      INSERT INTO reserva_hist
            (reserva_id, from_estado, to_estado, origem_evento, observacao)
      VALUES (r.id, 'PRE_RESERVA', 'CANCELADA', 'TIMEOUT', 'pre-reserva expirada');

      v_cnt := v_cnt + 1;
    END IF;
  END LOOP;

  COMMIT;
  p_out_canceladas := v_cnt;

EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    p_out_canceladas := -1;
    RAISE;
END reserva_prereserva_timeouts;
/
--------------------------------------------------------------------------------


--------------------------------------------------------------------------------
-- Fim do arquivo
--------------------------------------------------------------------------------
