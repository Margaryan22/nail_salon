package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.service.DTO.NailServiceDto;
import java.util.List;
import java.util.Optional;

public interface NailServiceService {
    List<NailServiceDto> getAllActiveServices();
    Optional<NailServiceDto> getServiceById(Long serviceId);
    List<NailServiceDto> getServicesByCategory(Long categoryId);
    List<NailServiceDto> searchServices(String query);
    NailServiceDto createService(NailServiceDto serviceDto);
    NailServiceDto updateService(Long serviceId, NailServiceDto serviceDto);
    void deactivateService(Long serviceId);
}