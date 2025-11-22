// src/components/ServiceList/ServiceList.tsx

import React from 'react';
import type { Service } from '../types/userTypes';

import ServiceItem from './ServiceItem';

interface ServiceListProps {
  services: Service[]; // ← обязательно передаём!
  initialDisplayCount?: number; // ← сколько показать
}

const ServiceList: React.FC<ServiceListProps> = ({
  services,
  initialDisplayCount,
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
          <ServiceItem key={service.serviceId} service={service} />
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
