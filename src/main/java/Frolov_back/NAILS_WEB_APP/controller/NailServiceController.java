package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.service.NailServiceService;
import Frolov_back.NAILS_WEB_APP.service.DTO.NailServiceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
@Tag(name = "4. Управление услугами", description = "API для работы с услугами салона")
public class NailServiceController {

    private final NailServiceService nailServiceService;

    @Operation(summary = "Получить все активные услуги")
    @GetMapping
    public ResponseEntity<List<NailServiceDto>> getAllServices() {
        List<NailServiceDto> services = nailServiceService.getAllActiveServices();
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "Получить услугу по ID")
    @GetMapping("/{serviceId}")
    public ResponseEntity<NailServiceDto> getServiceById(@PathVariable Long serviceId) {
        return nailServiceService.getServiceById(serviceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить услуги по категории")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<NailServiceDto>> getServicesByCategory(@PathVariable Long categoryId) {
        List<NailServiceDto> services = nailServiceService.getServicesByCategory(categoryId);
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "Поиск услуг")
    @GetMapping("/search")
    public ResponseEntity<List<NailServiceDto>> searchServices(@RequestParam String query) {
        List<NailServiceDto> services = nailServiceService.searchServices(query);
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "Создать новую услугу")
    @PostMapping
    public ResponseEntity<NailServiceDto> createService(@RequestBody NailServiceDto serviceDto) {
        NailServiceDto createdService = nailServiceService.createService(serviceDto);
        return ResponseEntity.ok(createdService);
    }

    @Operation(summary = "Обновить услугу")
    @PutMapping("/{serviceId}")
    public ResponseEntity<NailServiceDto> updateService(
            @PathVariable Long serviceId,
            @RequestBody NailServiceDto serviceDto) {
        NailServiceDto updatedService = nailServiceService.updateService(serviceId, serviceDto);
        return ResponseEntity.ok(updatedService);
    }

    @Operation(summary = "Деактивировать услугу")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<Void> deactivateService(@PathVariable Long serviceId) {
        nailServiceService.deactivateService(serviceId);
        return ResponseEntity.ok().build();
    }
}