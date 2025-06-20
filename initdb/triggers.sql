CREATE OR REPLACE FUNCTION check_driver_unique_phone_number()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM driver
        WHERE phone_number = NEW.phone_number
          AND id <> NEW.id
    ) THEN
        RAISE EXCEPTION 'APP: Водитель с телефоном % уже существует', NEW.phone_number;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_driver_unique_phone_number
BEFORE INSERT OR UPDATE OF phone_number ON driver
FOR EACH ROW
EXECUTE FUNCTION check_driver_unique_phone_number();

CREATE OR REPLACE FUNCTION check_consumer_unique_phone_number()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM consumer
        WHERE phone_number = NEW.phone_number
          AND id <> NEW.id
    ) THEN
        RAISE EXCEPTION 'APP: Заказчик с телефоном % уже существует', NEW.phone_number;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_consumer_unique_phone_number
BEFORE INSERT OR UPDATE OF phone_number ON consumer
FOR EACH ROW
EXECUTE FUNCTION check_consumer_unique_phone_number();

CREATE OR REPLACE FUNCTION check_mechanic_unique_phone_number()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM mechanic
        WHERE phone_number = NEW.phone_number
          AND id <> NEW.id
    ) THEN
        RAISE EXCEPTION 'APP: Механик с телефоном % уже существует', NEW.phone_number;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER enforce_mechanic_unique_phone_number
BEFORE INSERT OR UPDATE OF phone_number ON mechanic
FOR EACH ROW
EXECUTE FUNCTION check_mechanic_unique_phone_number();

CREATE OR REPLACE FUNCTION check_car_driver_availability()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM transport_order
        WHERE car_id = NEW.car_id
        AND id != COALESCE(NEW.id, -1)
        AND (
            (departure_time, arrival_time) OVERLAPS (NEW.departure_time, NEW.arrival_time)
        )
    ) THEN
        RAISE EXCEPTION 'APP: Автомобиль занят в указанный период времени';
    END IF;

    IF EXISTS (
        SELECT 1 FROM transport_order
        WHERE driver_id = NEW.driver_id
        AND id != COALESCE(NEW.id, -1)
        AND (
            (departure_time, arrival_time) OVERLAPS (NEW.departure_time, NEW.arrival_time)
        )
    ) THEN
        RAISE EXCEPTION 'APP: Водитель занят в указанный период времени';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER transport_order_availability_check
BEFORE INSERT OR UPDATE ON transport_order
FOR EACH ROW EXECUTE FUNCTION check_car_driver_availability();