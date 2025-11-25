// src/pages/LoginPage.tsx

import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../hooks';
import { login, clearServerMessage } from '../redux/authSlice';

const LoginPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const { isLoading, serverMessage, isAuthenticated } = useAppSelector(
    (state) => state.auth
  );

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    dispatch(clearServerMessage());
  }, [dispatch]);

  useEffect(() => {
    if (isAuthenticated) {
      navigate('/profile', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email.trim() || !password) return;

    await dispatch(login({ email: email.trim(), password }));
  };

  const togglePassword = () => setShowPassword((prev) => !prev);

  return (
    <div className='login-page-container'>
      <div className='login-card'>
        <h2 className='form-title'>Вход в аккаунт 🔑</h2>

        <form onSubmit={handleSubmit} noValidate>
          {/* Email */}
          <div className='form-group'>
            <label className='form-label' htmlFor='email'>
              Email
            </label>
            <input
              id='email'
              type='email'
              className='form-input' // ← ВАЖНО!
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder='you@example.com'
              disabled={isLoading}
              autoComplete='username'
              required
            />
          </div>

          {/* Пароль */}
          <div className='form-group password-group'>
            <label className='form-label' htmlFor='password'>
              Пароль
            </label>
            <div className='password-input-container'>
              <input
                id='password'
                type={showPassword ? 'text' : 'password'}
                className='form-input' // ← ВАЖНО!
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder='••••••••'
                disabled={isLoading}
                autoComplete='current-password'
                required
              />
              <button
                type='button'
                onClick={togglePassword}
                className='password-toggle'
                aria-label={showPassword ? 'Скрыть пароль' : 'Показать пароль'}
              >
                {showPassword ? '🙈' : '🐵'}
              </button>
            </div>
          </div>

          {/* Сообщение от сервера */}
          {serverMessage.text && (
            <div
              className={`response-message ${
                serverMessage.status && serverMessage.status >= 400
                  ? 'error'
                  : 'success'
              }`}
            >
              {serverMessage.text}
            </div>
          )}

          {/* Кнопка входа */}
          <button type='submit' disabled={isLoading} className='form-button'>
            {isLoading ? 'Входим...' : 'Войти'}
          </button>

          {/* Ссылка на регистрацию */}
          <p className='login-link-container'>
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

export default LoginPage;
