import { Input, Button, Typography } from "@material-tailwind/react";
import { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { jwtDecode } from "jwt-decode";
import { login as loginService } from "../../services/authService";

// ── Cuentas demo ─────────────────────────────────────────────────────────────
const DEMO_ACCOUNTS = [
  {
    label: "Admin",
    email: "admin.demo@soraka.com",
    password: "demo1234",
    color: "bg-purple-100 text-purple-800 hover:bg-purple-200",
    dot: "bg-purple-500",
  },
  {
    label: "Médico",
    email: "medico.demo@soraka.com",
    password: "demo1234",
    color: "bg-blue-100 text-blue-800 hover:bg-blue-200",
    dot: "bg-blue-500",
  },
  {
    label: "Paciente",
    email: "paciente.demo@soraka.com",
    password: "demo1234",
    color: "bg-green-100 text-green-700 hover:bg-green-200",
    dot: "bg-green-500",
  },
];

export function LoginForm() {
  const { login } = useAuth();
  const [email, setEmail]       = useState("");
  const [password, setPassword] = useState("");
  const [error, setError]       = useState("");
  const [loading, setLoading]   = useState(false);
  const navigate  = useNavigate();
  const location  = useLocation();

  const redirectByRole = (token) => {
    login(token);
    const decoded = jwtDecode(token);
    const from    = location.state?.from;
    if (from) return navigate(from, { replace: true });
    const routes = { PACIENTE: "/dashboard", MEDICO: "/medico", ADMIN: "/admin" };
    navigate(routes[decoded.rol] ?? "/", { replace: true });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const data = await loginService(email, password);
      redirectByRole(data.token);
    } catch {
      setError("Credenciales incorrectas o cuenta inactiva.");
    } finally {
      setLoading(false);
    }
  };

  const handleDemoLogin = async (account) => {
    setError("");
    setLoading(true);
    setEmail(account.email);
    setPassword(account.password);
    try {
      const data = await loginService(account.email, account.password);
      redirectByRole(data.token);
    } catch {
      setError("Error al acceder con la cuenta demo. Inténtalo de nuevo.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col gap-6 w-full max-w-sm px-4 sm:px-0">

      {/* ── Acceso rápido demo ─────────────────────────────────────────────── */}
      <div className="rounded-xl border border-blue-100 bg-gray-200 p-4 overflow-hidden">
        <p className="text-xs font-semibold text-blue-900 mb-3 uppercase tracking-wide">
          🚀 Acceso rápido — Demo
        </p>
        <div className="flex gap-2 flex-wrap">
          {DEMO_ACCOUNTS.map((account) => (
            <button
              key={account.label}
              type="button"
              disabled={loading}
              onClick={() => handleDemoLogin(account)}
              className={`
                flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold
                transition-colors duration-150 cursor-pointer disabled:opacity-50
                ${account.color}
              `}
            >
              <span className={`w-2 h-2 rounded-full shrink-0 ${account.dot}`} />
              {account.label}
            </button>
          ))}
        </div>
        <p className="text-xs text-blue-600 mt-2 opacity-70 break-words">
          La demo se resetea automáticamente cada 30 min.
        </p>
      </div>

      {/* ── Divisor ────────────────────────────────────────────────────────── */}
      <div className="flex items-center gap-3">
        <div className="flex-1 h-px bg-gray-200" />
        <span className="text-xs text-gray-400 whitespace-nowrap">o inicia sesión manualmente</span>
        <div className="flex-1 h-px bg-gray-200" />
      </div>

      {/* ── Formulario manual ──────────────────────────────────────────────── */}
      <form onSubmit={handleSubmit} className="flex flex-col gap-6">
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

        {error && (
          <p className="text-xs text-red-500 -mt-3">{error}</p>
        )}

        <Button
          type="submit"
          disabled={loading}
          className="bg-[#172554] py-4 shadow-md disabled:opacity-60"
          fullWidth
        >
          {loading ? "Iniciando sesión..." : "Iniciar Sesión"}
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
    </div>
  );
}