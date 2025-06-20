package by.evgen.motor_depot.controller;

import by.evgen.motor_depot.service.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("api/html")
@CrossOrigin(exposedHeaders = "Content-Disposition")
@RequiredArgsConstructor
public class ExportToHtmlController {
    private final ExportToHtmlService exportToHtmlService;

    @GetMapping("/cars")
    public void exportCarToHtml(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=car.html");
        exportToHtmlService.exportCar(response.getOutputStream());
    }

    @GetMapping("/car-driver-pairs")
    public void exportCarDriverToHtml(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=car-driver.html");
        exportToHtmlService.exportCarDriver(response.getOutputStream());
    }

    @GetMapping("/consumers")
    public void exportConsumerToHtml(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=consumer.html");
        exportToHtmlService.exportConsumer(response.getOutputStream());
    }

    @GetMapping("/drivers")
    public ResponseEntity<byte[]> exportDriverToHtml() {
        byte[] htmlBytes = exportToHtmlService.exportDriverAsByte();

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header("Content-Disposition", "attachment; filename=\"driver.html\"")
                .body(htmlBytes);
    }

    @GetMapping("/mechanics")
    public void exportMechanicToHtml(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=mechanic.html");
        exportToHtmlService.exportMechanic(response.getOutputStream());
    }

    @GetMapping("/orders")
    public void exportTransportOrderToHtml(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=order.html");
        exportToHtmlService.exportTransportOrder(response.getOutputStream());
    }

    @GetMapping("/technical-services")
    public void exportTSToHtml(HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=technical_service.html");
        exportToHtmlService.exportTechnicalService(response.getOutputStream());
    }
}
