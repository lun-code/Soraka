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