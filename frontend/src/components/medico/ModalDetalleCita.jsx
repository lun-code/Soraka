const coloresEstado = {
  DISPONIBLE: "bg-emerald-100 text-emerald-700",
  CONFIRMADA: "bg-orange-100 text-orange-700",
};

function formatFecha(fechaHora) {
  if (!fechaHora) return "—";
  return new Date(fechaHora).toLocaleString("es-ES", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

export function ModalDetalleCita({ cita, onClose }) {
  if (!cita) return null;

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-8 w-full max-w-md">
        <h4 className="text-lg font-semibold text-gray-800 mb-6">Detalle de la cita</h4>

        <div className="flex flex-col gap-4 text-sm">
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase mb-1">Paciente</p>
            <p className="text-gray-700">{cita.pacienteNombre ?? <span className="italic text-gray-400">Sin asignar</span>}</p>
          </div>
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase mb-1">Fecha y hora</p>
            <p className="text-gray-700">{formatFecha(cita.fechaHora)}</p>
          </div>
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase mb-1">Estado</p>
            <span className={`px-3 py-1 rounded-full text-xs font-semibold ${coloresEstado[cita.estado] ?? ""}`}>
              {cita.estado}
            </span>
          </div>
          <div>
            <p className="text-xs font-semibold text-gray-400 uppercase mb-1">Motivo</p>
            <p className="text-gray-700 break-all">{cita.motivo ?? "—"}</p>
          </div>
        </div>

        <div className="flex justify-end mt-6">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 text-sm hover:bg-gray-50 transition"
          >
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
}