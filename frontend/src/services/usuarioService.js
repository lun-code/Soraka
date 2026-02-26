import { BASE_URL, handleResponse } from "./api";

export const getUsuariosPublico = async () => {
    const res = await fetch(`${BASE_URL}/api/usuarios/publico`);
    return handleResponse(res);
};