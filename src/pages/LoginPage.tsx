import React, { useState, type FormEvent } from 'react';
import axios, { AxiosError, type AxiosResponse } from 'axios';
import { useNavigate } from 'react-router-dom';

// URL для запроса. Замените его на ваш реальный API-адрес
const API_LOGIN_URL = 'http://87.242.87.228:8080/api/v1/auth/login';

// --- Интерфейсы ---

interface LoginPayload {
  email: string;
  password: string;
}

interface AuthResponse {
  accessToken: string;
  // Предполагаем, что refreshToken приходит в теле, но мы его не сохраняем в localStorage!
  refreshToken: string;
  tokenType: string;
  userId: number;
  email: string;
  role: string;
}

interface ErrorResponse {
  error: string;
  message: string;
}

// --- Компонент ---

const Login: React.FC = () => {
  const navigate = useNavigate();

  const [email, setEmail] = useState<string>('');
  const [password, setPassword] = useState<string>('');
  const [message, setMessage] = useState<{
    text: string;
    type: 'error' | 'success' | 'loading' | '';
  }>({ text: '', type: '' });
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!email || !password) {
      setMessage({ text: 'Пожалуйста, заполните все поля.', type: 'error' });
      return;
    }

    setMessage({ text: 'Выполняется вход...', type: 'loading' });
    setIsLoading(true);

    const payload: LoginPayload = { email, password };

    try {
      // Важно: Установите withCredentials: true, если бэкенд использует HTTP-Only Cookie для Refresh Token.
      const response: AxiosResponse<AuthResponse> = await axios.post(
        API_LOGIN_URL,
        payload,
        { withCredentials: true }
      );

      const data = response.data;

      // 🛑 ВНИМАНИЕ: Для продакшн-кода:
      // Access Token ДОЛЖЕН храниться в памяти (например, в AuthContext/Redux),
      // а не в sessionStorage/localStorage.
      sessionStorage.setItem('accessToken', data.accessToken);

      // Refresh Token должен быть установлен БЭКЕНДОМ в HTTP-Only Cookie.
      // Мы не должны сохранять его или даже видеть на фронтенде.

      setMessage({
        text: `Успешный вход! Роль: ${data.role}. Перенаправление...`,
        type: 'success',
      });

      // --- ЛОГИКА УМНОЙ ПЕРЕАДРЕСАЦИИ ПО РОЛИ ---
      let redirectPath: string;
      switch (data.role.toUpperCase()) {
        case 'ADMIN':
          redirectPath = '/admin-dashboard';
          break;
        case 'MASTER':
          redirectPath = '/master-dashboard';
          break;
        case 'CLIENT':
          redirectPath = '/client-dashboard';
          break;
        default:
          redirectPath = '/default-user-page';
          break;
      }

      setTimeout(() => {
        console.log(`Перенаправление на: ${redirectPath}`);
        navigate(redirectPath);
      }, 1500);
    } catch (error) {
      const axiosError = error as AxiosError<ErrorResponse>;
      console.error('Ошибка Axios:', axiosError);

      let errorText: string;

      if (axiosError.response) {
        const status = axiosError.response.status;
        const serverErrorData = axiosError.response.data;

        errorText = `Ошибка входа (Статус ${status}): ${
          serverErrorData?.error ||
          serverErrorData?.message ||
          'Неверные учетные данные'
        }`;
      } else if (axiosError.request) {
        errorText =
          'Проблема с подключением к серверу. Запрос отправлен, но нет ответа.';
      } else {
        errorText = 'Ошибка настройки запроса. Пожалуйста, попробуйте снова.';
      }

      setMessage({
        text: errorText,
        type: 'error',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  };

  return (
    <div className='login-page-container'>
      <h2 className='form-title'>🔑 Вход</h2>
      <form onSubmit={handleSubmit} className='login-card'>
        {/* Email */}
        <div className='form-group'>
          <label htmlFor='email' className='form-label'>
            Email
          </label>
          <input
            type='email'
            id='email'
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className='form-input'
            placeholder='client@example.com'
          />
        </div>

        {/* Пароль */}
        <div className='form-group'>
          <label htmlFor='password' className='form-label'>
            Пароль
          </label>
          <div className='password-input-container'>
            <input
              id='password'
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className='form-input'
              placeholder='••••••••'
            />
            <button
              type='button'
              onClick={togglePasswordVisibility}
              className='password-toggle'
              aria-label={showPassword ? 'Скрыть пароль' : 'Показать пароль'}
            >
              {showPassword ? '👁️' : '🔒'}
            </button>
          </div>
        </div>

        {/* Сообщение от сервера/загрузка */}
        {message.text && (
          <div className={`response-message ${message.type}`}>
            {message.text}
          </div>
        )}

        {/* Кнопка Submit */}
        <div className='form-button-container'>
          <button type='submit' disabled={isLoading} className='form-button'>
            {isLoading ? 'Загрузка...' : 'Войти'}
          </button>
        </div>

        {/* Ссылка на регистрацию */}
        <div className='login-link-container'>
          <p className='register-link'>
            Если у вас нет аккаунта -
            <a href='/registration' className='login-link'>
              зарегистрируйтесь
            </a>
          </p>
        </div>
      </form>
    </div>
  );
};

export default Login;
