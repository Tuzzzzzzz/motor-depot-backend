CREATE DOMAIN phone_number AS VARCHAR(12)
CHECK (VALUE ~ '^(\+7|8)[0-9]{10}$');

CREATE DOMAIN email_address AS VARCHAR(256)
CHECK (VALUE ~ '^[а-яА-ЯёЁa-zA-Z0-9._%+-]+@[а-яА-ЯёЁa-zA-Z0-9.-]+[.][а-яА-ЯёЁa-zA-Z]{2,}$');

create table consumer(
	id bigserial,
	firstname varchar(256) not null,
	lastname varchar(256) not null,
	surname varchar(256),
	phone_number phone_number not null unique,
	email email_address,
	primary key(id)
);

create table driver(
	id bigserial,
	firstname varchar(256) not null,
	lastname varchar(256) not null,
	surname varchar(256),
	phone_number phone_number not null unique,
    email email_address,
	primary key(id)
);

create table car(
	id bigserial,
	license_plate varchar(20) not null unique,
	brand varchar(256) not null,
	model varchar(256) not null,
	body_width integer not null check (body_width > 0),
	body_height integer not null check (body_height > 0),
	body_length integer not null check (body_length > 0),
	cargo_capacity integer not null check (cargo_capacity > 0),
	primary key(id)
);

create table car_driver (
    car_id bigint not null,
    driver_id bigint not null,
    primary key (car_id, driver_id),
    foreign key (car_id) references car(id) on delete cascade,
    foreign key (driver_id) references driver(id) on delete cascade
);

create table transport_order(
	id bigserial,
	consumer_id bigint not null,
	car_id bigint not null,
	driver_id bigint not null,
	cost decimal(10, 2) not null check (cost > 0),
	departure_place text not null,
	arrival_place text not null,
	departure_time timestamp not null,
	arrival_time timestamp not null,
	check (arrival_time > departure_time),
	created_at timestamp not null default current_timestamp,
	weight integer not null check (weight > 0),
    width integer not null check (width > 0),
    height integer not null check (height > 0),
    length integer not null check (length > 0),
	primary key(id),
	foreign key(consumer_id) references consumer(id) on delete cascade,
	foreign key(car_id) references car(id) on delete cascade,
	foreign key(driver_id) references driver(id) on delete cascade
);

create table mechanic(
	id bigserial,
	firstname varchar(256) not null,
	lastname varchar(256) not null,
	surname varchar(256),
	phone_number phone_number not null unique,
    email email_address,
	primary key(id)
);

create table technical_service (
    id bigserial,
    service_start_time timestamp not null,
    service_end_time timestamp not null,
    check (service_end_time >= service_start_time),
    car_id bigint not null references car(id) on delete cascade,
    mechanic_id bigint not null references mechanic(id) on delete cascade,
    description text not null,
    report_created_at timestamp not null default current_timestamp,
    primary key(id)
);

-- car
create index idx_car_cargo_params on car(body_width, body_height, body_length, cargo_capacity);

-- transport_order
create index idx_transport_order_car on transport_order(car_id);
create index idx_transport_order_driver on transport_order(driver_id);
create index idx_transport_order_consumer on transport_order(consumer_id);
create index idx_transport_order_created_at on transport_order(created_at);
create index idx_transport_order_time_range on transport_order(arrival_time, departure_time);

-- completed_technical_service
create index idx_technical_service_car on technical_service(car_id);
create index idx_technical_service_mechanic on technical_service(mechanic_id);
create index idx_technical_service_report_created_at on technical_service(report_created_at);