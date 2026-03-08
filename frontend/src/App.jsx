import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css'
import { Home } from "./pages/home/Home"
import { Login } from "./pages/home/Login"
import { Especialistas } from './pages/home/Especialistas';
import { DashboardUsuario } from './pages/user/DashboardUsuario';
import { MisCitas } from './pages/user/MisCitas';
import { DashboardAdmin } from './pages/admin/DashboardAdmin';
import { AdminUsuarios } from './pages/admin/AdminUsuarios';
import { AdminMedicos } from './pages/admin/AdminMedicos';
import { AdminEspecialidades } from './pages/admin/AdminEspecialidades';
import { AdminCitas } from './pages/admin/AdminCitas';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { AuthProvider } from './contexts/AuthContext';
import { DashboardMedico } from './pages/medico/DashboardMedico';


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

            {/* ── Medico ──────────────────────────────────── */}
            <Route
              path='/medico'
              element={
                <ProtectedRoute allowedRoles={["MEDICO"]}>
                  <DashboardMedico />
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