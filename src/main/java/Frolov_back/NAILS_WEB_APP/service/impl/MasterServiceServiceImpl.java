package Frolov_back.NAILS_WEB_APP.service.impl;

import Frolov_back.NAILS_WEB_APP.domain.MasterServiceEntity;
import Frolov_back.NAILS_WEB_APP.domain.NailService;
import Frolov_back.NAILS_WEB_APP.domain.SystemUser;
import Frolov_back.NAILS_WEB_APP.repository.MasterServiceEntityRepository;
import Frolov_back.NAILS_WEB_APP.repository.NailServiceRepository;
import Frolov_back.NAILS_WEB_APP.repository.SystemUserRepository;
import Frolov_back.NAILS_WEB_APP.service.MasterServiceService;
import Frolov_back.NAILS_WEB_APP.service.DTO.MasterServiceRequestDto;
import Frolov_back.NAILS_WEB_APP.service.DTO.MasterServiceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterServiceServiceImpl implements MasterServiceService {

    private final MasterServiceEntityRepository masterServiceRepository;
    private final SystemUserRepository systemUserRepository;
    private final NailServiceRepository nailServiceRepository;

    @Override
    @Transactional
    public MasterServiceResponseDto addServiceToMaster(MasterServiceRequestDto requestDto) {
        SystemUser master = systemUserRepository.findById(requestDto.getMasterId())
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        NailService service = nailServiceRepository.findById(requestDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        // Проверяем не связаны ли уже
        if (masterServiceRepository.existsByMasterAndService(master, service)) {
            throw new RuntimeException("Мастер уже предоставляет эту услугу");
        }

        MasterServiceEntity masterService = new MasterServiceEntity();
        masterService.setMaster(master);
        masterService.setService(service);
        masterService.setMasterPrice(requestDto.getMasterPrice());

        MasterServiceEntity saved = masterServiceRepository.save(masterService);
        return convertToDto(saved);
    }

    @Override
    @Transactional
    public void removeServiceFromMaster(Long masterId, Long serviceId) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        NailService service = nailServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        masterServiceRepository.deleteByMasterAndService(master, service);
    }

    @Override
    public List<MasterServiceResponseDto> getMasterServices(Long masterId) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        return masterServiceRepository.findByMaster(master).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public List<MasterServiceResponseDto> getServiceMasters(Long serviceId) {
        NailService service = nailServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        return masterServiceRepository.findByService(service).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public boolean masterProvidesService(Long masterId, Long serviceId) {
        SystemUser master = systemUserRepository.findById(masterId)
                .orElseThrow(() -> new RuntimeException("Мастер не найден"));

        NailService service = nailServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Услуга не найдена"));

        return masterServiceRepository.existsByMasterAndService(master, service);
    }

    private MasterServiceResponseDto convertToDto(MasterServiceEntity masterService) {
        MasterServiceResponseDto dto = new MasterServiceResponseDto();
        dto.setMasterId(masterService.getMaster().getUserId());
        dto.setMasterName(masterService.getMaster().getFirstName() + " " + masterService.getMaster().getLastName());
        dto.setServiceId(masterService.getService().getServiceId());
        dto.setServiceName(masterService.getService().getName());
        dto.setMasterPrice(masterService.getMasterPrice());
        return dto;
    }
}