import { useState, useEffect } from "react";

const CITAS_POR_PAGINA = 5;

export function CitasReservadas() {
  const [citas, setCitas] = useState([]);
  const [paginaActual, setPaginaActual] = useState(1);
  const [modal, setModal] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch("http://localhost:8080/api/citas/mis-citas", {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => res.json())
      .then((data) => {
        setCitas(data);
        setPaginaActual(1);
      })
      .catch(() => console.error("Error cargando citas."));
  }, []);

  const handleCancelar = async (citaId) => {

    const token = localStorage.getItem("token");

    try {
      const res = await fetch(
        `http://localhost:8080/api/citas/${citaId}/cancelar`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (!res.ok) throw new Error("Error al cancelar");

      // Eliminar la cita cancelada.
      setCitas((prev) => prev.filter((c) => c.id !== citaId));

      // Saber cuantas citas quedan para recalcular paginación.
      const citasRestantes = citas.filter((c) => c.id !== citaId);

      const nuevasPaginas = Math.ceil(citasRestantes.length / CITAS_POR_PAGINA);
      if (paginaActual > nuevasPaginas && paginaActual > 1) {
        setPaginaActual((p) => p - 1);
      }
    } catch (err) {
      console.error("No se pudo cancelar la cita:", err);
      alert("Hubo un error al cancelar la cita. Inténtalo de nuevo.");
    } finally {
      cerrarModal();
    }
  };

  const abrirModal = (citaId) => {
    setModal(citaId)
  }

  const cerrarModal = () => {
    setModal(null)
  }  

  const totalPaginas = Math.ceil(citas.length / CITAS_POR_PAGINA);
  const citasPagina = citas.slice(
    (paginaActual - 1) * CITAS_POR_PAGINA,
    paginaActual * CITAS_POR_PAGINA
  );

  return (
    <>
      <section className="bg-white rounded-2xl shadow-lg p-8">
        <h3 className="text-xl font-semibold text-gray-800 mb-6">
          Mis citas
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
                      className="px-4 py-2 rounded-lg border text-red-600 font-medium hover:bg-red-600 hover:text-white"
                    >
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
              ¿Quieres cancelar la cita?
            </h4>

            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={cerrarModal}
                className="px-4 py-2 rounded-lg border text-gray-600 font-medium hover:bg-gray-100"
              >
                Volver atrás
              </button>
              <button
                onClick={() => handleCancelar(modal)}
                className="px-4 py-2 rounded-lg bg-red-600 text-white font-medium hover:bg-red-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Cancelar cita
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}