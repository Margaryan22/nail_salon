// src/pages/Registration/RegistrationPage.tsx

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

import { useAppSelector } from '../hooks/index';

import ClientRegistrationForm from '../components/Registration/ClientRegistrationForm';
import MasterRegistrationForm from '../components/Registration/MasterRegistrationForm';
import AdminRegistrationForm from '../components/Registration/AdminRegistrationForm';

const RegistrationPage: React.FC = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAppSelector(
    (state: { auth: any }) => state.auth
  );

  // Локальное состояние для активной вкладки
  const [activeForm, setActiveForm] = useState<'client' | 'master' | 'admin'>(
    'client'
  );

  return (
    <div className='registration-container'>
      {' '}
      {/* ← одна карточка, как у логина */}
      <div className='registration-form'>
        {' '}
        {/* ← белая карточка из твоего SCSS */}
        <h2 className='form-title'>Создать аккаунт🏃‍♂️</h2>
        {/* Вкладки — используем твои классы из SCSS */}
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
        {/* Формы — переключаются локально */}
        <div>
          {activeForm === 'client' && <ClientRegistrationForm />}
          {activeForm === 'master' && <MasterRegistrationForm />}
          {activeForm === 'admin' && <AdminRegistrationForm />}
        </div>
      </div>
    </div>
  );
};

export default RegistrationPage;
