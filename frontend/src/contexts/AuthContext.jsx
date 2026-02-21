import { createContext, useContext, useState } from "react";
import { jwtDecode } from "jwt-decode"
import { useNavigate } from "react-router-dom";

const AuthContext = createContext();

export function AuthProvider({ children }) {
    
    const navigate = useNavigate();
    
    const [usuario, setUsuario] = useState(() => {
        const token = localStorage.getItem("token")

        if(!token) {
            return null;
        }

        try {
            const decoded = jwtDecode(token);

            const ahora = Date.now() / 1000; // En segundos

            if(decoded.exp < ahora) { // token expirado
                
                localStorage.removeItem("token");

                return null;
            }

            return decoded;
        }catch {
            localStorage.removeItem("token"); // Token corrupto
            return null;
        }
    });

    const login = (token) => {
        try {
            const decoded = jwtDecode(token);
            localStorage.setItem("token", token);
            setUsuario(decoded);
        }catch {
            console.error("Token inválido recibido del servidor.");
        }
    };

    const logout = () => {
        localStorage.removeItem("token")
        setUsuario(null)
        navigate("/", {replace: true});
    };

    return (
        <AuthContext.Provider value={{ usuario, login, logout }}>
            { children }
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);

    if(!context) {
        throw new Error("useAuth debe usarse dentro de <AuthProvider>");
    }

    return context;
}