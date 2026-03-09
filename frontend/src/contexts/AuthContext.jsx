import { createContext, useContext, useState, useCallback } from "react";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export function AuthProvider({ children }) {

    const navigate = useNavigate();

    const [usuario, setUsuario] = useState(() => {
        const token = localStorage.getItem("token");

        if (!token) return null;

        try {
            const decoded = jwtDecode(token);
            const ahora = Date.now() / 1000;

            if (decoded.exp < ahora) {
                localStorage.removeItem("token");
                return null;
            }

            return decoded;
        } catch {
            localStorage.removeItem("token");
            return null;
        }
    });

    const logout = useCallback(() => {
        localStorage.removeItem("token");
        setUsuario(null);
        navigate("/", { replace: true });
    }, [navigate]);

    const login = (token) => {
        try {
            const decoded = jwtDecode(token);
            localStorage.setItem("token", token);
            setUsuario(decoded);
        } catch {
            console.error("Token inválido recibido del servidor.");
        }
    };

    const apiFetch = useCallback(
        (url, options) => {
            const token = localStorage.getItem("token");
            return fetch(url, {
                ...options,
                headers: {
                    Authorization: `Bearer ${token}`,
                    ...options?.headers,
                },
            }).then((res) => {
                if (res.status === 401) {
                    logout();
                    return undefined;
                }
                return res;
            });
        },
        [logout]
    );

    return (
        <AuthContext.Provider value={{ usuario, login, logout, apiFetch }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    }

    return context;
}