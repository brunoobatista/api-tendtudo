ALTER TABLE permissao ADD COLUMN nome VARCHAR(50) NOT NULL DEFAULT 'ROLE_USUARIO';
ALTER TABLE permissao RENAME COLUMN descricao TO funcao;