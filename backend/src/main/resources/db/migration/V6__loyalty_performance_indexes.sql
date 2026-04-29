create index idx_purchase_items_purchase on purchase_items(purchase_id);
create index idx_products_merchant_category on products(merchant_id, category);
create index idx_claimed_offers_customer_offer on claimed_offers(customer_id, offer_id);
