// src/pages/ReadyAppointmentPage.tsx

import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { api, ENDPOINTS } from '../api';
import { format } from 'date-fns';
import { ru } from 'date-fns/locale';
import { useAppSelector } from '../hooks';

// Интерфейс для данных, которые мы ожидаем получить
interface AppointmentData {
  masterId: string;
  masterName: string;
  serviceId: number;
  serviceName: string;
  servicePrice: number; // Динамическая стоимость
  serviceDuration: number; // Динамическая длительность в минутах
  date: string;
  timeLabel: string;
}

const ReadyAppointmentPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAppSelector((state) => state.auth);

  const [data, setData] = useState<AppointmentData | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [termsAccepted, setTermsAccepted] = useState(false); // 💡 Улучшенный useEffect: Приоритет данных из location.state, затем из sessionStorage

  useEffect(() => {
    // 1. Пытаемся получить данные из state (для чистой навигации)
    const state = location.state as AppointmentData;
    if (state && state.servicePrice !== undefined) {
      setData(state); // Сохранение здесь не требуется, так как это уже сделано на предыдущей странице
    } else {
      // 2. Если state пуст (например, после редиректа на домене), восстанавливаем из sessionStorage
      const saved = sessionStorage.getItem('pendingAppointment');
      if (saved) {
        try {
          setData(JSON.parse(saved));
        } catch (e) {
          console.error('Ошибка парсинга saved state:', e);
          // В случае ошибки парсинга, удаляем невалидные данные
          sessionStorage.removeItem('pendingAppointment');
        }
      }
    }
  }, [location.state]);

  if (!data || !user) {
    return (
      <div className='ready-appointment-page error-state'>
               {' '}
        <div className='appointment-error'>
                    <h1>Ошибка: данные записи не найдены.</h1>         {' '}
          <p>
                        Пожалуйста, начните выбор услуги и времени заново.      
               {' '}
          </p>
                   {' '}
          <button
            // 💡 ИСПРАВЛЕНИЕ: Редирект на страницу выбора услуги, чтобы начать процесс заново
            onClick={() => navigate('/services', { replace: true })}
            className='back-btn-small'
          >
                        ← Начать заново          {' '}
          </button>
                 {' '}
        </div>
             {' '}
      </div>
    );
  }

  const timeToSlotNumber = () => {
    const hours = parseInt(data.timeLabel.split(':')[0]); // Предполагаем, что рабочий день начинается в 9:00 (слот 1) // 9:00 -> 9 - 8 = 1; 10:00 -> 2
    return hours - 8;
  };

  const handleConfirm = async () => {
    if (!termsAccepted) {
      setError('Пожалуйста, примите условия записи.');
      return;
    }

    setIsLoading(true);
    setError(null);

    const requestBody = {
      clientId: user.userId,
      masterId: Number(data.masterId),
      serviceId: data.serviceId,
      appointmentDate: data.date,
      timeSlot: timeToSlotNumber(),
      notes: '',
    };

    try {
      await api.post(ENDPOINTS.APPOINTMENTS.BASE, requestBody);

      setSuccess(true);
      sessionStorage.removeItem('pendingAppointment');
      setTimeout(() => navigate('/account/appointments'), 2000);
    } catch (err: any) {
      if (err.response) {
        console.error('❌ Ошибка API при создании записи:', err.response.data);
        setError(
          `Не удалось создать запись. ${
            err.response.data.message || 'Попробуйте позже.'
          }`
        );
      } else {
        console.error('❌ Ошибка сети/запроса:', err.message);
        setError('Не удалось создать запись. Проверьте подключение.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const formatFullDate = () =>
    format(new Date(data.date), 'd MMMM, EEEE', { locale: ru });

  const formatTimeRange = () => {
    const [h, m] = data.timeLabel.split(':').map(Number);

    const startDate = new Date(data.date);
    startDate.setHours(h, m, 0);

    const endDate = new Date(
      startDate.getTime() + data.serviceDuration * 60000
    );

    const endH = endDate.getHours().toString().padStart(2, '0');
    const endM = endDate.getMinutes().toString().padStart(2, '0');

    return `${data.timeLabel}–${endH}:${endM}`;
  };

  return (
    <div className='ready-appointment-page'>
           {' '}
      <button onClick={() => navigate(-1)} className='back-button'>
                ← Назад      {' '}
      </button>
           {' '}
      <div className='appointment-card'>
               {' '}
        <div className='master-info'>
                    <div className='avatar-placeholder' />         {' '}
          <div className='master-details'>
                        <h3>{data.masterName}</h3>           {' '}
            <p className='master-role'>Бровист</p>         {' '}
          </div>
                 {' '}
        </div>
               {' '}
        <div className='appointment-datetime'>
                    <div className='calendar-icon' />         {' '}
          <div>
                        <p>{formatFullDate()}</p>           {' '}
            <p className='time-range'>{formatTimeRange()}</p>         {' '}
          </div>
                 {' '}
        </div>
                <div className='separator' />       {' '}
        <div className='service-item'>
                   {' '}
          <div>
                        <h4>{data.serviceName}</h4>           {' '}
            <p className='duration'> {data.serviceDuration} мин</p>         {' '}
          </div>
                    <p className='price'>{data.servicePrice} RUB</p>       {' '}
        </div>
               {' '}
        <div className='cancellation-note'>
                    <span className='cancel-icon'>X</span> Бесплатная          
          отмена и перенос более чем за 6 часов          {' '}
          <button className='details-link'>Подробнее</button>       {' '}
        </div>
             {' '}
      </div>
           {' '}
      <div className='summary'>
                <span>Итого</span>       {' '}
        <strong>{data.servicePrice} RUB</strong>     {' '}
      </div>
           {' '}
      <label className='terms-checkbox'>
               {' '}
        <input
          type='checkbox'
          required
          checked={termsAccepted}
          onChange={(e) => setTermsAccepted(e.target.checked)}
        />
                Я прочитал и принимаю условия записи*      {' '}
      </label>
           {' '}
      {success ? (
        <div className='success-message'>Запись успешно создана!</div>
      ) : (
        <button
          className='confirm-button'
          onClick={handleConfirm}
          disabled={isLoading || !termsAccepted}
        >
                    {isLoading ? 'Создаём запись...' : 'Завершить запись'}     
           {' '}
        </button>
      )}
            {error && <div className='error-message'>{error}</div>}   {' '}
    </div>
  );
};

export default ReadyAppointmentPage;
