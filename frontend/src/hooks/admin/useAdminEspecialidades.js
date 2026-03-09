import { useState, useEffect } from "react";
import { getEspecialidades, createEspecialidad, updateEspecialidad, deleteEspecialidad } from "../../services/adminService";

const FORM_VACIO = { nombre: "" };

export function useAdminEspecialidades(apiFetch) {
  const [especialidades, setEspecialidades] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalForm, setModalForm] = useState(false);
  const [modalEliminar, setModalEliminar] = useState(null);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(FORM_VACIO);
  const [error, setError] = useState("");
  const [guardando, setGuardando] = useState(false);

  const cargar = () => {
    setLoading(true);
    getEspecialidades(apiFetch)
      .then((data) => setEspecialidades(data.sort((a, b) => a.id - b.id)))
      .finally(() => setLoading(false));
  };

  useEffect(cargar, []);

  const abrirCrear = () => {
    setEditando(null);
    setForm(FORM_VACIO);
    setError("");
    setModalForm(true);
  };

  const abrirEditar = (esp) => {
    setEditando(esp);
    setForm({ nombre: esp.nombre });
    setError("");
    setModalForm(true);
  };

  const cerrarForm = () => {
    setModalForm(false);
    setEditando(null);
    setError("");
  };

  const handleGuardar = async () => {
    if (!form.nombre.trim()) {
      setError("El nombre es obligatorio.");
      return;
    }
    setGuardando(true);
    setError("");
    try {
      if (editando) {
        await updateEspecialidad(apiFetch, editando.id, form);
      } else {
        await createEspecialidad(apiFetch, form);
      }
      cargar();
      cerrarForm();
    } catch {
      setError("Error al guardar. Inténtalo de nuevo.");
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async () => {
    if (!modalEliminar) return;
    await deleteEspecialidad(apiFetch, modalEliminar.id);
    setModalEliminar(null);
    cargar();
  };

  return {
    especialidades, loading,
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  };
}