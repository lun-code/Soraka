import { useAuth } from "../../contexts/AuthContext";

export function SaludoUsuario() {
    
    const { usuario } = useAuth();

    return (
        <h2 className="text-2xl font-semibold text-gray-800 mb-6">Hola, {usuario.nombre}</h2>
    );
}