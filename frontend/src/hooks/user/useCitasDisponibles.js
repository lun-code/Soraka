import { useState, useEffect } from "react";
import { getCitasDisponibles, reservarCita } from "../../services/citaService";

const CITAS_POR_PAGINA = 5;

export function useCitasDisponibles(apiFetch, especialidad) {
  const [citas, setCitas] = useState([]);
  const [paginaActual, setPaginaActual] = useState(1);
  const [reservando, setReservando] = useState(null);
  const [modal, setModal] = useState(null);
  const [motivo, setMotivo] = useState("");
  const [errorMotivo, setErrorMotivo] = useState("");

  useEffect(() => {
    if (!especialidad) return;
    getCitasDisponibles(apiFetch)
      .then((data) => {
        setCitas(data.filter((c) => c.medicoEspecialidad === especialidad));
        setPaginaActual(1);
      })
      .catch(() => console.error("Error cargando citas."));
  }, [especialidad]);

  const abrirModal = (citaId) => {
    setMotivo("");
    setErrorMotivo("");
    setModal({ citaId });
  };

  const cerrarModal = () => {
    setModal(null);
    setMotivo("");
    setErrorMotivo("");
  };

  const handleReservar = async () => {
    if (!motivo.trim()) {
      setErrorMotivo("El motivo es obligatorio.");
      return;
    }
    setReservando(modal.citaId);
    try {
      await reservarCita(apiFetch, modal.citaId, motivo);
      setCitas((prev) => {
        const restantes = prev.filter((c) => c.id !== modal.citaId);
        const nuevasPaginas = Math.ceil(restantes.length / CITAS_POR_PAGINA);
        if (paginaActual > nuevasPaginas && paginaActual > 1) {
          setPaginaActual((p) => p - 1);
        }
        return restantes;
      });
      cerrarModal();
    } catch {
      alert("Hubo un error al reservar la cita. Inténtalo de nuevo.");
    } finally {
      setReservando(null);
    }
  };

  const totalPaginas = Math.ceil(citas.length / CITAS_POR_PAGINA);
  const citasPagina = citas.slice(
    (paginaActual - 1) * CITAS_POR_PAGINA,
    paginaActual * CITAS_POR_PAGINA
  );

  return {
    citasPagina, totalPaginas, paginaActual, modal, motivo, errorMotivo, reservando,
    setPaginaActual, setMotivo, setErrorMotivo,
    abrirModal, cerrarModal, handleReservar,
  };
}