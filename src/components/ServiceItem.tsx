// src/components/ServiceList/ServiceItem.tsx

import React from 'react';
import type { Service } from '../types/userTypes';

interface ServiceItemProps {
  service: Service;
}

const ServiceItem: React.FC<ServiceItemProps> = ({ service }) => {
  const { name, categoryName, basePrice, baseDuration, imageUrl } = service;

  return (
    <div className='service-item'>
      {/* Левая часть — фото + название */}
      <div className='service-info-left'>
        <div className='service-image-container'>
          <div
            className='service-image'
            style={{
              backgroundImage: imageUrl ? `url(${imageUrl})` : undefined,
            }}
          />
        </div>

        <div className='service-details'>
          <h3 className='service-name'>{name}</h3>
          <p className='service-category'>{categoryName}</p>
        </div>
      </div>

      {/* Правая часть — цена, время, кнопка */}
      <div className='service-info-right'>
        <div className='service-price-duration'>
          <p className='service-price'>
            от {basePrice.toLocaleString('ru-RU')} ₽
          </p>
          <p className='service-duration'>{baseDuration} мин.</p>
        </div>

        <button className='book-service-button'>Записаться</button>
      </div>
    </div>
  );
};

export default ServiceItem;
