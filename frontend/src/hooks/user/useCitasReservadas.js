import { useState, useEffect } from "react";
import { getMisCitas, cancelarCita } from "../../services/citaService";

const CITAS_POR_PAGINA = 5;

export function useCitasReservadas(apiFetch) {
  const [citas, setCitas] = useState([]);
  const [paginaActual, setPaginaActual] = useState(1);
  const [modal, setModal] = useState(null);

  useEffect(() => {
    getMisCitas(apiFetch)
      .then((data) => {
        const ahora = new Date();
        setCitas(data.filter((c) => new Date(c.fechaHora) > ahora));
        setPaginaActual(1);
      })
      .catch(() => console.error("Error cargando citas."));
  }, []);

  const handleCancelar = async (citaId) => {
    try {
      await cancelarCita(apiFetch, citaId);
      setCitas((prev) => {
        const restantes = prev.filter((c) => c.id !== citaId);
        const nuevasPaginas = Math.ceil(restantes.length / CITAS_POR_PAGINA);
        if (paginaActual > nuevasPaginas && paginaActual > 1) {
          setPaginaActual((p) => p - 1);
        }
        return restantes;
      });
    } catch {
      alert("Hubo un error al cancelar la cita. Inténtalo de nuevo.");
    } finally {
      cerrarModal();
    }
  };

  const abrirModal = (citaId) => setModal(citaId);
  const cerrarModal = () => setModal(null);

  const totalPaginas = Math.ceil(citas.length / CITAS_POR_PAGINA);
  const citasPagina = citas.slice(
    (paginaActual - 1) * CITAS_POR_PAGINA,
    paginaActual * CITAS_POR_PAGINA
  );

  return {
    citasPagina, totalPaginas, paginaActual, modal,
    setPaginaActual, abrirModal, cerrarModal, handleCancelar,
  };
}