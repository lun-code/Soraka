import { BASE_URL, handleResponse } from "./api";

export const getUsuariosCount = async () => {
    const res = await fetch(`${BASE_URL}/api/usuarios/count`);
    return handleResponse(res);
};