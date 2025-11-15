import React, { useRef, useState } from 'react';
import MasterCard from './MasterCard';
import { type Master } from '../types/MasterTypes';

import { Swiper as SwiperClass } from 'swiper';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, A11y } from 'swiper/modules';

interface MasterListProps {
  masters: Master[];
}

const MasterList: React.FC<MasterListProps> = ({ masters }) => {
  const [isHovered, setIsHovered] = useState(false);
  const swiperRef = useRef<SwiperClass | null>(null);

  const prevButtonClass = 'swiper-button-prev-custom';
  const nextButtonClass = 'swiper-button-next-custom';
  const disabledButtonClass = 'swiper-button-disabled-custom';

  return (
    <div className='master-list-container'>
      <h2>Специалисты {masters.length}</h2>

      <div
        className='master-list-card-wrapper'
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
      >
        <div className='swiper-wrapper-custom'>
          <Swiper
            onSwiper={(swiper) => (swiperRef.current = swiper)}
            modules={[Navigation, A11y]}
            spaceBetween={20}
            // ⬇️ КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: Отключаем зацикливание
            loop={false}
            // Используем 'auto' для гибкости ширины
            slidesPerView={'auto'}
            slidesPerGroup={1}
            grabCursor={true}
            navigation={{
              nextEl: `.${nextButtonClass}`,
              prevEl: `.${prevButtonClass}`,
              // Swiper сам добавит 'swiper-button-disabled-custom', когда достигнет края.
              disabledClass: disabledButtonClass,
            }}
            className='swiper mySwiper'
            breakpoints={{
              320: {
                slidesPerView: 'auto',
                spaceBetween: 10,
                slidesPerGroup: 1,
              },
              640: {
                slidesPerView: 'auto',
                spaceBetween: 15,
                slidesPerGroup: 1,
              },
              768: {
                slidesPerView: 'auto',
                spaceBetween: 20,
                slidesPerGroup: 1,
              },
              1024: {
                slidesPerView: 'auto',
                spaceBetween: 20,
                slidesPerGroup: 1,
              },
            }}
          >
            {masters.map((master) => (
              <SwiperSlide key={master.id}>
                <MasterCard master={master} />
              </SwiperSlide>
            ))}
          </Swiper>

          <div className={`${prevButtonClass} ${isHovered ? 'visible' : ''}`}>
            &#10094;
          </div>
          <div className={`${nextButtonClass} ${isHovered ? 'visible' : ''}`}>
            &#10095;
          </div>
        </div>
      </div>
    </div>
  );
};

export default MasterList;
