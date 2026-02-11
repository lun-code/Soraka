import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import { Home } from "./pages/Home"
import { Login } from "./pages/Login"
import { Especialistas } from './pages/Especialistas';
import { DashboardUsuario } from './pages/user/DashboardUsuario';

function App() {

  return (
    <BrowserRouter>
      <div className='min-h-screen bg-blue-gray-50'>
        <Routes>

          <Route path="/" element={<Home />} />

          <Route path="/login" element={<Login />} />

          <Route path="/especialistas" element={<Especialistas />} />
          
          <Route path='/DashboardUsuario' element={<DashboardUsuario />} />
          
        </Routes>
      </div>
    </BrowserRouter>
  );
}

export default App