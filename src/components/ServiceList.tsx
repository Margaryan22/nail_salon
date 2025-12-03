// src/components/ServiceList/ServiceList.tsx

import React from 'react';
import type { Service } from '../types/userTypes'; // ← подправь путь, если нужно
import ServiceItem from './ServiceItem';

export interface ServiceListProps {
  services: Service[];
  initialDisplayCount?: number;

  // ← НОВЫЙ ОПЦИОНАЛЬНЫЙ ПРОПС
  onServiceSelect?: (service: Service) => void;
}

const ServiceList: React.FC<ServiceListProps> = ({
  services,
  initialDisplayCount,
  onServiceSelect, // ← принимаем
}) => {
  const displayedServices = initialDisplayCount
    ? services.slice(0, initialDisplayCount)
    : services;

  if (services.length === 0) {
    return <div className='service-list-empty'>Услуги временно недоступны</div>;
  }

  return (
    <div className='service-list-block'>
      <h2>Услуги {services.length}</h2>

      <div className='service-items-container'>
        {displayedServices.map((service) => (
          <ServiceItem
            key={service.serviceId}
            service={service}
            // ← передаём клик только если он есть
            onClick={
              onServiceSelect ? () => onServiceSelect(service) : undefined
            }
          />
        ))}
      </div>

      {initialDisplayCount && services.length > initialDisplayCount && (
        <div className='show-all-button-container'>
          <button className='show-all-button'>Посмотреть все</button>
        </div>
      )}
    </div>
  );
};

export default ServiceList;
