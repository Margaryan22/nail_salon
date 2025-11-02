package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.NailService;
import Frolov_back.NAILS_WEB_APP.domain.ServiceCategory;
import Frolov_back.NAILS_WEB_APP.repository.NailServiceRepository;
import Frolov_back.NAILS_WEB_APP.repository.ServiceCategoryRepository;
import Frolov_back.NAILS_WEB_APP.service.NailServiceService;
import Frolov_back.NAILS_WEB_APP.service.DTO.NailServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NailServiceServiceImpl implements NailServiceService {

    private final NailServiceRepository nailServiceRepository;
    private final ServiceCategoryRepository serviceCategoryRepository;

    @Override
    public List<NailServiceDto> getAllActiveServices() {
        return nailServiceRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public Optional<NailServiceDto> getServiceById(Long serviceId) {
        return nailServiceRepository.findById(serviceId)
                .map(this::convertToDto);
    }

    @Override
    public List<NailServiceDto> getServicesByCategory(Long categoryId) {
        ServiceCategory category = serviceCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        return nailServiceRepository.findByCategoryAndActiveTrue(category).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<NailServiceDto> searchServices(String query) {
        return nailServiceRepository.findByNameContainingIgnoreCaseAndActiveTrue(query).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public NailServiceDto createService(NailServiceDto serviceDto) {
        // Проверяем уникальность имени
        if (nailServiceRepository.existsByName(serviceDto.getName())) {
            throw new RuntimeException("Услуга с таким названием уже существует");
        }

        ServiceCategory category = null;
        if (serviceDto.getCategoryId() != null) {
            category = serviceCategoryRepository.findById(serviceDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        }

        NailService service = new NailService();
        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setBaseDuration(serviceDto.getBaseDuration());
        service.setBasePrice(serviceDto.getBasePrice());
        service.setCategory(category);
        service.setActive(true);

        NailService savedService = nailServiceRepository.save(service);
        return convertToDto(savedService);
    }

    @Override
    @Transactional
    public NailServiceDto updateService(Long serviceId, NailServiceDto serviceDto) {
        NailService service = nailServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        ServiceCategory category = null;
        if (serviceDto.getCategoryId() != null) {
            category = serviceCategoryRepository.findById(serviceDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
        }

        service.setName(serviceDto.getName());
        service.setDescription(serviceDto.getDescription());
        service.setBaseDuration(serviceDto.getBaseDuration());
        service.setBasePrice(serviceDto.getBasePrice());
        service.setCategory(category);

        NailService updatedService = nailServiceRepository.save(service);
        return convertToDto(updatedService);
    }

    @Override
    @Transactional
    public void deactivateService(Long serviceId) {
        NailService service = nailServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));
        service.setActive(false);
        nailServiceRepository.save(service);
    }

    private NailServiceDto convertToDto(NailService service) {
        NailServiceDto dto = new NailServiceDto();
        dto.setServiceId(service.getServiceId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setBaseDuration(service.getBaseDuration());
        dto.setBasePrice(service.getBasePrice());
        dto.setActive(service.getActive());

        if (service.getCategory() != null) {
            dto.setCategoryId(service.getCategory().getCategoryId());
            dto.setCategoryName(service.getCategory().getName());
        }

        return dto;
    }
}