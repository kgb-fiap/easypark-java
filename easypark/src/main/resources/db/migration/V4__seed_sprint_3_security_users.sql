MERGE INTO usuario u
USING (
  SELECT
    'Motorista' AS nome,
    'easypark.motorista@fiap.com.br' AS email,
    '$2a$10$xtFHZYDKZj0dTacmXq3mHuaAANfFL1DRMhj.p4rHTmqvy/XNvWiDW' AS senha_hash,
    'cliente' AS perfil
  FROM dual
) src
ON (LOWER(u.email) = LOWER(src.email))
WHEN MATCHED THEN
  UPDATE SET
    u.nome = src.nome,
    u.senha_hash = src.senha_hash,
    u.perfil = src.perfil,
    u.suspenso = 'N',
    u.no_shows = 0,
    u.suspensao_ate = NULL
WHEN NOT MATCHED THEN
  INSERT (nome, email, senha_hash, perfil, suspenso, no_shows, criado_em)
  VALUES (src.nome, src.email, src.senha_hash, src.perfil, 'N', 0, SYSTIMESTAMP);

MERGE INTO usuario u
USING (
  SELECT
    'Operador' AS nome,
    'easypark.operador@fiap.com.br' AS email,
    '$2a$10$NEPjyA06X8fOWhySbl.X/Ol31SRZZ80F5Jrcraz1Sx5g0dzCbHMJG' AS senha_hash,
    'operador' AS perfil
  FROM dual
) src
ON (LOWER(u.email) = LOWER(src.email))
WHEN MATCHED THEN
  UPDATE SET
    u.nome = src.nome,
    u.senha_hash = src.senha_hash,
    u.perfil = src.perfil,
    u.suspenso = 'N',
    u.no_shows = 0,
    u.suspensao_ate = NULL
WHEN NOT MATCHED THEN
  INSERT (nome, email, senha_hash, perfil, suspenso, no_shows, criado_em)
  VALUES (src.nome, src.email, src.senha_hash, src.perfil, 'N', 0, SYSTIMESTAMP);

MERGE INTO usuario u
USING (
  SELECT
    'Admin' AS nome,
    'easypark.admin@fiap.com.br' AS email,
    '$2a$10$dkh3ogloYYJwKihjv5zI5Oe63eJ78dfr.TSANVfK7C/amyJcAy8KG' AS senha_hash,
    'admin' AS perfil
  FROM dual
) src
ON (LOWER(u.email) = LOWER(src.email))
WHEN MATCHED THEN
  UPDATE SET
    u.nome = src.nome,
    u.senha_hash = src.senha_hash,
    u.perfil = src.perfil,
    u.suspenso = 'N',
    u.no_shows = 0,
    u.suspensao_ate = NULL
WHEN NOT MATCHED THEN
  INSERT (nome, email, senha_hash, perfil, suspenso, no_shows, criado_em)
  VALUES (src.nome, src.email, src.senha_hash, src.perfil, 'N', 0, SYSTIMESTAMP);
