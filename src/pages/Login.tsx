import React, { useState, type FormEvent } from 'react';
// Импортируем axios и типы для обработки ответов и ошибок
import axios, { AxiosError, type AxiosResponse } from 'axios';
import { useNavigate } from 'react-router-dom';
// Удален импорт useAuth, как было запрошено пользователем.

// URL для запроса. Замените его на ваш реальный API-адрес
const API_LOGIN_URL = 'http://87.242.87.228:8080/api/v1/auth/login';

// --- Интерфейсы ---

interface LoginPayload {
  email: string;
  password: string;
}
interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  email: string;
  role: string;
}
// Интерфейс для ошибок, как они могут прийти от сервера
interface ErrorResponse {
  error: string;
  message: string;
}

// --- Компонент ---

const Login: React.FC = () => {
  // const { login } = useAuth(); // Удалено по запросу пользователя
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
      const response: AxiosResponse<AuthResponse> = await axios.post(
        API_LOGIN_URL,
        payload
      );

      const data = response.data; // !!! Прямое сохранение токенов в localStorage. ВНИМАНИЕ: Уязвимость к XSS-атакам.

      localStorage.setItem('accessToken', data.accessToken);
      localStorage.setItem('refreshToken', data.refreshToken);

      setMessage({
        text: `Успешный вход! Роль: ${data.role}. Перенаправление...`,
        type: 'success',
      }); // --- ЛОГИКА УМНОЙ ПЕРЕАДРЕСАЦИИ ПО РОЛИ ---

      let redirectPath: string; // Приводим роль к верхнему регистру, чтобы избежать ошибок из-за регистра
      switch (data.role.toUpperCase()) {
        case 'ADMIN':
          redirectPath = '/admin-dashboard';
          break;
        case 'MASTER': // ДОБАВЛЕНА НОВАЯ РОЛЬ
          redirectPath = '/master-dashboard';
          break;
        case 'CLIENT':
          redirectPath = '/client-dashboard';
          break;
        default: // Маршрут по умолчанию для всех остальных ролей
          redirectPath = '/default-user-page';
          break;
      }

      setTimeout(() => {
        console.log(`Перенаправление на: ${redirectPath}`); // Используем определенный маршрут
        navigate(redirectPath);
      }, 1500); // --- КОНЕЦ ЛОГИКИ ПЕРЕАДРЕСАЦИИ ---
    } catch (error) {
      // Axios ловит и сетевые ошибки, и HTTP-ошибки (4xx/5xx)
      const axiosError = error as AxiosError<ErrorResponse>;
      console.error('Ошибка Axios:', axiosError);

      let errorText: string;

      if (axiosError.response) {
        // Ошибка HTTP (4xx или 5xx)
        const status = axiosError.response.status;
        const serverErrorData = axiosError.response.data;

        errorText = `Ошибка входа (Статус ${status}): ${
          serverErrorData?.error ||
          serverErrorData?.message ||
          'Неверные учетные данные'
        }`;
      } else if (axiosError.request) {
        // Ошибка запроса (запрос отправлен, но нет ответа - таймаут, проблема CORS/сети)
        errorText =
          'Проблема с подключением к серверу. Запрос отправлен, но нет ответа.';
      } else {
        // Ошибка настройки
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
                  <h2 className='form-title'>🔑 Вход</h2>           {' '}
      <form onSubmit={handleSubmit} className='login-card'>
               {' '}
        <div className='form-group'>
                                       {' '}
          <label htmlFor='email' className='form-label'>
                                    Email                    {' '}
          </label>
                                       {' '}
          <input
            type='email'
            id='email'
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            className='form-input'
            placeholder='client@example.com'
          />
                                   {' '}
        </div>
                        {/* Пароль */}               {' '}
        <div className='form-group'>
                                       {' '}
          <label htmlFor='password' className='form-label'>
                                    Пароль                    {' '}
          </label>
                                       {' '}
          <div className='password-input-container'>
                                               {' '}
            <input
              id='password'
              type={showPassword ? 'text' : 'password'}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className='form-input'
              placeholder='••••••••'
            />
                                               {' '}
            <button
              type='button'
              onClick={togglePasswordVisibility}
              className='password-toggle'
              aria-label={showPassword ? 'Скрыть пароль' : 'Показать пароль'}
            >
                                          {/* Используем эмодзи для простоты */}
                                          {showPassword ? '👁️' : '🔒'}         
                           {' '}
            </button>
                                           {' '}
          </div>
                                   {' '}
        </div>
                        {/* Сообщение от сервера/загрузка */}               {' '}
        {message.text && (
          <div className={`response-message ${message.type}`}>
                                    {message.text}                   {' '}
          </div>
        )}
                        {/* Контейнер для центрирования кнопки */}             
         {' '}
        <div className='form-button-container'>
                              {/* Кнопка Submit */}                   {' '}
          <button type='submit' disabled={isLoading} className='form-button'>
                                    {isLoading ? 'Загрузка...' : 'Войти'}       
                       {' '}
          </button>
                                   {' '}
        </div>
                       {' '}
        {/* Ссылка на регистрацию, перемещенная внутрь form-card */}           
                   {' '}
        <div className='login-link-container'>
                                       {' '}
          <p className='register-link'>
                                    Если у вас нет аккаунта -            {' '}
            <a href='/registration' className='login-link'>
                            зарегистрируйтесь            {' '}
            </a>
                                           {' '}
          </p>
                                   {' '}
        </div>
                           {' '}
      </form>
                   {' '}
    </div>
  );
};

export default Login;
