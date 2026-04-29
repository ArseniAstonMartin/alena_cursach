alter table redemption_orders add column target_category varchar(128);
update redemption_orders set target_category = 'ANY' where target_category is null;
alter table redemption_orders alter column target_category set not null;
create index idx_redemption_target_category on redemption_orders(customer_id, target_category, status);
