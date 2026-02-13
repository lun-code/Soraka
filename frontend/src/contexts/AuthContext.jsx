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

        return jwtDecode(token);
    });

    const login = (token) => {
        localStorage.setItem("token", token)
        setUsuario(jwtDecode(token))
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
    return useContext(AuthContext);
}