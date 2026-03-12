import { Pencil, Trash2, UserPlus } from "lucide-react";
import { NavbarAdmin } from "../../components/admin/NavBarAdmin";
import { TablaAdmin } from "../../components/admin/TablaAdmin";
import { Modal, ModalConfirmar } from "../../components/admin/Modal";
import { useAuth } from "../../contexts/AuthContext";
import { useAdminUsuarios } from "../../hooks/admin/useAdminUsuarios";

const FILTROS = [
  { valor: "TODOS",    label: "Todos",     color: "border border-gray-300 text-gray-600 hover:bg-gray-100",           activo: "bg-gray-700 text-white border border-gray-700" },
  { valor: "ADMIN",    label: "Admins",     color: "border border-purple-300 text-purple-700 hover:bg-purple-50",      activo: "bg-purple-600 text-white border border-purple-600" },
  { valor: "MEDICO",   label: "Médicos",    color: "border border-blue-300 text-blue-700 hover:bg-blue-50",            activo: "bg-blue-600 text-white border border-blue-600" },
  { valor: "PACIENTE", label: "Pacientes",  color: "border border-green-300 text-green-700 hover:bg-green-50",         activo: "bg-green-600 text-white border border-green-600" },
];

const columns = [
  { key: "nombre", label: "Nombre" },
  { key: "email", label: "Email" },
  { key: "rol", label: "Rol", render: (r) => (
    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
      r.rol === "ADMIN"    ? "bg-purple-100 text-purple-700" :
      r.rol === "MEDICO"   ? "bg-blue-100 text-blue-700" :
                             "bg-green-100 text-green-700"
    }`}>{r.rol}</span>
  )},
  { key: "isActivo", label: "Estado", render: (r) => (
    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
      r.isActivo ? "bg-green-100 text-green-700" : "bg-red-100 text-red-600"
    }`}>{r.isActivo ? "Activo" : "Inactivo"}</span>
  )},
];

export function AdminUsuarios() {
  const { apiFetch } = useAuth();
  const {
    usuarios, loading,
    filtroRol, setFiltroRol, contadores,
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  } = useAdminUsuarios(apiFetch);

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-6xl mx-auto px-6 py-10">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <div>
          <h2 className="text-2xl font-semibold text-gray-800">Usuarios</h2>
          <p className="text-gray-500 text-sm mt-1">Gestiona los usuarios del sistema</p>
        </div>
        <button
          onClick={abrirCrear}
          className="flex items-center gap-2 px-4 py-2 bg-blue-900 text-white rounded-lg text-sm font-semibold hover:bg-blue-800 transition self-start sm:self-auto"
        >
          <UserPlus size={16} />
          Nuevo usuario
        </button>
      </div>

        {/* ── Filtros por rol ──────────────────────────────────────────────────── */}
        <div className="flex flex-wrap gap-2 mb-4">
          {FILTROS.map((f) => (
            <button
              key={f.valor}
              onClick={() => setFiltroRol(f.valor)}
              className={`
                flex items-center gap-1.5 px-4 py-1.5 rounded-full text-sm font-semibold
                transition-colors duration-150 cursor-pointer
                ${filtroRol === f.valor ? f.activo : f.color}
              `}
            >
              {f.label}
              <span className="text-xs px-1.5 py-0.5 rounded-full font-bold bg-black/15">
                {contadores?.[f.valor] ?? 0}
              </span>
            </button>
          ))}
        </div>

        {/* ── Tabla ───────────────────────────────────────────────────────────── */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <TablaAdmin
            columns={columns}
            data={usuarios}
            loading={loading}
            emptyMsg="No hay usuarios para el filtro seleccionado."
            acciones={(row) => (
              <>
                <button onClick={() => abrirEditar(row)} className="p-2 rounded-lg hover:bg-blue-50 text-blue-600 transition" title="Editar">
                  <Pencil size={15} />
                </button>
                <button onClick={() => setModalEliminar({ id: row.id, nombre: row.nombre })} className="p-2 rounded-lg hover:bg-red-50 text-red-500 transition" title="Eliminar">
                  <Trash2 size={15} />
                </button>
              </>
            )}
          />
        </div>
      </main>

      {/* ── Modal crear/editar ───────────────────────────────────────────────── */}
      <Modal
        open={modalForm}
        onClose={cerrarForm}
        title={editando ? "Editar usuario" : "Nuevo usuario"}
      >
        <div className="flex flex-col gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Nombre</label>
            <input
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input
              type="email"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Contraseña {editando && <span className="text-gray-400 font-normal">(dejar vacío para no cambiar)</span>}
            </label>
            <input
              type="password"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Rol</label>
            <select
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
              value={form.rol}
              onChange={(e) => setForm({ ...form, rol: e.target.value })}
            >
              <option value="PACIENTE">Paciente</option>
              <option value="MEDICO">Médico</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          {error && <p className="text-xs text-red-500">{error}</p>}
          <div className="flex justify-end gap-2 mt-2">
            <button onClick={cerrarForm} className="px-4 py-2 rounded-lg border text-sm text-gray-600 hover:bg-gray-50">
              Cancelar
            </button>
            <button
              onClick={handleGuardar}
              disabled={guardando}
              className="px-4 py-2 rounded-lg bg-blue-900 text-white text-sm font-semibold hover:bg-blue-800 disabled:opacity-60"
            >
              {guardando ? "Guardando..." : "Guardar"}
            </button>
          </div>
        </div>
      </Modal>

      {/* ── Modal confirmar eliminar ─────────────────────────────────────────── */}
      <ModalConfirmar
        open={!!modalEliminar}
        onClose={() => setModalEliminar(null)}
        onConfirm={handleEliminar}
        mensaje={`¿Eliminar a ${modalEliminar?.nombre}? Esta acción no se puede deshacer.`}
      />
    </div>
  );
}