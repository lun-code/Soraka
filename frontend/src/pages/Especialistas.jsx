import { useEffect, useState } from "react";
import { MapPin, ArrowLeft } from "lucide-react"; // Añadimos ArrowLeft
import { Link } from "react-router-dom"; // Necesario para navegar
import { NavBarDefault } from "../components/home/NavBarDefault";

export function Especialistas() {
  const [medicos, setMedicos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("http://localhost:8080/api/medicos/publicos")
      .then((res) => res.json())
      .then((data) => {
        setMedicos(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Error cargando médicos:", err);
        setLoading(false);
      });
  }, []);

  return (
    <>
      <NavBarDefault />
      <div className="min-h-screen bg-blue-gray-50 py-12">
        <div className="max-w-8xl mx-auto px-4 sm:px-6 lg:px-8">
          {/* Botón de Volver */}
          <div className="mb-8">
            <Link
              to="/"
              className="inline-flex items-center text-blue-600 hover:text-blue-800 font-medium transition-colors"
            >
              <ArrowLeft className="mr-2 h-5 w-5" />
              Volver al Inicio
            </Link>
          </div>

          {/* Encabezado de la página */}
          <div className="mb-12 text-center">
            <h2 className="text-3xl font-extrabold text-blue-900 sm:text-4xl">
              Nuestros Especialistas
            </h2>
            <p className="mt-4 text-lg text-gray-600">
              Encuentra al profesional adecuado para tu cuidado.
            </p>
          </div>

          {/* Listado de Médicos */}
          {loading ? (
            <div className="text-center py-20 text-blue-600 font-semibold">
              Cargando especialistas...
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-20">
              {medicos.length > 0 ? (
                medicos.map((medico) => (
                  <div
                    key={medico.id}
                    className="bg-indigo-50 rounded-2xl shadow-lg border border-gray-100 w-auto h-80"
                  >
                    <div className="p-8 w-full h-full flex flex-col">
                      <div className="flex items-center mb-6">
                        <div className="h-48 w-48 bg-blue-100 rounded-full flex items-center justify-center text-blue-600">
                          <img
                            src={medico.urlFoto}
                            alt={medico.nombre}
                            className="h-48 w-48 rounded-full object-cover"
                          />
                        </div>
                        <div className="ml-4">
                          <h3 className="text-4xl font-bold text-gray-900">
                            {medico.nombre}
                          </h3>
                          <p className="text-blue-600 font-medium text-xl">
                            {medico.especialidad}
                          </p>
                        </div>
                      </div>
                      <div className="flex items-center text-gray-600 text-2xl mt-auto">
                        <MapPin size={20} className="mr-2" />
                        {medico.ubicacion || "Consultorio Central"}
                      </div>
                    </div>
                  </div>
                ))
              ) : (
                <div className="col-span-full text-center py-10 text-gray-500">
                  No se encontraron médicos disponibles.
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </>
  );
}
