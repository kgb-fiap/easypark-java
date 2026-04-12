-------------------------------------------------------------------------------
-- UPDATE com decisão (suspender usuário por no-show)
-- Regras:
--  - Lê o usuário em FOR UPDATE.
--  - Se no_shows >= limite, marca suspenso='Y' por 7 dias (ajuste se quiser).
--  - Caso contrário, só informa que não atingiu o limite.
-------------------------------------------------------------------------------
SET SERVEROUTPUT ON
DECLARE
  v_usuario_id   NUMBER := 51; -- <<< exemplo de usuario que NÃO atiguiu o limite de no-shows
  --v_usuario_id   NUMBER := 52;  -- <<< exemplo de usuario suspenso que atiguiu o limite de no-shows
  v_limite       NUMBER := 3;  -- <<< ajuste o limite aqui
  v_ns           NUMBER;
BEGIN
  SELECT no_shows INTO v_ns
    FROM usuario
   WHERE id = v_usuario_id
   FOR UPDATE;

  IF v_ns >= v_limite THEN
    UPDATE usuario
       SET suspenso = 'Y',
           suspensao_ate = SYSTIMESTAMP + NUMTODSINTERVAL(7,'DAY')
     WHERE id = v_usuario_id;

    DBMS_OUTPUT.PUT_LINE('Usuário '||v_usuario_id||' suspenso por 7 dias. (no_shows='||v_ns||')');
  ELSE
    DBMS_OUTPUT.PUT_LINE('Usuário '||v_usuario_id||' NÃO atingiu o limite ('||v_ns||' < '||v_limite||').');
  END IF;

  COMMIT;
END;
/
-------------------------------------------------------------------------------
-- DELETE com decisão (deleta eventos de sensor antigos)
-- Regras:
--  - Usa cutoff (timestamp) em variável.
--  - Mostra quantos seriam removidos; só deleta se > 0.
-------------------------------------------------------------------------------
SET SERVEROUTPUT ON
DECLARE
  v_cutoff TIMESTAMP WITH TIME ZONE := SYSTIMESTAMP - NUMTODSINTERVAL(1,'DAY'); 
  v_count  NUMBER;
BEGIN
  SELECT COUNT(*)
    INTO v_count
    FROM sensor_evento
   WHERE ocorrido_em < v_cutoff;

  DBMS_OUTPUT.PUT_LINE('Eventos anteriores a '||TO_CHAR(v_cutoff,'YYYY-MM-DD HH24:MI:SS TZR')||
                       ': '||v_count);

  IF v_count > 0 THEN
    DELETE FROM sensor_evento
     WHERE ocorrido_em < v_cutoff;

    DBMS_OUTPUT.PUT_LINE('Removidos: '||SQL%ROWCOUNT);
    COMMIT;
  ELSE
    DBMS_OUTPUT.PUT_LINE('Nada a remover.');
  END IF;
END;
/
