// Предполагаем, что MasterData находится в './userTypes'
import { type MasterData } from './userTypes';

/**
 * Тип данных для отображения мастера на карточке.
 * Расширяет MasterData, добавляя поля, необходимые для UI (рейтинг, фото, и т.д.).
 */
export interface Master extends MasterData {
  id: number; // Уникальный ID мастера
  rank: string; // 'Старший Мастер', 'Топ Мастер' (вместо specialization можно использовать это для отображения ранга)
  rating: number; // Средний рейтинг
  reviewCount: number; // Количество отзывов
  imageUrl: string; // URL фото мастера
  // MasterData поля (firstName, lastName, specialization, workExperience, etc.) также доступны
}

// Тип для Услуги остается без изменений:
export interface Service {
  serviceId: number;
  categoryId: number;
  categoryName: string; // Название категории из API
  name: string; // Название услуги
  description: string;
  baseDuration: string; // Продолжительность
  basePrice: number; // Цена
  active: boolean;
  // Добавляем поле для отображения, если оно нужно
  imageUrl?: string;
}
export interface CategorizedServices {
  categoryName: string;
  services: Service[];
}
