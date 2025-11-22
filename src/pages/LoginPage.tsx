// src/components/Login/Login.tsx

import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import { useAppDispatch, useAppSelector } from '../hooks';
import { login, clearServerMessage } from '../redux/authSlice';

const Login: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { isLoading, serverMessage, isAuthenticated, user, accessToken } =
    useAppSelector((state) => state.auth);

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // Очистка сообщений при заходе
  useEffect(() => {
    dispatch(clearServerMessage());
  }, [dispatch]);

  // Редирект после успешного логина
  useEffect(() => {
    if (isAuthenticated && user) {
      const role = user.role;

      switch (role) {
        case 'ADMIN':
          navigate('/admin/dashboard', { replace: true });
          break;
        case 'MASTER':
          navigate('/master/dashboard', { replace: true });
          break;
        case 'CLIENT':
        default:
          navigate('/profile', { replace: true });
          break;
      }
    }
  }, [isAuthenticated, user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!email.trim() || !password) {
      return;
    }

    // Используем thunk из authSlice — он сам сохранит accessToken!
    const result = await dispatch(login({ email: email.trim(), password }));

    // Если логин не удался — ничего не делаем
    if (login.rejected.match(result)) {
      console.log('Ошибка входа:', result.payload);
    }
  };

  const togglePassword = () => setShowPassword((prev) => !prev);

  return (
    <div className='login-page-container'>
      <div className='login-card'>
        <h2 className='form-title'>Вход в аккаунт</h2>

        <form onSubmit={handleSubmit} noValidate>
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
              placeholder='you@example.com'
              className='form-input'
              disabled={isLoading}
            />
          </div>

          <div className='form-group password-group'>
            <label htmlFor='password' className='form-label'>
              Пароль
            </label>
            <div className='password-input-container'>
              <input
                type={showPassword ? 'text' : 'password'}
                id='password'
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder='••••••••'
                className='form-input'
                disabled={isLoading}
              />
              <button
                type='button'
                onClick={togglePassword}
                className='password-toggle'
                disabled={isLoading}
              >
                {showPassword ? 'Скрыть' : 'Показать'}
              </button>
            </div>
          </div>

          {/* Сообщения */}
          {serverMessage.text && (
            <div
              className={`response-message ${
                serverMessage.status &&
                serverMessage.status >= 200 &&
                serverMessage.status < 300
                  ? 'success'
                  : 'error'
              }`}
            >
              {serverMessage.text}
            </div>
          )}

          <button type='submit' disabled={isLoading} className='form-button'>
            {isLoading ? 'Входим...' : 'Войти'}
          </button>

          <p className='register-prompt'>
            Нет аккаунта?{' '}
            <Link to='/registration' className='login-link'>
              Зарегистрироваться
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Login;
