// src/components/Registration/ClientRegistrationForm.tsx

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

interface ClientFormData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  birthdate: string; // YYYY-MM-DD
}

const ClientRegistrationForm: React.FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate(); // 🔑 ИЗМЕНЕНО: Используем isRegisteredSuccess вместо isAuthenticated для блокировки/отображения
  const { isLoading, serverMessage, isRegisteredSuccess } = useAppSelector(
    (state) => state.auth
  );

  const [showPassword, setShowPassword] = useState(false);

  const {
    handleSubmit,
    control,
    setValue,
    watch,
    setError,
    formState: { errors, isSubmitted },
  } = useForm<ClientFormData>({
    defaultValues: {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '+7',
      birthdate: '',
    },
    mode: 'onBlur',
  });

  const phoneValue = watch('phone'); // Авто +7 при пустом поле

  useEffect(() => {
    if (!phoneValue || phoneValue === '') {
      setValue('phone', '+7', { shouldValidate: false });
    }
  }, [phoneValue, setValue]); // Очистка сообщения при уходе с формы

  useEffect(() => {
    return () => {
      dispatch(clearServerMessage());
    };
  }, [dispatch]); // Обработка серверных ошибок (email/phone уже заняты)

  useEffect(() => {
    // Проверяем, что это сообщение об ошибке (не успех)
    if (
      serverMessage.text &&
      serverMessage.status &&
      serverMessage.status >= 400
    ) {
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
  }, [serverMessage.text, serverMessage.status, setError]);

  const onSubmit = (data: ClientFormData) => {
    // 💡 ИЗМЕНЕНИЕ: Очищаем предыдущие ошибки перед новым запросом
    dispatch(clearServerMessage());

    const registerData: RegisterDto = {
      email: data.email.trim(),
      password: data.password,
      firstName: data.firstName.trim(),
      lastName: data.lastName.trim(),
      phone: parsePhoneNumber(data.phone),
      role: 'CLIENT',
      birthdate: data.birthdate,
    };

    dispatch(register(registerData));
  };

  const togglePassword = () => setShowPassword((prev) => !prev);
  const hasError = (field: keyof ClientFormData) => !!errors[field]; // Валидация даты рождения (возраст от 16 до 100 лет)

  const validateAge = (value: string) => {
    if (!value) return 'Обязательное поле';
    const birthDate = new Date(value);
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const monthDiff = today.getMonth() - birthDate.getMonth();
    if (
      monthDiff < 0 ||
      (monthDiff === 0 && today.getDate() < birthDate.getDate())
    ) {
      age--;
    }
    if (age < 16) return 'Вам должно быть не менее 16 лет';
    if (age > 100) return 'Некорректная дата рождения';
    return true;
  };

  // Определяем, было ли сообщение об успехе (для отображения плашки в форме)
  // Используем флаг isRegisteredSuccess для гарантии
  const isSuccess = isRegisteredSuccess;
  // Определяем, было ли общее сообщение об ошибке (не связанное с полями)
  const isServerError =
    serverMessage.text && serverMessage.status && serverMessage.status >= 400;

  return (
    <div className='registration-form-wrapper'>
           
      <form
        onSubmit={handleSubmit(onSubmit)}
        className='registration-form client-form'
        noValidate
      >
                <p className='form-title'>Регистрация клиента</p>       
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
                className={`form-input ${
                  hasError('firstName') ? 'input-error' : ''
                }`}
                placeholder='Анна'
                disabled={isLoading || isSuccess} // Блокируем форму после успешной регистрации
                autoCapitalize='none'
                autoComplete='given-name'
                spellCheck={false}
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
                className={`form-input ${
                  hasError('lastName') ? 'input-error' : ''
                }`}
                placeholder='Иванова'
                disabled={isLoading || isSuccess} // Блокируем форму после успешной регистрации
                autoCapitalize='none'
                autoComplete='family-name'
                spellCheck={false}
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
                'Введите корректный номер (+7 и 10 цифр)',
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
                disabled={isLoading || isSuccess} // Блокируем форму после успешной регистрации
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
                placeholder='anna@example.com'
                disabled={isLoading || isSuccess} // Блокируем форму после успешной регистрации
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
                  disabled={isLoading || isSuccess} // Блокируем форму после успешной регистрации
                />
                               
                <button
                  type='button'
                  className='password-toggle'
                  onClick={togglePassword}
                  disabled={isLoading || isSuccess} // Блокируем форму после успешной регистрации
                >
                                    {showPassword ? '🙈' : '🐵'}               
                </button>
                             
              </div>
            )}
          />
                   
          {hasError('password') && (
            <p className='error-message'>{errors.password?.message}</p>
          )}
                 
        </div>
                {/* Дата рождения */}       
        <div className='form-group'>
                    <label className='form-label'>Дата рождения</label>         
          <Controller
            name='birthdate'
            control={control}
            rules={{
              required: 'Укажите дату рождения',
              validate: validateAge,
            }}
            render={({ field }) => (
              <input
                {...field}
                type='date'
                max={new Date().toISOString().split('T')[0]} // не в будущем
                className={`form-input ${
                  hasError('birthdate') ? 'input-error' : ''
                }`}
                disabled={isLoading || isSuccess} // Блокируем форму после успешной регистрации
              />
            )}
          />
                   
          {hasError('birthdate') && (
            <p className='error-message'>{errors.birthdate?.message}</p>
          )}
                 
        </div>
                {/* Сообщение от сервера */}       
        {(isSuccess || isServerError) && ( // Отображаем либо успех, либо общую ошибку
          <div
            className={`response-message ${isSuccess ? 'success' : 'error'}`}
          >
                        {serverMessage.text}         
          </div>
        )}
               
        <button
          type='submit'
          className='form-button'
          // 💡 ИЗМЕНЕНО: Блокируем только при загрузке или успехе
          disabled={!!(isLoading || isSuccess)}
        >
                   
          {isLoading
            ? 'Создаём аккаунт...'
            : isSuccess
            ? '✅ Успех'
            : 'Зарегистрироваться'}
                 
        </button>
             
      </form>
         
    </div>
  );
};

export default ClientRegistrationForm;
