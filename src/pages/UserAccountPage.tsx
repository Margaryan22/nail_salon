// src/pages/UserAccountPage.tsx

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../hooks';
import { api } from '../api';
import { logout, fetchMe } from '../redux/authSlice';
import type { User, Appointment } from '../types/userTypes';

const UserAccountPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { isAuthenticated, user: reduxUser } = useAppSelector(
    (state) => state.auth
  );

  const [profile, setProfile] = useState<User | null>(null);
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [isLoadingProfile, setIsLoadingProfile] = useState(true);
  const [isLoadingAppointments, setIsLoadingAppointments] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Проверка авторизации
  useEffect(() => {
    if (!isAuthenticated) {
      navigate('/login', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  // Загрузка профиля при входе на страницу
  useEffect(() => {
    if (!isAuthenticated) return;

    const loadProfile = async () => {
      try {
        setIsLoadingProfile(true);
        const result = await dispatch(fetchMe());
        if (fetchMe.fulfilled.match(result)) {
          setProfile(result.payload);
        } else {
          setError('Не удалось загрузить профиль');
          dispatch(logout());
          navigate('/login');
        }
      } catch (err) {
        setError('Ошибка загрузки профиля');
      } finally {
        setIsLoadingProfile(false);
      }
    };

    loadProfile();
  }, [dispatch, isAuthenticated, navigate]);

  // Загрузка записей после получения профиля
  useEffect(() => {
    if (!profile?.userId) return;

    const loadAppointments = async () => {
      try {
        setIsLoadingAppointments(true);
        const { data } = await api.get<Appointment[]>(
          `/appointments/client/${profile.userId}`
        );
        setAppointments(data);
      } catch (err: any) {
        console.error('Ошибка загрузки записей:', err);
        setError('Не удалось загрузить записи');
      } finally {
        setIsLoadingAppointments(false);
      }
    };

    loadAppointments();
  }, [profile?.userId]);

  const handleLogout = () => {
    dispatch(logout());
    navigate('/login', { replace: true });
  };

  const getInitials = (firstName?: string, lastName?: string) =>
    ((firstName?.[0] || '') + (lastName?.[0] || '')).toUpperCase() || '??';

  const formatDate = (date: string) =>
    new Date(date).toLocaleString('ru-RU', {
      day: '2-digit',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });

  if (!isAuthenticated) return null;

  return (
    <div className='user-page-container'>
      <h1 className='user-page-header'>Личный кабинет</h1>

      {/* Профиль */}
      <div className='content-block profile-info'>
        {isLoadingProfile ? (
          <p>Загрузка профиля...</p>
        ) : error ? (
          <p className='error'>{error}</p>
        ) : profile ? (
          <>
            <div className='user-profile-summary'>
              <div className='user-avatar-large'>
                <span className='avatar-text'>
                  {getInitials(profile.firstName, profile.lastName)}
                </span>
              </div>
              <div className='user-details'>
                <h2>
                  {profile.firstName} {profile.lastName}
                </h2>
                <p className='user-role-text'>
                  {profile.role === 'CLIENT'
                    ? 'Клиент'
                    : profile.role === 'MASTER'
                    ? 'Мастер'
                    : 'Админ'}
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
        ) : null}
      </div>

      {/* Записи */}
      <div className='content-block appointments-section'>
        <h2>Мои записи ({appointments.length})</h2>
        {isLoadingAppointments ? (
          <p>Загрузка...</p>
        ) : appointments.length === 0 ? (
          <p>У вас пока нет записей</p>
        ) : (
          <div className='appointments-list'>
            {appointments.map((app) => (
              <div key={app.appointmentId} className='appointment-card'>
                <div className='appointment-header'>
                  <h3>{app.serviceName}</h3>
                  <span className={`status status-${app.status.toLowerCase()}`}>
                    {app.status === 'BOOKED'
                      ? 'Записан'
                      : app.status === 'COMPLETED'
                      ? 'Выполнена'
                      : 'Отменена'}
                  </span>
                </div>
                <p>
                  <strong>Мастер:</strong> {app.masterName}
                </p>
                <p>
                  <strong>Дата:</strong> {formatDate(app.appointmentDatetime)}
                </p>
                <p>
                  <strong>Цена:</strong> {app.price} ₽
                </p>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default UserAccountPage;
