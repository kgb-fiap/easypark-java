UPDATE usuario
   SET nome = 'Motorista'
 WHERE LOWER(email) = LOWER('easypark.motorista@fiap.com.br');

UPDATE usuario
   SET nome = 'Operador'
 WHERE LOWER(email) = LOWER('easypark.operador@fiap.com.br');

UPDATE usuario
   SET nome = 'Admin'
 WHERE LOWER(email) = LOWER('easypark.admin@fiap.com.br');
