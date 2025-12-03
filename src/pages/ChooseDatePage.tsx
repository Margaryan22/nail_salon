// src/pages/ChooseDatePage.tsx

import React, { useEffect, useState } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { api, ENDPOINTS } from '../api';
import { format, addDays, startOfDay, isSameDay } from 'date-fns';
import { ru } from 'date-fns/locale';

interface TimeSlot {
  startTime: string;
  endTime: string;
  available: boolean;
  slotNumber: number;
}

// Добавляем все ожидаемые поля в интерфейс State, чтобы не потерять их
interface ChooseDateState {
  masterName?: string;
  serviceName?: string;
  serviceId?: number;
  servicePrice?: number; // <--- Добавлено
  serviceDuration?: number; // <--- Добавлено
}

const ChooseDatePage: React.FC = () => {
  const navigate = useNavigate();
  const { masterId } = useParams<{ masterId: string }>();
  const location = useLocation(); // Получаем весь объект state с предыдущей страницы

  const fullState = location.state as ChooseDateState | null;

  const masterName = fullState?.masterName || 'мастеру';
  const serviceName = fullState?.serviceName || 'услугу'; // 7 дней начиная с завтра

  const tomorrow = addDays(startOfDay(new Date()), 1);
  const [days] = useState<Date[]>(() =>
    Array.from({ length: 7 }, (_, i) => addDays(tomorrow, i))
  );
  const [selectedDate, setSelectedDate] = useState<Date | null>(tomorrow);

  const [slots, setSlots] = useState<TimeSlot[]>([]);
  const [isLoadingSlots, setIsLoadingSlots] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
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
        console.error(err);
        setError('Не удалось загрузить время');
        setSlots([]);
      } finally {
        setIsLoadingSlots(false);
      }
    };

    fetchSlots();
  }, [selectedDate, masterId]);

  const formatTime = (iso: string) => format(new Date(iso), 'HH:mm'); // 🔑 КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: Передаем весь объект fullState дальше

  const handleTimeClick = (slot: TimeSlot) => {
    navigate('/appointment/confirm', {
      state: {
        // Копируем все данные об услуге и мастере, полученные ранее
        ...fullState,
        masterId, // Добавляем данные о времени/дате
        date: format(selectedDate!, 'yyyy-MM-dd'),
        startTime: slot.startTime, // Не обязательно, но полезно
        timeLabel: formatTime(slot.startTime),
      },
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
      >
                <div className='day-number'>{dayNum}</div>
        <div className='day-info'>
                    <span className='weekday'>{weekdayShort}</span>
          <span className='month'>{monthShort}</span>
        </div>
      </button>
    );
  };

  if (!masterId) {
    return <div className='error'>Ошибка: мастер не выбран</div>;
  }

  return (
    <div className='choose-date-page'>
      <button onClick={() => navigate(-1)} className='back-button'>
                ← Назад
      </button>
      <div className='page-header'>
                <h1>Выберите дату и время</h1>
        <p className='subtitle'>
                    к {masterName} — {serviceName}
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
          ) : error ? (
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
