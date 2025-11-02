import React, { useState, useEffect, useCallback } from 'react';
import { useForm, Controller } from 'react-hook-form';

import type { ApiResponse, AdminData } from '../../types/userTypes';

import {
  emailValidation,
  requiredValidation,
  passwordValidation,
  // minLengthValidation удален, так как он не используется
  PERMISSIVE_NAME_REGEX,
  PASSWORD_REGEX,
} from '../../utils/validationRules';

import {
  formatPhoneNumber,
  parsePhoneNumber,
  isValidPhoneNumber,
} from '../../utils/phoneFormatter';

// --- ИНТЕРФЕЙСЫ ---

interface AdminFormData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string; // ИЗМЕНЕНИЕ: Уровень прав вместо Секретного кода
  permissionsLevel: string; // Используем string для <select>
}

interface FormProps {
  onRegister: (data: AdminData) => Promise<ApiResponse>;
  isLoading: boolean;
  serverMessage: { text: string; status: number | null };
}

const AdminRegistrationForm: React.FC<FormProps> = ({
  onRegister,
  isLoading,
  serverMessage,
}) => {
  const [responseMessage, setResponseMessage] = useState<{
    type: 'success' | 'error' | 'loading' | null;
    message: string;
  }>({ type: null, message: '' });
  const [showPassword, setShowPassword] = useState(false);

  const {
    handleSubmit,
    control,
    setValue,
    watch,
    formState: { errors, isSubmitted, isValid },
  } = useForm<AdminFormData>({
    defaultValues: {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '', // ИЗМЕНЕНИЕ: Уровень прав по умолчанию
      permissionsLevel: '1',
    },
    mode: 'onSubmit',
  });

  const phoneValue = watch('phone'); // --- ЛОГИКА ТЕЛЕФОНА ---
  /**
   * Кастомный обработчик изменения для форматирования телефона
   */

  const handlePhoneChange = (
    value: string,
    formOnChange: (value: string) => void
  ) => {
    const formatted = formatPhoneNumber(value);
    formOnChange(formatted);
  };
  /**
   * Правила валидации для телефона
   */

  const phoneValidationRules = {
    ...requiredValidation('Телефон')(),
    validate: (value: string) => {
      if (!isValidPhoneNumber(value)) {
        return 'Пожалуйста, введите полный российский номер телефона.';
      }
      return true;
    },
  }; // Эффект для инициализации телефона: устанавливаем +7, если поле пустое

  useEffect(() => {
    if (!phoneValue || phoneValue === '') {
      setValue('phone', '+7', { shouldValidate: false });
    }
  }, [phoneValue, setValue]); // --- ЛОГИКА ФОРМЫ И СЕРВЕРА --- // Эффект для отображения серверного сообщения

  useEffect(() => {
    if (serverMessage && serverMessage.text) {
      setResponseMessage({
        type:
          serverMessage.status &&
          serverMessage.status >= 200 &&
          serverMessage.status < 300
            ? 'success'
            : 'error',
        message: serverMessage.text,
      });
    }
  }, [serverMessage]); // Функция для отправки формы

  const onSubmit = useCallback(
    async (data: AdminFormData) => {
      setResponseMessage({
        type: 'loading',
        message: 'Регистрация администратора...',
      });

      const fullData: AdminData = {
        email: data.email,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName, // Очищаем телефон от маски перед отправкой на сервер
        phone: parsePhoneNumber(data.phone),
        role: 'ADMIN', // Преобразуем permissionsLevel из строки в число
        permissionsLevel: parseInt(data.permissionsLevel, 10),
      };

      try {
        const result = await onRegister(fullData);
        if (result.status >= 200 && result.status < 300) {
          setResponseMessage({
            type: 'success',
            message: result.message || 'Регистрация прошла успешно!',
          });
        } else {
          setResponseMessage({
            type: 'error',
            message: result.message || 'Произошла ошибка регистрации.',
          });
        }
      } catch (error) {
        console.error('Registration submission error:', error);
        setResponseMessage({
          type: 'error',
          message: 'Не удалось связаться с сервером.',
        });
      }
    },
    [onRegister]
  ); // --- ЛОГИКА ПАРОЛЯ --- // Функция переключения видимости пароля

  const togglePasswordVisibility = () => {
    setShowPassword((prev) => !prev);
  }; // --- РЕНДЕР ---

  return (
    <div className='registration-container'>
           {' '}
      <form
        onSubmit={handleSubmit(onSubmit)}
        className='registration-form'
        noValidate
      >
                <p className='form-title'>Регистрация как Администратор</p>     
          {/* Имя */}       {' '}
        <div className='form-group'>
                   {' '}
          <label htmlFor='firstName' className='form-label'>
                        Имя          {' '}
          </label>
                   {' '}
          <Controller
            name='firstName'
            control={control}
            rules={{
              ...requiredValidation('Имя')(),
              pattern: {
                value: PERMISSIVE_NAME_REGEX,
                message: 'Имя: только буквы, мин. 2 символа.',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                className={`form-input ${
                  errors.firstName && isSubmitted ? 'input-error' : ''
                }`}
              />
            )}
          />
                   {' '}
          {errors.firstName && isSubmitted && (
            <p className='error-message'>{errors.firstName.message}</p>
          )}
                 {' '}
        </div>
                {/* Фамилия */}       {' '}
        <div className='form-group'>
                   {' '}
          <label htmlFor='lastName' className='form-label'>
                        Фамилия          {' '}
          </label>
                   {' '}
          <Controller
            name='lastName'
            control={control}
            rules={{
              ...requiredValidation('Фамилия')(),
              pattern: {
                value: PERMISSIVE_NAME_REGEX,
                message: 'Фамилия: только буквы, мин. 2 символа.',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                className={`form-input ${
                  errors.lastName && isSubmitted ? 'input-error' : ''
                }`}
              />
            )}
          />
                   {' '}
          {errors.lastName && isSubmitted && (
            <p className='error-message'>{errors.lastName.message}</p>
          )}
                 {' '}
        </div>
                {/* Телефон */}       {' '}
        <div className='form-group'>
                   {' '}
          <label htmlFor='phone' className='form-label'>
                        Телефон          {' '}
          </label>
                   {' '}
          <Controller
            name='phone'
            control={control}
            rules={phoneValidationRules}
            render={({ field }) => (
              <input
                {...field}
                onChange={(e) =>
                  handlePhoneChange(e.target.value, field.onChange)
                }
                type='tel'
                maxLength={22}
                className={`form-input ${
                  errors.phone && isSubmitted ? 'input-error' : ''
                }`}
                placeholder='+7 (___) ___ - __ - __'
              />
            )}
          />
                   {' '}
          {errors.phone && isSubmitted && (
            <p className='error-message'>{errors.phone.message}</p>
          )}
                 {' '}
        </div>
                {/* Email */}       {' '}
        <div className='form-group'>
                   {' '}
          <label htmlFor='email' className='form-label'>
                        Email          {' '}
          </label>
                   {' '}
          <Controller
            name='email'
            control={control}
            rules={emailValidation()}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                className={`form-input ${
                  errors.email && isSubmitted ? 'input-error' : ''
                }`}
                placeholder='example@mail.ru'
              />
            )}
          />
                   {' '}
          {errors.email && isSubmitted && (
            <p className='error-message'>{errors.email.message}</p>
          )}
                 {' '}
        </div>
                {/* Пароль */}       {' '}
        <div className='form-group password-group'>
                   {' '}
          <label htmlFor='password' className='form-label'>
                        Пароль          {' '}
          </label>
                   {' '}
          <Controller
            name='password'
            control={control}
            rules={{
              ...passwordValidation(8)('Пароль'),
              pattern: {
                value: PASSWORD_REGEX,
                message:
                  'Пароль: мин. 8 символов, 1 заглавная, 1 цифра, 1 спецсимвол (@$!%*?&).',
              },
            }}
            render={({ field }) => (
              <div className='password-input-container'>
                               {' '}
                <input
                  {...field}
                  type={showPassword ? 'text' : 'password'}
                  className={`form-input ${
                    errors.password && isSubmitted ? 'input-error' : ''
                  }`}
                  autoComplete='new-password'
                />
                               {' '}
                <button
                  type='button'
                  className='password-toggle'
                  onClick={togglePasswordVisibility}
                  disabled={isLoading}
                >
                                   {' '}
                  {showPassword ? (
                    <svg
                      width='20'
                      height='20'
                      viewBox='0 0 24 24'
                      fill='none'
                      xmlns='http://www.w3.org/2000/svg'
                    >
                                           {' '}
                      <path
                        d='M12 4.5C7 4.5 1.9 7.71 0.5 12C1.9 16.29 7 19.5 12 19.5C17 19.5 22.1 16.29 23.5 12C22.1 7.71 17 4.5 12 4.5ZM12 17C9.24 17 7 14.76 7 12C7 9.24 9.24 7 12 7C14.76 7 17 9.24 17 12C17 14.76 14.76 17 12 17ZM12 9C10.34 9 9 10.34 9 12C9 13.66 10.34 15 12 15C13.66 15 15 13.66 15 12C15 10.34 13.66 9 12 9Z'
                        fill='#999'
                      />
                                         {' '}
                    </svg>
                  ) : (
                    <svg
                      width='20'
                      height='20'
                      viewBox='0 0 24 24'
                      fill='none'
                      xmlns='http://www.w3.org/2000/svg'
                    >
                                           {' '}
                      <path
                        d='M12 7C13.1 7 14 7.9 14 9C14 10.1 13.1 11 12 11C10.9 11 10 10.1 10 9C10 7.9 10.9 7 12 7ZM12 2C6.48 2 2.12 4.9 0.06 9.9L0 10L0.06 10.1C0.32 10.71 0.63 11.31 1 11.9C2.44 14.93 5.07 17 8 17H12V19H18V17H20V15H22V13H20V11H22V9H20V7H18V5H12V2ZM12 15C9.79 15 8 13.21 8 11C8 8.79 9.79 7 12 7C14.21 7 16 8.79 16 11C16 13.21 14.21 15 12 15Z'
                        fill='#999'
                      />
                                         {' '}
                    </svg>
                  )}
                                 {' '}
                </button>
                             {' '}
              </div>
            )}
          />
                   {' '}
          {errors.password && isSubmitted && (
            <p className='error-message'>{errors.password.message}</p>
          )}
                 {' '}
        </div>
                {/* Уровень прав (Select) */}       {' '}
        <div className='form-group'>
                   {' '}
          <label htmlFor='permissionsLevel' className='form-label'>
                        Уровень прав          {' '}
          </label>
                   {' '}
          <Controller
            name='permissionsLevel'
            control={control}
            rules={requiredValidation('Уровень прав')()}
            render={({ field }) => (
              <select
                {...field}
                className={`form-input form-select ${
                  errors.permissionsLevel && isSubmitted ? 'input-error' : ''
                }`}
              >
                                <option value='1'>Уровень 1</option>           
                    <option value='2'>Уровень 2</option>             {' '}
              </select>
            )}
          />
                   {' '}
          {errors.permissionsLevel && isSubmitted && (
            <p className='error-message'>{errors.permissionsLevel.message}</p>
          )}
                 {' '}
        </div>
               {' '}
        {responseMessage.type && (
          <div className={`response-message ${responseMessage.type}`}>
                        {responseMessage.message}         {' '}
          </div>
        )}
               {' '}
        <button
          type='submit'
          className={`form-button ${isSubmitted && !isValid ? 'error' : ''}`}
          disabled={isLoading || (isSubmitted && !isValid)}
        >
                   {' '}
          {isLoading
            ? 'Регистрация...'
            : isSubmitted && !isValid
            ? 'Ошибка валидации'
            : 'Зарегистрироваться'}
                 {' '}
        </button>
             {' '}
      </form>
         {' '}
    </div>
  );
};

export default AdminRegistrationForm;
