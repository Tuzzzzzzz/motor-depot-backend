package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/excel")
@RequiredArgsConstructor
public class ExportToExcelController {
    private final ExportToExcelService exportToExcelService;

    @GetMapping("/car-driver-pairs")
    public void exportCarDriverToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=car_driver.xlsx");
        exportToExcelService.exportCarDriver(response.getOutputStream());
    }

    @GetMapping("/cars")
    public void exportCarToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=car.xlsx");
        exportToExcelService.exportCar(response.getOutputStream());
    }

    @GetMapping("/consumers")
    public void exportConsumerToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=consumer.xlsx");
        exportToExcelService.exportConsumer(response.getOutputStream());
    }

    @GetMapping("/drivers")
    public ResponseEntity<byte[]> exportDriverToExcel(HttpServletResponse response) throws IOException {
        byte[] excelBytes = exportToExcelService.exportDriverAsByte();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=drivers.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    @GetMapping("/mechanics")
    public void exportMechanicToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=mechanic.xlsx");
        exportToExcelService.exportMechanic(response.getOutputStream());
    }

    @GetMapping("/orders")
    public void exportTransportOrderToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=order.xlsx");
        exportToExcelService.exportTransportOrder(response.getOutputStream());
    }

    @GetMapping("/technical-services")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=ts.xlsx");
        exportToExcelService.exportTechnicalService(response.getOutputStream());
    }
}
