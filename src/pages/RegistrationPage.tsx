// src/pages/Registration/RegistrationPage.tsx

import React, { useState, useEffect, useRef } from 'react'; // useRef может понадобиться, но попробуем без него
import { useNavigate } from 'react-router-dom';

import { useAppSelector } from '../hooks';

import ClientRegistrationForm from '../components/Registration/ClientRegistrationForm';
import MasterRegistrationForm from '../components/Registration/MasterRegistrationForm';
import AdminRegistrationForm from '../components/Registration/AdminRegistrationForm';

const RegistrationPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, isRegisteredSuccess } = useAppSelector(
    (state: { auth: any }) => state.auth
  );

  const [activeForm, setActiveForm] = useState<'client' | 'master' | 'admin'>(
    'client'
  ); // Состояние для управления сообщением об успехе (для рендеринга)
  const [isRegistrationComplete, setIsRegistrationComplete] = useState(false);

  useEffect(() => {
    // 🔑 ИСПРАВЛЕНИЕ: ПРОВЕРКА ДУБЛИРУЕТСЯ ВНУТРИ УСЛОВИЯ
    if (isRegisteredSuccess && !isRegistrationComplete) {
      console.log('--- Условие успеха выполнено. Запускаю таймер. ---');
      // 1. Устанавливаем флаг для отображения сообщения об успехе
      setIsRegistrationComplete(true); // <--- Меняем стейт, который раньше вызывал перезапуск // 2. Запускаем таймер на 3 секунды

      const timer = setTimeout(() => {
        console.log('--- ТАЙМЕР ЗАВЕРШЕН. Пытаюсь перейти на /login ---');
        navigate('/login', { replace: true });
      }, 3000); // Очистка таймера
    }

    // Этот блок запускается, когда isAuthenticated меняется, и не влияет на таймер выше
    if (isAuthenticated) {
      navigate('/profile', { replace: true });
    }
  }, [isRegisteredSuccess, isAuthenticated, navigate]); // УСЛОВНЫЙ РЕНДЕРИНГ: Показываем сообщение об успехе, если регистрация завершена
  // 🔑 КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ: Убрали isRegistrationComplete из зависимостей.
  // Теперь useEffect запустится только при изменении isRegisteredSuccess или isAuthenticated.
  // Изменение isRegistrationComplete внутри не вызовет немедленной очистки таймера.
  // isRegistrationComplete используется внутри if для предотвращения бесконечного цикла,
  // но не является причиной повторного запуска.

  if (isRegistrationComplete) {
    return (
      <div className='registration-container'>
               
        <div className='registration-form success-message'>
                   
          <h2 className='form-title'>✅ Аккаунт успешно зарегистрирован!</h2>   
                <p>Сейчас вы будете перенаправлены на страницу входа...</p>     
           
        </div>
             
      </div>
    );
  }

  return (
    // ... (остальной JSX)
    <div className='registration-container'>
           
      <div className='registration-form'>
                <h2 className='form-title'>Создать аккаунт🏃‍♂️</h2>               
        <div className='role-tabs'>
                   
          <button
            className={`tab-button ${
              activeForm === 'client' ? 'active-tab' : ''
            }`}
            onClick={() => setActiveForm('client')}
          >
                        Клиент          
          </button>
                   
          <button
            className={`tab-button ${
              activeForm === 'master' ? 'active-tab' : ''
            }`}
            onClick={() => setActiveForm('master')}
          >
                        Мастер          
          </button>
                   
          <button
            className={`tab-button ${
              activeForm === 'admin' ? 'active-tab' : ''
            }`}
            onClick={() => setActiveForm('admin')}
          >
                        Администратор          
          </button>
                 
        </div>
                       
        <div>
                    {activeForm === 'client' && <ClientRegistrationForm />}     
              {activeForm === 'master' && <MasterRegistrationForm />}         
          {activeForm === 'admin' && <AdminRegistrationForm />}       
        </div>
               
        <p className='registration-link-note'>
                    Уже есть аккаунт?          
          <span onClick={() => navigate('/login')} className='link-text'>
                        Войти          
          </span>
                 
        </p>
             
      </div>
         
    </div>
  );
};

export default RegistrationPage;
