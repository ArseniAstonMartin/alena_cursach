create table roles (id bigserial primary key, name varchar(64) not null unique);
create table customers (id bigserial primary key, full_name varchar(255) not null, email varchar(255) not null unique, password_hash varchar(255) not null, segment varchar(32) not null, created_at timestamptz not null default now());
create table customer_roles (customer_id bigint not null references customers(id) on delete cascade, role_id bigint not null references roles(id), primary key(customer_id, role_id));
create table merchants (id bigserial primary key, name varchar(255) not null, category varchar(128) not null);
create table products (id bigserial primary key, merchant_id bigint not null references merchants(id), name varchar(255) not null, category varchar(128) not null, price numeric(12,2) not null check(price >= 0));
create table purchases (id bigserial primary key, customer_id bigint not null references customers(id), merchant_id bigint not null references merchants(id), status varchar(32) not null, total_amount numeric(12,2) not null, purchased_at timestamptz not null default now());
create table purchase_items (id bigserial primary key, purchase_id bigint not null references purchases(id) on delete cascade, product_id bigint not null references products(id), quantity integer not null check(quantity > 0), unit_price numeric(12,2) not null);
create table loyalty_accounts (id bigserial primary key, customer_id bigint not null unique references customers(id) on delete cascade, points_balance integer not null default 0 check(points_balance >= 0));
create table reward_transactions (id bigserial primary key, account_id bigint not null references loyalty_accounts(id), type varchar(32) not null, points integer not null, reason varchar(255) not null, created_at timestamptz not null default now());
create table offers (id bigserial primary key, title varchar(255) not null, description varchar(1000) not null, target_category varchar(128) not null, target_segment varchar(32) not null, bonus_points integer not null, status varchar(32) not null, valid_until date not null);
create table claimed_offers (id bigserial primary key, customer_id bigint not null references customers(id), offer_id bigint not null references offers(id), claimed_at timestamptz not null default now(), unique(customer_id, offer_id));
create table refresh_tokens (id bigserial primary key, customer_id bigint not null references customers(id) on delete cascade, token varchar(255) not null unique, expires_at timestamptz not null);
create index idx_products_category on products(category);
create index idx_purchases_customer_date on purchases(customer_id, purchased_at desc);
create index idx_reward_account_date on reward_transactions(account_id, created_at desc);
create index idx_offers_status_segment on offers(status, target_segment);
insert into roles(name) values ('ROLE_ADMIN'), ('ROLE_CUSTOMER');
insert into customers(full_name, email, password_hash, segment) values
('Admin User','admin@loyalty.local','seed:admin12345','PREMIUM'),
('Alice Petrova','alice@example.com','seed:password123','REGULAR');
insert into customer_roles(customer_id, role_id) values (1,1),(1,2),(2,2);
insert into merchants(name, category) values ('Green Market','GROCERY'),('Book Space','BOOKS'),('FitLab','SPORT');
insert into products(merchant_id, name, category, price) values (1,'Organic Basket','GROCERY',49.90),(1,'Coffee Beans','GROCERY',16.50),(2,'Clean Architecture Book','BOOKS',42.00),(2,'Java 21 Guide','BOOKS',39.00),(3,'Yoga Mat','SPORT',25.00),(3,'Protein Pack','SPORT',31.00);
insert into loyalty_accounts(customer_id, points_balance) values (1,1000),(2,240);
insert into offers(title, description, target_category, target_segment, bonus_points, status, valid_until) values
('Grocery weekend','Earn extra points for healthy groceries','GROCERY','REGULAR',120,'ACTIVE', current_date + interval '45 days'),
('Premium book club','Premium readers receive boosted rewards','BOOKS','PREMIUM',250,'ACTIVE', current_date + interval '60 days'),
('Newcomer sport start','First sport purchase bonus','SPORT','NEWCOMER',90,'ACTIVE', current_date + interval '30 days');
