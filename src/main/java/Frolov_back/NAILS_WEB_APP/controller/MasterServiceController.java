package Frolov_back.NAILS_WEB_APP.controller;

import Frolov_back.NAILS_WEB_APP.service.MasterServiceService;
import Frolov_back.NAILS_WEB_APP.DTO.MasterServiceRequestDto;
import Frolov_back.NAILS_WEB_APP.DTO.MasterServiceResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/master-services")
@RequiredArgsConstructor
@Tag(name = "5. Услуги мастеров", description = "API для связи мастеров с услугами")
public class MasterServiceController {

    private final MasterServiceService masterServiceService;

    @Operation(summary = "Добавить услугу мастеру")
    @PostMapping
    public ResponseEntity<MasterServiceResponseDto> addServiceToMaster(@RequestBody MasterServiceRequestDto requestDto) {
        MasterServiceResponseDto response = masterServiceService.addServiceToMaster(requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить услугу у мастера")
    @DeleteMapping
    public ResponseEntity<Void> removeServiceFromMaster(
            @RequestParam Long masterId,
            @RequestParam Long serviceId) {
        masterServiceService.removeServiceFromMaster(masterId, serviceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить услуги мастера")
    @GetMapping("/master/{masterId}")
    public ResponseEntity<List<MasterServiceResponseDto>> getMasterServices(@PathVariable Long masterId) {
        List<MasterServiceResponseDto> services = masterServiceService.getMasterServices(masterId);
        return ResponseEntity.ok(services);
    }

    @Operation(summary = "Получить мастеров для услуги")
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<MasterServiceResponseDto>> getServiceMasters(@PathVariable Long serviceId) {
        List<MasterServiceResponseDto> masters = masterServiceService.getServiceMasters(serviceId);
        return ResponseEntity.ok(masters);
    }

    @Operation(summary = "Проверить предоставляет ли мастер услугу")
    @GetMapping("/check")
    public ResponseEntity<Boolean> masterProvidesService(
            @RequestParam Long masterId,
            @RequestParam Long serviceId) {
        boolean provides = masterServiceService.masterProvidesService(masterId, serviceId);
        return ResponseEntity.ok(provides);
    }
}
