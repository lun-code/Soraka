import { BASE_URL, publicJsonHeaders, handleResponse } from "./api";

export const login = async (email, password) => {
    const res = await fetch(`${BASE_URL}/auth/login`, {
        method: "POST",
        headers: publicJsonHeaders(),
        body: JSON.stringify({ email, password }),
    });
    return handleResponse(res);
};