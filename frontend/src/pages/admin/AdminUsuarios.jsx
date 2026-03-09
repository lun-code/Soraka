import { Pencil, Trash2, UserPlus } from "lucide-react";
import { NavbarAdmin } from "../../components/admin/NavBarAdmin";
import { TablaAdmin } from "../../components/admin/TablaAdmin";
import { Modal, ModalConfirmar } from "../../components/admin/Modal";
import { useAuth } from "../../contexts/AuthContext";
import { useAdminUsuarios } from "../../hooks/admin/useAdminUsuarios";

const columns = [
  { key: "id", label: "ID" },
  { key: "nombre", label: "Nombre" },
  { key: "email", label: "Email" },
  { key: "rol", label: "Rol", render: (r) => (
    <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
      r.rol === "ADMIN" ? "bg-purple-100 text-purple-700" :
      r.rol === "MEDICO" ? "bg-blue-100 text-blue-700" :
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
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  } = useAdminUsuarios(apiFetch);

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-6xl mx-auto px-6 py-10">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-semibold text-gray-800">Usuarios</h2>
            <p className="text-gray-500 text-sm mt-1">Gestiona los usuarios del sistema</p>
          </div>
          <button
            onClick={abrirCrear}
            className="flex items-center gap-2 px-4 py-2 bg-blue-900 text-white rounded-lg text-sm font-semibold hover:bg-blue-800 transition"
          >
            <UserPlus size={16} />
            Nuevo usuario
          </button>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <TablaAdmin
            columns={columns}
            data={usuarios}
            loading={loading}
            emptyMsg="No hay usuarios registrados."
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

      <Modal
        open={modalForm}
        onClose={cerrarForm}
        title={editando ? "Editar usuario" : "Nuevo usuario"}
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
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Nombre</label>
            <input className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} placeholder="Nombre completo" />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Email</label>
            <input type="email" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="correo@ejemplo.com" />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">
              Contraseña {editando && <span className="text-gray-400 font-normal">(dejar vacío para no cambiar)</span>}
            </label>
            <input type="password" className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="••••••••" />
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Rol</label>
            <select className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.rol} onChange={(e) => setForm({ ...form, rol: e.target.value })}>
              <option value="PACIENTE">PACIENTE</option>
              <option value="MEDICO">MEDICO</option>
              <option value="ADMIN">ADMIN</option>
            </select>
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