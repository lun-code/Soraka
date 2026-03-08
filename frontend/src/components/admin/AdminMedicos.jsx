import { useEffect, useState } from "react";
import { Pencil, Trash2, Plus } from "lucide-react";
import { NavbarAdmin } from "../admin/NavBarAdmin";
import { TablaAdmin } from "../../components/admin/TablaAdmin";
import { Modal, ModalConfirmar } from "../../components/admin/Modal";
import { useAuth } from "../../contexts/AuthContext";
import {
  getMedicos,
  createMedico,
  updateMedico,
  deleteMedico,
  getEspecialidades,
  getUsuarios,
} from "../../services/adminService";

const FORM_VACIO = { usuarioId: "", especialidadId: "", urlFoto: "" };

export function AdminMedicos() {
  const { apiFetch } = useAuth();
  const [medicos, setMedicos] = useState([]);
  const [especialidades, setEspecialidades] = useState([]);
  const [usuariosMedico, setUsuariosMedico] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalForm, setModalForm] = useState(false);
  const [modalEliminar, setModalEliminar] = useState(null);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(FORM_VACIO);
  const [error, setError] = useState("");
  const [guardando, setGuardando] = useState(false);

  const cargar = () => {
    setLoading(true);
    Promise.all([getMedicos(apiFetch), getEspecialidades(apiFetch), getUsuarios(apiFetch)])
      .then(([m, e, u]) => {
        setMedicos(m);
        setEspecialidades(e);
        // Solo usuarios con rol MEDICO sin médico ya asignado
        const medicosAsignadosIds = new Set(m.map((med) => med.usuarioId));
        const disponibles = u.filter(
          (usr) => usr.rol === "MEDICO" && !medicosAsignadosIds.has(usr.id)
        );
        setUsuariosMedico(disponibles);
      })
      .finally(() => setLoading(false));
  };

  useEffect(cargar, []);

  const abrirCrear = () => {
    setEditando(null);
    setForm({ ...FORM_VACIO, especialidadId: especialidades[0]?.id ?? "" });
    setError("");
    setModalForm(true);
  };

  const abrirEditar = (medico) => {
    setEditando(medico);
    setForm({
      usuarioId: medico.usuarioId ?? "",
      especialidadId: medico.especialidadId ?? "",
      urlFoto: medico.urlFoto ?? "",
    });
    setError("");
    setModalForm(true);
  };

  const cerrarForm = () => {
    setModalForm(false);
    setEditando(null);
    setError("");
  };

  const handleGuardar = async () => {
    if (!editando && !form.usuarioId) {
      setError("El usuario es obligatorio.");
      return;
    }
    if (!form.especialidadId) {
      setError("La especialidad es obligatoria.");
      return;
    }
    setGuardando(true);
    setError("");
    try {
      const payload = {
        especialidadId: Number(form.especialidadId),
        urlFoto: form.urlFoto || "",
      };
      if (editando) {
        await updateMedico(apiFetch, editando.id, payload);
      } else {
        await createMedico(apiFetch, { ...payload, usuarioId: Number(form.usuarioId) });
      }
      cargar();
      cerrarForm();
    } catch {
      setError("Error al guardar. Revisa los datos e inténtalo de nuevo.");
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async () => {
    if (!modalEliminar) return;
    await deleteMedico(apiFetch, modalEliminar.id);
    setModalEliminar(null);
    cargar();
  };

  const columns = [
    { key: "id", label: "ID" },
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

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-6xl mx-auto px-6 py-10">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-semibold text-gray-800">Médicos</h2>
            <p className="text-gray-500 text-sm mt-1">Gestiona el equipo médico</p>
          </div>
          <button
            onClick={abrirCrear}
            className="flex items-center gap-2 px-4 py-2 bg-blue-900 text-white rounded-lg text-sm font-semibold hover:bg-blue-800 transition"
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
                <button
                  onClick={() => abrirEditar(row)}
                  className="p-2 rounded-lg hover:bg-blue-50 text-blue-600 transition"
                  title="Editar"
                >
                  <Pencil size={15} />
                </button>
                <button
                  onClick={() => setModalEliminar({ id: row.id, nombre: row.nombreUsuario })}
                  className="p-2 rounded-lg hover:bg-red-50 text-red-500 transition"
                  title="Eliminar"
                >
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
            <button
              onClick={cerrarForm}
              className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 text-sm hover:bg-gray-50 transition"
            >
              Cancelar
            </button>
            <button
              onClick={handleGuardar}
              disabled={guardando}
              className="px-4 py-2 rounded-lg bg-blue-900 text-white text-sm font-semibold hover:bg-blue-800 transition disabled:opacity-50"
            >
              {guardando ? "Guardando..." : "Guardar"}
            </button>
          </>
        }
      >
        <div className="flex flex-col gap-4">
          {error && <p className="text-red-500 text-sm">{error}</p>}

          {/* Al crear: selector de usuario con rol MEDICO */}
          {!editando && (
            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-1">
                Usuario (rol MEDICO)
              </label>
              <select
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={form.usuarioId}
                onChange={(e) => setForm({ ...form, usuarioId: e.target.value })}
              >
                <option value="">Seleccionar usuario</option>
                {usuariosMedico.map((u) => (
                  <option key={u.id} value={u.id}>
                    {u.nombre} — {u.email}
                  </option>
                ))}
              </select>
              {usuariosMedico.length === 0 && (
                <p className="text-xs text-amber-600 mt-1">
                  No hay usuarios con rol MEDICO sin asignar. Crea primero un usuario con ese rol.
                </p>
              )}
            </div>
          )}

          {/* Al editar: usuario actual solo lectura */}
          {editando && (
            <div>
              <label className="block text-xs font-semibold text-gray-600 mb-1">Usuario</label>
              <input
                disabled
                className="w-full border border-gray-200 bg-gray-50 rounded-lg px-3 py-2 text-sm text-gray-500"
                value={editando.nombreUsuario}
              />
              <p className="text-xs text-gray-400 mt-1">El usuario no se puede cambiar desde aquí.</p>
            </div>
          )}

          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Especialidad</label>
            <select
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.especialidadId}
              onChange={(e) => setForm({ ...form, especialidadId: e.target.value })}
            >
              <option value="">Seleccionar especialidad</option>
              {especialidades.map((e) => (
                <option key={e.id} value={e.id}>
                  {e.nombre}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">URL de foto</label>
            <input
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.urlFoto}
              onChange={(e) => setForm({ ...form, urlFoto: e.target.value })}
              placeholder="https://..."
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