# Schemat bazy danych

Plik generują reguły projektu (panel Maven → Lifecycle → verify). Nie edytujcie go ręcznie.

Tabele: 6 (w tym tabele łączące @ManyToMany: order_request_tag) · kolumny: 34 · relacje: 3 × @ManyToOne, 3 × @OneToMany, 1 × @OneToOne, 1 × @ManyToMany

| Tabela | Encja | Kolumny | Klucze obce |
|---|---|---|---|
| customer | Customer | 9 | user_account_id |
| delivery_offer | DeliveryOffer | 8 | courier_id, order_request_id |
| order_request | OrderRequest | 7 | customer_id |
| tag | Tag | 5 | — |
| user_account | UserAccount | 5 | — |

```mermaid
erDiagram
    customer {
        Long id PK
        Boolean active
        String city
        String country
        String email
        String full_name
        LocalDate registered_on
        String street
        Long user_account_id FK
    }
    delivery_offer {
        Long id PK
        Boolean accepted
        Long courier_id FK
        BigDecimal delivery_fee
        LocalDate estimated_delivery
        String notes
        Long order_request_id FK
        String seller_location
    }
    order_request {
        Long id PK
        BigDecimal budget
        Long customer_id FK
        String item_name
        String origin_country
        LocalDate request_date
        OrderStatus status
    }
    tag {
        Long id PK
        Boolean active
        LocalDate created_on
        String description
        String name
    }
    user_account {
        Long id PK
        LocalDateTime created_at
        String display_name
        String email
        AccountRole role
    }
    customer ||--o{ delivery_offer : "courier_id"
    customer ||--o{ order_request : "customer_id"
    order_request ||--o{ delivery_offer : "order_request_id"
    order_request }o--o{ tag : "order_request_tag"
    user_account |o--o| customer : "user_account_id"
```
