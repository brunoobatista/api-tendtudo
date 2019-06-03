ALTER TABLE usuario ADD COLUMN ativo INTEGER NOT NULL DEFAULT 1;
ALTER TABLE cliente ADD COLUMN ativo INTEGER NOT NULL DEFAULT 1;
ALTER TABLE venda ADD COLUMN ativo INTEGER NOT NULL DEFAULT 1;
ALTER TABLE fornecedor ADD COLUMN ativo INTEGER NOT NULL DEFAULT 1;
ALTER TABLE produto ADD COLUMN ativo INTEGER NOT NULL DEFAULT 1;
ALTER TABLE tipo ADD COLUMN ativo INTEGER NOT NULL DEFAULT 1;

ALTER TABLE cliente RENAME COLUMN ativado TO confirmado;

ALTER TABLE usuario ALTER COLUMN password DROP NOT NULL;