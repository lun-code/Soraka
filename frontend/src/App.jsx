import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import { Home } from "./pages/Home"
import { Login } from "./pages/Login"
import { Especialistas } from './pages/Especialistas';
import { DashboardUsuario } from './pages/user/DashboardUsuario';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { AuthProvider } from './contexts/AuthContext';

function App() {

  return (
    <BrowserRouter>
      <AuthProvider>
        <div className='min-h-screen bg-blue-gray-50'>
          <Routes>

            <Route path="/" element={<Home />} />

            <Route path="/login" element={<Login />} />

            <Route path="/especialistas" element={<Especialistas />} />
            
            <Route 
              path='/dashboard'
              element={
                <ProtectedRoute allowedRoles={["PACIENTE"]}>
                  <DashboardUsuario />
                </ProtectedRoute>
              }
            />
          </Routes>
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App