alter table redemption_orders add column status varchar(32) not null default 'ACTIVE';
alter table redemption_orders add column expires_at timestamptz not null default (now() + interval '90 days');
alter table redemption_orders add column used_at timestamptz;
alter table redemption_orders add column purchase_id bigint references purchases(id);
create index idx_redemption_status_expiry on redemption_orders(customer_id, status, expires_at);

alter table purchases add column gross_amount numeric(12,2);
alter table purchases add column discount_amount numeric(12,2) not null default 0;
alter table purchases add column certificate_id bigint references redemption_orders(id);
update purchases set gross_amount = total_amount where gross_amount is null;
alter table purchases alter column gross_amount set not null;
