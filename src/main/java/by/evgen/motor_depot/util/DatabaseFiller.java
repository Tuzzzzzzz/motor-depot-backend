package by.evgen.motor_depot.util;

import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

record PersonName(
        String firstname,
        String lastname,
        String surname
) {
}

record BrandModelPair(
        String brand,
        String model
) {
}

record CargoConfigurationOfCar(
        Integer bodyWidth,
        Integer bodyHeight,
        Integer bodyLength,
        Integer cargoCapacity
) {
}

record CarDriverIdsPair(
       Long carId,
       Long driverId
) {
}

@Service
public class DatabaseFiller {

    private static final int DRIVER_COUNT = 1000;
    private static final int CONSUMER_COUNT = 5000;
    private static final int MECHANIC_COUNT = 200;
    private static final int CARS_PER_DRIVER = 2;
    private static final int TRANSPORT_ORDERS_COUNT = 10000;
    private static final int TECHNICAL_SERVICES_COUNT = 1000;
    private static final int SEED = 1;

    private final JdbcTemplate jdbcTemplate;
    private final Faker faker;

    public DatabaseFiller(@Autowired JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        faker = new Faker(new Locale("ru"), new Random(SEED));
    }

    private final List<String> firstnames = List.of(
            "Александр", "Алексей", "Анатолий", "Андрей", "Антон",
            "Аркадий", "Арсений", "Артём", "Борис", "Вадим",
            "Валентин", "Валерий", "Василий", "Виктор", "Виталий",
            "Владимир", "Владислав", "Вячеслав", "Геннадий", "Георгий"
    );

    private final List<String> lastnames = List.of(
            "Иванов", "Петров", "Сидоров", "Смирнов", "Кузнецов",
            "Васильев", "Павлов", "Семёнов", "Голубев", "Виноградов",
            "Богданов", "Воробьёв", "Фёдоров", "Михайлов", "Беляев"
    );

    private final List<String> surnames = List.of(
            "Александрович", "Алексеевич", "Анатольевич", "Андреевич", "Антонович",
            "Аркадьевич", "Арсеньевич", "Артёмович", "Борисович", "Вадимович"
    );

    private final List<BrandModelPair> TRUCK_BRAND_MODELS = List.of(
            new BrandModelPair("Volvo", "FH16"),
            new BrandModelPair("Scania", "R500"),
            new BrandModelPair("MAN", "TGX"),
            new BrandModelPair("Mercedes-Benz", "Actros"),
            new BrandModelPair("DAF", "XF"),
            new BrandModelPair("Iveco", "Stralis"),
            new BrandModelPair("Renault Trucks", "T"),
            new BrandModelPair("Kamaz", "5490"),
            new BrandModelPair("MAZ", "6430"),
            new BrandModelPair("GAZ", "Next")
    );

    private final List<String> tsDescriptions = List.of(
            "Замена масла и фильтров",
            "Проверка тормозной системы",
            "Диагностика двигателя",
            "Регулировка развала-схождения",
            "Замена ремня ГРМ",
            "Проверка электрооборудования",
            "Ремонт подвески",
            "Замена свечей зажигания",
            "Обслуживание КПП",
            "Проверка системы охлаждения"
    );


    @Transactional
//    @EventListener(ApplicationReadyEvent.class)
    public void fill() {
        List<Long> consumerIds = generatePersons(CONSUMER_COUNT,"""
                insert into consumer(firstname, lastname, surname, phone_number, email)
                values (?, ?, ?, ?, ?)
                returning id"""
        );
        List<Long> driverIds = generatePersons(DRIVER_COUNT, """
                insert into driver(firstname, lastname, surname, phone_number, email)
                values (?, ?, ?, ?, ?)
                returning id""");
        List<Long> carIds = generateCars(DRIVER_COUNT * CARS_PER_DRIVER);
        List<CarDriverIdsPair> carDriverIdsPairs = assignCarsToDrivers(carIds, driverIds);
        List<Long> mechanicIds = generatePersons(MECHANIC_COUNT, """
                insert into mechanic(firstname, lastname, surname, phone_number, email)
                values (?, ?, ?, ?, ?)
                returning id""");

        generateTransportOrders(TRANSPORT_ORDERS_COUNT, consumerIds, carDriverIdsPairs);
        generateCompletedTechnicalService(TECHNICAL_SERVICES_COUNT, carIds, mechanicIds);
    }


    private long phoneCounter = 8_900_000_00_00L;

    private String generatePhone() {
        return String.valueOf(phoneCounter++);
    }


    private long licensePlateCounter = 0;

    private String generatePlateCounter() {
        return "CAR-%d".formatted(licensePlateCounter++);
    }


    private PersonName generatePersonName() {

        return new PersonName(
                faker.options().nextElement(firstnames),
                faker.options().nextElement(lastnames),
                faker.options().nextElement(surnames)
        );
    }

    private List<Long> generatePersons(int count, String sql) {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {

            PersonName personName = generatePersonName();

            Long id = jdbcTemplate.queryForObject(
                    sql,
                    Long.class,
                    personName.firstname(),
                    personName.lastname(),
                    personName.surname(),
                    generatePhone(),
                    faker.internet().emailAddress()
            );
            ids.add(id);
        }
        return ids;
    }

