import { useState, useEffect } from "react";
import { getCitas, updateCita, deleteCita, getMedicos } from "../../services/adminService";

const FORM_VACIO = { medicoId: "", fechaHora: "" };

function toInputDatetime(fechaHora) {
  if (!fechaHora) return "";
  return fechaHora.slice(0, 16);
}

export function useAdminCitas(apiFetch) {
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

  return {
    citas, medicos, loading,
    modalForm, modalEliminar, form, error, guardando,
    setForm, setModalEliminar,
    abrirEditar, cerrarForm, handleGuardar, handleEliminar,
  };
}