import { useEffect, useState } from "react";
import { Users, Stethoscope, BookOpen, CalendarDays, CalendarCheck, CalendarX } from "lucide-react";
import { NavbarAdmin } from "../admin/NavBarAdmin";
import { useAuth } from "../../contexts/AuthContext";
import { getUsuarios, getMedicos, getEspecialidades, getCitas } from "../../services/adminService";

function StatCard({ icon, label, value, color }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 flex items-center gap-4">
      <div className={`p-3 rounded-xl ${color}`}>{icon}</div>
      <div>
        <p className="text-sm text-gray-500 font-medium">{label}</p>
        <p className="text-3xl font-bold text-gray-800">{value ?? "—"}</p>
      </div>
    </div>
  );
}

export function DashboardAdmin() {
  const { apiFetch, usuario } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      getUsuarios(apiFetch),
      getMedicos(apiFetch),
      getEspecialidades(apiFetch),
      getCitas(apiFetch),
    ])
      .then(([usuarios, medicos, especialidades, citas]) => {
        const ahora = new Date();
        const citasDisponibles = citas.filter((c) => !c.pacienteNombre && new Date(c.fechaHora) > ahora);
        const citasReservadas = citas.filter((c) => c.pacienteNombre);
        setStats({ usuarios, medicos, especialidades, citas, citasDisponibles, citasReservadas });
      })
      .catch(() => setStats(null))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-6xl mx-auto px-6 py-10">
        <h2 className="text-2xl font-semibold text-gray-800 mb-2">
          Bienvenido, {usuario?.nombre}
        </h2>
        <p className="text-gray-500 mb-8">Resumen general del sistema</p>

        {loading ? (
          <div className="text-center py-20 text-blue-600 font-semibold">Cargando estadísticas...</div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            <StatCard
              icon={<Users size={24} className="text-white" />}
              label="Pacientes registrados"
              value={stats?.usuarios?.length}
              color="bg-blue-600"
            />
            <StatCard
              icon={<Stethoscope size={24} className="text-white" />}
              label="Médicos"
              value={stats?.medicos?.length}
              color="bg-indigo-600"
            />
            <StatCard
              icon={<BookOpen size={24} className="text-white" />}
              label="Especialidades"
              value={stats?.especialidades?.length}
              color="bg-violet-600"
            />
            <StatCard
              icon={<CalendarDays size={24} className="text-white" />}
              label="Total de citas"
              value={stats?.citas?.length}
              color="bg-gray-700"
            />
            <StatCard
              icon={<CalendarCheck size={24} className="text-white" />}
              label="Citas disponibles"
              value={stats?.citasDisponibles?.length}
              color="bg-emerald-600"
            />
            <StatCard
              icon={<CalendarX size={24} className="text-white" />}
              label="Citas reservadas"
              value={stats?.citasReservadas?.length}
              color="bg-orange-500"
            />
          </div>
        )}
      </main>
    </div>
  );
}