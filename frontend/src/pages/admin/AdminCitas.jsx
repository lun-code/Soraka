import { Pencil, Trash2 } from "lucide-react";
import { NavbarAdmin } from "../../components/admin/NavBarAdmin";
import { TablaAdmin } from "../../components/admin/TablaAdmin";
import { Modal, ModalConfirmar } from "../../components/admin/Modal";
import { useAuth } from "../../contexts/AuthContext";
import { useAdminCitas } from "../../hooks/admin/useAdminCitas";

function formatFecha(fechaHora) {
  if (!fechaHora) return "—";
  return new Date(fechaHora).toLocaleString("es-ES", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

const coloresEstado = {
  DISPONIBLE: "bg-emerald-100 text-emerald-700",
  CONFIRMADA: "bg-orange-100 text-orange-700",
  REALIZADA:  "bg-blue-100 text-blue-700",
  CADUCADA:   "bg-red-100 text-red-500",
};

const FILTROS = [
  { valor: "TODAS",      label: "Todas",       color: "border border-gray-300 text-gray-600 hover:bg-gray-100",          activo: "bg-gray-700 text-white border border-gray-700" },
  { valor: "DISPONIBLE", label: "Disponibles",  color: "border border-green-300 text-green-700 hover:bg-emerald-50",      activo: "bg-green-600 text-white border border-green-600" },
  { valor: "CONFIRMADA", label: "Confirmadas",  color: "border border-orange-300 text-orange-700 hover:bg-orange-50",     activo: "bg-orange-500 text-white border border-orange-500" },
  { valor: "REALIZADA",  label: "Realizadas",   color: "border border-blue-300 text-blue-700 hover:bg-blue-50",           activo: "bg-blue-600 text-white border border-blue-600" },
  { valor: "CADUCADA",   label: "Caducadas",    color: "border border-red-300 text-red-600 hover:bg-red-50",              activo: "bg-red-500 text-white border border-red-500" },
];

const columns = [
  { key: "medicoNombre",       label: "Médico" },
  { key: "medicoEspecialidad", label: "Especialidad" },
  { key: "fechaHora",          label: "Fecha y hora", render: (r) => formatFecha(r.fechaHora) },
  {
    key: "estado", label: "Estado",
    render: (r) => (
      <span className={`px-2 py-1 rounded-full text-xs font-semibold ${coloresEstado[r.estado] ?? ""}`}>
        {r.estado}{r.pacienteNombre ? ` · ${r.pacienteNombre}` : ""}
      </span>
    ),
  },
];

export function AdminCitas() {
  const { apiFetch } = useAuth();
  const {
    citas, medicos, loading,
    filtroEstado, setFiltroEstado, contadores,
    modalForm, modalEliminar, form, error, guardando,
    setForm, setModalEliminar,
    abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  } = useAdminCitas(apiFetch);

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-7xl mx-auto px-6 py-10">
        <div className="mb-6">
          <h2 className="text-2xl font-semibold text-gray-800">Citas</h2>
          <p className="text-gray-500 text-sm mt-1">Gestiona todas las citas del sistema</p>
        </div>

        {/* ── Filtros por estado ─────────────────────────────────────────────── */}
        <div className="flex flex-wrap gap-2 mb-4">
          {FILTROS.map((f) => (
            <button
              key={f.valor}
              onClick={() => setFiltroEstado(f.valor)}
              className={`
                flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold
                transition-colors duration-150 cursor-pointer
                ${filtroEstado === f.valor ? f.activo : f.color}
              `}
            >
              {f.label}
              <span className={`text-xs px-1.5 py-0.5 rounded-full font-bold ${filtroEstado === f.valor ? "bg-white/20" : "bg-black/10"}`}>
                {contadores[f.valor]}
              </span>
            </button>
          ))}
        </div>

        {/* ── Tabla/Tarjetas ────────────────────────────────────────────────── */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4 md:p-6">
          <TablaAdmin
            columns={columns}
            data={citas}
            loading={loading}
            emptyMsg="No hay citas para el filtro seleccionado."
            acciones={(row) => (
              <>
                <button onClick={() => abrirEditar(row)} className="p-2 rounded-lg hover:bg-blue-50 text-blue-600 transition" title="Editar">
                  <Pencil size={15} />
                </button>
                <button onClick={() => setModalEliminar({ id: row.id, nombre: `Cita #${row.id}` })} className="p-2 rounded-lg hover:bg-red-50 text-red-500 transition" title="Eliminar">
                  <Trash2 size={15} />
                </button>
              </>
            )}
          />
        </div>
      </main>

      {/* ── Modal editar ──────────────────────────────────────────────────────── */}
      <Modal open={modalForm} onClose={cerrarForm} title="Editar cita">
        <div className="flex flex-col gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Médico</label>
            <select
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              value={form.medicoId}
              onChange={(e) => setForm({ ...form, medicoId: e.target.value })}
            >
              <option value="">Selecciona un médico</option>
              {medicos.map((m) => (
                <option key={m.id} value={m.id}>{m.nombreUsuario}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Fecha y hora</label>
            <input
              type="datetime-local"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              value={form.fechaHora}
              onChange={(e) => setForm({ ...form, fechaHora: e.target.value })}
            />
          </div>
          {error && <p className="text-xs text-red-500">{error}</p>}
          <div className="flex justify-end gap-2 mt-2">
            <button onClick={cerrarForm} className="px-4 py-2 rounded-lg border text-sm text-gray-600 hover:bg-gray-50">Cancelar</button>
            <button onClick={handleGuardar} disabled={guardando} className="px-4 py-2 rounded-lg bg-blue-900 text-white text-sm font-semibold hover:bg-blue-800 disabled:opacity-60">
              {guardando ? "Guardando..." : "Guardar"}
            </button>
          </div>
        </div>
      </Modal>

      {/* ── Modal confirmar eliminar ───────────────────────────────────────────── */}
      <ModalConfirmar
        open={!!modalEliminar}
        onClose={() => setModalEliminar(null)}
        onConfirm={handleEliminar}
        mensaje={`¿Eliminar ${modalEliminar?.nombre}? Esta acción no se puede deshacer.`}
      />
    </div>
  );
}