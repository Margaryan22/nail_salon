import React, { useEffect, useState } from 'react';
import { publicApi, ENDPOINTS, api } from '../api';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../hooks';
import { fetchMe, logout } from '../redux/authSlice';

import MasterList from '../components/MasterList';
import ServiceList from '../components/ServiceList';

// Имя компонента оставлено как ProfilePage
const ProfilePage: React.FC = () => {
  const [masters, setMasters] = useState<any[]>([]);
  const [services, setServices] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const { accessToken } = useAppSelector((state) => state.auth);

  // Проверка токена при клике на аватар
  const handleAvatarClick = async () => {
    if (!accessToken) {
      navigate('/login');
      return;
    }

    const result = await dispatch(fetchMe());

    if (fetchMe.fulfilled.match(result)) {
      navigate('/account');
    } else {
      dispatch(logout());
      navigate('/login');
    }
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        setIsLoading(true);
        const [mastersRes, servicesRes] = await Promise.all([
          api.get(ENDPOINTS.USERS.MASTERS),
          api.get(ENDPOINTS.SERVICES.ALL),
        ]);
        setMasters(mastersRes.data);
        setServices(servicesRes.data);
      } catch (err) {
        setError('Не удалось загрузить данные');
      } finally {
        setIsLoading(false);
      }
    };
    fetchData();
  }, []);

  if (isLoading) return <div className='loading'>Загрузка...</div>;
  if (error) return <div className='error'>{error}</div>;

  return (
    <div className='master-list-page-container'>
      {/* Кнопка с классом для круглого и фиксированного позиционирования */}
      <button
        className='avatar-profile-button'
        onClick={handleAvatarClick}
        title='Личный кабинет'
      >
        A
      </button>

      {/* Пропсы не меняем */}
      <MasterList masters={masters} />
      <ServiceList services={services} />
    </div>
  );
};

export default ProfilePage;
