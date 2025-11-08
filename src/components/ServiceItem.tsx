import React from 'react';
import { type Service } from '../types/MasterTypes'; // Используем Service из MasterTypes

interface ServiceItemProps {
  service: {
    name: string;
    category: string; // Ожидается 'category', а не 'categoryName'
    priceMin: number;
    duration: string; // Ожидается string, как в API 'baseDuration'
    imageUrl: string;
  };
}

const ServiceItem: React.FC<ServiceItemProps> = ({ service }) => {
  const { name, category, priceMin, duration, imageUrl } = service;

  // Предполагаем, что SCSS файл ServiceItem.scss импортирован
  return (
    <div className='service-item'>
      <div className='service-info-left'>
        <div className='service-image-container'>
          <div
            className='service-image'
            style={{ backgroundImage: `url(${imageUrl})` }}
          >
            {/*  */}
          </div>
        </div>
        <div className='service-details'>
          <p className='service-name'>{name}</p>
          <p className='service-category'>{category}</p>
        </div>
      </div>

      <div className='service-info-right'>
        <div className='service-price-duration'>
          {/* Используем toLocaleString для форматирования числа */}
          <p className='service-price'>
            от {priceMin.toLocaleString('ru-RU')} RUB
          </p>
          <p className='service-duration'>{duration}</p>
        </div>
        <button className='book-service-button'>Записаться</button>
      </div>
    </div>
  );
};

export default ServiceItem;
