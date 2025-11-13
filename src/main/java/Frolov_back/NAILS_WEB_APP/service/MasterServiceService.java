package Frolov_back.NAILS_WEB_APP.service;

import Frolov_back.NAILS_WEB_APP.DTO.MasterServiceRequestDto;
import Frolov_back.NAILS_WEB_APP.DTO.MasterServiceResponseDto;

import java.util.List;

public interface MasterServiceService {
    MasterServiceResponseDto addServiceToMaster(MasterServiceRequestDto requestDto);
    void removeServiceFromMaster(Long masterId, Long serviceId);
    List<MasterServiceResponseDto> getMasterServices(Long masterId);
    List<MasterServiceResponseDto> getServiceMasters(Long serviceId);
    boolean masterProvidesService(Long masterId, Long serviceId);
}
