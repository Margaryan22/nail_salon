// src/components/MasterList/MasterList.tsx

import React, { useRef } from 'react';
import type { MasterCardType } from '../types/userTypes';

import MasterCard from './MasterCard';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, A11y } from 'swiper/modules';

interface MasterListProps {
  masters: MasterCardType[];
}

const MasterList: React.FC<MasterListProps> = ({ masters }) => {
  // 1. Создаем ссылки (Refs) для кастомных кнопок
  const prevRef = useRef<HTMLDivElement | null>(null);
  const nextRef = useRef<HTMLDivElement | null>(null);

  if (masters.length === 0) {
    return (
      <div className='master-list-container'>
        <h2>Наши мастера</h2>
        <p>Мастера временно недоступны</p>
      </div>
    );
  }

  return (
    // Внешний контейнер для общих отступов
    <div className='master-list-container'>
      <h2>
        Наши мастера <span className='masters-count'>({masters.length})</span>
      </h2>

      {/* 💡 НОВЫЙ КОНТЕЙНЕР ДЛЯ РАМКИ, ФОНА И ТЕНИ */}
      <div className='master-list-card-wrapper'>
        {/* Контейнер для Swiper и позиционирования стрелок */}
        <div className='swiper-wrapper-custom'>
          <Swiper
            modules={[Navigation, A11y]}
            spaceBetween={30}
            slidesPerView={1}
            breakpoints={{
              640: { slidesPerView: 2 },
              768: { slidesPerView: 3 },
              1024: { slidesPerView: 4 },
            }}
            // 3. Настраиваем Navigation для использования кастомных кнопок и классов
            navigation={{
              prevEl: prevRef.current, // Элемент для кнопки "Назад"
              nextEl: nextRef.current, // Элемент для кнопки "Вперед"
              disabledClass: 'swiper-button-disabled-custom', // Ваш класс для отключенной кнопки
            }}
            onBeforeInit={(swiper) => {
              // Ручное прикрепление Refs, т.к. React обновляет их после первого рендера
              if (
                typeof swiper.params.navigation !== 'boolean' &&
                swiper.params.navigation
              ) {
                swiper.params.navigation.prevEl = prevRef.current;
                swiper.params.navigation.nextEl = nextRef.current;
              }
            }}
            grabCursor
            // 💡 ИЗМЕНЕНИЕ: Отключаем бесконечную прокрутку, чтобы останавливаться на последнем слайде
            loop={false}
            className='mySwiper' // Используем класс, указанный в вашем SCSS
          >
            {masters.map((master) => (
              // 💡 ИЗМЕНЕНИЕ: Используем userId в качестве ключа
              <SwiperSlide key={master.id}>
                <MasterCard master={master} />
              </SwiperSlide>
            ))}
          </Swiper>

          {/* 4. Рендерим кастомные кнопки */}
          <div ref={prevRef} className='swiper-button-prev-custom visible'>
            &#10094; {/* Стрелка влево */}
          </div>

          <div ref={nextRef} className='swiper-button-next-custom visible'>
            &#10095; {/* Стрелка вправо */}
          </div>
        </div>
      </div>
    </div>
  );
};

export default MasterList;
