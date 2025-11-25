// src/pages/ServiceCatalogPage.tsx

import React, { useState, useEffect } from 'react';

import { api, publicApi } from '../api';
import type { Service } from '../types/userTypes';
import ServiceItem from '../components/ServiceItem';

const CATEGORY_TABS = ['Маникюр', 'Педикюр', 'Наращивание', 'Дизайн'] as const;

type CategoryName = (typeof CATEGORY_TABS)[number];

const ServiceCatalogPage: React.FC = () => {
  const [services, setServices] = useState<Service[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState<CategoryName>(CATEGORY_TABS[0]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Самый простой и надёжный способ — объект с ключами-категориями
  const categorySectionRefs: Record<string, HTMLDivElement | null> = {};

  const setCategoryRef = (category: string) => (el: HTMLDivElement | null) => {
    categorySectionRefs[category] = el;
  };

  useEffect(() => {
    const fetchServices = async () => {
      try {
        setIsLoading(true);
        const { data } = await publicApi.get<Service[]>('/services');
        setServices(data.filter((s) => s.active));
        setError(null);
      } catch (err) {
        console.error(err);
        setError('Не удалось загрузить услуги');
      } finally {
        setIsLoading(false);
      }
    };
    fetchServices();
  }, []);

  const scrollToCategory = (category: CategoryName) => {
    setActiveTab(category);
    categorySectionRefs[category]?.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    });
  };

  const groupedServices = React.useMemo(() => {
    let filtered = services;

    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(
        (s) =>
          s.name.toLowerCase().includes(term) ||
          s.description.toLowerCase().includes(term)
      );
    }

    const result: { categoryName: string; services: Service[] }[] = [];

    CATEGORY_TABS.forEach((cat) => {
      const catServices = filtered.filter((s) => s.categoryName === cat);
      if (catServices.length > 0) {
        result.push({ categoryName: cat, services: catServices });
      }
    });

    return result;
  }, [services, searchTerm]);

  if (isLoading) return <div className='loading'>Загрузка услуг...</div>;
  if (error) return <div className='error'>{error}</div>;

  return (
    <div className='service-catalog-page'>
      <h1 className='page-title'>Каталог услуг</h1>

      <div className='search-bar'>
        <input
          type='text'
          placeholder='Поиск услуги...'
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className='search-input'
        />
      </div>

      <div className='category-tabs'>
        {CATEGORY_TABS.map((tab) => (
          <button
            key={tab}
            onClick={() => scrollToCategory(tab)}
            className={activeTab === tab ? 'active' : ''}
          >
            {tab}
          </button>
        ))}
      </div>

      <div className='catalog-content'>
        {groupedServices.length === 0 ? (
          <p className='no-results'>По вашему запросу ничего не найдено</p>
        ) : (
          groupedServices.map((group) => (
            <section
              key={group.categoryName}
              ref={setCategoryRef(group.categoryName)} // ← вот так — без ошибок!
              className='category-section'
            >
              <h2 className='category-title'>{group.categoryName}</h2>
              <div className='services-grid'>
                {group.services.map((service) => (
                  <ServiceItem key={service.serviceId} service={service} />
                ))}
              </div>
            </section>
          ))
        )}
      </div>
    </div>
  );
};

export default ServiceCatalogPage;
