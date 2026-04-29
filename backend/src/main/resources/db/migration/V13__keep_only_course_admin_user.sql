-- Reset demo data for final course testing: keep only the course admin account.
-- New customers should be created through the registration flow.
update purchases
set certificate_id = null
where customer_id in (select id from customers where email <> 'admin@admin.local');

update redemption_orders
set purchase_id = null
where customer_id in (select id from customers where email <> 'admin@admin.local')
   or purchase_id in (select p.id from purchases p join customers c on c.id = p.customer_id where c.email <> 'admin@admin.local');

delete from purchase_items
where purchase_id in (select p.id from purchases p join customers c on c.id = p.customer_id where c.email <> 'admin@admin.local');

delete from purchase_reward_breakdowns
where purchase_id in (select p.id from purchases p join customers c on c.id = p.customer_id where c.email <> 'admin@admin.local');

delete from refresh_tokens
where customer_id in (select id from customers where email <> 'admin@admin.local');

delete from claimed_offers
where customer_id in (select id from customers where email <> 'admin@admin.local');

delete from purchases
where customer_id in (select id from customers where email <> 'admin@admin.local');

delete from redemption_orders
where customer_id in (select id from customers where email <> 'admin@admin.local')
   or account_id in (select la.id from loyalty_accounts la join customers c on c.id = la.customer_id where c.email <> 'admin@admin.local');

delete from reward_transactions
where account_id in (select la.id from loyalty_accounts la join customers c on c.id = la.customer_id where c.email <> 'admin@admin.local');

delete from loyalty_accounts
where customer_id in (select id from customers where email <> 'admin@admin.local');

delete from customer_roles
where customer_id in (select id from customers where email <> 'admin@admin.local');

delete from customers
where email <> 'admin@admin.local';

insert into customer_roles(customer_id, role_id)
select c.id, r.id
from customers c, roles r
where c.email = 'admin@admin.local' and r.name in ('ROLE_ADMIN', 'ROLE_CUSTOMER')
on conflict do nothing;

insert into loyalty_accounts(customer_id, points_balance)
select c.id, 500
from customers c
where c.email = 'admin@admin.local'
  and not exists (select 1 from loyalty_accounts la where la.customer_id = c.id);
