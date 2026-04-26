INSERT INTO clientes (nome, cpf, email, telefone, endereco) VALUES
  ('Carlos Mendes',   '529.982.247-25', 'carlos@email.com',  '(41) 98765-4321', 'Rua das Flores, 100, Curitiba-PR'),
  ('Ana Paula Costa', '275.487.932-07', 'ana@email.com',     '(41) 91234-5678', 'Av. Batel, 500, Curitiba-PR'),
  ('Roberto Lima',    '154.369.028-56', 'roberto@email.com', '(41) 99876-5432', 'Rua XV de Novembro, 250, Curitiba-PR'),
  ('Fernanda Souza',  '432.801.675-84', 'fern@email.com',    '(41) 93456-7890', 'Rua Imaculada, 88, Curitiba-PR')
ON CONFLICT (cpf) DO NOTHING;

INSERT INTO veiculos (cliente_id, marca, modelo, ano, cor, placa, quilometragem, tipo, observacoes)
SELECT c.id,'Honda','Civic','2021','Prata','ABC-1234',42300,'SEDAN','' FROM clientes c WHERE c.cpf='529.982.247-25' ON CONFLICT (placa) DO NOTHING;
INSERT INTO veiculos (cliente_id, marca, modelo, ano, cor, placa, quilometragem, tipo, observacoes)
SELECT c.id,'Toyota','Corolla','2019','Branco','DEF-5678',87100,'SEDAN','' FROM clientes c WHERE c.cpf='275.487.932-07' ON CONFLICT (placa) DO NOTHING;
INSERT INTO veiculos (cliente_id, marca, modelo, ano, cor, placa, quilometragem, tipo, observacoes)
SELECT c.id,'Volkswagen','Gol','2018','Preto','GHI-9012',103450,'HATCH','Amassado leve na porta traseira' FROM clientes c WHERE c.cpf='154.369.028-56' ON CONFLICT (placa) DO NOTHING;
INSERT INTO veiculos (cliente_id, marca, modelo, ano, cor, placa, quilometragem, tipo, observacoes)
SELECT c.id,'Fiat','Argo','2022','Vermelho','JKL-3456',18200,'HATCH','' FROM clientes c WHERE c.cpf='432.801.675-84' ON CONFLICT (placa) DO NOTHING;
INSERT INTO veiculos (cliente_id, marca, modelo, ano, cor, placa, quilometragem, tipo, observacoes)
SELECT c.id,'Jeep','Compass','2020','Cinza','MNO-7890',56000,'SUV','' FROM clientes c WHERE c.cpf='529.982.247-25' ON CONFLICT (placa) DO NOTHING;

INSERT INTO estoque (codigo, nome, categoria, quantidade, quantidade_minima, preco_unitario) VALUES
  ('FO-920','Filtro de óleo W920','Filtros',3,10,28.00),
  ('PF-BSH','Pastilha de freio Bosch','Freios',2,8,145.00),
  ('VT-001','Vela de ignição NGK','Ignição',24,12,35.00),
  ('OL-055','Óleo 5W30 Sintético 1L','Lubrificantes',18,20,42.00),
  ('AM-044','Amortecedor dianteiro','Suspensão',6,4,320.00),
  ('CR-012','Correia dentada Gates','Motor',11,6,189.00),
  ('FP-033','Filtro de combustível','Filtros',15,8,45.00),
  ('RL-022','Rolamento de roda','Suspensão',8,4,280.00),
  ('EV-001','Módulo controlador BMS','Elétrico',2,1,1200.00),
  ('EV-002','Cabo de carregamento Tipo 2','Elétrico',5,2,380.00),
  ('EV-003','Pastilha freio regenerativo','Freios',4,2,220.00),
  ('EV-004','Fluido resfriamento bateria','Elétrico',8,3,95.00)
ON CONFLICT (codigo) DO NOTHING;

INSERT INTO estoque_tipos (estoque_id, tipo)
SELECT e.id, t.tipo FROM estoque e
CROSS JOIN (VALUES ('SEDAN'),('HATCH'),('SUV'),('PICKUP')) AS t(tipo)
WHERE e.codigo IN ('FO-920','PF-BSH','VT-001','OL-055','AM-044','CR-012','FP-033','RL-022')
ON CONFLICT DO NOTHING;

INSERT INTO estoque_tipos (estoque_id, tipo)
SELECT e.id,'ELETRICO' FROM estoque e
WHERE e.codigo IN ('EV-001','EV-002','EV-003','EV-004')
ON CONFLICT DO NOTHING;
