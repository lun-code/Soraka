import { Pencil, Trash2, Plus } from "lucide-react";
import { NavbarAdmin } from "../../components/admin/NavBarAdmin";
import { TablaAdmin } from "../../components/admin/TablaAdmin";
import { Modal, ModalConfirmar } from "../../components/admin/Modal";
import { useAuth } from "../../contexts/AuthContext";
import { useAdminEspecialidades } from "../../hooks/admin/useAdminEspecialidades";

const columns = [
  { key: "id", label: "ID" },
  { key: "nombre", label: "Nombre" },
];

export function AdminEspecialidades() {
  const { apiFetch } = useAuth();
  const {
    especialidades, loading,
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  } = useAdminEspecialidades(apiFetch);

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-6xl mx-auto px-6 py-10">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-semibold text-gray-800">Especialidades</h2>
            <p className="text-gray-500 text-sm mt-1">Gestiona las especialidades médicas</p>
          </div>
          <button
            onClick={abrirCrear}
            className="flex items-center gap-2 px-4 py-2 bg-blue-900 text-white rounded-lg text-sm font-semibold hover:bg-blue-800 transition"
          >
            <Plus size={16} />
            Nueva especialidad
          </button>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 max-w-md mx-auto">
          <TablaAdmin
            columns={columns}
            data={especialidades}
            loading={loading}
            emptyMsg="No hay especialidades registradas."
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
        title={editando ? "Editar especialidad" : "Nueva especialidad"}
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
            <input
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.nombre}
              onChange={(e) => setForm({ ...form, nombre: e.target.value })}
              placeholder="Ej: Cardiología"
            />
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