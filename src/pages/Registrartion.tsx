import React, { useState, useCallback } from 'react';

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
 * Реальный POST запрос на регистрацию.
 */
const apiRegister = async (data: RegistrationData): Promise<ApiResponse> => {
  try {
    const response = await fetch(
      'http://87.242.87.228:8080/api/v1/auth/register',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
      }
    );

    const responseData = await response.json();
    console.error(
      'Server error details:',
      JSON.stringify(responseData, null, 2)
    );
    if (!response.ok) {
      console.error('Server error details:', responseData); // <- Добавьте это
      return {
        status: response.status,
        message: responseData.message || 'Ошибка регистрации',
      };
    } // Для 201 - успех

    return {
      status: response.status,
      message:
        responseData.message ||
        `Успешная регистрация для ${data.firstName} (${data.role})!`,
    };
  } catch (error) {
    console.error('Network error:', error);
    return {
      status: 500,
      message: 'Произошла ошибка сети/сервера.',
    };
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
    if (serverMessage.status === 201) return 'success';
    if (
      serverMessage.status === 400 ||
      serverMessage.status === 409 ||
      serverMessage.status === 500
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
               <strong>Статус:</strong> {serverMessage.status || '...'}     {' '}
            <strong> Сообщение:</strong> {serverMessage.text}         
          </p>
                 
        </div>
      )}
         
    </div>
  );
};

export default Registration;
