import { useAuth } from "../../contexts/AuthContext";
import { useCitasDisponibles } from "../../hooks/user/useCitasDisponibles";

export function TablaCitasDisponibles({ especialidad }) {
  const { apiFetch } = useAuth();
  const {
    citasPagina, totalPaginas, paginaActual, modal, motivo, errorMotivo, reservando,
    setPaginaActual, setMotivo, setErrorMotivo,
    abrirModal, cerrarModal, handleReservar,
  } = useCitasDisponibles(apiFetch, especialidad);

  return (
    <>
      <section className="bg-white rounded-2xl shadow-lg p-4 md:p-8">
        <h3 className="text-xl font-semibold text-gray-800 mb-6">Citas disponibles</h3>
        
        <div className="w-full">
          {/* Tabla responsiva */}
          <table className="w-full border-separate border-spacing-y-3">
            {/* Ocultamos el header en móvil */}
            <thead className="hidden md:table-header-group">
              <tr className="text-left text-sm uppercase tracking-wide text-white bg-gray-800">
                <th className="px-6 py-3 rounded-l-xl">Médico</th>
                <th className="px-6 py-3">Fecha</th>
                <th className="px-6 py-3">Hora</th>
                <th className="px-6 py-3 rounded-r-xl text-center">Acción</th>
              </tr>
            </thead>
            <tbody className="block md:table-row-group">
              {citasPagina.map((cita) => (
                <tr 
                  key={cita.id} 
                  className="bg-gray-50 text-sm text-gray-700 hover:bg-gray-100 transition rounded-xl block md:table-row border md:border-none mb-4 md:mb-0 shadow-sm md:shadow-none"
                >
                  {/* Médico */}
                  <td className="px-6 py-3 md:py-4 md:rounded-l-xl font-medium block md:table-cell border-b md:border-none">
                    <span className="md:hidden font-bold text-gray-500 uppercase text-[10px] block">Médico</span>
                    {cita.medicoNombre}
                  </td>
                  
                  {/* Fecha */}
                  <td className="px-6 py-3 md:py-4 block md:table-cell border-b md:border-none">
                    <span className="md:hidden font-bold text-gray-500 uppercase text-[10px] block">Fecha</span>
                    {new Date(cita.fechaHora).toLocaleDateString("es-ES")}
                  </td>
                  
                  {/* Hora */}
                  <td className="px-6 py-3 md:py-4 block md:table-cell border-b md:border-none">
                    <span className="md:hidden font-bold text-gray-500 uppercase text-[10px] block">Hora</span>
                    {new Date(cita.fechaHora).toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" })}
                  </td>
                  
                  {/* Acción */}
                  <td className="px-6 py-4 md:rounded-r-xl text-center block md:table-cell">
                    <button 
                      onClick={() => abrirModal(cita.id)} 
                      className="w-full md:w-auto px-4 py-2 rounded-lg border text-blue-600 font-medium hover:bg-blue-600 hover:text-white transition"
                    >
                      Reservar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Paginación (ajustada para móvil) */}
        {totalPaginas > 1 && (
          <div className="flex flex-col md:flex-row items-center justify-between mt-6 gap-4">
            <p className="text-sm text-gray-500 order-2 md:order-1">
              Página {paginaActual} de {totalPaginas}
            </p>
            <div className="flex gap-2 w-full md:w-auto order-1 md:order-2">
              <button onClick={() => setPaginaActual((p) => p - 1)} disabled={paginaActual === 1}
                className="flex-1 md:flex-none px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40">
                Anterior
              </button>
              <button onClick={() => setPaginaActual((p) => p + 1)} disabled={paginaActual === totalPaginas}
                className="flex-1 md:flex-none px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40">
                Siguiente
              </button>
            </div>
          </div>
        )}
      </section>

      {/* Modal - Se mantiene igual pero asegurando p-6 o p-8 para móvil */}
      {modal && (
        <div className="fixed inset-0 bg-black/40 flex items-end md:items-center justify-center z-50 p-0 md:p-4">
          <div className="bg-white rounded-t-2xl md:rounded-2xl shadow-xl p-6 md:p-8 w-full max-w-md animate-in slide-in-from-bottom duration-300">
            <h4 className="text-lg font-semibold text-gray-800 mb-2">Reservar cita</h4>
            <p className="text-sm text-gray-500 mb-4">Indica el motivo de tu consulta para confirmar la reserva.</p>
            <textarea
              value={motivo}
              onChange={(e) => { setMotivo(e.target.value); if (e.target.value.trim()) setErrorMotivo(""); }}
              placeholder="Ej: Dolor de cabeza frecuente..."
              rows={4}
              maxLength={255}
              className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm text-gray-700 resize-none focus:ring-2 focus:ring-blue-500"
            />
            {/* ... resto del modal ... */}
            <div className="flex flex-col md:flex-row justify-end gap-3 mt-6">
              <button onClick={cerrarModal} className="order-2 md:order-1 px-4 py-2 rounded-lg border text-gray-600 font-medium">Cancelar</button>
              <button onClick={handleReservar} disabled={reservando === modal.citaId}
                className="order-1 md:order-2 px-4 py-2 rounded-lg bg-blue-600 text-white font-medium hover:bg-blue-700">
                {reservando === modal.citaId ? "Reservando..." : "Confirmar"}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}