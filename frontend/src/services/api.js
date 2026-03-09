export const BASE_URL = "http://localhost:8080";

export const publicJsonHeaders = () => ({
    "Content-Type": "application/json",
});

export const handleResponse = async (res) => {
    if (!res.ok) throw new Error(`Error ${res.status}: ${res.statusText}`);
    return res.json();
};