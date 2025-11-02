import React, { useState, useEffect, useCallback } from 'react';
import { useForm, Controller } from 'react-hook-form';

import type { ApiResponse, ClientData } from '../../types/userTypes';

import {
  emailValidation,
  requiredValidation,
  passwordValidation,
  validateBirthdate,
  PERMISSIVE_NAME_REGEX,
  PASSWORD_REGEX,
  phoneValidation,
} from '../../utils/validationRules';

import {
  parsePhoneNumber,
  formatPhoneNumber,
} from '../../utils/phoneFormatter';
import { Link } from 'react-router-dom';

interface ClientFormData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  birthdate: string;
}

interface FormProps {
  onRegister: (data: ClientData) => Promise<ApiResponse>;
  isLoading: boolean;
  serverMessage: { text: string; status: number | null };
}

const ClientRegistrationForm: React.FC<FormProps> = ({
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
    // !!! ДОБАВЛЕН setError для установки серверных ошибок !!!
    setError,
    formState: { errors, isSubmitted, isValid },
  } = useForm<ClientFormData>({
    defaultValues: {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '',
      birthdate: '',
    },
    mode: 'onBlur',
  });

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
  }, [serverMessage]);

  const onSubmit = useCallback(
    async (data: ClientFormData) => {
      setResponseMessage({
        type: 'loading',
        message: 'Регистрация клиента...',
      });

      const fullData: ClientData = {
        ...data,
        birthdate: data.birthdate || '',
        phone: `${parsePhoneNumber(data.phone)}`,
        role: 'CLIENT',
      };

      console.log('Данные для отправки:', fullData);
      try {
        const result = await onRegister(fullData);

        if (result.status >= 200 && result.status < 300) {
          setResponseMessage({
            type: 'success',
            message: result.message || 'Регистрация прошла успешно!',
          });
        } else {
          // --- ОБНОВЛЕННАЯ ЛОГИКА: ОБРАБОТКА КОНКРЕТНЫХ СЕРВЕРНЫХ ОШИБОК ---
          let specificErrorFound = false;

          // Проверка на занятый Email
          if (
            result.message &&
            result.message.toLowerCase().includes('email already exists')
          ) {
            setError('email', {
              type: 'server',
              message: 'Этот Email уже зарегистрирован.',
            });
            specificErrorFound = true;
          }

          // Проверка на занятый Телефон
          if (
            result.message &&
            result.message.toLowerCase().includes('phone number already exists')
          ) {
            setError('phone', {
              type: 'server',
              message: 'Этот номер телефона уже зарегистрирован.',
            });
            specificErrorFound = true;
          }

          if (specificErrorFound) {
            setResponseMessage({
              type: 'error',
              // Это сообщение будет видно НАД кнопкой, если есть ошибки под полями
              message: 'Пожалуйста, проверьте поля с ошибками.',
            });
          } else {
            // Если это общая или неизвестная ошибка сервера
            setResponseMessage({
              type: 'error',
              message: result.message || 'Произошла ошибка регистрации.',
            });
          }
          // --- КОНЕЦ ОБНОВЛЕННОЙ ЛОГИКИ ---
        }
      } catch (error) {
        console.error('Registration submission error:', error);
        setResponseMessage({
          type: 'error',
          message: 'Не удалось связаться с сервером.',
        });
      }
    },
    [onRegister, setError]
  );

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword);
  };

  const handlePhoneChange = useCallback(
    (value: string, fieldOnChange: (...event: any[]) => void) => {
      const digitsOnly = value.replace(/\D/g, '');
      let formattedValue = value;

      if (digitsOnly.length === 0) {
        formattedValue = '';
      } else {
        formattedValue = formatPhoneNumber(value);
      }
      fieldOnChange(formattedValue);
    },
    []
  );

  return (
    <div className='registration-container'>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className='registration-form'
        noValidate
      >
        <p className='form-title'>Регистрация как Клиент</p>

        {/* Имя */}
        <div className='form-group'>
          <label htmlFor='firstName' className='form-label'>
            Имя
          </label>
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
                  errors.firstName ? 'input-error' : ''
                }`}
              />
            )}
          />
          {/* !!! ОТОБРАЖЕНИЕ ОШИБКИ !!! */}
          {errors.firstName && (
            <p className='error-message'>{errors.firstName.message}</p>
          )}
        </div>

        {/* Фамилия */}
        <div className='form-group'>
          <label htmlFor='lastName' className='form-label'>
            Фамилия
          </label>
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
                className={`form-input ${errors.lastName ? 'input-error' : ''}`}
              />
            )}
          />
          {/* !!! ОТОБРАЖЕНИЕ ОШИБКИ !!! */}
          {errors.lastName && (
            <p className='error-message'>{errors.lastName.message}</p>
          )}
        </div>

        {/* Телефон */}
        <div className='form-group'>
          <label htmlFor='phone' className='form-label'>
            Телефон
          </label>
          <Controller
            name='phone'
            control={control}
            rules={phoneValidation()}
            render={({ field }) => (
              <input
                {...field}
                onChange={(e) =>
                  handlePhoneChange(e.target.value, field.onChange)
                }
                type='tel'
                maxLength={22}
                className={`form-input ${errors.phone ? 'input-error' : ''}`}
                placeholder='+7 (___) ___ - __ - __'
              />
            )}
          />
          {/* !!! ОТОБРАЖЕНИЕ ОШИБКИ (включая серверную) !!! */}
          {errors.phone && (
            <p className='error-message'>{errors.phone.message}</p>
          )}
        </div>

        {/* Email */}
        <div className='form-group'>
          <label htmlFor='email' className='form-label'>
            Email
          </label>
          <Controller
            name='email'
            control={control}
            rules={emailValidation()}
            render={({ field }) => (
              <input
                {...field}
                type='text'
                className={`form-input ${errors.email ? 'input-error' : ''}`}
                placeholder='example@mail.ru'
              />
            )}
          />
          {/* !!! ОТОБРАЖЕНИЕ ОШИБКИ (включая серверную) !!! */}
          {errors.email && (
            <p className='error-message'>{errors.email.message}</p>
          )}
        </div>

        {/* Пароль */}
        <div className='form-group password-group'>
          <label htmlFor='password' className='form-label'>
            Пароль
          </label>
          <Controller
            name='password'
            control={control}
            rules={{
              ...passwordValidation(8)('Пароль'),
              pattern: {
                value: PASSWORD_REGEX,
                message:
                  'Пароль: мин. 8 символов, 1 заглавная, 1 цифра, 1 спецсимвол (@$#%!).',
              },
            }}
            render={({ field }) => (
              <div className='password-input-container'>
                <input
                  {...field}
                  type={showPassword ? 'text' : 'password'}
                  className={`form-input ${
                    errors.password ? 'input-error' : ''
                  }`}
                  autoComplete='new-password'
                  autoCapitalize='off'
                  autoCorrect='off'
                  inputMode='text'
                />
                <button
                  type='button'
                  className='password-toggle'
                  onClick={togglePasswordVisibility}
                  disabled={isLoading}
                >
                  {showPassword ? (
                    <svg
                      width='20'
                      height='20'
                      viewBox='0 0 24 24'
                      fill='none'
                      xmlns='http://www.w3.org/2000/svg'
                    >
                      <path
                        d='M12 4.5C7 4.5 1.9 7.71 0.5 12C1.9 16.29 7 19.5 12 19.5C17 19.5 22.1 16.29 23.5 12C22.1 7.71 17 4.5 12 4.5ZM12 17C9.24 17 7 14.76 7 12C7 9.24 9.24 7 12 7C14.76 7 17 9.24 17 12C17 14.76 14.76 17 12 17ZM12 9C10.34 9 9 10.34 9 12C9 13.66 10.34 15 12 15C13.66 15 15 13.66 15 12C15 10.34 13.66 9 12 9Z'
                        fill='#999'
                      />
                    </svg>
                  ) : (
                    <svg
                      width='20'
                      height='20'
                      viewBox='0 0 24 24'
                      fill='none'
                      xmlns='http://www.w3.org/2000/svg'
                    >
                      <path
                        d='M12 7C13.1 7 14 7.9 14 9C14 10.1 13.1 11 12 11C10.9 11 10 10.1 10 9C10 7.9 10.9 7 12 7ZM12 2C6.48 2 2.12 4.9 0.06 9.9L0 10L0.06 10.1C0.32 10.71 0.63 11.31 1 11.9C2.44 14.93 5.07 17 8 17H12V19H18V17H20V15H22V13H20V11H22V9H20V7H18V5H12V2ZM12 15C9.79 15 8 13.21 8 11C8 8.79 9.79 7 12 7C14.21 7 16 8.79 16 11C16 13.21 14.21 15 12 15Z'
                        fill='#999'
                      />
                    </svg>
                  )}
                </button>
              </div>
            )}
          />
          {errors.password && (
            <p className='error-message'>{errors.password.message}</p>
          )}
        </div>

        {/* Дата рождения */}
        <div className='form-group'>
          <label htmlFor='birthdate' className='form-label'>
            Дата рождения
          </label>
          <Controller
            name='birthdate'
            control={control}
            rules={{
              ...requiredValidation('Дата рождения')(),
              validate: validateBirthdate,
            }}
            render={({ field }) => (
              <input
                {...field}
                type='date'
                className={`form-input ${
                  errors.birthdate ? 'input-error' : ''
                }`}
              />
            )}
          />
          {errors.birthdate && (
            <p className='error-message'>{errors.birthdate.message}</p>
          )}
        </div>

        {/* СООБЩЕНИЕ ОТВЕТА СЕРВЕРА (отображается над кнопкой) */}
        {responseMessage.type && (
          <div className={`response-message ${responseMessage.type}`}>
            {responseMessage.message}
          </div>
        )}

        {/* КНОПКА SUBMIT */}
        <button
          type='submit'
          // Класс 'error' может быть оставлен для стилей, но текст кнопки больше не меняется
          className={`form-button ${isSubmitted && !isValid ? 'error' : ''}`}
          disabled={isLoading || (isSubmitted && !isValid)}
        >
          {isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
        </button>
        <p className='login-link-container'>
          Если у вас уже есть аккаунт -{' '}
          <Link to='/login' className='login-link'>
            авторизируйтесь
          </Link>
        </p>
      </form>
    </div>
  );
};

export default ClientRegistrationForm;
