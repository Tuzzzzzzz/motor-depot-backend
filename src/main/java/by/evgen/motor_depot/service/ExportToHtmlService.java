package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.*;
import by.evgen.motor_depot.database.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportToHtmlService {
    private final CarDriverRepository carDriverRepository;
    private final CarRepository carRepository;
    private final ConsumerRepository consumerRepository;
    private final DriverRepository driverRepository;
    private final MechanicRepository mechanicRepository;
    private final TechnicalServiceRepository technicalServiceRepository;
    private final TransportOrderRepository transportOrderRepository;
    private final TemplateEngine templateEngine;


    public void exportCarDriver(OutputStream outputStream) {
        List<CarDriver> carDriverList = carDriverRepository.findAllForExport();

        Context context = new Context();
        context.setVariable("carDriverList", carDriverList);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            templateEngine.process("car_driver", context, writer);
        }
    }

    public void exportCar(OutputStream outputStream) {
        List<Car> cars = carRepository.findAllForExport();

        Context context = new Context();
        context.setVariable("cars", cars);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            templateEngine.process("car", context, writer);
        }
    }

    public void exportConsumer(OutputStream outputStream) {
        List<Consumer> consumers = consumerRepository.findAllForExport();

        Context context = new Context();
        context.setVariable("consumers", consumers);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            templateEngine.process("consumer", context, writer);
        }
    }

    public byte[] exportDriverAsByte() {
        List<Driver> drivers = driverRepository.findAllForExport();

        Context context = new Context();
        context.setVariable("drivers", drivers);

        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, StandardCharsets.UTF_8))
        ) {
            templateEngine.process("driver", context, writer);
            writer.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed export drivers to html", e);
        }
    }

    public void exportMechanic(OutputStream outputStream) {
        List<Mechanic> mechanics = mechanicRepository.findAllForExport();

        Context context = new Context();
        context.setVariable("mechanics", mechanics);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            templateEngine.process("mechanic", context, writer);
        }
    }

    public void exportTechnicalService(OutputStream outputStream) {
        List<TechnicalService> technicalServices = technicalServiceRepository.findAllForExport();

        Context context = new Context();
        context.setVariable("technicalServices", technicalServices);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            templateEngine.process("technical_service", context, writer);
        }
    }

    public void exportTransportOrder(OutputStream outputStream) {
        List<TransportOrder> transportOrders = transportOrderRepository.findAllForExport();

        Context context = new Context();
        context.setVariable("orders", transportOrders);

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            templateEngine.process("order", context, writer);
        }
    }
}
