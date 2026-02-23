import { useState, useEffect } from "react";

const CITAS_POR_PAGINA = 5;

export function TablaCitasDisponibles({ especialidad }) {
  const [citas, setCitas] = useState([]);
  const [paginaActual, setPaginaActual] = useState(1);

  useEffect(() => {
    if (!especialidad) return;

    const token = localStorage.getItem("token");

    fetch("http://localhost:8080/api/citas/disponibles", {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    })
      .then((res) => res.json())
      .then((data) => {
        const filtradas = data.filter((cita) => cita.medicoEspecialidad === especialidad);
        setCitas(filtradas);
        setPaginaActual(1); // Resetear a la primera página al cambiar especialidad
      })
      .catch((err) => {
        console.error("Error cargando citas.");
      });
  }, [especialidad]);

  const totalPaginas = Math.ceil(citas.length / CITAS_POR_PAGINA);
  
  const citasPagina = citas.slice(
    (paginaActual - 1) * CITAS_POR_PAGINA,
    paginaActual * CITAS_POR_PAGINA
  );

  return (
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
                <td className="px-6 py-4">{new Date(cita.fechaHora).toLocaleDateString("es-ES")}</td>
                <td className="px-6 py-4">{new Date(cita.fechaHora).toLocaleTimeString("es-ES", { hour: "2-digit", minute: "2-digit" })}</td>
                <td className="px-6 py-4 rounded-r-xl text-center">
                  <button
                    className="
                      px-4
                      py-2
                      rounded-lg
                      border
                      text-blue-600
                      font-medium
                      hover:bg-blue-600
                      hover:text-white
                    "
                  >
                    Reservar
                  </button>
                  <button
                    className="
                      px-4
                      py-2
                      rounded-lg
                      border
                      text-red-600
                      font-medium
                      hover:bg-red-600
                      hover:text-white
                    "
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
  );
}