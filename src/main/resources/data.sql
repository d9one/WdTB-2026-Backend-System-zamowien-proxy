-- Dane startowe aplikacji. Tabele tworzy Hibernate z encji, a ten plik Spring Boot uruchamia zaraz potem.
-- LAB 01: dopiszcie tu konta wszystkich osób z zespołu (reguła R1.03).
-- LAB 02: dopiszcie dane do swoich tabel (kolejność: najpierw tabele, na które wskazują klucze obce).
insert into user_account (email, display_name, role, created_at) values
    ('s674170@student.wroclaw.merito.pl',   'Jakub',   'USER',    current_timestamp),
    ('s130124@student.wroclaw.merito.pl',  'Jakub',  'USER',    current_timestamp),
    ('s105560@student.wroclaw.merito.pl',       'Daniel', 'USER', current_timestamp);

-- ===== LAB 02 · customer =====
insert into customer (full_name, email, registered_on, active, street, city, country) values
    ('Jan Kowalski', 'jan@example.com', '2026-10-01', true, 'Główna 1', 'Warszawa', 'Polska'),
    ('Anna Nowak', 'anna@example.com', '2026-10-05', true, 'Długa 5', 'Kraków', 'Polska'),
    ('Piotr Wiśniewski', 'piotr@example.com', '2026-10-10', false, null, null, null);

-- ===== LAB 02 · order_request =====
insert into order_request (item_name, budget, status, origin_country, request_date) values
    ('Włoska oliwa tłoczona na zimno', 150.00, 'PENDING', 'Włochy', '2026-11-01'),
    ('Japońska zielona herbata Matcha', 200.00, 'ACCEPTED', 'Japonia', '2026-11-02'),
    ('Szwajcarska czekolada', 50.00, 'DELIVERED', 'Szwajcaria', '2026-11-03');

-- ===== LAB 02 · delivery_offer =====
insert into delivery_offer (delivery_fee, estimated_delivery, accepted, seller_location, notes) values
    (30.00, '2026-11-10', false, 'Rzym', 'Wracam do Polski w przyszłym tygodniu.'),
    (50.00, '2026-11-15', true, 'Tokio', 'Mogę kupić na lotnisku.'),
    (15.00, '2026-11-05', true, 'Zurych', null);

-- LAB 01: Wasze konta (zmieńcie na swoje dane, rola USER)
-- insert into user_account (email, display_name, role, created_at) values
--     ('imie.nazwisko@student.merito.pl', 'Imię', 'USER', current_timestamp);
