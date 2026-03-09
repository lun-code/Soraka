import { BASE_URL } from "./api";

// ── USUARIOS ──────────────────────────────────────────────
export const getUsuarios = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/usuarios`);
    if (!res) return [];
    return res.json();
};

export const createUsuario = async (apiFetch, data) => {
    const res = await apiFetch(`${BASE_URL}/auth/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const updateUsuario = async (apiFetch, id, data) => {
    const res = await apiFetch(`${BASE_URL}/api/usuarios/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const deleteUsuario = async (apiFetch, id) => {
    const res = await apiFetch(`${BASE_URL}/api/usuarios/${id}`, {
        method: "DELETE",
    });
    if (!res) return;
    return res;
};

// ── MÉDICOS ───────────────────────────────────────────────
export const getMedicos = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/medicos`);
    if (!res) return [];
    return res.json();
};

export const createMedico = async (apiFetch, data) => {
    const res = await apiFetch(`${BASE_URL}/api/medicos`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const updateMedico = async (apiFetch, id, data) => {
    const res = await apiFetch(`${BASE_URL}/api/medicos/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const deleteMedico = async (apiFetch, id) => {
    const res = await apiFetch(`${BASE_URL}/api/medicos/${id}`, {
        method: "DELETE",
    });
    if (!res) return;
    return res;
};

// ── ESPECIALIDADES ────────────────────────────────────────
export const getEspecialidades = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/especialidades`);
    if (!res) return [];
    return res.json();
};

export const createEspecialidad = async (apiFetch, data) => {
    const res = await apiFetch(`${BASE_URL}/api/especialidades`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const updateEspecialidad = async (apiFetch, id, data) => {
    const res = await apiFetch(`${BASE_URL}/api/especialidades/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const deleteEspecialidad = async (apiFetch, id) => {
    const res = await apiFetch(`${BASE_URL}/api/especialidades/${id}`, {
        method: "DELETE",
    });
    if (!res) return;
    return res;
};

// ── CITAS ─────────────────────────────────────────────────
export const getCitas = async (apiFetch) => {
    const res = await apiFetch(`${BASE_URL}/api/citas`);
    if (!res) return [];
    return res.json();
};

export const createCita = async (apiFetch, data) => {
    const res = await apiFetch(`${BASE_URL}/api/citas`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const updateCita = async (apiFetch, id, data) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/${id}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res) return;
    return res.json();
};

export const deleteCita = async (apiFetch, id) => {
    const res = await apiFetch(`${BASE_URL}/api/citas/${id}`, {
        method: "DELETE",
    });
    if (!res) return;
    return res;
};