-- Dane startowe aplikacji. Tabele tworzy Hibernate z encji, a ten plik Spring Boot uruchamia zaraz potem.
-- LAB 01: dopiszcie tu konta wszystkich osób z zespołu (reguła R1.03).
-- LAB 02: dopiszcie dane do swoich tabel (kolejność: najpierw tabele, na które wskazują klucze obce).
insert into user_account (email, display_name, role, created_at) values
    ('s674170@student.wroclaw.merito.pl',   'Jakub',   'USER',    current_timestamp),
    ('s130124@student.wroclaw.merito.pl',  'Jakub',  'USER',    current_timestamp),
    ('s105560@student.wroclaw.merito.pl',       'Daniel', 'USER', current_timestamp);

-- ===== LAB 02 · customer =====
--insert into customer (full_name, email, registered_on, active, street, city, country) values
    --('Jan Kowalski', 'jan@example.com', '2026-10-01', true, 'Główna 1', 'Warszawa', 'Polska'),
    --('Anna Nowak', 'anna@example.com', '2026-10-05', true, 'Długa 5', 'Kraków', 'Polska'),
    --('Piotr Wiśniewski', 'piotr@example.com', '2026-10-10', false, null, null, null);

-- ===== LAB 02 · order_request =====
--insert into order_request (item_name, budget, status, origin_country, request_date) values
    --('Włoska oliwa tłoczona na zimno', 150.00, 'PENDING', 'Włochy', '2026-11-01'),
    --('Japońska zielona herbata Matcha', 200.00, 'ACCEPTED', 'Japonia', '2026-11-02'),
    --('Szwajcarska czekolada', 50.00, 'DELIVERED', 'Szwajcaria', '2026-11-03');

-- ===== LAB 02 · delivery_offer =====
--insert into delivery_offer (delivery_fee, estimated_delivery, accepted, seller_location, notes) values
    --(30.00, '2026-11-10', false, 'Rzym', 'Wracam do Polski w przyszłym tygodniu.'),
    --(50.00, '2026-11-15', true, 'Tokio', 'Mogę kupić na lotnisku.'),
    --(15.00, '2026-11-05', true, 'Zurych', null);

-- ===== LAB 03 · customer =====
insert into customer (full_name, email, registered_on, active, street, city, country, user_account_id) values
    ('Jan Kowalski', 'jan@example.com', '2026-10-01', true, 'Główna 1', 'Warszawa', 'Polska', 1),
    ('Anna Nowak', 'anna@example.com', '2026-10-05', true, 'Długa 5', 'Kraków', 'Polska', 2),
    ('Piotr Wiśniewski', 'piotr@example.com', '2026-10-10', false, null, null, null, null);

-- ===== LAB 03 · tag =====
insert into tag (name, description, active, created_on) values
    ('Żywność', 'Produkty spożywcze i napoje', true, '2026-11-01'),
    ('Delikatne', 'Szkło, ceramika, elektronika', true, '2026-11-01'),
    ('Luksusowe', 'Wysoka wartość, wymaga szczególnej opieki', true, '2026-11-01');

-- ===== LAB 03 · order_request =====
insert into order_request (item_name, budget, status, origin_country, request_date, customer_id) values
    ('Włoska oliwa tłoczona na zimno', 150.00, 'PENDING', 'Włochy', '2026-11-01', 1),
    ('Japońska zielona herbata Matcha', 200.00, 'ACCEPTED', 'Japonia', '2026-11-02', 2),
    ('Szwajcarska czekolada', 50.00, 'DELIVERED', 'Szwajcaria', '2026-11-03', 1);

-- ===== LAB 03 · delivery_offer =====
insert into delivery_offer (delivery_fee, estimated_delivery, accepted, seller_location, notes, order_request_id, courier_id) values
    (30.00, '2026-11-10', false, 'Rzym', 'Wracam do Polski w przyszłym tygodniu.', 1, 2),
    (50.00, '2026-11-15', true, 'Tokio', 'Mogę kupić na lotnisku.', 2, 1),
    (15.00, '2026-11-05', true, 'Zurych', null, 3, 2);

-- ===== LAB 03 · order_request_tag =====
insert into order_request_tag (order_request_id, tag_id) values
     (1, 1), -- Oliwa to żywność
     (1, 2), -- Oliwa to delikatne (butelka)
     (2, 1), -- Matcha to żywność
     (3, 1), -- Czekolada to żywność
     (3, 3); -- Czekolada to luksusowe
