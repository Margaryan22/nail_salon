// src/types/userTypes.ts

export type Role = 'CLIENT' | 'MASTER' | 'ADMIN';

export interface BaseUserData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  role: Role; // Добавим роль в общий тип
}

// Тип для Клиента
export interface ClientData extends BaseUserData {
  role: 'CLIENT';
  birthdate: string; // Формат 'YYYY-MM-DD'
}

// Тип для Мастера
export interface MasterData extends BaseUserData {
  role: 'MASTER';
  specialization: string;
  workExperience?: number; // Лет опыта, made optional to avoid missing property error
  description: string;
}

// Тип для Администратора
export interface AdminData extends BaseUserData {
  role: 'ADMIN';
  permissionsLevel: number; // Уникальное поле для админа (вместо опыта и специализации)
}

// Общий тип для обработчика регистрации
export type RegistrationData = ClientData | MasterData | AdminData;

export interface ApiResponse {
  status: number;
  message: string;
}
