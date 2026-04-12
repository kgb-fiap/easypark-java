-------------------------------------------------------------------------------
-- INNER JOIN + GROUP BY + ORDER BY
-- Faturamento por estacionamento e dia (considera qualquer status)
-------------------------------------------------------------------------------
SET SERVEROUTPUT ON
DECLARE
  CURSOR c IS
    SELECT e.nome AS estacionamento,
           TRUNC(p.criado_em) AS dia,
           p.status,
           COUNT(*) AS transacoes,
           SUM(p.valor) AS total
      FROM pagamento p
      JOIN reserva r        ON r.id = p.reserva_id
      JOIN vaga v           ON v.id = r.vaga_id
      JOIN nivel n          ON n.id = v.nivel_id
      JOIN estacionamento e ON e.id = n.estacionamento_id
     GROUP BY e.nome, TRUNC(p.criado_em), p.status
     ORDER BY dia DESC, estacionamento, p.status;
BEGIN
  DBMS_OUTPUT.PUT_LINE('Estacionamento | Dia        | Status     | Tx | Total');
  DBMS_OUTPUT.PUT_LINE('------------------------------------------------------');
  FOR rec IN c LOOP
    DBMS_OUTPUT.PUT_LINE(rec.estacionamento||' | '||
                         TO_CHAR(rec.dia,'YYYY-MM-DD')||' | '||
                         RPAD(rec.status,10)||' | '||
                         TO_CHAR(rec.transacoes,'FM9990')||' | R$ '||
                         TO_CHAR(NVL(rec.total,0),'FM9990D00','NLS_NUMERIC_CHARACTERS=,.'));
  END LOOP;
END;
/
-------------------------------------------------------------------------------
-- LEFT JOIN + GROUP BY + ORDER BY
-- Todas as vagas com o último status (mesmo sem status), total de eventos por vaga
-------------------------------------------------------------------------------
SET SERVEROUTPUT ON
DECLARE
  CURSOR c IS
    SELECT v.id,
           v.codigo,
           tv.nome AS tipo_vaga,
           NVL(vs.status_ocupacao,'SEM_STATUS') AS status_atual,
           COUNT(se.id) AS eventos
      FROM vaga v
      JOIN tipo_vaga tv  ON tv.id = v.tipo_vaga_id
      LEFT JOIN vaga_status vs ON vs.vaga_id = v.id
      LEFT JOIN sensor s  ON s.vaga_id = v.id
      LEFT JOIN sensor_evento se ON se.sensor_id = s.id
     GROUP BY v.id, v.codigo, tv.nome, vs.status_ocupacao
     ORDER BY v.codigo;
BEGIN
  DBMS_OUTPUT.PUT_LINE('Vaga | Tipo        | Status        | Eventos');
  DBMS_OUTPUT.PUT_LINE('---------------------------------------------');
  FOR rec IN c LOOP
    DBMS_OUTPUT.PUT_LINE(RPAD(rec.codigo,6)||' | '||
                         RPAD(rec.tipo_vaga,11)||' | '||
                         RPAD(rec.status_atual,13)||' | '||
                         TO_CHAR(rec.eventos,'FM9990'));
  END LOOP;
END;
/
-------------------------------------------------------------------------------
-- RIGHT JOIN + GROUP BY + ORDER BY
-- Sensores e suas vagas (lista sensores mesmo se a consulta de vaga ficar nula)
-------------------------------------------------------------------------------
SET SERVEROUTPUT ON
DECLARE
  CURSOR c IS
    SELECT s.identificador_externo AS sensor,
           v.codigo AS vaga,
           COUNT(se.id) AS eventos
      FROM vaga v
      RIGHT JOIN sensor s        ON s.vaga_id = v.id
      LEFT  JOIN sensor_evento se ON se.sensor_id = s.id
     GROUP BY s.identificador_externo, v.codigo
     ORDER BY eventos DESC NULLS LAST, sensor;
BEGIN
  DBMS_OUTPUT.PUT_LINE('Sensor       | Vaga    | Eventos');
  DBMS_OUTPUT.PUT_LINE('---------------------------------');
  FOR rec IN c LOOP
    DBMS_OUTPUT.PUT_LINE(RPAD(rec.sensor,12)||' | '||
                         RPAD(NVL(rec.vaga,'(sem vaga)'),8)||' | '||
                         TO_CHAR(rec.eventos,'FM9990'));
  END LOOP;
END;
/
