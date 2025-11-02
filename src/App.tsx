import './scss/app.scss';
import Registration from './pages/Registrartion';
import Login from './pages/Login';
import { Route, Routes } from 'react-router-dom';
function App() {
  return (
    <>
      <div className='app-container'>
        <Routes>
          <Route path='/registration' element={<Registration />} />
          <Route path='/login' element={<Login />} />
        </Routes>
      </div>
    </>
  );
}

export default App;
