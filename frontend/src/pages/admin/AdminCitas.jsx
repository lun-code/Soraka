import { useEffect, useState } from "react";
import { Pencil, Trash2 } from "lucide-react";
import { NavbarAdmin } from "../../components/admin/NavBarAdmin";
import { TablaAdmin } from "../../components/admin/TablaAdmin";
import { Modal, ModalConfirmar } from "../../components/admin/Modal";
import { useAuth } from "../../contexts/AuthContext";
import {
  getCitas,
  updateCita,
  deleteCita,
  getMedicos,
} from "../../services/adminService";

const FORM_VACIO = { medicoId: "", fechaHora: "" };

function formatFecha(fechaHora) {
  if (!fechaHora) return "—";
  const d = new Date(fechaHora);
  return d.toLocaleString("es-ES", {
    day: "2-digit", month: "2-digit", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
}

// Convierte "2025-06-10T09:00:00" → "2025-06-10T09:00" (valor para input datetime-local)
function toInputDatetime(fechaHora) {
  if (!fechaHora) return "";
  return fechaHora.slice(0, 16);
}

export function AdminCitas() {
  const { apiFetch } = useAuth();
  const [citas, setCitas] = useState([]);
  const [medicos, setMedicos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalForm, setModalForm] = useState(false);
  const [modalEliminar, setModalEliminar] = useState(null);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(FORM_VACIO);
  const [error, setError] = useState("");
  const [guardando, setGuardando] = useState(false);

  const cargar = () => {
    setLoading(true);
    Promise.all([getCitas(apiFetch), getMedicos(apiFetch)])
      .then(([c, m]) => { setCitas(c); setMedicos(m); })
      .finally(() => setLoading(false));
  };

  useEffect(cargar, []);

  const abrirEditar = (cita) => {
    setEditando(cita);
    setForm({
      medicoId: cita.medicoId ?? "",
      fechaHora: toInputDatetime(cita.fechaHora),
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
    if (!form.medicoId || !form.fechaHora) {
      setError("Médico y fecha/hora son obligatorios.");
      return;
    }
    setGuardando(true);
    setError("");
    try {
      await updateCita(apiFetch, editando.id, {
        medicoId: Number(form.medicoId),
        fechaHora: form.fechaHora + ":00",
      });
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
    await deleteCita(apiFetch, modalEliminar.id);
    setModalEliminar(null);
    cargar();
  };

  const columns = [
    { key: "id", label: "ID" },
    { key: "medicoNombre", label: "Médico" },
    { key: "medicoEspecialidad", label: "Especialidad" },
    { key: "fechaHora", label: "Fecha y hora", render: (r) => formatFecha(r.fechaHora) },
    {
      key: "estado", label: "Estado",
      render: (r) => {
        const colores = {
          DISPONIBLE: "bg-emerald-100 text-emerald-700",
          CONFIRMADA: "bg-orange-100 text-orange-700",
          REALIZADA:  "bg-blue-100 text-blue-700",
          CADUCADA:   "bg-red-100 text-red-500",
        };
        return (
          <span className={`px-2 py-1 rounded-full text-xs font-semibold ${colores[r.estado] ?? ""}`}>
            {r.estado}
            {r.pacienteNombre ? ` · ${r.pacienteNombre}` : ""}
          </span>
        );
      }
    },
  ];

  return (
    <div>
      <NavbarAdmin />
      <main className="max-w-7xl mx-auto px-6 py-10">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-semibold text-gray-800">Citas</h2>
            <p className="text-gray-500 text-sm mt-1">Gestiona todas las citas del sistema</p>
          </div>
        </div>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <TablaAdmin
            columns={columns}
            data={citas}
            loading={loading}
            emptyMsg="No hay citas registradas."
            acciones={(row) => (
              <>
                <button onClick={() => abrirEditar(row)} className="p-2 rounded-lg hover:bg-blue-50 text-blue-600 transition" title="Editar">
                  <Pencil size={15} />
                </button>
                <button onClick={() => setModalEliminar({ id: row.id, nombre: `cita #${row.id}` })} className="p-2 rounded-lg hover:bg-red-50 text-red-500 transition" title="Eliminar">
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
        title={"Editar cita"}
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
            <label className="block text-xs font-semibold text-gray-600 mb-1">Médico</label>
            <select
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.medicoId}
              onChange={(e) => setForm({ ...form, medicoId: e.target.value })}
            >
              <option value="">Seleccionar médico</option>
              {medicos.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.nombreUsuario} — {m.especialidadNombre ?? m.especialidad ?? ""}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-xs font-semibold text-gray-600 mb-1">Fecha y hora</label>
            <input
              type="datetime-local"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              value={form.fechaHora}
              onChange={(e) => setForm({ ...form, fechaHora: e.target.value })}
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