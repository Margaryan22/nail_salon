import React from 'react';
import { type Master } from '../types/MasterTypes';

interface MasterCardProps {
  master: Master;
}

const MasterCard: React.FC<MasterCardProps> = ({ master }) => {
  const { firstName, lastName, rank, rating, reviewCount, imageUrl } = master;

  // Предполагаем, что SCSS файл MasterCard.scss импортирован
  return (
    <div className='master-card'>
      <div className='master-image-container'>
        <div
          className='master-image'
          style={{ backgroundImage: `url(${imageUrl})` }}
        >
          {/*  */}
        </div>
      </div>
      <div className='master-info'>
        <h3 className='master-name'>
          {firstName} {lastName}
        </h3>
        <p className='master-rank'>{rank}</p>
        <div className='master-rating'>
          <span className='star-icon'>⭐️</span>
          <span className='rating-value'>{rating}</span>
          <span className='review-count'>({reviewCount})</span>
        </div>
        <button className='book-button'>Записаться</button>
      </div>
    </div>
  );
};

export default MasterCard;
