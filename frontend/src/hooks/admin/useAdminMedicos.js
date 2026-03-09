import { useState, useEffect } from "react";
import {
  getMedicos, createMedico, updateMedico, deleteMedico,
  getEspecialidades, getUsuarios,
} from "../../services/adminService";

const FORM_VACIO = { usuarioId: "", especialidadId: "", urlFoto: "" };

export function useAdminMedicos(apiFetch) {
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

  return {
    medicos, especialidades, usuariosMedico, loading,
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  };
}