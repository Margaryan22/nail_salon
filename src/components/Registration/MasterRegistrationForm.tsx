// src/components/Registration/MasterRegistrationForm.tsx

import React, { useEffect, useState } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';

import { useAppDispatch, useAppSelector } from '../../hooks/index';
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

const MASTER_SPECIALIZATIONS = [
  'Маникюр',
  'Педикюр',
  'Наращивание',
  'Диазйн',
] as const;

interface MasterFormData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  specialization: string;
  description: string;
  workExperience: number | '';
}

const MasterRegistrationForm: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { isLoading, serverMessage, isAuthenticated } = useAppSelector(
    (state) => state.auth
  );

  const [showPassword, setShowPassword] = useState(false);

  const {
    handleSubmit,
    control,
    setValue,
    watch,
    setError,
    formState: { errors },
  } = useForm<MasterFormData>({
    defaultValues: {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '+7',
      specialization: '',
      description: '',
      workExperience: '',
    },
    mode: 'onBlur',
  });

  const phoneValue = watch('phone');

  // Форматирование телефона
  useEffect(() => {
    if (!phoneValue || phoneValue === '') {
      setValue('phone', '+7', { shouldValidate: false });
    } else {
      const formatted = formatPhoneNumber(phoneValue);
      if (formatted !== phoneValue) {
        setValue('phone', formatted, { shouldValidate: true });
      }
    }
  }, [phoneValue, setValue]);

  // Очистка сообщения
  useEffect(() => {
    return () => {
      dispatch(clearServerMessage());
    };
  }, [dispatch]);

  // Редирект после успеха
  useEffect(() => {
    if (isAuthenticated) {
      navigate('/profile', { replace: true });
    }
  }, [isAuthenticated, navigate]);

  // Обработка серверных ошибок
  useEffect(() => {
    if (serverMessage.text) {
      const msg = serverMessage.text.toLowerCase();
      if (msg.includes('email')) {
        setError('email', {
          type: 'server',
          message: 'Этот email уже зарегистрирован',
        });
      }
      if (msg.includes('phone') || msg.includes('телефон')) {
        setError('phone', {
          type: 'server',
          message: 'Этот номер телефона уже используется',
        });
      }
    }
  }, [serverMessage.text, setError]);

  const onSubmit = (data: MasterFormData) => {
    const registerData: RegisterDto = {
      email: data.email.trim(),
      password: data.password,
      firstName: data.firstName.trim(),
      lastName: data.lastName.trim(),
      phone: parsePhoneNumber(data.phone),
      role: 'MASTER',
      specialization: data.specialization,
      description: data.description.trim(),
      workExperience:
        data.workExperience === '' ? 0 : Number(data.workExperience),
    };

    dispatch(register(registerData));
  };

  const togglePassword = () => setShowPassword((prev) => !prev);
  const hasError = (field: keyof MasterFormData) => !!errors[field];

  return (
    <div className='registration-form-wrapper'>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className='registration-form master-form'
        noValidate
      >
        <p className='form-title'>Регистрация мастера</p>

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
                message: 'Только буквы, минимум 2 символа',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                placeholder='Александра'
                className={`form-input ${
                  hasError('firstName') ? 'input-error' : ''
                }`}
                disabled={isLoading}
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
                message: 'Только буквы, минимум 2 символа',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                placeholder='Кузнецова'
                className={`form-input ${
                  hasError('lastName') ? 'input-error' : ''
                }`}
                disabled={isLoading}
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
                isValidPhoneNumber(v) || 'Введите полный номер (+7 и 10 цифр)',
            }}
            render={({ field }) => (
              <input
                {...field}
                type='tel'
                placeholder='+7 (999) 123-45-67'
                className={`form-input ${
                  hasError('phone') ? 'input-error' : ''
                }`}
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
                placeholder='master@nails-salon.ru'
                className={`form-input ${
                  hasError('email') ? 'input-error' : ''
                }`}
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
                  'Минимум 8 символов: заглавная буква, цифра и спецсимвол',
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
                  onClick={togglePassword}
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

        {/* Специализация */}
        <div className='form-group'>
          <label className='form-label'>Специализация</label>
          <Controller
            name='specialization'
            control={control}
            rules={requiredValidation('Специализация')()}
            render={({ field }) => (
              <select
                {...field}
                className={`form-input form-select ${
                  hasError('specialization') ? 'input-error' : ''
                }`}
                disabled={isLoading}
              >
                <option value='' disabled>
                  Выберите специализацию
                </option>
                {MASTER_SPECIALIZATIONS.map((spec) => (
                  <option key={spec} value={spec}>
                    {spec}
                  </option>
                ))}
              </select>
            )}
          />
          {hasError('specialization') && (
            <p className='error-message'>{errors.specialization?.message}</p>
          )}
        </div>

        {/* Опыт работы */}
        <div className='form-group'>
          <label className='form-label'>Опыт работы (лет)</label>
          <Controller
            name='workExperience'
            control={control}
            rules={{
              required: 'Укажите опыт работы',
              min: { value: 0, message: 'Не может быть отрицательным' },
              max: { value: 50, message: 'Максимум 50 лет' },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='number'
                min='0'
                max='50'
                placeholder='5'
                className={`form-input ${
                  hasError('workExperience') ? 'input-error' : ''
                }`}
                disabled={isLoading}
                onChange={(e) =>
                  field.onChange(
                    e.target.value === '' ? '' : Number(e.target.value)
                  )
                }
              />
            )}
          />
          {hasError('workExperience') && (
            <p className='error-message'>{errors.workExperience?.message}</p>
          )}
        </div>

        {/* Описание */}
        <div className='form-group'>
          <label className='form-label'>О себе</label>
          <Controller
            name='description'
            control={control}
            rules={{
              required: 'Расскажите немного о себе',
              maxLength: { value: 500, message: 'Максимум 500 символов' },
            }}
            render={({ field }) => (
              <textarea
                {...field}
                rows={4}
                placeholder='Я мастер маникюра с многолетним опытом...'
                className={`form-textarea ${
                  hasError('description') ? 'input-error' : ''
                }`}
                disabled={isLoading}
              />
            )}
          />
          {hasError('description') && (
            <p className='error-message'>{errors.description?.message}</p>
          )}
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
          {isLoading ? 'Отправляем заявку...' : 'Стать мастером'}
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

export default MasterRegistrationForm;
