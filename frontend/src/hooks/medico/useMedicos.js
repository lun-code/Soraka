// hooks/useMedicos.js
import { useState, useEffect } from "react";
import { getMedicosPublicos } from "../../services/medicoService";

export function useMedicos() {
  const [medicos, setMedicos] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMedicosPublicos()
      .then(setMedicos)
      .catch((err) => console.error("Error:", err))
      .finally(() => setLoading(false));
  }, []);

  return { medicos, loading };
}