import { useState, useEffect } from "react";
import { getUsuarios, createUsuario, updateUsuario, deleteUsuario } from "../../services/adminService";

const FORM_VACIO = { nombre: "", email: "", password: "", rol: "PACIENTE" };

export function useAdminUsuarios(apiFetch) {
  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalForm, setModalForm] = useState(false);
  const [modalEliminar, setModalEliminar] = useState(null);
  const [editando, setEditando] = useState(null);
  const [form, setForm] = useState(FORM_VACIO);
  const [error, setError] = useState("");
  const [guardando, setGuardando] = useState(false);

  const cargar = () => {
    setLoading(true);
    getUsuarios(apiFetch)
      .then(setUsuarios)
      .finally(() => setLoading(false));
  };

  useEffect(cargar, []);

  const abrirCrear = () => {
    setEditando(null);
    setForm(FORM_VACIO);
    setError("");
    setModalForm(true);
  };

  const abrirEditar = (usuario) => {
    setEditando(usuario);
    setForm({ nombre: usuario.nombre, email: usuario.email, password: "", rol: usuario.rol });
    setError("");
    setModalForm(true);
  };

  const cerrarForm = () => {
    setModalForm(false);
    setEditando(null);
    setForm(FORM_VACIO);
    setError("");
  };

  const handleGuardar = async () => {
    if (!form.nombre.trim() || !form.email.trim()) {
      setError("Nombre y email son obligatorios.");
      return;
    }
    if (!editando && !form.password.trim()) {
      setError("La contraseña es obligatoria al crear un usuario.");
      return;
    }
    setGuardando(true);
    setError("");
    try {
      if (editando) {
        const payload = { nombre: form.nombre, email: form.email, rol: form.rol };
        if (form.password.trim()) payload.password = form.password;
        await updateUsuario(apiFetch, editando.id, payload);
      } else {
        await createUsuario(apiFetch, form);
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
    await deleteUsuario(apiFetch, modalEliminar.id);
    setModalEliminar(null);
    cargar();
  };

  return {
    usuarios, loading,
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  };
}