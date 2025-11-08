import React from 'react';
import { Link } from 'react-router-dom';
import ServiceItem from './ServiceItem';
import { type Service } from '../types/MasterTypes'; // Используем Service из MasterTypes

interface ServiceListProps {
  services: Service[];
  initialDisplayCount?: number;
}

const ServiceList: React.FC<ServiceListProps> = ({
  services,
  initialDisplayCount = 5,
}) => {
  // Логика showAll и toggleShowAll удалена, так как кнопка теперь ведет на отдельную страницу

  // 💡 ВАЖНО: Мы используем slice для отображения части списка на главной странице
  const servicesToDisplay = services.slice(0, initialDisplayCount);
  const hasMoreServices = services.length > initialDisplayCount;

  // Предполагаем, что SCSS файл ServiceList.scss импортирован
  return (
    <div className='service-list-block'>
      <h2>Услуги {services.length}</h2>

      <div className='service-items-container'>
        {servicesToDisplay.map((service) => (
          // ⬇️ ИСПРАВЛЕНИЕ 1: Используем serviceId вместо id
          <ServiceItem
            key={service.serviceId}
            service={{
              // ⬇️ ИСПРАВЛЕНИЕ 2: Приводим API-поля к ожидаемому ServiceItemProps
              name: service.name,
              category: service.categoryName, // categoryName -> category
              priceMin: service.basePrice, // basePrice -> priceMin
              duration: service.baseDuration, // baseDuration -> duration
              // Предполагаем, что ServiceItemProps ожидает строку, а Service может не иметь поля imageUrl
              imageUrl: service.imageUrl || '',
            }}
          />
        ))}
      </div>

      {hasMoreServices && (
        <div className='show-all-button-container'>
          {/* ⬇️ ИСПОЛЬЗУЕМ LINK ДЛЯ НАВИГАЦИИ на ServiceCatalogPage */}
          <Link to='/services' className='show-all-button'>
            Посмотреть все
          </Link>
        </div>
      )}
    </div>
  );
};

export default ServiceList;
