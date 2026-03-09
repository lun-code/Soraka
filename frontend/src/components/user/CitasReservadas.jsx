import { useAuth } from "../../contexts/AuthContext";
import { useCitasReservadas } from "../../hooks/user/useCitasReservadas";

export function CitasReservadas() {
  const { apiFetch } = useAuth();
  const {
    citasPagina, totalPaginas, paginaActual, modal,
    setPaginaActual, abrirModal, cerrarModal, handleCancelar,
  } = useCitasReservadas(apiFetch);

  return (
    <>
      <section className="bg-white rounded-2xl shadow-lg p-8">
        <h3 className="text-xl font-semibold text-gray-800 mb-6">Mis citas</h3>
        <div className="overflow-x-auto">
          <table className="w-full border-separate border-spacing-y-3">
            <thead>
              <tr className="text-left text-sm uppercase tracking-wide text-white bg-gray-800">
                <th className="px-6 py-3 rounded-l-xl">Médico</th>
                <th className="px-6 py-3">Fecha</th>
                <th className="px-6 py-3">Hora</th>
                <th className="px-6 py-3 rounded-r-xl text-center">Acción</th>
              </tr>
            </thead>
            <tbody>
              {citasPagina.map((cita) => (
                <tr key={cita.id} className="bg-gray-50 text-sm text-gray-700 hover:bg-gray-200 transition rounded-xl">
                  <td className="px-6 py-4 rounded-l-xl font-medium">{cita.medicoNombre}</td>
                  <td className="px-6 py-4">{new Date(cita.fechaHora).toLocaleDateString("es-ES")}</td>
                  <td className="px-6 py-4">{new Date(cita.fechaHora).toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" })}</td>
                  <td className="px-6 py-4 rounded-r-xl text-center">
                    <button onClick={() => abrirModal(cita.id)} className="px-4 py-2 rounded-lg border text-red-500 font-medium hover:bg-red-500 hover:text-white transition">
                      Cancelar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {totalPaginas > 1 && (
          <div className="flex items-center justify-between mt-6">
            <p className="text-sm text-gray-500">Página {paginaActual} de {totalPaginas}</p>
            <div className="flex gap-2">
              <button onClick={() => setPaginaActual((p) => p - 1)} disabled={paginaActual === 1}
                className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed">
                Anterior
              </button>
              <button onClick={() => setPaginaActual((p) => p + 1)} disabled={paginaActual === totalPaginas}
                className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed">
                Siguiente
              </button>
            </div>
          </div>
        )}
      </section>

      {modal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-sm text-center">
            <h4 className="text-lg font-semibold text-gray-800 mb-2">¿Cancelar cita?</h4>
            <p className="text-sm text-gray-500 mb-6">Esta acción no se puede deshacer.</p>
            <div className="flex justify-center gap-3">
              <button onClick={cerrarModal} className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 text-sm hover:bg-gray-50 transition">Volver</button>
              <button onClick={() => handleCancelar(modal)} className="px-4 py-2 rounded-lg bg-red-500 text-white text-sm font-semibold hover:bg-red-600 transition">Cancelar cita</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}