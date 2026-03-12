import { useState, useEffect } from "react";
import { getUsuarios, createUsuario, updateUsuario, deleteUsuario } from "../../services/adminService";

const FORM_VACIO = { nombre: "", email: "", password: "", rol: "PACIENTE" };

export function useAdminUsuarios(apiFetch) {
  const [usuarios, setUsuarios]         = useState([]);
  const [loading, setLoading]           = useState(true);
  const [filtroRol, setFiltroRol]       = useState("TODOS");
  const [modalForm, setModalForm]       = useState(false);
  const [modalEliminar, setModalEliminar] = useState(null);
  const [editando, setEditando]         = useState(null);
  const [form, setForm]                 = useState(FORM_VACIO);
  const [error, setError]               = useState("");
  const [guardando, setGuardando]       = useState(false);

  const cargar = () => {
    setLoading(true);
    getUsuarios(apiFetch)
      .then((data) => {
        const orden = { ADMIN: 0, MEDICO: 1, PACIENTE: 2 };
        setUsuarios(data.sort((a, b) => orden[a.rol] - orden[b.rol]));
      })
      .finally(() => setLoading(false));
  };

  useEffect(cargar, []);

  // ── Usuarios filtrados ───────────────────────────────────────────────────────
  const usuariosFiltrados = filtroRol === "TODOS"
    ? usuarios
    : usuarios.filter((u) => u.rol === filtroRol);

  // ── Contadores por rol ───────────────────────────────────────────────────────
  const contadores = {
    TODOS:    usuarios.length,
    ADMIN:    usuarios.filter((u) => u.rol === "ADMIN").length,
    MEDICO:   usuarios.filter((u) => u.rol === "MEDICO").length,
    PACIENTE: usuarios.filter((u) => u.rol === "PACIENTE").length,
  };

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
    usuarios: usuariosFiltrados, loading,
    filtroRol, setFiltroRol, contadores,
    modalForm, modalEliminar, editando, form, error, guardando,
    setForm, setModalEliminar,
    abrirCrear, abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  };
}