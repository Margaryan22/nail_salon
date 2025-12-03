// src/pages/ChooseDatePage.tsx

import React, { useEffect, useState, useMemo } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { api, ENDPOINTS } from '../api';
import { format, addDays, startOfDay, isSameDay } from 'date-fns';
import { ru } from 'date-fns/locale';

interface TimeSlot {
  startTime: string; // ISO строка времени
  endTime: string;
  available: boolean;
  slotNumber: number;
}

interface ChooseDateState {
  masterName?: string;
  serviceName?: string;
  serviceId?: number; // КРИТИЧНО
  servicePrice?: number; // КРИТИЧНО
  serviceDuration?: number; // КРИТИЧНО
}

const ChooseDatePage: React.FC = () => {
  const navigate = useNavigate();
  const { masterId } = useParams<{ masterId: string }>();
  const location = useLocation();

  const { masterName, serviceName, serviceId, servicePrice, serviceDuration } =
    (location.state as ChooseDateState) || {};

  const requiredData = useMemo(
    () => ({
      masterId: masterId,
      serviceId: serviceId,
      servicePrice: servicePrice,
      serviceDuration: serviceDuration,
    }),
    [masterId, serviceId, servicePrice, serviceDuration]
  );
  const isDataValid = useMemo(
    () =>
      !!requiredData.masterId &&
      requiredData.serviceId !== undefined &&
      requiredData.servicePrice !== undefined &&
      requiredData.serviceDuration !== undefined,
    [requiredData]
  ); // --- Стейт для выбора даты и слотов ---

  const tomorrow = useMemo(() => addDays(startOfDay(new Date()), 1), []);
  const [days] = useState<Date[]>(() =>
    Array.from({ length: 7 }, (_, i) => addDays(tomorrow, i))
  );
  const [selectedDate, setSelectedDate] = useState<Date | null>(tomorrow);
  const [slots, setSlots] = useState<TimeSlot[]>([]);
  const [isLoadingSlots, setIsLoadingSlots] = useState(false);
  const [error, setError] = useState<string | null>(null); // --- useEffect для загрузки слотов ---

  useEffect(() => {
    if (!isDataValid) {
      setError(
        'Критическая ошибка: не удалось получить полные данные услуги. Начните выбор заново.'
      );
      setSlots([]);
      return;
    }
    if (!selectedDate || !masterId) return;

    const fetchSlots = async () => {
      setIsLoadingSlots(true);
      setError(null);
      try {
        const dateStr = format(selectedDate, 'yyyy-MM-dd');
        const url = ENDPOINTS.APPOINTMENTS.AVAILABLE_SLOTS(masterId);
        const response = await api.get(url, { params: { date: dateStr } });
        setSlots(response.data || []);
      } catch (err) {
        console.error('Ошибка при загрузке слотов:', err);
        setError('Не удалось загрузить свободное время');
        setSlots([]);
      } finally {
        setIsLoadingSlots(false);
      }
    };

    fetchSlots();
  }, [selectedDate, masterId, isDataValid]); // --- Хелперы и хендлеры ---

  const formatTime = (iso: string) => format(new Date(iso), 'HH:mm');

  const handleTimeClick = (slot: TimeSlot) => {
    if (!isDataValid || !selectedDate) {
      setError(
        'Ошибка: Недостаточно данных для перехода. Перезагрузите страницу.'
      );
      return;
    }

    const timeLabel = formatTime(slot.startTime);

    const appointmentData = {
      masterName: masterName || 'Мастер',
      serviceName: serviceName || 'Услуга',
      serviceId: serviceId!,
      servicePrice: servicePrice!,
      serviceDuration: serviceDuration!,
      masterId: masterId!,
      date: format(selectedDate, 'yyyy-MM-dd'),
      timeLabel,
    }; // 💡 УЛУЧШЕНИЕ 1: Использование try/catch для сохранения

    try {
      sessionStorage.setItem(
        'pendingAppointment',
        JSON.stringify(appointmentData)
      );
    } catch (e) {
      console.error('Ошибка сохранения в sessionStorage:', e);
      setError('Ошибка сохранения данных сессии. Попробуйте снова.');
      return;
    } // 💡 УЛУЧШЕНИЕ 2: Запускаем переход

    navigate('/appointment/confirm', {
      state: appointmentData,
    });
  };

  const formatDateBlock = (date: Date) => {
    const dayNum = format(date, 'd');
    const monthShort = format(date, 'LLL', { locale: ru });
    const weekdayShort = format(date, 'eee', { locale: ru });
    const isSelected = selectedDate && isSameDay(date, selectedDate);

    return (
      <button
        key={date.toISOString()}
        className={`date-block ${isSelected ? 'selected' : ''}`}
        onClick={() => setSelectedDate(date)}
        disabled={!isDataValid}
      >
                <div className='day-number'>{dayNum}</div>       
        <div className='day-info'>
                    <span className='weekday'>{weekdayShort}</span>         
          <span className='month'>{monthShort}</span>       
        </div>
             
      </button>
    );
  }; // --- Условный рендеринг для ошибок ---

  if (!masterId || !isDataValid) {
    return (
      <div className='error-page-container'>
               
        <div className='error-box'>
                    <h1>Ошибка выбора услуги</h1>         
          <p>
                       
            {error ||
              'Не удалось получить данные о мастере или выбранной услуге.'}
                     
          </p>
                   
          <button
            className='primary-button'
            onClick={() => navigate('/services', { replace: true })}
          >
                        Начать выбор заново          
          </button>
                 
        </div>
             
      </div>
    );
  } // --- Основной рендеринг ---

  return (
    <div className='choose-date-page'>
           
      <button onClick={() => navigate(-1)} className='back-button'>
                ← Назад      
      </button>
           
      <div className='page-header'>
                <h1>Выберите дату и время</h1>       
        <p className='subtitle'>
                    к {masterName || 'выбранному мастеру'} —          
          {serviceName || 'выбранная услуга'}       
        </p>
             
      </div>
           
      <div className='dates-container'>
               
        <div className='dates-grid'>
                    {days.map((date) => formatDateBlock(date))}       
        </div>
             
      </div>
           
      {selectedDate && (
        <div className='time-section'>
                   
          <h2 className='selected-date-title'>
                        {format(selectedDate, 'd MMMM, EEEE', { locale: ru })} 
                   
          </h2>
                   
          {isLoadingSlots ? (
            <div className='loading'>Загрузка времени...</div>
          ) : error && !isDataValid ? (
            <div className='error'>{error}</div>
          ) : slots.length === 0 ? (
            <p className='no-slots'>На этот день нет свободного времени</p>
          ) : (
            <div className='time-grid'>
                           
              {slots
                .filter((slot) => slot.available)
                .map((slot) => (
                  <button
                    key={slot.slotNumber}
                    className='time-slot'
                    onClick={() => handleTimeClick(slot)}
                  >
                                        {formatTime(slot.startTime)}           
                         
                  </button>
                ))}
                         
            </div>
          )}
                 
        </div>
      )}
         
    </div>
  );
};

export default ChooseDatePage;
