insert into customers(full_name, email, password_hash, segment)
select 'Course Admin', 'admin@admin.local', 'seed:admin', 'PREMIUM'
where not exists (select 1 from customers where email = 'admin@admin.local');

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
