import { Pencil, Trash2, Plus } from "lucide-react";
import { NavbarAdmin } from "../../components/admin/NavBarAdmin";
import { TablaAdmin } from "../../components/admin/TablaAdmin";
import { Modal, ModalConfirmar } from "../../components/admin/Modal";
import { useAuth } from "../../contexts/AuthContext";
import { useAdminMedicos } from "../../hooks/admin/useAdminMedicos";

const columns = [
  { key: "nombreUsuario", label: "Nombre" },
  { key: "emailUsuario", label: "Email" },
  { key: "nombreEspecialidad", label: "Especialidad" },
  {
    key: "urlFoto", label: "Foto", render: (r) =>
      r.urlFoto
        ? <img src={r.urlFoto} alt="foto" className="h-8 w-8 rounded-full object-cover" />
        : <span className="text-gray-400 text-xs">Sin foto</span>
  },
];

export function AdminMedicos() {
  const { apiFetch } = useAuth();
  const {
    medicos, especialidades, usuariosMedico, loading,
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  } = useAdminMedicos(apiFetch);

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-6xl mx-auto px-6 py-10">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-semibold text-gray-800">Médicos</h2>
          <p className="text-gray-500 text-sm mt-1">Gestiona el equipo médico</p>
        </div>
        <button
          onClick={abrirCrear}
          className="flex items-center gap-2 px-4 py-2 bg-blue-900 text-white rounded-lg text-sm font-semibold hover:bg-blue-800 transition self-start sm:self-auto"
        >
          <Plus size={16} />
          Nuevo médico
        </button>
      </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <TablaAdmin
            columns={columns}
            data={medicos}
            loading={loading}
            emptyMsg="No hay médicos registrados."
            acciones={(row) => (
              <>
                <button onClick={() => abrirEditar(row)} className="p-2 rounded-lg hover:bg-blue-50 text-blue-600 transition" title="Editar">
                  <Pencil size={15} />
                </button>
                <button onClick={() => setModalEliminar({ id: row.id, nombre: row.nombreUsuario })} className="p-2 rounded-lg hover:bg-red-50 text-red-500 transition" title="Eliminar">
                  <Trash2 size={15} />
                </button>
              </>
            )}
          />
        </div>
      </main>

      <Modal
        open={modalForm}
        onClose={cerrarForm}
        title={editando ? "Editar médico" : "Nuevo médico"}
        footer={
          <>
            <button onClick={cerrarForm} className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 text-sm hover:bg-gray-50 transition">Cancelar</button>
            <button onClick={handleGuardar} disabled={guardando} className="px-4 py-2 rounded-lg bg-blue-900 text-white text-sm font-semibold hover:bg-blue-800 transition disabled:opacity-50">
              {guardando ? "Guardando..." : "Guardar"}
            </button>
          </>
        }
      >
        <div className="flex flex-col gap-4">
          {error && <p className="text-red-500 text-sm">{error}</p>}
          {!editando && (
            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-1">Usuario</label>
              <select className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={form.usuarioId} onChange={(e) => setForm({ ...form, usuarioId: e.target.value })}>
                <option value="">Seleccionar usuario</option>
                {usuariosMedico.map((u) => (
                  <option key={u.id} value={u.id}>{u.nombre} — {u.email}</option>
                ))}
              </select>
            </div>
          )}
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Especialidad</label>
            <select className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.especialidadId} onChange={(e) => setForm({ ...form, especialidadId: e.target.value })}>
              <option value="">Seleccionar especialidad</option>
              {especialidades.map((e) => (
                <option key={e.id} value={e.id}>{e.nombre}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">URL de foto</label>
            <input className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.urlFoto} onChange={(e) => setForm({ ...form, urlFoto: e.target.value })} placeholder="https://..." />
          </div>
        </div>
      </Modal>

      <ModalConfirmar
        open={!!modalEliminar}
        onClose={() => setModalEliminar(null)}
        onConfirm={handleEliminar}
        nombre={modalEliminar?.nombre}
      />
    </div>
  );
}