import React, { useState } from 'react';
import MasterList from '../components/MasterList';
import ServiceList from '../components/ServiceList';
import { type Master, type Service } from '../types/MasterTypes';

// --- DUMMY DATA (Остается без изменений) ---
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
    firstName: 'Ксения',
    lastName: 'Зыкова',
    email: 'k.zykova@salon.com',
    phone: '1234567892',
    role: 'MASTER',
    specialization: 'Брови',
    description: 'Эксперт по оформлению бровей.',
    rank: 'Бровист',
    rating: 4.9,
    reviewCount: 39,
    imageUrl: '/images/master3.jpg',
    password: '',
  },
  {
    id: 4,
    firstName: 'Ника',
    lastName: 'Миронова',
    email: 'n.mironova@salon.com',
    phone: '1234567893',
    role: 'MASTER',
    specialization: 'Маникюр/Педикюр',
    description: 'Стаж работы 3 года.',
    rank: 'Старший мастер',
    rating: 5,
    reviewCount: 49,
    imageUrl: '/images/master4.jpg',
    password: '',
  },
  {
    id: 4,
    firstName: 'Ника',
    lastName: 'Миронова',
    email: 'n.mironova@salon.com',
    phone: '1234567893',
    role: 'MASTER',
    specialization: 'Маникюр/Педикюр',
    description: 'Стаж работы 3 года.',
    rank: 'Старший мастер',
    rating: 5,
    reviewCount: 49,
    imageUrl: '/images/master4.jpg',
    password: '',
  },
  {
    id: 4,
    firstName: 'Ника',
    lastName: 'Миронова',
    email: 'n.mironova@salon.com',
    phone: '1234567893',
    role: 'MASTER',
    specialization: 'Маникюр/Педикюр',
    description: 'Стаж работы 3 года.',
    rank: 'Старший мастер',
    rating: 5,
    reviewCount: 49,
    imageUrl: '/images/master4.jpg',
    password: '',
  },
  {
    id: 4,
    firstName: 'Ника',
    lastName: 'Миронова',
    email: 'n.mironova@salon.com',
    phone: '1234567893',
    role: 'MASTER',
    specialization: 'Маникюр/Педикюр',
    description: 'Стаж работы 3 года.',
    rank: 'Старший мастер',
    rating: 5,
    reviewCount: 49,
    imageUrl: '/images/master4.jpg',
    password: '',
  },
  {
    id: 4,
    firstName: 'Ника',
    lastName: 'Миронова',
    email: 'n.mironova@salon.com',
    phone: '1234567893',
    role: 'MASTER',
    specialization: 'Маникюр/Педикюр',
    description: 'Стаж работы 3 года.',
    rank: 'Старший мастер',
    rating: 5,
    reviewCount: 49,
    imageUrl: '/images/master4.jpg',
    password: '',
  },
  {
    id: 4,
    firstName: 'Ника',
    lastName: 'Миронова',
    email: 'n.mironova@salon.com',
    phone: '1234567893',
    role: 'MASTER',
    specialization: 'Маникюр/Педикюр',
    description: 'Стаж работы 3 года.',
    rank: 'Старший мастер',
    rating: 5,
    reviewCount: 49,
    imageUrl: '/images/master4.jpg',
    password: '',
  },
];

const DUMMY_SERVICES: Service[] = [
  {
    serviceId: 101, // ⬅️ Исправлено: id -> serviceId
    name: 'Маникюр с однотонным покрытием гель-лак',
    categoryId: 1, // ⬅️ Добавлено: числовой ID
    categoryName: 'Маникюр', // ⬅️ Исправлено: categoryId (string) -> categoryName
    basePrice: 2100, // ⬅️ Исправлено: priceMin -> basePrice
    baseDuration: '2 ч', // ⬅️ Исправлено: duration -> baseDuration
    description: '', // ⬅️ Добавлено: обязательное поле API
    active: true, // ⬅️ Добавлено: обязательное поле API
    imageUrl: '/images/service_manicure_gel.jpg',
  },
  {
    serviceId: 102, // ⬅️ Исправлено: id -> serviceId
    name: 'Маникюр без покрытия',
    categoryId: 1,
    categoryName: 'Маникюр', // ⬅️ Исправлено: category -> categoryName
    basePrice: 1200,
    baseDuration: '1 ч',
    description: '',
    active: true,
    imageUrl: '/images/service_manicure_basic.jpg',
  },
  {
    serviceId: 103,
    name: 'Снятие покрытия + маникюр без покрытия',
    categoryId: 1,
    categoryName: 'Маникюр',
    basePrice: 1400,
    baseDuration: '1 ч 15 м',
    description: '',
    active: true,
    imageUrl: '/images/service_manicure_removal.jpg',
  },
  {
    serviceId: 104,
    name: 'Маникюр с покрытием гель-лак + укрепление гелем без изменения формы (простая коррекция)',
    categoryId: 1,
    categoryName: 'Маникюр',
    basePrice: 2400,
    baseDuration: '2 ч',
    description: '',
    active: true,
    imageUrl: '/images/service_manicure_strengthening.jpg',
  },
  {
    serviceId: 105,
    name: 'Снятие покрытия + маникюр + покрытие прозрачным укрепляющим лаком',
    categoryId: 1,
    categoryName: 'Маникюр',
    basePrice: 1600,
    baseDuration: '1 ч',
    description: '',
    active: true,
    imageUrl: '/images/service_manicure_clear.jpg',
  },
  {
    serviceId: 201,
    name: 'Классический педикюр с покрытием',
    categoryId: 2, // ⬅️ ID для Педикюра
    categoryName: 'Педикюр',
    basePrice: 3000,
    baseDuration: '2 ч 30 м',
    description: '',
    active: true,
    imageUrl: '/images/service_pedicure_classic.jpg',
  },
  {
    serviceId: 202,
    name: 'Экспресс-педикюр (только пальцы)',
    categoryId: 2,
    categoryName: 'Педикюр',
    basePrice: 1800,
    baseDuration: '1 ч 15 м',
    description: '',
    active: true,
    imageUrl: '/images/service_pedicure_express.jpg',
  },
  {
    serviceId: 301,
    name: 'Стрижка женская на длинные волосы',
    categoryId: 3, // ⬅️ ID для Ухода за волосами
    categoryName: 'Уход за волосами',
    basePrice: 2500,
    baseDuration: '1 ч 30 м',
    description: '',
    active: true,
    imageUrl: '/images/service_hair_cut.jpg',
  },
  {
    serviceId: 302,
    name: 'Окрашивание в один тон',
    categoryId: 3,
    categoryName: 'Уход за волосами',
    basePrice: 4000,
    baseDuration: '3 ч',
    description: '',
    active: true,
    imageUrl: '/images/service_hair_dye.jpg',
  },
];
const UserPage: React.FC = () => {
  const [masters, setMasters] = useState<Master[]>(DUMMY_MASTERS);
  const [services, setServices] = useState<Service[]>(DUMMY_SERVICES);
  // Предполагаем, что SCSS файл UserPage.scss импортирован в корневом файле main.tsx

  return (
    <div className='user-page-container'>
      <h1 className='user-page-header'>
        Добро пожаловать, [Имя пользователя]! 👋
      </h1>

      <div className='content-block'>
        <MasterList masters={masters} />
      </div>

      <div className='content-block'>
        <ServiceList services={services} initialDisplayCount={5} />
      </div>
    </div>
  );
};

export default UserPage;
