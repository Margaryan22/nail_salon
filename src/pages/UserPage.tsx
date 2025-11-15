import React, { useState, useEffect } from 'react';
import axios from 'axios';
import MasterList from '../components/MasterList';
import ServiceList from '../components/ServiceList';
import { type Master, type Service } from '../types/MasterTypes';

// --- DUMMY DATA (Остается без изменений) ---
// Мастеров пока оставим DUMMY_MASTERS, чтобы не усложнять.
const DUMMY_MASTERS: Master[] = [
  {
    id: 1,
    firstName: 'Анастасия',
    lastName: 'Смирнова',
    email: 'a.smirn@salon.com',
    phone: '1234567890',
    role: 'MASTER',
    specialization: 'Маникюр/Педикюр',
    description: 'Опыт более 5 лет.',
    rank: 'Старший Мастер',
    rating: 5,
    reviewCount: 76,
    imageUrl: '/images/master1.jpg',
    password: '',
  },
  {
    id: 2,
    firstName: 'Светлана',
    lastName: 'Горева',
    email: 's.goreva@salon.com',
    phone: '1234567891',
    role: 'MASTER',
    specialization: 'Маникюр',
    description: 'Топ-мастер по дизайну.',
    rank: 'Топ Мастер',
    rating: 5,
    reviewCount: 74,
    imageUrl: '/images/master2.jpg',
    password: '',
  },
  {
    id: 3,
    firstName: 'Светлана',
    lastName: 'Горева',
    email: 's.goreva@salon.com',
    phone: '1234567891',
    role: 'MASTER',
    specialization: 'Маникюр',
    description: 'Топ-мастер по дизайну.',
    rank: 'Топ Мастер',
    rating: 5,
    reviewCount: 74,
    imageUrl: '/images/master2.jpg',
    password: '',
  },
  {
    id: 4,
    firstName: 'Светлана',
    lastName: 'Горева',
    email: 's.goreva@salon.com',
    phone: '1234567891',
    role: 'MASTER',
    specialization: 'Маникюр',
    description: 'Топ-мастер по дизайну.',
    rank: 'Топ Мастер',
    rating: 5,
    reviewCount: 74,
    imageUrl: '/images/master2.jpg',
    password: '',
  },
  {
    id: 5,
    firstName: 'Светлана',
    lastName: 'Горева',
    email: 's.goreva@salon.com',
    phone: '1234567891',
    role: 'MASTER',
    specialization: 'Маникюр',
    description: 'Топ-мастер по дизайну.',
    rank: 'Топ Мастер',
    rating: 5,
    reviewCount: 74,
    imageUrl: '/images/master2.jpg',
    password: '',
  },
  {
    id: 6,
    firstName: 'Светлана',
    lastName: 'Горева',
    email: 's.goreva@salon.com',
    phone: '1234567891',
    role: 'MASTER',
    specialization: 'Маникюр',
    description: 'Топ-мастер по дизайну.',
    rank: 'Топ Мастер',
    rating: 5,
    reviewCount: 74,
    imageUrl: '/images/master2.jpg',
    password: '',
  },
  {
    id: 7,
    firstName: 'Светлана',
    lastName: 'Горева',
    email: 's.goreva@salon.com',
    phone: '1234567891',
    role: 'MASTER',
    specialization: 'Маникюр',
    description: 'Топ-мастер по дизайну.',
    rank: 'Топ Мастер',
    rating: 5,
    reviewCount: 74,
    imageUrl: '/images/master2.jpg',
    password: '',
  }, // ... (Остальные мастера)
];

const UserPage: React.FC = () => {
  const [masters] = useState<Master[]>(DUMMY_MASTERS);
  const [services, setServices] = useState<Service[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchServices = async () => {
      setIsLoading(true);
      setError(null);

      try {
        const response = await axios.get<Service[]>('/api/v1/services');
        setServices(response.data);
      } catch (err) {
        if (axios.isAxiosError(err)) {
          setError(`Ошибка загрузки услуг: ${err.message}`);
          console.error('Axios Error:', err.response?.data || err.message);
        } else {
          setError('Произошла непредвиденная ошибка');
          console.error('Unknown Error:', err);
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchServices();
  }, []);

  return (
    <div className='user-page-container'>
           {' '}
      <h1 className='user-page-header'>
                Добро пожаловать, [Имя пользователя]! 👋      {' '}
      </h1>
           {' '}
      <div className='content-block'>
                <MasterList masters={masters} />     {' '}
      </div>
           {' '}
      <div className='content-block'>
                <h2>Список услуг</h2>       {' '}
        {isLoading && <p>Загрузка услуг...</p>}       {' '}
        {error && <p className='error-message'>❌ Ошибка: {error}</p>}       {' '}
        {!isLoading && !error && (
          <ServiceList services={services} initialDisplayCount={5} />
        )}
             {' '}
      </div>
         {' '}
    </div>
  );
};

export default UserPage;
