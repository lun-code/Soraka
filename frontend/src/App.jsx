import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import { Home } from "./pages/Home"
import { Login } from "./pages/Login"
import { Especialistas } from './pages/Especialistas';
import { DashboardUsuario } from './pages/user/DashboardUsuario';
import { MisCitas } from './pages/user/MisCitas';
import { DashboardAdmin } from './components/admin/DashboardAdmin';
import { AdminUsuarios } from './components/admin/AdminUsuarios';
import { AdminMedicos } from './components/admin/AdminMedicos';
import { AdminEspecialidades } from './components/admin/AdminEspecialidades';
import { AdminCitas } from './components/admin/AdminCitas';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { AuthProvider } from './contexts/AuthContext';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className='min-h-screen bg-blue-gray-50'>
          <Routes>

            {/* ── Públicas ───────────────────────────────── */}
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/especialistas" element={<Especialistas />} />

            {/* ── Paciente ───────────────────────────────── */}
            <Route
              path='/dashboard'
              element={
                <ProtectedRoute allowedRoles={["PACIENTE"]}>
                  <DashboardUsuario />
                </ProtectedRoute>
              }
            />
            <Route
              path='/mis-citas'
              element={
                <ProtectedRoute allowedRoles={["PACIENTE"]}>
                  <MisCitas />
                </ProtectedRoute>
              }
            />

            {/* ── Admin ──────────────────────────────────── */}
            <Route
              path='/admin'
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <DashboardAdmin />
                </ProtectedRoute>
              }
            />
            <Route
              path='/admin/usuarios'
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <AdminUsuarios />
                </ProtectedRoute>
              }
            />
            <Route
              path='/admin/medicos'
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <AdminMedicos />
                </ProtectedRoute>
              }
            />
            <Route
              path='/admin/especialidades'
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <AdminEspecialidades />
                </ProtectedRoute>
              }
            />
            <Route
              path='/admin/citas'
              element={
                <ProtectedRoute allowedRoles={["ADMIN"]}>
                  <AdminCitas />
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