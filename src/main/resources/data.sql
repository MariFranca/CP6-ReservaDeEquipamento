-- Dados de exemplo carregados automaticamente no perfil H2 (ddl-auto=create-drop)
-- Facilita testar a API imediatamente pelo Insomnia, sem precisar cadastrar tudo manualmente.

INSERT INTO professores (id, nome, email) VALUES (1, 'João da Silva', 'joao.silva@fiap.com.br');
INSERT INTO professores (id, nome, email) VALUES (2, 'Maria Souza', 'maria.souza@fiap.com.br');

INSERT INTO cursos (id, nome) VALUES (1, 'Engenharia de Software');
INSERT INTO cursos (id, nome) VALUES (2, 'Análise e Desenvolvimento de Sistemas');

INSERT INTO salas (id, numero, bloco) VALUES (1, '204', 'A');
INSERT INTO salas (id, numero, bloco) VALUES (2, '205', 'A');

INSERT INTO equipamentos (id, identificacao, tipo, descricao, ativo) VALUES (1, 'Datashow 01', 'Datashow', 'Projetor Epson', true);
INSERT INTO equipamentos (id, identificacao, tipo, descricao, ativo) VALUES (2, 'Datashow 02', 'Datashow', 'Projetor Epson', true);
INSERT INTO equipamentos (id, identificacao, tipo, descricao, ativo) VALUES (3, 'Microfone 01', 'Microfone', 'Microfone sem fio', false);
INSERT INTO equipamentos (id, identificacao, tipo, descricao, ativo) VALUES (4, 'Cabo HDMI 01', 'Cabo HDMI', null, true);
INSERT INTO equipamentos (id, identificacao, tipo, descricao, ativo) VALUES (5, 'Extensão 01', 'Extensão', null, true);
