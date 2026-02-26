import { BASE_URL } from "./api";

export const getCitasDisponibles = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/disponibles`);
    return res.json();
};

export const getMisCitas = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/mis-citas`);
    return res.json();
};

export const reservarCita = async (apiFetch, citaId, motivo) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/${citaId}/reservar`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ motivo }),
    });
    return res.json();
};

export const cancelarCita = async (apiFetch, citaId) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/${citaId}/cancelar`, {
        method: "POST",
    });
    return res.json();
};