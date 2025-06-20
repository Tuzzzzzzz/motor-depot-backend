package by.evgen.motor_depot.service;

import by.evgen.motor_depot.database.entity.*;
import by.evgen.motor_depot.database.repository.*;
import by.evgen.motor_depot.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportToExcelService {
    private final CarDriverRepository carDriverRepository;
    private final CarRepository carRepository;
    private final ConsumerRepository consumerRepository;
    private final DriverRepository driverRepository;
    private final MechanicRepository mechanicRepository;
    private final TechnicalServiceRepository technicalServiceRepository;
    private final TransportOrderRepository transportOrderRepository;


    public void exportCarDriver(OutputStream outputStream) throws IOException {
        List<CarDriver> carDriverList = carDriverRepository.findAllForExport();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("car-driver");

            Map<String, Function<CarDriver, Object>> headers = new LinkedHashMap<>();
            headers.put("car.license_plate", cd -> cd.getCar().getLicensePlate());
            headers.put("car.brand", cd -> cd.getCar().getBrand());
            headers.put("car.model", cd -> cd.getCar().getModel());
            headers.put("car.body_width", cd -> cd.getCar().getBodyWidth());
            headers.put("car.body_height", cd -> cd.getCar().getBodyHeight());
            headers.put("car.body_length", cd -> cd.getCar().getBodyLength());
            headers.put("car.cargo_capacity", cd -> cd.getCar().getCargoCapacity());
            headers.put("driver.firstname", cd -> cd.getDriver().getFirstname());
            headers.put("driver.lastname", cd -> cd.getDriver().getLastname());
            headers.put("driver.surname", cd -> cd.getDriver().getSurname());
            headers.put("driver.phone_number", cd -> cd.getDriver().getPhoneNumber());
            headers.put("driver.email", cd -> cd.getDriver().getEmail());

            Row headerRow = sheet.createRow(0);

            int colNum = 0;
            for (String header : headers.keySet()) {
                headerRow.createCell(colNum++).setCellValue(header);
            }

            int rowNum = 1;
            for (CarDriver carDriver : carDriverList) {
                Row row = sheet.createRow(rowNum++);
                colNum = 0;
                for (Function<CarDriver, Object> valueExtractor : headers.values()) {
                    Object value = valueExtractor.apply(carDriver);
                    Cell cell = row.createCell(colNum++);
                    if (value instanceof Integer intValue) {
                        cell.setCellValue(intValue);
                    } else {
                        cell.setCellValue(value.toString());
                    }
                }
            }
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }


    public void exportCar(OutputStream outputStream) throws IOException {
        List<Car> cars = carRepository.findAllForExport();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("car");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("license_plate");
            headerRow.createCell(2).setCellValue("brand");
            headerRow.createCell(3).setCellValue("model");
            headerRow.createCell(4).setCellValue("body_width");
            headerRow.createCell(5).setCellValue("body_height");
            headerRow.createCell(6).setCellValue("body_length");
            headerRow.createCell(7).setCellValue("cargo_capacity");

            int rowNum = 1;
            for (Car car : cars) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(car.getId());
                row.createCell(1).setCellValue(car.getLicensePlate());
                row.createCell(2).setCellValue(car.getBrand());
                row.createCell(3).setCellValue(car.getModel());
                row.createCell(4).setCellValue(car.getBodyWidth());
                row.createCell(5).setCellValue(car.getBodyHeight());
                row.createCell(6).setCellValue(car.getBodyLength());
                row.createCell(7).setCellValue(car.getCargoCapacity());
            }

            for (int i = 0; i < 8; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }


    public void exportConsumer(OutputStream outputStream) throws IOException {
        List<Consumer> consumers = consumerRepository.findAllForExport();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("consumer");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("firstname");
            headerRow.createCell(2).setCellValue("lastname");
            headerRow.createCell(3).setCellValue("surname");
            headerRow.createCell(4).setCellValue("phone_number");
            headerRow.createCell(5).setCellValue("email");

            int rowNum = 1;
            for (Consumer consumer : consumers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(consumer.getId());
                row.createCell(1).setCellValue(consumer.getFirstname());
                row.createCell(2).setCellValue(consumer.getLastname());
                row.createCell(3).setCellValue(consumer.getSurname());
                row.createCell(4).setCellValue(consumer.getPhoneNumber());
                row.createCell(5).setCellValue(consumer.getEmail());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }


    public byte[] exportDriverAsByte() {
        List<Driver> drivers = driverRepository.findAllForExport();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("driver");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("firstname");
            headerRow.createCell(2).setCellValue("lastname");
            headerRow.createCell(3).setCellValue("surname");
            headerRow.createCell(4).setCellValue("phone_number");
            headerRow.createCell(5).setCellValue("email");

            int rowNum = 1;
            for (Driver driver : drivers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(driver.getId());
                row.createCell(1).setCellValue(driver.getFirstname());
                row.createCell(2).setCellValue(driver.getLastname());
                row.createCell(3).setCellValue(driver.getSurname());
                row.createCell(4).setCellValue(driver.getPhoneNumber());
                row.createCell(5).setCellValue(driver.getEmail());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed export drivers to excel", e);
        }
    }


    public void exportMechanic(OutputStream outputStream) throws IOException {
        List<Mechanic> mechanics = mechanicRepository.findAllForExport();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("mechanic");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("firstname");
            headerRow.createCell(2).setCellValue("lastname");
            headerRow.createCell(3).setCellValue("surname");
            headerRow.createCell(4).setCellValue("phone_number");
            headerRow.createCell(5).setCellValue("email");

            int rowNum = 1;
            for (Mechanic mechanic : mechanics) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(mechanic.getId());
                row.createCell(1).setCellValue(mechanic.getFirstname());
                row.createCell(2).setCellValue(mechanic.getLastname());
                row.createCell(3).setCellValue(mechanic.getSurname());
                row.createCell(4).setCellValue(mechanic.getPhoneNumber());
                row.createCell(5).setCellValue(mechanic.getEmail());
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }


    public void exportTechnicalService(OutputStream outputStream) throws IOException {
        List<TechnicalService> technicalServices = technicalServiceRepository.findAllForExport();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("technical-services");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("service_start_time");
            headerRow.createCell(2).setCellValue("service_end_time");
            headerRow.createCell(3).setCellValue("description");
            headerRow.createCell(4).setCellValue("report_created_at");

            headerRow.createCell(5).setCellValue("car.license_plate");
            headerRow.createCell(6).setCellValue("car.brand");
            headerRow.createCell(7).setCellValue("car.model");
            headerRow.createCell(8).setCellValue("car.body_width");
            headerRow.createCell(9).setCellValue("car.body_height");
            headerRow.createCell(10).setCellValue("car.body_length");
            headerRow.createCell(11).setCellValue("car.cargo_capacity");

            headerRow.createCell(12).setCellValue("mechanic.firstname");
            headerRow.createCell(13).setCellValue("mechanic.lastname");
            headerRow.createCell(14).setCellValue("mechanic.surname");
            headerRow.createCell(15).setCellValue("mechanic.phone_number");
            headerRow.createCell(16).setCellValue("mechanic.email");

            CellStyle dateStyle = ExcelUtil.createDateStyle(workbook);
            sheet.setDefaultColumnStyle(1, dateStyle);
            sheet.setDefaultColumnStyle(2, dateStyle);
            sheet.setDefaultColumnStyle(4, dateStyle);

            int rowNum = 1;
            for (TechnicalService service : technicalServices) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(service.getId());
                row.createCell(1).setCellValue(service.getServiceStartTime());
                row.createCell(2).setCellValue(service.getServiceEndTime());
                row.createCell(3).setCellValue(service.getDescription());
                row.createCell(4).setCellValue(service.getReportCreatedAt());

                Car car = service.getCar();
                row.createCell(5).setCellValue(car.getLicensePlate());
                row.createCell(6).setCellValue(car.getBrand());
                row.createCell(7).setCellValue(car.getModel());
                row.createCell(8).setCellValue(car.getBodyWidth());
                row.createCell(9).setCellValue(car.getBodyHeight());
                row.createCell(10).setCellValue(car.getBodyLength());
                row.createCell(11).setCellValue(car.getCargoCapacity());

                Mechanic mechanic = service.getMechanic();
                row.createCell(12).setCellValue(mechanic.getFirstname());
                row.createCell(13).setCellValue(mechanic.getLastname());
                row.createCell(14).setCellValue(mechanic.getSurname());
                row.createCell(15).setCellValue(mechanic.getPhoneNumber());
                row.createCell(16).setCellValue(mechanic.getEmail());
            }

            for (int i = 0; i < 17; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }


    public void exportTransportOrder(OutputStream outputStream) throws IOException {
        List<TransportOrder> orders = transportOrderRepository.findAllForExport();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("transport-orders");

            CellStyle dateCellStyle = workbook.createCellStyle();
            CreationHelper createHelper = workbook.getCreationHelper();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd.MM.yyyy HH:mm"));

            Row headerRow = sheet.createRow(0);

            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("cost");
            headerRow.createCell(2).setCellValue("departure_place");
            headerRow.createCell(3).setCellValue("arrival_place");
            headerRow.createCell(4).setCellValue("departure_time");
            headerRow.createCell(5).setCellValue("arrival_time");
            headerRow.createCell(6).setCellValue("created_at");
            headerRow.createCell(7).setCellValue("weight");
            headerRow.createCell(8).setCellValue("width");
            headerRow.createCell(9).setCellValue("height");
            headerRow.createCell(10).setCellValue("length");

            headerRow.createCell(11).setCellValue("consumer.firstname");
            headerRow.createCell(12).setCellValue("consumer.lastname");
            headerRow.createCell(13).setCellValue("consumer.surname");
            headerRow.createCell(14).setCellValue("consumer.phone_number");
            headerRow.createCell(15).setCellValue("consumer.email");

            headerRow.createCell(16).setCellValue("car.license_plate");
            headerRow.createCell(17).setCellValue("car.brand");
            headerRow.createCell(18).setCellValue("car.model");
            headerRow.createCell(19).setCellValue("car.body_width");
            headerRow.createCell(20).setCellValue("car.body_height");
            headerRow.createCell(21).setCellValue("car.body_length");
            headerRow.createCell(22).setCellValue("car.cargo_capacity");

            headerRow.createCell(23).setCellValue("driver.firstname");
            headerRow.createCell(24).setCellValue("driver.lastname");
            headerRow.createCell(25).setCellValue("driver.surname");
            headerRow.createCell(26).setCellValue("driver.phone_number");
            headerRow.createCell(27).setCellValue("driver.email");

            CellStyle dateStyle = ExcelUtil.createDateStyle(workbook);
            sheet.setDefaultColumnStyle(4, dateStyle);
            sheet.setDefaultColumnStyle(5, dateStyle);
            sheet.setDefaultColumnStyle(6, dateStyle);

            int rowNum = 1;
            for (TransportOrder order : orders) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getCost().doubleValue());
                row.createCell(2).setCellValue(order.getDeparturePlace());
                row.createCell(3).setCellValue(order.getArrivalPlace());

                Cell departureTimeCell = row.createCell(4);
                departureTimeCell.setCellValue(order.getDepartureTime());

                Cell arrivalTimeCell = row.createCell(5);
                arrivalTimeCell.setCellValue(order.getArrivalTime());

                Cell createdAtCell = row.createCell(6);
                createdAtCell.setCellValue(order.getCreatedAt());

                row.createCell(7).setCellValue(order.getWeight());
                row.createCell(8).setCellValue(order.getWidth());
                row.createCell(9).setCellValue(order.getHeight());
                row.createCell(10).setCellValue(order.getLength());

                Consumer consumer = order.getConsumer();
                row.createCell(11).setCellValue(consumer.getFirstname());
                row.createCell(12).setCellValue(consumer.getLastname());
                row.createCell(13).setCellValue(consumer.getSurname());
                row.createCell(14).setCellValue(consumer.getPhoneNumber());
                row.createCell(15).setCellValue(consumer.getEmail());

                Car car = order.getCar();
                row.createCell(16).setCellValue(car.getLicensePlate());
                row.createCell(17).setCellValue(car.getBrand());
                row.createCell(18).setCellValue(car.getModel());
                row.createCell(19).setCellValue(car.getBodyWidth());
                row.createCell(20).setCellValue(car.getBodyHeight());
                row.createCell(21).setCellValue(car.getBodyLength());
                row.createCell(22).setCellValue(car.getCargoCapacity());

                Driver driver = order.getDriver();
                row.createCell(23).setCellValue(driver.getFirstname());
                row.createCell(24).setCellValue(driver.getLastname());
                row.createCell(25).setCellValue(driver.getSurname());
                row.createCell(26).setCellValue(driver.getPhoneNumber());
                row.createCell(27).setCellValue(driver.getEmail());
            }

            for (int i = 0; i < 28; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
        }
    }
}
