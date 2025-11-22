// src/types/api.ts

export type Role = 'CLIENT' | 'MASTER' | 'ADMIN';

export interface LoginDto {
  email: string;
  password: string;
}

export interface RegisterDto {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: Role;

  // Опциональные поля в зависимости от роли
  birthdate?: string; // только для CLIENT
  specialization?: string; // только для MASTER
  workExperience?: number; // только для MASTER
  description?: string; // только для MASTER
  permissionsLevel?: number; // только для ADMIN (если используешь)
}

export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
}

export interface User {
  userId: any;
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: Role;
  createdAt: string;
  updatedAt: string;

  // Опциональные профили
  clientProfile?: ClientProfile | null;
  masterProfile?: MasterProfile | null;
}

export interface ClientProfile {
  id: number;
  userId: number;
  birthdate: string; // "2023-01-01"
}

export interface MasterProfile {
  id: number;
  userId: number;
  specialization: string;
  workExperience: number;
  description: string;
  rating?: number;
  reviewCount?: number;
}

// Тип для карточки мастера на фронте (расширенный)
export interface MasterCardType extends User {
  masterProfile: MasterProfile & {
    rating: number;
    reviewCount: number;
  };
  imageUrl?: string; // если будешь добавлять аватарки позже
}

// Тип для хранения в Redux / Context
export interface AuthState {
  user: User | null;
  token: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
}
export interface Service {
  serviceId: number;
  categoryId: number;
  categoryName: string;
  name: string;
  description: string;
  baseDuration: number; // в минутах
  basePrice: number; // в рублях (или твоей валюте)
  active: boolean;
  imageUrl?: string; // опционально, если будешь добавлять фото услуги
}

// Если нужно для отображения в категориях:
export interface CategorizedService {
  categoryId: number;
  categoryName: string;
  services: Service[];
}
export interface MasterResponse {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  phone: string;
  createdAt: string;
  userType: 'MASTER';
  role: 'MASTER';
}
export interface Appointment {
  appointmentId: number;
  clientId: number;
  clientName: string;
  masterId: number;
  masterName: string;
  serviceId: number;
  serviceName: string;
  appointmentDatetime: string;
  endDatetime: string;
  price: number;
  status: 'BOOKED' | 'COMPLETED' | 'CANCELLED';
  notes: string | null;
  createdAt: string;
}
