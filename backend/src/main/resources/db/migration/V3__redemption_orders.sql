create table redemption_orders (
    id bigserial primary key,
    customer_id bigint not null references customers(id),
    account_id bigint not null references loyalty_accounts(id),
    points_spent integer not null check(points_spent > 0),
    discount_amount numeric(12,2) not null check(discount_amount > 0),
    reward_name varchar(160) not null,
    confirmation_code varchar(64) not null unique,
    created_at timestamptz not null default now()
);
create index idx_redemption_customer_date on redemption_orders(customer_id, created_at desc);
