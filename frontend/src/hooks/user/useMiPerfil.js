import { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthContext";

export function useMiPerfil() {
  const { usuario, apiFetch } = useAuth();
  const [perfil, setPerfil]   = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!usuario?.sub) return;
    apiFetch(`/api/usuarios/${usuario.sub}`)
      .then((res) => res?.json())
      .then((data) => setPerfil(data))
      .catch(() => setPerfil(null))
      .finally(() => setLoading(false));
  }, [usuario]);

  return { perfil, usuario, loading };
}