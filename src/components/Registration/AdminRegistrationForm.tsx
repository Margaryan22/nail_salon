// src/components/Registration/AdminRegistrationForm.tsx

import React, { useState, useEffect } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { useAppDispatch, useAppSelector } from '../../hooks/index'; // если у тебя есть хуки (или просто useDispatch/useSelector)
import { register, clearServerMessage } from '../../redux/authSlice';
import type { RegisterDto } from '../../types/userTypes';

import {
  emailValidation,
  requiredValidation,
  passwordValidation,
  PERMISSIVE_NAME_REGEX,
  PASSWORD_REGEX,
} from '../../utils/validationRules';

import {
  formatPhoneNumber,
  parsePhoneNumber,
  isValidPhoneNumber,
} from '../../utils/phoneFormatter';
import { Link } from 'react-router-dom';

interface AdminFormData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  permissionsLevel: string;
}

const AdminRegistrationForm: React.FC = () => {
  const dispatch = useAppDispatch();
  const { isLoading, serverMessage, isAuthenticated } = useAppSelector(
    (state) => state.auth
  );

  const [showPassword, setShowPassword] = useState(false);

  const {
    handleSubmit,
    control,
    setValue,
    watch,
    formState: { errors, isSubmitted },
  } = useForm<AdminFormData>({
    defaultValues: {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '+7',
      permissionsLevel: '1',
    },
    mode: 'onSubmit',
  });

  const phoneValue = watch('phone');

  // Авто-добавление +7 при пустом поле
  useEffect(() => {
    if (!phoneValue || phoneValue === '') {
      setValue('phone', '+7', { shouldValidate: false });
    }
  }, [phoneValue, setValue]);

  // Очистка сообщения при размонтировании или смене формы
  useEffect(() => {
    return () => {
      dispatch(clearServerMessage());
    };
  }, [dispatch]);

  // Перенаправление после успешной регистрации (опционально)
  // useEffect(() => {
  //   if (isAuthenticated) {
  //     navigate('/admin/dashboard');
  //   }
  // }, [isAuthenticated]);

  const onSubmit = async (data: AdminFormData) => {
    const registerData: RegisterDto = {
      email: data.email.trim(),
      password: data.password,
      firstName: data.firstName.trim(),
      lastName: data.lastName.trim(),
      phone: parsePhoneNumber(data.phone), // убираем +7 и форматирование
      role: 'ADMIN',
      permissionsLevel: parseInt(data.permissionsLevel, 10),
    };

    dispatch(register(registerData));
  };

  const togglePasswordVisibility = () => setShowPassword((prev) => !prev);

  const hasError = (field: keyof AdminFormData) =>
    !!errors[field] && isSubmitted;

  return (
    <div className='registration-form-wrapper'>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className='registration-form admin-form'
        noValidate
      >
        <p className='form-title'>Регистрация администратора</p>

        {/* Имя */}
        <div className='form-group'>
          <label className='form-label'>Имя</label>
          <Controller
            name='firstName'
            control={control}
            rules={{
              ...requiredValidation('Имя')(),
              pattern: {
                value: PERMISSIVE_NAME_REGEX,
                message: 'Имя: только буквы, минимум 2 символа',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                className={`form-input ${
                  hasError('firstName') ? 'input-error' : ''
                }`}
                disabled={isLoading}
                placeholder='Иван'
              />
            )}
          />
          {hasError('firstName') && (
            <p className='error-message'>{errors.firstName?.message}</p>
          )}
        </div>

        {/* Фамилия */}
        <div className='form-group'>
          <label className='form-label'>Фамилия</label>
          <Controller
            name='lastName'
            control={control}
            rules={{
              ...requiredValidation('Фамилия')(),
              pattern: {
                value: PERMISSIVE_NAME_REGEX,
                message: 'Фамилия: только буквы, минимум 2 символа',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                className={`form-input ${
                  hasError('lastName') ? 'input-error' : ''
                }`}
                disabled={isLoading}
                placeholder='Иванов'
              />
            )}
          />
          {hasError('lastName') && (
            <p className='error-message'>{errors.lastName?.message}</p>
          )}
        </div>

        {/* Телефон */}
        <div className='form-group'>
          <label className='form-label'>Телефон</label>
          <Controller
            name='phone'
            control={control}
            rules={{
              ...requiredValidation('Телефон')(),
              validate: (v) =>
                isValidPhoneNumber(v) ||
                'Введите корректный номер (10 цифр после +7)',
            }}
            render={({ field }) => (
              <input
                {...field}
                onChange={(e) =>
                  field.onChange(formatPhoneNumber(e.target.value))
                }
                type='tel'
                className={`form-input ${
                  hasError('phone') ? 'input-error' : ''
                }`}
                placeholder='+7 (999) 123-45-67'
                disabled={isLoading}
              />
            )}
          />
          {hasError('phone') && (
            <p className='error-message'>{errors.phone?.message}</p>
          )}
        </div>

        {/* Email */}
        <div className='form-group'>
          <label className='form-label'>Email</label>
          <Controller
            name='email'
            control={control}
            rules={emailValidation()}
            render={({ field }) => (
              <input
                {...field}
                type='email'
                className={`form-input ${
                  hasError('email') ? 'input-error' : ''
                }`}
                placeholder='admin@nails-salon.ru'
                disabled={isLoading}
              />
            )}
          />
          {hasError('email') && (
            <p className='error-message'>{errors.email?.message}</p>
          )}
        </div>

        {/* Пароль */}
        <div className='form-group password-group'>
          <label className='form-label'>Пароль</label>
          <Controller
            name='password'
            control={control}
            rules={{
              ...passwordValidation(8)('Пароль'),
              pattern: {
                value: PASSWORD_REGEX,
                message:
                  'Минимум 8 символов, заглавная буква, цифра и спецсимвол',
              },
            }}
            render={({ field }) => (
              <div className='password-input-container'>
                <input
                  {...field}
                  type={showPassword ? 'text' : 'password'}
                  className={`form-input ${
                    hasError('password') ? 'input-error' : ''
                  }`}
                  autoComplete='new-password'
                  disabled={isLoading}
                />
                <button
                  type='button'
                  className='password-toggle'
                  onClick={togglePasswordVisibility}
                  disabled={isLoading}
                >
                  {showPassword ? 'Скрыть' : 'Показать'}
                </button>
              </div>
            )}
          />
          {hasError('password') && (
            <p className='error-message'>{errors.password?.message}</p>
          )}
        </div>

        {/* Уровень прав */}
        <div className='form-group'>
          <label className='form-label'>Уровень прав</label>
          <Controller
            name='permissionsLevel'
            control={control}
            rules={requiredValidation('Уровень прав')()}
            render={({ field }) => (
              <select
                {...field}
                className={`form-input form-select ${
                  hasError('permissionsLevel') ? 'input-error' : ''
                }`}
                disabled={isLoading}
              >
                <option value='1'>Уровень 1 — Базовый доступ</option>
                <option value='2'>Уровень 2 — Полный доступ</option>
              </select>
            )}
          />
        </div>

        {/* Сообщение от сервера */}
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

        <button type='submit' className='form-button' disabled={isLoading}>
          {isLoading
            ? 'Создание администратора...'
            : 'Зарегистрировать администратора'}
        </button>
        <p className='login-link-container'>
          Уже есть аккаунт?{' '}
          <Link to='/login' className='login-link'>
            Войти
          </Link>
        </p>
      </form>
    </div>
  );
};

export default AdminRegistrationForm;
