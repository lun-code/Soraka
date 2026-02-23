import { useEffect, useState } from "react";

export function SelectorEspecialidad({ value, onChange }) {

  const [especialidades, setEspecialidades] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/especialidades")
      .then((res) => res.json())
      .then((data) => {
        setEspecialidades(data)
      })
      .catch((err) => {
        console.error("Error cargando especialidades");
      })
  }, []);

  return (
    <div className="mb-8">
      <label className="block text-sm font-medium text-gray-700 mb-2">
        Pedir cita
      </label>

      <select
        value={value ?? ""}
        onChange={(e) => onChange(e.target.value)}
        className="          
          w-full
          max-w-sm
          rounded-lg
          border
          border-gray-300
          bg-white
          px-4
          py-2
          text-gray-700
          shadow-sm
          focus:border-blue-500
          focus:ring-2
          focus:ring-blue-500
          focus:outline-none"
      >
        {" "}
        // Primer onChange es del HTML, el segundo la funcion del padre.
        <option value="" disabled>
          Seleciona una especialidad
        </option>
        {especialidades.map((especialidad) => (
          <option key={especialidad.id} value={especialidad.nombre}>
            {especialidad.nombre}
          </option>
        ))}
      </select>
    </div>
  );
}
