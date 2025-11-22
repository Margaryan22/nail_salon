// src/pages/UserAccountPage.tsx

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppSelector, useAppDispatch } from '../hooks';
import { api } from '../api';
import { logout } from '../redux/authSlice';
import type { User, Appointment } from '../types/userTypes';

const UserAccountPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  const [profile, setProfile] = useState<User | null>(null);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [isLoadingProfile, setIsLoadingProfile] = useState(true);
  const [isLoadingAppointments, setIsLoadingAppointments] = useState(true);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [appointmentsError, setAppointmentsError] = useState<string | null>(
    null
  );

  // Первый эффект — только проверка авторизации и загрузка профиля
  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login', { replace: true });
      return;
    }

    const fetchProfile = async () => {
      try {
        setIsLoadingProfile(true);
        const { data } = await api.get<User>('/users/me');
        setProfile(data);
        setProfileError(null);
      } catch (err: any) {
        console.error('Ошибка загрузки профиля:', err);
        setProfileError('Не удалось загрузить профиль');
        if (err.response?.status === 401) {
          dispatch(logout());
          navigate('/login');
        }
      } finally {
        setIsLoadingProfile(false);
      }
    };

    fetchProfile();
  }, [isAuthenticated, navigate, dispatch]);

  useEffect(() => {
    if (!profile?.userId) {
      console.log('profile.userId ещё нет → ждём... (profile =', profile, ')');
      return;
    }

    console.log(
      `profile.userId появился: ${profile.userId} → грузим записи клиента`
    );

    const fetchAppointments = async () => {
      try {
        console.log(
          `Делаем запрос: GET /appointments/client/${profile.userId}`
        );
        setIsLoadingAppointments(true);

        const response = await api.get<Appointment[]>(
          `/appointments/client/${profile.userId}`
        );

        console.log('Записи успешно получены:', response.data);
        setAppointments(response.data);
        setAppointmentsError(null);
      } catch (err: any) {
        console.error('ОШИБКА загрузки записей:', err);
        console.error('Статус:', err.response?.status);
        console.error('Тело ошибки:', err.response?.data);
        console.error('URL был:', err.config?.url);
        setAppointmentsError('Не удалось загрузить записи');
      } finally {
        setIsLoadingAppointments(false);
        console.log('Загрузка записей завершена (успешно или с ошибкой)');
      }
    };

    fetchAppointments();
  }, [profile?.userId]); // ← это главное!
  const handleLogout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (err) {
      console.warn('Ошибка выхода');
    } finally {
      dispatch(logout());
      navigate('/login', { replace: true });
    }
  };

  const getInitials = (firstName?: string, lastName?: string) => {
    return (
      ((firstName?.[0] || '') + (lastName?.[0] || '')).toUpperCase() || '??'
    );
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString('ru-RU', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'BOOKED':
        return 'Записан';
      case 'COMPLETED':
        return 'Выполнена';
      case 'CANCELLED':
        return 'Отменена';
      default:
        return status;
    }
  };

  return (
    <div className='user-page-container'>
      <h1 className='user-page-header'>Личный кабинет</h1>

      {/* Профиль */}
      <div className='content-block profile-info'>
        {isLoadingProfile ? (
          <p>Загрузка профиля...</p>
        ) : profileError ? (
          <p className='error'>{profileError}</p>
        ) : profile ? (
          <>
            <div className='user-profile-summary'>
              <div className='user-avatar-large'>
                <span className='avatar-text'>
                  {getInitials(profile.firstName, profile.lastName)}
                </span>
              </div>
              <div className='user-details'>
                <h1>
                  {profile.firstName} {profile.lastName || ''}
                </h1>
                <p className='user-role-text'>
                  {profile.role === 'CLIENT'
                    ? 'Клиент'
                    : profile.role === 'MASTER'
                    ? 'Мастер'
                    : 'Администратор'}
                </p>
              </div>
            </div>

            <div className='contact-details'>
              <p>
                <strong>Email:</strong> {profile.email}
              </p>
              <p>
                <strong>Телефон:</strong> {profile.phone || 'Не указан'}
              </p>
            </div>

            <button onClick={handleLogout} className='logout-button'>
              Выйти из аккаунта
            </button>
          </>
        ) : (
          <p>Профиль не загружен</p>
        )}
      </div>

      {/* Записи */}
      <div className='content-block appointments-section'>
        <h2>Мои записи ({appointments.length})</h2>

        {isLoadingAppointments ? (
          <p>Загрузка записей...</p>
        ) : appointmentsError ? (
          <p className='error'>{appointmentsError}</p>
        ) : appointments.length === 0 ? (
          <p className='no-appointments-message'>У вас пока нет записей</p>
        ) : (
          <div className='appointments-list'>
            {appointments.map((app) => (
              <div key={app.appointmentId} className='appointment-card'>
                <div className='appointment-header'>
                  <h3>{app.serviceName}</h3>
                  <span className={`status status-${app.status.toLowerCase()}`}>
                    {getStatusText(app.status)}
                  </span>
                </div>

                <div className='appointment-details'>
                  <p>
                    <strong>Мастер:</strong> {app.masterName}
                  </p>
                  <p>
                    <strong>Дата и время:</strong>{' '}
                    {formatDate(app.appointmentDatetime)}
                  </p>
                  <p>
                    <strong>Цена:</strong> {app.price} ₽
                  </p>
                  {app.notes && (
                    <p className='appointment-notes'>
                      <strong>Пожелания:</strong> {app.notes}
                    </p>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default UserAccountPage;
