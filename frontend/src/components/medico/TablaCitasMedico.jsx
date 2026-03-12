import { useState } from "react";
import { ModalDetalleCita } from "./ModalDetalleCita";

const CITAS_POR_PAGINA = 8;

const coloresEstado = {
  DISPONIBLE: "bg-green-100 text-green-700",
  CONFIRMADA: "bg-orange-100 text-orange-700",
  REALIZADA:  "bg-blue-100 text-blue-700",
  CADUCADA:   "bg-red-100 text-red-500",
};

function formatFecha(fechaHora) {
  if (!fechaHora) return "—";
  return new Date(fechaHora).toLocaleString("es-ES", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

export function TablaCitasMedico({ citas, loading }) {
  const [pagina, setPagina] = useState(1);
  const [citaSeleccionada, setCitaSeleccionada] = useState(null);

  const totalPaginas = Math.ceil(citas.length / CITAS_POR_PAGINA);
  const citasPagina = citas.slice((pagina - 1) * CITAS_POR_PAGINA, pagina * CITAS_POR_PAGINA);

  if (loading) return <div className="text-center py-10 text-blue-600 font-semibold">Cargando citas...</div>;
  if (citas.length === 0) return <div className="text-center py-10 text-gray-400">No tienes citas próximas.</div>;

  return (
    <>
      {/* ── Tabla (md+) ──────────────────────────────────────────────────────── */}
      <div className="hidden md:block overflow-x-auto">
        <table className="w-full border-separate border-spacing-y-3">
          <thead>
            <tr className="text-left text-sm uppercase tracking-wide text-white bg-gray-800">
              <th className="px-6 py-3 rounded-l-xl">Fecha y hora</th>
              <th className="px-6 py-3">Paciente</th>
              <th className="px-6 py-3">Motivo</th>
              <th className="px-6 py-3 text-center">Estado</th>
              <th className="px-6 py-3 rounded-r-xl text-center">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {citasPagina.map((cita) => (
              <tr key={cita.id} className="bg-gray-50 text-sm hover:bg-blue-50 transition-colors">
                <td className="px-6 py-3 rounded-l-xl text-gray-700 font-medium">{formatFecha(cita.fechaHora)}</td>
                <td className="px-6 py-3 text-gray-700">{cita.pacienteNombre ?? <span className="text-gray-400 italic">Sin asignar</span>}</td>
                <td className="px-6 py-3 text-gray-500 max-w-xs truncate">{cita.motivo ?? "—"}</td>
                <td className="px-6 py-3 text-center">
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold ${coloresEstado[cita.estado] ?? "bg-gray-100 text-gray-600"}`}>
                    {cita.estado}
                  </span>
                </td>
                <td className="px-6 py-3 rounded-r-xl text-center">
                  <button
                    onClick={() => setCitaSeleccionada(cita)}
                    className="px-3 py-1.5 rounded-lg border border-blue-600 text-blue-600 text-xs font-medium hover:bg-blue-600 hover:text-white transition"
                  >
                    Ver detalle
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* ── Tarjetas (móvil) ─────────────────────────────────────────────────── */}
      <div className="flex flex-col gap-3 md:hidden">
        {citasPagina.map((cita) => (
          <div key={cita.id} className="bg-gray-50 rounded-xl p-4 border border-gray-100 flex flex-col gap-2">
            <div className="flex items-center justify-between gap-2">
              <p className="text-sm font-semibold text-gray-800">{formatFecha(cita.fechaHora)}</p>
              <span className={`px-2 py-0.5 rounded-full text-xs font-semibold shrink-0 ${coloresEstado[cita.estado] ?? "bg-gray-100 text-gray-600"}`}>
                {cita.estado}
              </span>
            </div>
            <p className="text-xs text-gray-500">
              <span className="font-medium text-gray-700">
                {cita.pacienteNombre ?? <span className="italic text-gray-400">Sin asignar</span>}
              </span>
              {cita.motivo ? ` · ${cita.motivo}` : ""}
            </p>
            <div className="flex justify-end pt-1">
              <button
                onClick={() => setCitaSeleccionada(cita)}
                className="px-3 py-1.5 rounded-lg border border-blue-600 text-blue-600 text-xs font-medium hover:bg-blue-600 hover:text-white transition"
              >
                Ver detalle
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* ── Paginación ───────────────────────────────────────────────────────── */}
      {totalPaginas > 1 && (
        <div className="flex items-center justify-between mt-6">
          <p className="text-sm text-gray-500">Página {pagina} de {totalPaginas}</p>
          <div className="flex gap-2">
            <button onClick={() => setPagina((p) => p - 1)} disabled={pagina === 1}
              className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed">
              Anterior
            </button>
            <button onClick={() => setPagina((p) => p + 1)} disabled={pagina === totalPaginas}
              className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed">
              Siguiente
            </button>
          </div>
        </div>
      )}

      <ModalDetalleCita cita={citaSeleccionada} onClose={() => setCitaSeleccionada(null)} />
    </>
  );
}