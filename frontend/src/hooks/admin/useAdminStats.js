import { useState, useEffect } from "react";
import { getUsuarios, getMedicos, getEspecialidades, getCitas } from "../../services/adminService";

export function useAdminStats(apiFetch) {
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
        setStats({
          usuarios: usuarios.filter(u => u.rol === "PACIENTE"),
          medicos,
          especialidades,
          citas,
          citasDisponibles,
          citasReservadas
        });
      })
      .catch(() => setStats(null))
      .finally(() => setLoading(false));
  }, []);

  return { stats, loading };
}