create table reward_rules (
    id bigserial primary key,
    category varchar(128) not null unique,
    cashback_percent numeric(5,2) not null check(cashback_percent >= 0),
    description varchar(255) not null,
    active boolean not null default true
);
insert into reward_rules(category, cashback_percent, description) values
('GROCERY', 6.00, 'Everyday grocery cashback'),
('BOOKS', 10.00, 'Education and reading boosted cashback'),
('SPORT', 8.00, 'Healthy lifestyle partner cashback');
