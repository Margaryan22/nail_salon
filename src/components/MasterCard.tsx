// src/components/MasterList/MasterCard.tsx

import React from 'react';
import { useNavigate } from 'react-router-dom'; // <<< ДОБАВЛЕН ИМПОРТ useNavigate
import type { MasterCardType } from '../types/userTypes';

interface MasterCardProps {
  master: MasterCardType;
}

const MasterCard: React.FC<MasterCardProps> = ({ master }) => {
  const navigate = useNavigate(); // <<< Инициализация хука useNavigate
  console.log('master object:', master); // ← ВОТ ЭТО СРОЧНО ДОБАВЬ!

  // АПИ ВЕРНУЛ ТОЛЬКО ЭТИ ПОЛЯ. masterProfile ОТСУТСТВУЕТ.
  const { id, firstName, lastName } = master; // <<< Добавил master.id для навигации

  const imageUrl = '/default-master-avatar.jpg'; // fallback

  // Обработчик для кнопки "Записаться"
  const handleBookingClick = () => {
    navigate(`/services-by-master/${master.userId}`, {
      state: {
        masterName: `${master.firstName} ${master.lastName}`,
        // Можно передать и фото, если захочешь:
        // masterAvatar: master.avatar || '/default-master-avatar.jpg',
      },
    });
  };

  return (
    <div className='master-card'>
      <div className='master-image-container'>
        <div
          className='master-image'
          style={{ backgroundImage: `url(${imageUrl})` }}
        />
      </div>

      <div className='master-info'>
        <h3 className='master-name'>
          {firstName} {lastName}
        </h3>

        <p className='master-specialization'>Специализация</p>

        <button
          className='book-button'
          type='button'
          onClick={handleBookingClick} // <<< ДОБАВЛЕН ОБРАБОТЧИК КЛИКА
        >
          Записаться
        </button>
      </div>
    </div>
  );
};

export default MasterCard;
