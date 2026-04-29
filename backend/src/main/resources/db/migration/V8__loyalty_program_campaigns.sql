insert into offers(title, description, target_category, target_segment, bonus_points, status, valid_until) values
('Book lover accelerator','Claim before buying books and receive a fixed bonus on top of cashback','BOOKS','REGULAR',180,'ACTIVE', current_date + interval '50 days'),
('Sport comeback','Extra points for sport purchases to motivate healthy habits','SPORT','AT_RISK',220,'ACTIVE', current_date + interval '40 days');
update reward_rules set cashback_percent = 12.00, description = 'Education category has the strongest cashback' where category = 'BOOKS';
