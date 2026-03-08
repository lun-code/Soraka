import { useEffect, useState } from "react";
import { NavbarMedico } from "../../components/medico/NavbarMedico";
import { useAuth } from "../../contexts/AuthContext";
import { getMisCitasMedico } from "../../services/citaService";

const CITAS_POR_PAGINA = 8;

function formatFecha(fechaHora) {
  if (!fechaHora) return "—";
  const d = new Date(fechaHora);
  return d.toLocaleString("es-ES", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

export function DashboardMedico() {
  const { apiFetch, usuario } = useAuth();
  const [citas, setCitas] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pagina, setPagina] = useState(1);

  useEffect(() => {
    getMisCitasMedico(apiFetch)
      .then((data) => {
        // Ordenar por fecha ascendente
        const ordenadas = data.sort((a, b) => new Date(a.fechaHora) - new Date(b.fechaHora));
        setCitas(ordenadas);
      })
      .catch(() => setCitas([]))
      .finally(() => setLoading(false));
  }, []);

  const totalPaginas = Math.ceil(citas.length / CITAS_POR_PAGINA);
  const citasPagina = citas.slice((pagina - 1) * CITAS_POR_PAGINA, pagina * CITAS_POR_PAGINA);

  const coloresEstado = {
    DISPONIBLE: "bg-emerald-100 text-emerald-700",
    CONFIRMADA: "bg-orange-100 text-orange-700",
  };

  return (
    <div>
      <NavbarMedico />
      <main className="max-w-5xl mx-auto px-6 py-10">
        <h2 className="text-2xl font-semibold text-gray-800 mb-1">
          Hola, {usuario?.nombre}
        </h2>
        <p className="text-gray-500 text-sm mb-8">
          Estas son tus próximas citas
        </p>

        <section className="bg-white rounded-2xl shadow-lg p-8">
          <h3 className="text-xl font-semibold text-gray-800 mb-6">Mis citas</h3>

          {loading ? (
            <div className="text-center py-10 text-blue-600 font-semibold">
              Cargando citas...
            </div>
          ) : citas.length === 0 ? (
            <div className="text-center py-10 text-gray-400">
              No tienes citas próximas.
            </div>
          ) : (
            <>
              <div className="overflow-x-auto">
                <table className="w-full border-separate border-spacing-y-3">
                  <thead>
                    <tr className="text-left text-sm uppercase tracking-wide text-white bg-gray-800">
                      <th className="px-6 py-3 rounded-l-xl">Fecha y hora</th>
                      <th className="px-6 py-3">Paciente</th>
                      <th className="px-6 py-3">Motivo</th>
                      <th className="px-6 py-3 rounded-r-xl text-center">Estado</th>
                    </tr>
                  </thead>
                  <tbody>
                    {citasPagina.map((cita) => (
                      <tr
                        key={cita.id}
                        className="bg-gray-50 text-sm hover:bg-blue-50 transition-colors"
                      >
                        <td className="px-6 py-3 rounded-l-xl text-gray-700 font-medium">
                          {formatFecha(cita.fechaHora)}
                        </td>
                        <td className="px-6 py-3 text-gray-700">
                          {cita.pacienteNombre ?? <span className="text-gray-400 italic">Sin asignar</span>}
                        </td>
                        <td className="px-6 py-3 text-gray-500">
                          {cita.motivo ?? "—"}
                        </td>
                        <td className="px-6 py-3 rounded-r-xl text-center">
                          <span className={`px-3 py-1 rounded-full text-xs font-semibold ${coloresEstado[cita.estado] ?? ""}`}>
                            {cita.estado}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Paginación */}
              {totalPaginas > 1 && (
                <div className="flex items-center justify-between mt-4 text-sm text-gray-600">
                  <span>Página {pagina} de {totalPaginas} ({citas.length} citas)</span>
                  <div className="flex gap-2">
                    <button
                      onClick={() => setPagina((p) => Math.max(1, p - 1))}
                      disabled={pagina === 1}
                      className="px-3 py-1 rounded hover:bg-gray-100 disabled:opacity-40 transition"
                    >
                      ←
                    </button>
                    <button
                      onClick={() => setPagina((p) => Math.min(totalPaginas, p + 1))}
                      disabled={pagina === totalPaginas}
                      className="px-3 py-1 rounded hover:bg-gray-100 disabled:opacity-40 transition"
                    >
                      →
                    </button>
                  </div>
                </div>
              )}
            </>
          )}
        </section>
      </main>
    </div>
  );
}