    private List<CarDriverIdsPair> assignCarsToDrivers(List<Long> carIds, List<Long> driverIds) {
        List<CarDriverIdsPair> ids = new ArrayList<>();
        for (int i = 0; i < driverIds.size(); i++) {
            for (int j = 0; j < CARS_PER_DRIVER; j++) {
                jdbcTemplate.update(
                        """
                               insert into car_driver(car_id, driver_id)
                               values (?, ?)""",
                        carIds.get(i * CARS_PER_DRIVER + j),
                        driverIds.get(i)
                );
                ids.add(
                        new CarDriverIdsPair(
                            carIds.get(i * CARS_PER_DRIVER + j),
                            driverIds.get(i)
                        )
                );
            }
        }
        return ids;
    }


    private Map<Long, CargoConfigurationOfCar> cargoConfigurationOfCars = new HashMap<>();

    private List<Long> generateCars(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    BrandModelPair brandModel = TRUCK_BRAND_MODELS.get(i % TRUCK_BRAND_MODELS.size());
                    Integer bodyWidth = faker.number().numberBetween(200, 251);
                    Integer bodyHeight = faker.number().numberBetween(200, 301);
                    Integer bodyLength = faker.number().numberBetween(500, 801);
                    Integer cargoCapacity = faker.number().numberBetween(10000, 30001);

                    Long id = jdbcTemplate.queryForObject(
                            """
                            insert into car(license_plate, brand, model, body_width, body_height, body_length, cargo_capacity)
                            values (?, ?, ?, ?, ?, ?, ?)
                            returning id""",
                            Long.class,
                            generatePlateCounter(),
                            brandModel.brand(),
                            brandModel.model(),
                            bodyWidth,
                            bodyHeight,
                            bodyLength,
                            cargoCapacity
                    );

                    cargoConfigurationOfCars.put(
                            id,
                            new CargoConfigurationOfCar(
                                    bodyWidth,
                                    bodyHeight,
                                    bodyLength,
                                    cargoCapacity
                            ));

                    return id;
                })
                .toList();
    }

    private void generateTransportOrders(int count, List<Long> consumerIds, List<CarDriverIdsPair> carDriverIdsPairs) {

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {

            LocalDateTime departureTime = now
                    .minusDays(faker.number().numberBetween(1, 31))
                    .minusHours(faker.number().numberBetween(0, 24))
                    .minusMinutes(faker.number().numberBetween(0, 60));

            LocalDateTime arrivalTime = departureTime.plusHours(faker.number().numberBetween(1, 11));

            CarDriverIdsPair pair = faker.options().nextElement(carDriverIdsPairs);

            Long carId = pair.carId();

            Long driverId = pair.driverId();

            var carConfig = cargoConfigurationOfCars.get(carId);

            int cargoWidth = faker.number().numberBetween(50, carConfig.bodyWidth());
            int cargoHeight = faker.number().numberBetween(50, carConfig.bodyHeight());
            int cargoLength = faker.number().numberBetween(50, carConfig.bodyLength());
            int cargoWeight = faker.number().numberBetween(50, carConfig.cargoCapacity());

            jdbcTemplate.queryForObject(
                    """
                            insert into transport_order(
                            consumer_id,
                            car_id,
                            driver_id,
                            cost,
                            departure_place,
                            arrival_place,
                            departure_time,
                            arrival_time,
                            weight,
                            width,
                            height,
                            length
                    )
                    values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    returning id""",
                    Long.class,
                    faker.options().nextElement(consumerIds),
                    carId,
                    driverId,
                    BigDecimal.valueOf(faker.number().randomDouble(2, 5000, 50001)),
                    faker.address().city(),
                    faker.address().city(),
                    Timestamp.valueOf(departureTime),
                    Timestamp.valueOf(arrivalTime),
                    cargoWeight,
                    cargoWidth,
                    cargoHeight,
                    cargoLength
            );
        }
    }


    private void generateCompletedTechnicalService(int count, List<Long> carIds, List<Long> mechanicIds) {

        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < count; i++) {
            LocalDateTime serviceEndTime = now
                    .minusDays(faker.number().numberBetween(1, 31))
                    .minusHours(faker.number().numberBetween(0, 24))
                    .minusMinutes(faker.number().numberBetween(0, 60));

            LocalDateTime serviceStartTime = serviceEndTime
                    .minusHours(faker.number().numberBetween(0, 24))
                    .minusMinutes(faker.number().numberBetween(0, 60));

            jdbcTemplate.queryForObject(
                    """
                    insert into technical_service(service_start_time, service_end_time, car_id, mechanic_id, description)
                    values (?, ?, ?, ?, ?)
                    returning id""",
                    Long.class,
                    Timestamp.valueOf(serviceStartTime),
                    Timestamp.valueOf(serviceEndTime),
                    faker.options().nextElement(carIds),
                    faker.options().nextElement(mechanicIds),
                    faker.options().nextElement(tsDescriptions)
            );
        }
    }
}