import { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthContext";
import { crearApiFetch } from "../../services/api";
import { getCitasDisponibles, reservarCita } from "../../services/citaService";


const CITAS_POR_PAGINA = 5;

export function TablaCitasDisponibles({ especialidad }) {

  const { logout } = useAuth();
  const apiFetch = crearApiFetch(logout);

  const [citas, setCitas] = useState([]);
  const [paginaActual, setPaginaActual] = useState(1);
  const [reservando, setReservando] = useState(null);
  const [modal, setModal] = useState(null); // { citaId } o null
  const [motivo, setMotivo] = useState("");
  const [errorMotivo, setErrorMotivo] = useState("");

  useEffect(() => {
    if (!especialidad) return;
    getCitasDisponibles(apiFetch)
      .then((data) => {
        setCitas(data.filter((c) => c.medicoEspecialidad === especialidad));
        setPaginaActual(1);
      })
      .catch(() => console.error("Error cargando citas."));
  }, [especialidad]);


  const abrirModal = (citaId) => {
    setMotivo("");
    setErrorMotivo("");
    setModal({ citaId });
  };

  const cerrarModal = () => {
    setModal(null);
    setMotivo("");
    setErrorMotivo("");
  };

    const handleReservar = async () => {
    if (!motivo.trim()) {
      setErrorMotivo("El motivo es obligatorio.");
      return;
    }

    setReservando(modal.citaId);

    try {
      await reservarCita(apiFetch, modal.citaId, motivo);

      setCitas((prev) => prev.filter((c) => c.id !== modal.citaId));
      const citasRestantes = citas.filter((c) => c.id !== modal.citaId);
      const nuevasPaginas = Math.ceil(citasRestantes.length / CITAS_POR_PAGINA);
      if (paginaActual > nuevasPaginas && paginaActual > 1) {
        setPaginaActual((p) => p - 1);
      }
      cerrarModal();
    } catch (err) {
      console.error("No se pudo reservar la cita:", err);
      alert("Hubo un error al reservar la cita. Inténtalo de nuevo.");
    } finally {
      setReservando(null);
    }
  };


  const totalPaginas = Math.ceil(citas.length / CITAS_POR_PAGINA);
  const citasPagina = citas.slice(
    (paginaActual - 1) * CITAS_POR_PAGINA,
    paginaActual * CITAS_POR_PAGINA
  );

  return (
    <>
      <section className="bg-white rounded-2xl shadow-lg p-8">
        <h3 className="text-xl font-semibold text-gray-800 mb-6">
          Citas disponibles
        </h3>

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
                <tr
                  key={cita.id}
                  className="bg-gray-50 text-sm text-gray-700 hover:bg-gray-200 transition rounded-xl"
                >
                  <td className="px-6 py-4 rounded-l-xl font-medium">
                    {cita.medicoNombre}
                  </td>
                  <td className="px-6 py-4">
                    {new Date(cita.fechaHora).toLocaleDateString("es-ES")}
                  </td>
                  <td className="px-6 py-4">
                    {new Date(cita.fechaHora).toLocaleTimeString("es-ES", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </td>
                  <td className="px-6 py-4 rounded-r-xl text-center flex gap-2 justify-center">
                    <button
                      onClick={() => abrirModal(cita.id)}
                      className="px-4 py-2 rounded-lg border text-blue-600 font-medium hover:bg-blue-600 hover:text-white"
                    >
                      Reservar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {totalPaginas > 1 && (
          <div className="flex items-center justify-between mt-6">
            <p className="text-sm text-gray-500">
              Página {paginaActual} de {totalPaginas}
            </p>
            <div className="flex gap-2">
              <button
                onClick={() => setPaginaActual((p) => p - 1)}
                disabled={paginaActual === 1}
                className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
              >
                Anterior
              </button>
              <button
                onClick={() => setPaginaActual((p) => p + 1)}
                disabled={paginaActual === totalPaginas}
                className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
              >
                Siguiente
              </button>
            </div>
          </div>
        )}
      </section>

      {/* Modal */}
      {modal && (
        <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
            <h4 className="text-lg font-semibold text-gray-800 mb-2">
              Reservar cita
            </h4>
            <p className="text-sm text-gray-500 mb-4">
              Indica el motivo de tu consulta para confirmar la reserva.
            </p>

            <textarea
              value={motivo}
              onChange={(e) => {
                setMotivo(e.target.value);
                if (e.target.value.trim()) setErrorMotivo("");
              }}
              placeholder="Ej: Dolor de cabeza frecuente..."
              rows={4}
              className="w-full border border-gray-500 rounded-lg px-4 py-3 text-sm text-gray-700 resize-none focus:outline-none focus:ring-2 focus:ring-blue-500 [border-style:solid]"
            />
            {errorMotivo && (
              <p className="text-red-500 text-xs mt-1">{errorMotivo}</p>
            )}

            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={cerrarModal}
                className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100"
              >
                Cancelar
              </button>
              <button
                onClick={handleReservar}
                disabled={reservando === modal.citaId}
                className="px-4 py-2 rounded-lg bg-blue-600 text-white font-medium hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {reservando === modal.citaId ? "Reservando..." : "Confirmar"}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}