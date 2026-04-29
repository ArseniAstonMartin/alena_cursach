create table purchase_reward_breakdowns (
    id bigserial primary key,
    purchase_id bigint not null unique references purchases(id) on delete cascade,
    base_points integer not null,
    segment_multiplier numeric(5,2) not null,
    offer_bonus_points integer not null,
    final_points integer not null,
    explanation varchar(500) not null,
    created_at timestamptz not null default now()
);
