import { useState, useEffect } from "react";
import { getMisCitasMedico } from "../../services/citaService";

export function useCitasMedico(apiFetch) {
  const [citas, setCitas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMisCitasMedico(apiFetch)
      .then((data) => {
        const confirmadas = data
          .filter((c) => c.estado === "CONFIRMADA")
          .sort((a, b) => new Date(a.fechaHora) - new Date(b.fechaHora));
        setCitas(confirmadas);
      })
      .catch(() => setCitas([]))
      .finally(() => setLoading(false));
  }, []);

  return { citas, loading };
}