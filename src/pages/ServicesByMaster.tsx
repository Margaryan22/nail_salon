// src/pages/ServicesByMaster.tsx

import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { api, ENDPOINTS } from '../api';
import ServiceList from '../components/ServiceList';
import { type Service } from '../types/userTypes';

const ServicesByMaster: React.FC = () => {
  const { masterId } = useParams<{ masterId: string }>();
  const location = useLocation();
  const navigate = useNavigate();

  const [services, setServices] = useState<Service[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const masterNameFromState = (location.state as { masterName?: string })
    ?.masterName;
  const [masterName, setMasterName] = useState<string>(
    masterNameFromState || `Мастер ${masterId}`
  );

  useEffect(() => {
    if (!masterId) {
      setError('Ошибка: ID мастера не предоставлен.');
      setIsLoading(false);
      return;
    }

    const fetchData = async () => {
      try {
        setIsLoading(true);
        setError(null);

        const serviceUrl = `${ENDPOINTS.MASTER_SERVICES.SERVICE_BY_MASTER}${masterId}`;
        const response = await api.get(serviceUrl);
        const rawServices = response.data || [];

        const normalizedServices: Service[] = rawServices.map((item: any) => ({
          serviceId: item.serviceId,
          name: item.serviceName,
          categoryName: 'Услуги мастера',
          basePrice: item.masterPrice,
          // 💡 ИСПРАВЛЕНИЕ: Берем длительность из API, если есть, иначе 60 минут
          baseDuration: item.duration || 60,
          description: '',
          active: true,
          imageUrl: undefined,
        }));

        setServices(normalizedServices);

        if (rawServices.length > 0 && rawServices[0].masterName) {
          setMasterName(rawServices[0].masterName);
        } else if (masterNameFromState) {
          setMasterName(masterNameFromState);
        }
      } catch (err) {
        console.error('Ошибка загрузки услуг мастера:', err);
        setError('Не удалось загрузить услуги мастера.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [masterId, masterNameFromState]); // ← КЛЮЧЕВОЙ ХЕНДЛЕР С ИСПРАВЛЕНИЕМ

  const handleServiceSelect = (service: Service) => {
    navigate(`/choose-date/${masterId}`, {
      state: {
        masterName,
        serviceId: service.serviceId,
        serviceName: service.name,
        // 💡 ДОБАВЛЕНИЕ: Передача стоимости и длительности в state
        servicePrice: service.basePrice,
        serviceDuration: service.baseDuration,
      },
    });
  };

  if (isLoading) return <div className='loading'>Загрузка услуг...</div>;
  if (error) return <div className='error'>{error}</div>;

  return (
    <div className='services-by-master-container'>
           {' '}
      <button onClick={() => navigate(-1)} className='back-button'>
                ← Назад      {' '}
      </button>
           {' '}
      <h1 className='page-header'>Выберите услугу для записи к {masterName}</h1>
           {' '}
      {services.length > 0 ? (
        <ServiceList
          services={services}
          onServiceSelect={handleServiceSelect}
        />
      ) : (
        <p className='no-services-message'>
                    К сожалению, у этого мастера пока нет доступных услуг.      
           {' '}
        </p>
      )}
         {' '}
    </div>
  );
};

export default ServicesByMaster;
