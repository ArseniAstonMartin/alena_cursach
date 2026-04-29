create table product_categories (
    code varchar(128) primary key,
    display_name varchar(160) not null,
    description varchar(500) not null,
    cashback_percent numeric(5,2) not null check(cashback_percent >= 0),
    strategic_priority integer not null default 50,
    mission_multiplier numeric(5,2) not null default 1.00,
    active boolean not null default true,
    created_at timestamptz not null default now()
);

insert into product_categories(code, display_name, description, cashback_percent, strategic_priority, mission_multiplier, active) values
('GROCERY', 'Everyday groceries', 'High-frequency products for retention mechanics', 6.00, 40, 1.00, true),
('BOOKS', 'Books and education', 'Education category with higher cashback and knowledge-based offers', 12.00, 70, 1.15, true),
('SPORT', 'Sport and wellness', 'Strategic growth category for cross-sell missions', 8.00, 90, 1.25, true)
on conflict (code) do nothing;

insert into product_categories(code, display_name, description, cashback_percent, strategic_priority, mission_multiplier, active)
select distinct p.category, initcap(lower(p.category)), 'Auto-created category from existing products', coalesce(rr.cashback_percent, 5.00), 50, 1.00, true
from products p
left join reward_rules rr on rr.category = p.category
where not exists (select 1 from product_categories pc where pc.code = p.category);

alter table products add constraint fk_products_category_code foreign key (category) references product_categories(code);
alter table reward_rules add constraint fk_reward_rules_category_code foreign key (category) references product_categories(code);
