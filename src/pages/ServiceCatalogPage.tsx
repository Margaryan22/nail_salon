// src/pages/ServiceCatalogPage.tsx

import React, { useState, useEffect, useRef } from 'react';
import ServiceItem from '../components/ServiceItem'; // Используем существующий компонент
import { type Service, type CategorizedServices } from '../types/MasterTypes'; // Предполагаем, что Service/CategorizedServices там

// 💡 Названия категорий, которые будут использоваться для вкладок и скролла
const CATEGORY_TABS = ['Маникюр', 'Педикюр', 'СПА'];

const ServiceCatalogPage: React.FC = () => {
  // 1. Состояния для данных и UI
  const [allServices, setAllServices] = useState<Service[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState(CATEGORY_TABS[0]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Ref'ы для скролла к разделам
  const categoryRefs = useRef<Map<string, HTMLDivElement | null>>(new Map());

  // 2. Имитация API-запроса для получения всех услуг
  useEffect(() => {
    const fetchServices = async () => {
      // В реальном приложении:
      // const response = await fetch('/api/v1/services');
      // const data = await response.json();

      // Имитация данных (используем ранее определенные DUMMY_SERVICES с добавлением API-полей)
      const DUMMY_API_SERVICES: Service[] = [
        {
          serviceId: 101,
          categoryId: 1,
          categoryName: 'Маникюр',
          name: 'Маникюр с однотонным покрытием гель-лак',
          description: '...',
          basePrice: 2100,
          baseDuration: '2 ч',
          active: true,
          imageUrl: '/images/service_manicure_gel.jpg',
        },
        {
          serviceId: 102,
          categoryId: 1,
          categoryName: 'Маникюр',
          name: 'Маникюр без покрытия',
          description: '...',
          basePrice: 1200,
          baseDuration: '1 ч',
          active: true,
          imageUrl: '/images/service_manicure_basic.jpg',
        },
        {
          serviceId: 104,
          categoryId: 1,
          categoryName: 'Маникюр',
          name: 'Маникюр с покрытием гель-лак + укрепление гелем',
          description: '...',
          basePrice: 2400,
          baseDuration: '2 ч',
          active: true,
          imageUrl: '/images/service_manicure_strengthening.jpg',
        },
        {
          serviceId: 106,
          categoryId: 1,
          categoryName: 'Маникюр',
          name: 'Наращивание ногтей',
          description: '...',
          basePrice: 3500,
          baseDuration: '2 ч 30 м',
          active: true,
          imageUrl: '/images/service_manicure_fake.jpg',
        },
        {
          serviceId: 107,
          categoryId: 1,
          categoryName: 'Маникюр',
          name: 'Сложная коррекция + покрытие гель-лаком',
          description: '...',
          basePrice: 2700,
          baseDuration: '2 ч 30 м',
          active: true,
          imageUrl: '/images/service_manicure_hard.jpg',
        },

        {
          serviceId: 201,
          categoryId: 2,
          categoryName: 'Педикюр',
          name: 'Классический педикюр с покрытием',
          description: '...',
          basePrice: 3000,
          baseDuration: '2 ч 30 м',
          active: true,
          imageUrl: '/images/service_pedicure_classic.jpg',
        },
        {
          serviceId: 202,
          categoryId: 2,
          categoryName: 'Педикюр',
          name: 'Экспресс-педикюр (только пальцы)',
          description: '...',
          basePrice: 1800,
          baseDuration: '1 ч 15 м',
          active: true,
          imageUrl: '/images/service_pedicure_express.jpg',
        },

        {
          serviceId: 301,
          categoryId: 3,
          categoryName: 'СПА',
          name: 'СПА-уход для рук',
          description: '...',
          basePrice: 1500,
          baseDuration: '1 ч',
          active: true,
          imageUrl: '/images/service_spa_hand.jpg',
        },
        {
          serviceId: 302,
          categoryId: 3,
          categoryName: 'СПА',
          name: 'СПА-уход для ног',
          description: '...',
          basePrice: 2000,
          baseDuration: '1 ч 30 м',
          active: true,
          imageUrl: '/images/service_spa_foot.jpg',
        },
      ];

      // Задержка для имитации загрузки
      await new Promise((resolve) => setTimeout(resolve, 500));

      setAllServices(DUMMY_API_SERVICES);
      setIsLoading(false);
      setError(null);
    };

    fetchServices();
  }, []);

  // 3. Функция скролла к категории
  const scrollToCategory = (categoryName: string) => {
    setActiveTab(categoryName); // Активируем вкладку
    const element = categoryRefs.current.get(categoryName);
    if (element) {
      // Плавный скролл
      element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  // 4. Группировка и фильтрация услуг
  const filteredAndGroupedServices = (() => {
    let servicesToGroup = allServices;

    // 4.1. Поиск (фильтрация по названию)
    if (searchTerm) {
      servicesToGroup = servicesToGroup.filter((service) =>
        service.name.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // 4.2. Группировка по категориям
    const groupedMap = servicesToGroup.reduce((acc, service) => {
      // Используем только те категории, которые есть в CATEGORY_TABS
      if (CATEGORY_TABS.includes(service.categoryName)) {
        const categoryName = service.categoryName;
        if (!acc.has(categoryName)) {
          acc.set(categoryName, []);
        }
        acc.get(categoryName)?.push(service);
      }
      return acc;
    }, new Map<string, Service[]>());

    // Преобразование Map в массив для удобного рендеринга
    const groupedArray: CategorizedServices[] = Array.from(
      groupedMap.entries()
    ).map(([categoryName, services]) => ({
      categoryName,
      services,
    }));

    // Сортируем массив, чтобы категории шли в порядке CATEGORY_TABS
    groupedArray.sort(
      (a, b) =>
        CATEGORY_TABS.indexOf(a.categoryName) -
        CATEGORY_TABS.indexOf(b.categoryName)
    );

    return groupedArray;
  })();

  // 5. Рендеринг
  if (isLoading)
    return <div className='loading-spinner'>Загрузка услуг...</div>;
  if (error)
    return <div className='error-message'>Ошибка загрузки: {error}</div>;

  return (
    <div className='service-catalog-page-container'>
      <h1>Выбор услуг</h1>

      {/* 5.1. Поиск */}
      <div className='search-bar-container'>
        <input
          type='text'
          placeholder='🔍 Поиск услуги...'
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className='service-search-input'
        />
      </div>

      {/* 5.2. Вкладки/Категории */}
      <div className='category-tabs-wrapper'>
        {CATEGORY_TABS.map((tabName) => (
          <button
            key={tabName}
            className={`category-tab ${activeTab === tabName ? 'active' : ''}`}
            onClick={() => scrollToCategory(tabName)}
          >
            {tabName}
          </button>
        ))}
      </div>

      {/* 5.3. Каталог услуг */}
      <div className='services-catalog-content'>
        {filteredAndGroupedServices.length === 0 && (
          <p className='no-results'>Услуги по вашему запросу не найдены.</p>
        )}

        {filteredAndGroupedServices.map((group) => (
          <div
            key={group.categoryName}
            className='service-category-block'
            // ⬇️ Устанавливаем Ref для скролла
            ref={(el) => {
              // Явно не возвращаем значение, только устанавливаем его
              categoryRefs.current.set(group.categoryName, el);
            }}
          >
            <h2 className='category-title'>{group.categoryName}</h2>

            <div className='category-services-list'>
              {group.services.map((service) => (
                <ServiceItem
                  key={service.serviceId}
                  service={{
                    name: service.name,
                    category: service.categoryName,
                    priceMin: service.basePrice,
                    duration: service.baseDuration,
                    imageUrl: service.imageUrl || '',
                  }}
                />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ServiceCatalogPage;
