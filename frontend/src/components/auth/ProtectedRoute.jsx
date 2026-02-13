import { useAuth } from "../../contexts/AuthContext";

export function ProtectedRoute({ children, allowedRoles }) {

    const { usuario } = useAuth();

    if(!usuario) {
        return null;
    }

    if(!allowedRoles.includes(usuario.rol)) {
        return null;
    }

    return children;
}