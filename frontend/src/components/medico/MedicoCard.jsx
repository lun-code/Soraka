// components/especialistas/MedicoCard.jsx
import { MapPin } from "lucide-react";

export function MedicoCard({ medico }) {
  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex flex-col items-center text-center gap-4">
        <div className="h-28 w-28 rounded-full overflow-hidden border-4 border-slate-50 shadow-inner">
          <img
            src={medico.urlFoto || "https://via.placeholder.com/150"}
            alt={medico.nombre}
            className="h-full w-full object-cover"
          />
        </div>
        <div className="w-full overflow-hidden">
          <h3 className="text-lg font-extrabold text-slate-900 truncate">{medico.nombre}</h3>
          <p className="text-blue-600 font-bold text-xs uppercase tracking-wider mt-1">
            {medico.especialidad}
          </p>
          <div className="mt-4 flex items-center justify-center text-slate-500 text-sm italic">
            <MapPin size={14} className="mr-1 text-blue-400 shrink-0" />
            {medico.ubicacion || "Consultorio Central"}
          </div>
        </div>
      </div>
    </div>
  );
}