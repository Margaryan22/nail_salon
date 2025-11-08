import './scss/app.scss';
import Registration from './pages/RegistrartionPage';
import Login from './pages/LoginPage';
import { Route, Routes } from 'react-router-dom';
import UserPage from './pages/UserPage';
import ServiceCatalogPage from './pages/ServiceCatalogPage';
function App() {
  return (
    <>
      <div className='app-container'>
        <Routes>
          <Route path='/registration' element={<Registration />} />
          <Route path='/login' element={<Login />} />
          <Route path='/user_page' element={<UserPage />} />
          <Route path='/services' element={<ServiceCatalogPage />} />
        </Routes>
      </div>
    </>
  );
}

export default App;
