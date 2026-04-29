create table customer_segment_thresholds (
    segment varchar(32) primary key,
    min_total_spend numeric(12,2) not null,
    multiplier numeric(5,2) not null,
    description varchar(255) not null
);
insert into customer_segment_thresholds(segment, min_total_spend, multiplier, description) values
('NEWCOMER', 0, 1.20, 'Welcome boost for new customers'),
('REGULAR', 150, 1.00, 'Stable cashback level'),
('PREMIUM', 700, 1.35, 'Premium multiplier for loyal customers'),
('AT_RISK', 0, 1.50, 'Win-back multiplier for inactive customers');
