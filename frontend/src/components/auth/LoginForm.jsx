import { Input, Button, Typography } from "@material-tailwind/react";
import { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { jwtDecode } from "jwt-decode";
import { login as loginService } from "../../services/authService";

export function LoginForm() {

  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();
  const location = useLocation();


  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const data = await loginService(email, password);
      login(data.token);

      const decoded = jwtDecode(data.token);
      const rol = decoded.rol;

      // Si venía de una ruta protegida, volver ahí. Si no, ruta por rol.
      const from = location.state?.from;

      if (from) {
        navigate(from, { replace: true });
      } else if (rol === "PACIENTE") {
        navigate("/dashboard");
      } else if (rol === "MEDICO") {
        navigate("/medico");
      } else if (rol === "ADMIN") {
        navigate("/admin");
      }

    } catch (error) {
      console.log(error.message);
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-6 w-80 max-w-screen-lg sm:w-96">
      <div>
        <Input
          size="lg"
          placeholder="nombre@correo.com"
          color="light-blue"
          variant="static"
          label="Email"
          className="border-solid"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </div>
      <div>
        <Input
          type="password"
          size="lg"
          placeholder="********"
          label="Contraseña"
          color="light-blue"
          variant="static"
          className="border-solid"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>

      <Button 
        type="submit"
        className="bg-[#172554] py-4 shadow-md" 
        fullWidth
      >
        Iniciar Sesión
      </Button>

      <div className="flex justify-between items-center mt-2">
        <Typography
          variant="small"
          className="font-medium text-blue-500 hover:text-black"
        >
          ¿Olvidaste tu contraseña?
        </Typography>
        <Link to="/">
          <Typography
            variant="small"
            className="font-medium text-gray-900 hover:text-blue-500"
          >
            Volver al inicio
          </Typography>
        </Link>
      </div>
    </form>
  );
}
