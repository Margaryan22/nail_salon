import React, { useState, useCallback } from 'react';
import axios, { AxiosError } from 'axios'; // Импортируем axios и AxiosError

// Импортируем обновленные типы
import type {
  Role,
  RegistrationData,
  ApiResponse,
  AdminData,
  MasterData,
  ClientData,
} from '../types/userTypes';

// Импортируем все компоненты форм
import ClientRegistrationForm from '../components/Registration/ClientRegistrationForm';
import MasterRegistrationForm from '../components/Registration/MasterRegistrationForm';
import AdminRegistrationForm from '../components/Registration/AdminRegistrationForm';

/**
 * Реальный POST запрос на регистрацию с использованием axios.
 */
const API_REGISTER_URL = 'http://87.242.87.228:8080/api/v1/auth/register';

const apiRegister = async (data: RegistrationData): Promise<ApiResponse> => {
  try {
    // Использование axios.post:
    // - Автоматически устанавливает Content-Type: application/json.
    // - Автоматически парсит ответ в response.data.
    // - Бросает исключение для статусов 4xx/5xx.
    const response = await axios.post(API_REGISTER_URL, data); // Если код дошел сюда, статус - 2xx (успех)

    const responseData = response.data;

    return {
      status: response.status,
      message:
        responseData.message ||
        `Успешная регистрация для ${data.firstName} (${data.role})!`,
    };
  } catch (error) {
    const axiosError = error as AxiosError;
    console.error('Ошибка Axios при регистрации:', axiosError);

    if (axiosError.response) {
      // Ошибка HTTP (4xx или 5xx)
      const status = axiosError.response.status;
      const responseData = axiosError.response.data as {
        message?: string;
        error?: string;
      };

      return {
        status: status,
        message:
          responseData.message ||
          responseData.error ||
          'Ошибка регистрации. Проверьте введенные данные.',
      };
    } else if (axiosError.request) {
      // Ошибка запроса (нет ответа от сервера)
      return {
        status: 503, // Service Unavailable (или 500, в зависимости от предпочтений)
        message: 'Проблема с подключением к серверу. Сервер недоступен.',
      };
    } else {
      // Другие ошибки
      return {
        status: 500,
        message: 'Произошла неизвестная ошибка при отправке запроса.',
      };
    }
  }
};

// Определяем общий тип пропсов для всех форм, используя дженерик для onRegister
interface FormProps<T extends RegistrationData> {
  onRegister: (data: T) => Promise<ApiResponse>;
  isLoading: boolean;
  serverMessage: { text: string; status: number | null };
}

const Registration: React.FC = () => {
  const [role, setRole] = useState<Role>('CLIENT');
  const [isLoading, setIsLoading] = useState(false);
  const [serverMessage, setServerMessage] = useState<{
    text: string;
    status: number | null;
  }>({ text: '', status: null }); // Универсальный обработчик регистрации

  const handleRegister = useCallback(
    async (data: RegistrationData): Promise<ApiResponse> => {
      setIsLoading(true);
      setServerMessage({ text: 'Отправка данных...', status: null });
      console.log('Данные для отправки:', data);

      try {
        const response = await apiRegister(data);
        setServerMessage({ text: response.message, status: response.status });
        return response;
      } catch (error) {
        const errorMessage: ApiResponse = {
          status: 500,
          message: 'Произошла ошибка сети/сервера.',
        };
        setServerMessage({
          text: errorMessage.message,
          status: errorMessage.status,
        });
        return errorMessage;
      } finally {
        setIsLoading(false);
      }
    },
    []
  ); // Вычисление класса сообщения

  const getMessageClass = () => {
    if (
      serverMessage.status &&
      serverMessage.status >= 200 &&
      serverMessage.status < 300
    )
      return 'success';
    if (
      serverMessage.status === 400 ||
      serverMessage.status === 409 ||
      serverMessage.status === 500 ||
      serverMessage.status === 503
    )
      return 'error';
    if (isLoading) return 'loading';
    return '';
  };

  const messageClass = getMessageClass(); // Логика рендеринга соответствующей формы // Примечание: В компонентах форм (ClientRegistrationForm и т.д.) добавьте mode: 'onSubmit' в useForm

  const renderForm = () => {
    switch (role) {
      case 'CLIENT':
        const clientProps: FormProps<ClientData> = {
          onRegister: handleRegister as (
            data: ClientData
          ) => Promise<ApiResponse>,
          isLoading,
          serverMessage,
        };
        return <ClientRegistrationForm {...clientProps} />;
      case 'MASTER':
        const masterProps: FormProps<MasterData> = {
          onRegister: handleRegister as (
            data: MasterData
          ) => Promise<ApiResponse>,
          isLoading,
          serverMessage,
        };
        return <MasterRegistrationForm {...masterProps} />;
      case 'ADMIN':
        const adminProps: FormProps<AdminData> = {
          onRegister: handleRegister as (
            data: AdminData
          ) => Promise<ApiResponse>,
          isLoading,
          serverMessage,
        };
        return <AdminRegistrationForm {...adminProps} />;
      default:
        return null;
    }
  };

  return (
    <div className='registration-container'>
                  <h2>Регистрация нового пользователя</h2>           
      {/* Блок переключения ролей (табов) */}           
      <div className='role-tabs'>
                               
        {(['CLIENT', 'MASTER', 'ADMIN'] as Role[]).map((r) => (
          <button
            key={r}
            className={`tab-button ${role === r ? 'active-tab' : ''}`}
            onClick={() => {
              setRole(r);
              setServerMessage({ text: '', status: null });
            }}
            disabled={isLoading}
          >
                                               
            {r === 'CLIENT' ? 'Клиент' : r === 'MASTER' ? 'Мастер' : 'Админ'}   
                                       
          </button>
        ))}
                           
      </div>
                  {/* Рендеринг активной формы */}      {renderForm()}         
       
      {/* Отображаем сообщение (кроме статуса 400 - ошибки валидации внутри формы) */}
                       
      {serverMessage.text && serverMessage.status !== 400 && (
        <div className={`response-message ${messageClass}`}>
                                       
          <p>
                           <strong>Статус:</strong>
            {serverMessage.status || '...'}                 
            <strong> Сообщение:</strong> {serverMessage.text}                   
          </p>
                                   
        </div>
      )}
                   
    </div>
  );
};

export default Registration;
