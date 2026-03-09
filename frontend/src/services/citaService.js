import { BASE_URL } from "./api";

export const getCitasDisponibles = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/disponibles`);
    if (!res) return [];
    return res.json();
};

export const getMisCitas = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/mis-citas`);
    if (!res) return [];
    return res.json();
};

export const getMisCitasMedico = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/mis-citas-medico`);
    if (!res) return [];
    return res.json();
};

export const reservarCita = async (apiFetch, citaId, motivo) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/${citaId}/reservar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ motivo }),
    });

    if (!res) return;
    if (!res.ok) {
        const error = await res.json().catch(() => ({}));
        throw new Error(error.message || "Error al reservar la cita");
    }
};

export const cancelarCita = async (apiFetch, citaId) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/${citaId}/cancelar`, {
        method: "POST",
    });

    if (!res) return;
    if (!res.ok) {
        const error = await res.json().catch(() => ({}));
        throw new Error(error.message || "Error al cancelar la cita");
    }
};