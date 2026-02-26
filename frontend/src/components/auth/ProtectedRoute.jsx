import { useAuth } from "../../contexts/AuthContext";
import { Navigate } from "react-router-dom";


export function ProtectedRoute({ children, allowedRoles }) {

    const { usuario } = useAuth();

    if(!usuario) {
        return <Navigate to="/login" replace />;
    }

    if(!allowedRoles.includes(usuario.rol)) {
        return <Navigate to="/" />;
    }

    return children;
}