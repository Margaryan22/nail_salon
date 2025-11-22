// src/pages/MasterListPage.tsx

import React, { useEffect, useState } from 'react';
import { publicApi, ENDPOINTS } from '../api';
import type { User, MasterCardType } from '../types/userTypes';
import { useNavigate } from 'react-router-dom'; // 👈 Импортируем хук для навигации

// ... (определение ServiceType и импорты MasterList, ServiceList)
type ServiceType = any; // Замените на фактический тип, если он есть

import MasterList from '../components/MasterList';
import ServiceList from '../components/ServiceList';

const ProfilePage: React.FC = () => {
  const [masters, setMasters] = useState<MasterCardType[]>([]);
  const [services, setServices] = useState<ServiceType[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // 1. Инициализируем хук навигации
  const navigate = useNavigate();

  // 2. Функция-обработчик для клика по аватару
  const handleAvatarClick = () => {
    // Предполагаем, что страница аккаунта находится по маршруту /account
    navigate('/account');
  };

  // ... (useEffect остается без изменений, за исключением имени функции)
  useEffect(() => {
    // Объединяем загрузку мастеров и услуг для упрощения управления состоянием загрузки/ошибки
    const fetchData = async () => {
      let mastersData: MasterCardType[] = [];
      let servicesData: ServiceType[] = [];
      let fetchError: string | null = null;

      try {
        setIsLoading(true);
        setError(null);

        // --- Загрузка мастеров ---
        try {
          const mastersResponse = await publicApi.get<MasterCardType[]>(
            ENDPOINTS.USERS.MASTERS
          );
          mastersData = mastersResponse.data;
        } catch (err) {
          console.error('Ошибка загрузки мастеров:', err);
          fetchError = 'Не удалось загрузить список мастеров.';
        }

        // --- Загрузка услуг ---
        // Используем publicApi и эндпоинт SERVICES.ALL
        try {
          const servicesResponse = await publicApi.get<ServiceType[]>(
            ENDPOINTS.SERVICES.ALL
          );
          servicesData = servicesResponse.data;
        } catch (err) {
          console.error('Ошибка загрузки услуг:', err);
          // Добавляем ошибку к общей, если она уже есть, или устанавливаем новую
          fetchError = fetchError
            ? fetchError + ' Также не удалось загрузить список услуг.'
            : 'Не удалось загрузить список услуг.';
        }

        // Устанавливаем данные или ошибку
        if (fetchError) {
          setError(fetchError);
          setMasters([]);
          setServices([]);
        } else {
          setMasters(mastersData);
          setServices(servicesData);
        }
      } catch (e) {
        // Ловим любые неожиданные ошибки
        setError((prev) =>
          prev ? prev : 'Произошла непредвиденная ошибка при загрузке данных.'
        );
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, []);

  if (isLoading) {
    return (
      <div className='page-container'>
        <div className='loading'>Загрузка списка мастеров и услуг...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className='page-container'>
        <div className='error'>{error}</div>
      </div>
    );
  }

  return (
    // 3. Добавляем кнопку аватара в самом начале контейнера
    <div className='master-list-page-container'>
      <button
        className='avatar-button'
        onClick={handleAvatarClick}
        title='Перейти в личный кабинет'
      >
        👤
      </button>

      <MasterList masters={masters} />
      <ServiceList services={services} />
    </div>
  );
};

export default ProfilePage;
