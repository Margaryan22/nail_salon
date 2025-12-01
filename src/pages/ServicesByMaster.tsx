import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom'; // ← добавил useLocation
import { api, ENDPOINTS } from '../api';
import ServiceList from '../components/ServiceList';
import { type Service } from '../types/userTypes';

const ServicesByMaster: React.FC = () => {
  const { masterId } = useParams<{ masterId: string }>();
  const location = useLocation(); // ← достаём state
  const navigate = useNavigate();

  const [services, setServices] = useState<Service[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // ← Извлекаем имя из state, с fallback на ID
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

        // Преобразуем данные под твой тип Service
        const normalizedServices: Service[] = rawServices.map((item: any) => ({
          serviceId: item.serviceId,
          name: item.serviceName,
          categoryName: 'Услуги мастера', // или подтяни реальную категорию позже
          basePrice: item.masterPrice, // ← это цена у конкретного мастера!
          baseDuration: 60, // ← заглушка, если нет точного времени
          description: '',
          active: true,
          imageUrl: undefined,
        }));

        setServices(normalizedServices);

        // Имя мастера можно взять прямо из ответа!
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
  }, [masterId, masterNameFromState]);

  if (isLoading) return <div className='loading'>Загрузка услуг...</div>;
  if (error) return <div className='error'>{error}</div>;

  return (
    <div className='services-by-master-container'>
      <button onClick={() => navigate(-1)} className='back-button'>
        &larr; Назад
      </button>
      <h1 className='page-header'>Выберите услугу для записи к {masterName}</h1>

      {services.length > 0 ? (
        <ServiceList services={services} />
      ) : (
        <p className='no-services-message'>
          К сожалению, у этого мастера пока нет доступных услуг.
        </p>
      )}
    </div>
  );
};

export default ServicesByMaster;
