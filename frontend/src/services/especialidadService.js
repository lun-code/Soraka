import { BASE_URL, handleResponse } from "./api";

export const getEspecialidades = async () => {
    const res = await fetch(`${BASE_URL}/api/especialidades`);
    return handleResponse(res);
};