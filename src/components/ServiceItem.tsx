// src/components/ServiceList/ServiceItem.tsx

import React from 'react';
import type { Service } from '../types/userTypes'; // ← подправь путь, если нужно

// ← Обновлённый интерфейс с onClick
interface ServiceItemProps {
  service: Service;
  onClick?: () => void; // ← теперь поддерживается!
}

const ServiceItem: React.FC<ServiceItemProps> = ({ service, onClick }) => {
  const {
    name = 'Без названия',
    categoryName = 'Без категории',
    basePrice = 0,
    baseDuration = 0,
    imageUrl,
  } = service;

  const price = Number(basePrice) || 0;
  const duration = Number(baseDuration) || 0;

  const isClickable = !!onClick;

  return (
    <div
      className={`service-item ${isClickable ? 'clickable' : ''}`}
      onClick={onClick}
      style={isClickable ? { cursor: 'pointer' } : undefined}
    >
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

        {/* Кнопка "Записаться" — только если можно кликнуть */}
        {isClickable && (
          <button
            className='book-service-button'
            onClick={(e) => {
              e.stopPropagation(); // ← предотвращаем двойной клик
              onClick?.();
            }}
          >
            Записаться
          </button>
        )}
      </div>
    </div>
  );
};

export default ServiceItem;
