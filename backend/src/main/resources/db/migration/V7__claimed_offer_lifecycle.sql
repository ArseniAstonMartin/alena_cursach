alter table claimed_offers add column status varchar(32) not null default 'CLAIMED';
alter table claimed_offers add column used_at timestamptz;
create index idx_claimed_offers_status on claimed_offers(customer_id, status);
