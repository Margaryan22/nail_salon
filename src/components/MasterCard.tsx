// src/components/MasterList/MasterCard.tsx (ИСПРАВЛЕНО)

import React from 'react';
import type { MasterCardType } from '../types/userTypes';

interface MasterCardProps {
  master: MasterCardType;
}

const MasterCard: React.FC<MasterCardProps> = ({ master }) => {
  // АПИ ВЕРНУЛ ТОЛЬКО ЭТИ ПОЛЯ. masterProfile ОТСУТСТВУЕТ.
  const { firstName, lastName } = master;

  const imageUrl = '/default-master-avatar.jpg'; // fallback

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

        <button className='book-button' type='button'>
          Записаться
        </button>
      </div>
    </div>
  );
};

export default MasterCard;
