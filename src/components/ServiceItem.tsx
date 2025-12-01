// src/components/ServiceList/ServiceItem.tsx

import React from 'react';
import type { Service } from '../types/userTypes';

interface ServiceItemProps {
  service: Service;
}

const ServiceItem: React.FC<ServiceItemProps> = ({ service }) => {
  // Деструктурируем с дефолтными значениями — это спасает от null/undefined
  const {
    name = 'Без названия',
    categoryName = 'Без категории',
    basePrice = 0,
    baseDuration = 0,
    imageUrl,
  } = service;

  // На всякий случай принудительно приводим к числу (если вдруг строка "2500")
  const price = Number(basePrice) || 0;
  const duration = Number(baseDuration) || 0;

  return (
    <div className='service-item'>
      <div className='service-info-left'>
        <div className='service-image-container'>
          <div
            className='service-image'
            style={{
              backgroundImage: imageUrl ? `url(${imageUrl})` : 'none',
            }}
          />
        </div>

        <div className='service-details'>
          <h3 className='service-name'>{name}</h3>
          <p className='service-category'>{categoryName}</p>
        </div>
      </div>

      <div className='service-info-right'>
        <div className='service-price-duration'>
          <p className='service-price'>от {price.toLocaleString('ru-RU')} ₽</p>
          <p className='service-duration'>{duration} мин.</p>
        </div>

        <button className='book-service-button'>Записаться</button>
      </div>
    </div>
  );
};

export default ServiceItem;
