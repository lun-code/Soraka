export const BASE_URL = "http://localhost:8080";

export const publicJsonHeaders = () => ({
    "Content-Type": "application/json",
});

export const handleResponse = async (res) => {
    if (!res.ok) throw new Error(`Error ${res.status}: ${res.statusText}`);
    return res.json();
};

export function crearApiFetch(logout) {
    return async (url, options = {}) => {
        const token = localStorage.getItem("token");

        const res = await fetch(url, {
            ...options,
            headers: {
                Authorization: `Bearer ${token}`,
                ...options.headers,
            },
        });

        if (res.status === 401) {
            logout();
            return;
        }

        return res;
    };
}