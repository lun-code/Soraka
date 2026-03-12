import { useMedicos } from "../../hooks/medico/useMedicos";
import { MedicoCard } from "../../components/medico/MedicoCard";
import { NavBarPublica } from "../../components/home/NavBarPublica";
import { Link } from "react-router-dom";
import { ArrowLeft } from "lucide-react";

export function Especialistas() {
  const { medicos, loading } = useMedicos(); // <-- Toda la lógica en una línea

  return (
    <>
      <NavBarPublica />
      <div className="min-h-screen bg-slate-50 py-6 lg:py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          
          <Link to="/" className="inline-flex items-center text-blue-600 font-bold mb-6">
            <ArrowLeft className="mr-2 h-4 w-4" /> Volver
          </Link>

          <div className="mb-10 text-center">
            <h2 className="text-3xl font-black text-slate-900">Nuestros Especialistas</h2>
          </div>

          {loading ? (
            <p className="text-center py-20 text-blue-600 font-bold">Cargando...</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
              {medicos.map((m) => <MedicoCard key={m.id} medico={m} />)}
            </div>
          )}

        </div>
      </div>
    </>
  );
}