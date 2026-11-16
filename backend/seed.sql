-- Synthetic demo-only catalog rows. Flyway V1 creates the schema first.
INSERT INTO categories(id,name) VALUES (1,'Computer Science'),(2,'Literature'),(3,'Science'),(4,'History') ON CONFLICT DO NOTHING;
INSERT INTO books(id,title,authors,isbn,publisher,publication_year,language,category_id,description,tags,price,borrow_count)
VALUES (1,'The Algorithm Atlas','Mira Sen','9780000000001','Demo Press',2023,'English',1,'A friendly map of core algorithms.','algorithms,computer science',450,91),
(2,'Gardens of the Monsoon','Asha Raman','9780000000002','Demo Press',2021,'English',2,'Stories of family, memory, and a changing city.','fiction,community',380,76),
(3,'A Brief History of Stars','Dev Kapoor','9780000000003','Demo Press',2024,'English',3,'An illustrated introduction to the night sky.','astronomy,science',520,68),
(4,'Designing Small Systems','Nina Das','9780000000004','Demo Press',2022,'English',1,'Practical patterns for resilient software.','software,design',610,57),
(5,'The Quiet Archive','Kabir Iyer','9780000000005','Demo Press',2020,'English',4,'How communities preserve the stories they inherit.','history,archives',330,42),
(6,'Maps of Salt and Wind','Tara Menon','9780000000006','Demo Press',2023,'English',2,'A coastal family follows old maps through a changing landscape.','fiction,coast',390,38),
(7,'Practical Machine Learning','Arjun Mehta','9780000000007','Demo Press',2024,'English',1,'A hands-on introduction to learning systems and thoughtful evaluation.','machine learning,computing',640,82),
(8,'The Garden of Small Worlds','Tara Bose','9780000000008','Demo Press',2019,'English',3,'Discovering ecology through tiny habitats around us.','ecology,nature',410,35),
(9,'Cities Before Dawn','Leela Mukherjee','9780000000009','Demo Press',2018,'English',4,'How trade, craft, and night work shaped historic cities.','history,cities',360,31),
(10,'Learning TypeScript','Omar Khan','9780000000010','Demo Press',2025,'English',1,'Build safer web applications with a practical TypeScript guide.','typescript,software',590,49),
(11,'The River Between Us','Ria Dutta','9780000000011','Demo Press',2022,'English',2,'Two neighbors tell the story of a river and the town it joins.','fiction,river',420,46),
(12,'Patterns in Nature','Ishaan Roy','9780000000012','Demo Press',2023,'English',3,'A visual tour of repeating forms in plants, weather, and minerals.','science,nature',480,52)
ON CONFLICT (isbn) DO NOTHING;
INSERT INTO copies(book_id,barcode,status,shelf_location) VALUES
(1,'DEMO-COPY-001','AVAILABLE','CS-A1'),(1,'DEMO-COPY-002','AVAILABLE','CS-A1'),
(2,'DEMO-COPY-003','AVAILABLE','LIT-B2'),(3,'DEMO-COPY-004','AVAILABLE','SCI-C1'),
(4,'DEMO-COPY-005','AVAILABLE','CS-A2'),(5,'DEMO-COPY-006','AVAILABLE','HIS-D1'),
(6,'DEMO-COPY-007','AVAILABLE','LIT-C1'),(7,'DEMO-COPY-008','AVAILABLE','CS-B3'),
(7,'DEMO-COPY-009','AVAILABLE','CS-B3'),(8,'DEMO-COPY-010','AVAILABLE','SCI-A2'),
(9,'DEMO-COPY-011','AVAILABLE','HIS-A4'),(10,'DEMO-COPY-012','AVAILABLE','CS-B1'),
(10,'DEMO-COPY-013','AVAILABLE','CS-B1'),(10,'DEMO-COPY-014','AVAILABLE','CS-B1'),
(11,'DEMO-COPY-015','AVAILABLE','LIT-A3'),(12,'DEMO-COPY-016','AVAILABLE','SCI-C2')
ON CONFLICT (barcode) DO NOTHING;
