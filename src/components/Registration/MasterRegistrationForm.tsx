import React, { useState, useEffect, useCallback } from 'react';
import { useForm, Controller } from 'react-hook-form';

import {
  emailValidation,
  requiredValidation,
  passwordValidation,
  maxLengthValidation,
  PERMISSIVE_NAME_REGEX,
  PASSWORD_REGEX,
} from '../../utils/validationRules';

import {
  formatPhoneNumber,
  parsePhoneNumber,
  isValidPhoneNumber,
} from '../../utils/phoneFormatter';

import type { ApiResponse, MasterData } from '../../types/userTypes';

// Типы данных формы
interface MasterFormData {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone: string;
  specializations: string; // ИЗМЕНЕНО: теперь это одна строка для Select
  description: string;
  workExperience: number; // Опыт работы в годах
}

// Фиксированный список специализаций для мастера
const MASTER_SPECIALIZATIONS = ['Ногти', 'Брови/ресницы', 'Волосы'];

interface FormProps {
  onRegister: (data: MasterData) => Promise<ApiResponse>;
  isLoading: boolean;
  serverMessage: { text: string; status: number | null };
}

const MasterRegistrationForm: React.FC<FormProps> = ({
  onRegister,
  isLoading,
  serverMessage,
}) => {
  const [responseMessage, setResponseMessage] = useState<{
    type: 'success' | 'error' | 'loading' | null;
    message: string;
  }>({ type: null, message: '' });

  const {
    handleSubmit,
    control,
    setValue,
    watch,
    formState: { errors, isSubmitted, isValid },
  } = useForm<MasterFormData>({
    defaultValues: {
      email: '',
      password: '',
      firstName: '',
      lastName: '',
      phone: '+7',
      specializations: '', // Дефолтное значение для Select (пустая строка соответствует disabled option)
      description: '',
      workExperience: 0,
    },
    mode: 'onSubmit',
  });

  const phoneValue = watch('phone'); // Эффект для форматирования телефона

  useEffect(() => {
    if (phoneValue && phoneValue.length > 0) {
      const formatted = formatPhoneNumber(phoneValue);
      if (formatted !== phoneValue) {
        setValue('phone', formatted, { shouldValidate: true });
      }
    } else if (phoneValue === '') {
      setValue('phone', '+7', { shouldValidate: true });
    }
  }, [phoneValue, setValue]); // Эффект для отображения серверного сообщения

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
    async (data: MasterFormData) => {
      setResponseMessage({
        type: 'loading',
        message: 'Регистрация мастера...',
      });

      const phoneParsed = parsePhoneNumber(data.phone);

      const fullData: MasterData = {
        role: 'MASTER',
        email: data.email,
        password: data.password,
        firstName: data.firstName,
        lastName: data.lastName,
        phone: `+${phoneParsed}`, // ИЗМЕНЕНИЕ ЗДЕСЬ: Специализация теперь чистая строка
        specialization: data.specializations, // БЫЛО: [data.specializations]
        description: data.description,
        workExperience: data.workExperience,
      };

      console.log('Данные для отправки MASTER:', fullData);

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
  );

  return (
    <div className='registration-container'>
      <form
        onSubmit={handleSubmit(onSubmit)}
        className='registration-form'
        noValidate
      >
                <p className='form-title'>Регистрация Мастера</p>       
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
                  errors.firstName && isSubmitted ? 'input-error' : ''
                }`}
              />
            )}
          />
                   
          {errors.firstName && isSubmitted && (
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
                className={`form-input ${
                  errors.lastName && isSubmitted ? 'input-error' : ''
                }`}
              />
            )}
          />
                   
          {errors.lastName && isSubmitted && (
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
            rules={{
              ...requiredValidation('Телефон')(),
              validate: (value: string) => {
                if (!isValidPhoneNumber(value)) {
                  return 'Пожалуйста, введите полный российский номер телефона.';
                }
                return true;
              },
            }}
            render={({ field }) => (
              <input
                {...field} // ИСПРАВЛЕНО: Кастомный onChange для форматирования должен быть здесь
                onChange={(e) => field.onChange(e.target.value)}
                type='tel' // ИСПРАВЛЕНО: maxLength={22} должен быть внутри JSX-тега
                maxLength={22}
                className={`form-input ${
                  errors.phone && isSubmitted ? 'input-error' : ''
                }`}
                placeholder='+7 (___) - ___ - __ - __'
              />
            )}
          />
                   
          {errors.phone && isSubmitted && (
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
                className={`form-input ${
                  errors.email && isSubmitted ? 'input-error' : ''
                }`}
                placeholder='example@mail.ru'
              />
            )}
          />
                   
          {errors.email && isSubmitted && (
            <p className='error-message'>{errors.email.message}</p>
          )}
                 
        </div>
                {/* Пароль */}       
        <div className='form-group'>
                   
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
                  'Пароль: мин. 8 символов, 1 заглавная, 1 цифра, 1 спецсимвол (@$!%*?&).',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                type='password'
                className={`form-input ${
                  errors.password && isSubmitted ? 'input-error' : ''
                }`}
                autoComplete='new-password'
              />
            )}
          />
                   
          {errors.password && isSubmitted && (
            <p className='error-message'>{errors.password.message}</p>
          )}
                 
        </div>
                {/* Специализация (Выпадающий список) */}       
        <div className='form-group'>
                   
          <label htmlFor='specializations' className='form-label'>
                        Специализация          
          </label>
                   
          <Controller
            name='specializations'
            control={control}
            rules={requiredValidation('Специализация')()}
            render={({ field }) => (
              <select
                {...field}
                className={`form-input ${
                  errors.specializations && isSubmitted ? 'input-error' : ''
                }`}
              >
                               
                <option value='' disabled>
                                    Выберите специализацию                
                </option>
                               
                {MASTER_SPECIALIZATIONS.map((option) => (
                  <option key={option} value={option}>
                                        {option}                 
                  </option>
                ))}
                             
              </select>
            )}
          />
                   
          {errors.specializations && isSubmitted && (
            <p className='error-message'>{errors.specializations.message}</p>
          )}
                 
        </div>
        {/* Опыт работы (Work Experience) */}
        <div className='form-group'>
                   
          <label htmlFor='workExperience' className='form-label'>
                        Опыт работы (лет)          
          </label>
                   
          <Controller
            name='workExperience'
            control={control}
            rules={{
              ...requiredValidation('Опыт работы')(),
              min: {
                value: 0,
                message: 'Опыт работы не может быть отрицательным.',
              },
              // ИСПРАВЛЕНО: Добавлено максимальное ограничение (чтобы избежать 445)
              max: {
                value: 60,
                message: 'Опыт работы не может превышать 60 лет.',
              },
              pattern: {
                value: /^\d+$/,
                message: 'Опыт работы должен быть целым числом.',
              },
            }}
            render={({ field }) => (
              <input
                {...field}
                // Дополнительная логика для корректного отображения и парсинга number:
                // Устанавливаем 0, если поле пусто, и отображаем пусто, если 0 (для required валидации)
                onChange={(e) =>
                  field.onChange(
                    e.target.value ? parseInt(e.target.value, 10) : 0
                  )
                }
                value={field.value === 0 ? '' : field.value}
                type='number'
                inputMode='numeric'
                className={`form-input ${
                  errors.workExperience && isSubmitted ? 'input-error' : ''
                }`}
              />
            )}
          />
                   
          {errors.workExperience && isSubmitted && (
            <p className='error-message'>{errors.workExperience.message}</p>
          )}
                 
        </div>
                {/* Описание */}       
        <div className='form-group'>
                   
          <label htmlFor='description' className='form-label'>
                        О себе / Опыт работы          
          </label>
                   
          <Controller
            name='description'
            control={control}
            rules={{
              ...requiredValidation('О себе / Опыт работы')(),
              ...maxLengthValidation(500)('О себе / Опыт работы'),
            }}
            render={({ field }) => (
              <textarea
                {...field}
                className={`form-textarea ${
                  errors.description && isSubmitted ? 'input-error' : ''
                }`}
                rows={4}
              ></textarea>
            )}
          />
                   
          {errors.description && isSubmitted && (
            <p className='error-message'>{errors.description.message}</p>
          )}
                 
        </div>
               
        {responseMessage.type && (
          <div className={`response-message ${responseMessage.type}`}>
                        {responseMessage.message}         
          </div>
        )}
               
        <button
          type='submit'
          className={`form-button ${isSubmitted && !isValid ? 'error' : ''}`}
          disabled={isLoading || (isSubmitted && !isValid)}
        >
                   
          {isLoading
            ? 'Регистрация...'
            : isSubmitted && !isValid
            ? 'Ошибка валидации'
            : 'Зарегистрироваться'}
                 
        </button>
             
      </form>
         
    </div>
  );
};

export default MasterRegistrationForm;
