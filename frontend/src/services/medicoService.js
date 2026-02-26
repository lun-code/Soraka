import { BASE_URL, handleResponse } from "./api";

export const getMedicosPublicos = async () => {
    const res = await fetch(`${BASE_URL}/api/medicos/publicos`);
    return handleResponse(res);
};