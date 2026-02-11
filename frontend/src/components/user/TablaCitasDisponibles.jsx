import { useState, useEffect } from "react";

export function TablaCitasDisponibles({ especialidad }) {
  const [citas, setCitas] = useState([]);

  useEffect(() => {
    setCitas([
      { id: 1, fecha: "20/03/2026", hora: "10:00", medico: "Dr. García" },
      { id: 2, fecha: "21/03/2026", hora: "12:00", medico: "Dr. López" },
    ]);
  }, [especialidad]);

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
          {citas.map((cita) => (
            <tr
              key={cita.id}
              className="bg-gray-50 text-sm text-gray-700 hover:bg-gray-200 transition rounded-xl"
            >
              <td className="px-6 py-4 rounded-l-xl font-medium">
                {cita.medico}
              </td>
              <td className="px-6 py-4">{cita.fecha}</td>
              <td className="px-6 py-4">{cita.hora}</td>
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
  </section>
);

}
