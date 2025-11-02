import './scss/app.scss';
import Registration from './pages/Registrartion';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
function App() {
  return (
    <>
      <div className='app-container'>
        <Routes>
          <Route path='/registration' element={<Registration />} />
        </Routes>
      </div>
    </>
  );
}

export default App;
