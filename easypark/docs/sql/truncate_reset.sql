SET SERVEROUTPUT ON
--------------------------------------------------------------------------------
-- TRUNCATE em ordem: filhas -> pais
--------------------------------------------------------------------------------
-- Filhas de PAGAMENTO
TRUNCATE TABLE pagamento_cartao;
TRUNCATE TABLE pagamento_pagador;

-- Pagamento 
TRUNCATE TABLE pagamento;

-- Filhas de RESERVA
TRUNCATE TABLE reserva_hist;
TRUNCATE TABLE reserva_preco;

-- Eventos e cache ligados à VAGA/SENSOR
TRUNCATE TABLE sensor_evento;
TRUNCATE TABLE vaga_status;
TRUNCATE TABLE sensor;

-- RESERVA depende de VAGA e USUARIO
TRUNCATE TABLE reserva;

-- VAGA depende de NIVEL e TIPO_VAGA
TRUNCATE TABLE vaga;

-- TIPO_VAGA não tem dependentes restantes
TRUNCATE TABLE tipo_vaga;

-- NIVEL depende de ESTACIONAMENTO
TRUNCATE TABLE nivel;

-- ESTACIONAMENTO depende de OPERADORA
TRUNCATE TABLE estacionamento;

-- USUARIO 
TRUNCATE TABLE usuario;

-- OPERADORA sem dependentes restantes
TRUNCATE TABLE operadora;

-- novo domínio de endereço (filhas → pais)
TRUNCATE TABLE endereco;
TRUNCATE TABLE bairro;
TRUNCATE TABLE cidade;
TRUNCATE TABLE uf;

COMMIT;
