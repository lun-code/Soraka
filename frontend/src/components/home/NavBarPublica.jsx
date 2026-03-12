import logo from "../../assets/descarga.png";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { LogIn, LogOut } from "lucide-react";
import { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthContext";
import {
  Navbar,
  Collapse,
  Typography,
  Button,
  IconButton,
} from "@material-tailwind/react";

const navItems = [
  { name: "Inicio", path: "/" },
  { name: "Especialistas", path: "/especialistas" },
  { name: "Contacto", path: "/contacto" },
];

function NavList({ location, closeMenu }) {
  return (
    <ul className="flex flex-col gap-3 lg:mb-0 lg:mt-0 lg:flex-row lg:items-center lg:gap-8">
      {navItems.map((item) => (
        <Typography
          key={item.name}
          as="li"
          variant="small"
          color="white"
          className="flex justify-center font-bold"
        >
          <Link
            to={item.path}
            onClick={closeMenu}
            className={`w-full text-center lg:w-auto px-4 py-2 rounded-lg transition-all ${
              location.pathname === item.path
                ? "bg-white text-[#172554]"
                : "hover:text-blue-200"
            }`}
          >
            {item.name}
          </Link>
        </Typography>
      ))}
    </ul>
  );
}

export function NavBarPublica() {
  const [openNav, setOpenNav] = useState(false);
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    setOpenNav(false);
    logout();
    navigate("/", { replace: true });
  };

  const handleIrApp = () => {
    setOpenNav(false);
    const rutas = { PACIENTE: "/dashboard", ADMIN: "/admin", MEDICO: "/medico" };
    navigate(rutas[usuario.rol] ?? "/dashboard", { replace: true });
  };

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 960) setOpenNav(false);
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <div className="w-full bg-[#172554]">
      <Navbar 
        fullWidth
        blurred={false}
        shadow={false}
        className="max-w-full border-none bg-[#172554] px-4 py-3 lg:px-10 lg:py-0 lg:h-24"
      >
        <div className="flex h-full items-center justify-between text-white">
          
          {/* Logo - Siempre a la izquierda */}
          <Link to="/" className="flex items-center gap-3 shrink-0">
            <img
              src={logo}
              alt="logo"
              className="h-10 lg:h-14 w-auto rounded-full border border-white/20"
            />
            <span className="text-lg lg:text-xl font-bold hidden sm:block">
              Clínica Virtual
            </span>
          </Link>

          {/* Menú y Botones - Solo visibles en Desktop */}
          <div className="hidden lg:flex items-center ml-auto gap-10">
            <NavList location={location} />
            
            <div className="flex items-center gap-4 border-l border-white/20 pl-10">
              {usuario ? (
                <>
                  <Button
                    size="sm"
                    color="white"
                    className="text-[#172554] font-bold px-5 py-2.5 normal-case"
                    onClick={handleIrApp}
                  >
                    Ir a la app
                  </Button>
                  <button 
                    onClick={handleLogout}
                    className="text-white/70 hover:text-white transition-colors"
                  >
                    <LogOut size={20} />
                  </button>
                </>
              ) : (
                <Link to="/login">
                  <Button
                    size="sm"
                    className="bg-white text-[#172554] font-bold px-6 py-2.5 capitalize flex items-center gap-2"
                  >
                    <LogIn size={16} /> Iniciar Sesión
                  </Button>
                </Link>
              )}
            </div>
          </div>

          {/* Botón Móvil - Solo visible en pantallas pequeñas */}
          <IconButton
            variant="text"
            className="ml-auto h-10 w-10 text-white lg:hidden hover:bg-white/10"
            onClick={() => setOpenNav(!openNav)}
          >
            {openNav ? (
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" className="h-7 w-7" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            ) : (
              <svg xmlns="http://www.w3.org/2000/svg" className="h-7 w-7" fill="none" stroke="currentColor" strokeWidth={2}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            )}
          </IconButton>
        </div>

        {/* Menú Desplegable Móvil - Optimizado */}
        <Collapse open={openNav} className="bg-[#172554]">
          <div className="py-6 flex flex-col gap-6">
            <NavList location={location} closeMenu={() => setOpenNav(false)} />
            
            <div className="flex flex-col gap-3 border-t border-white/10 pt-6">
              {usuario ? (
                <Button fullWidth size="lg" color="white" className="text-[#172554] font-bold" onClick={handleIrApp}>
                  Ir a mi Panel
                </Button>
              ) : (
                <Link to="/login" onClick={() => setOpenNav(false)}>
                  <Button fullWidth size="lg" color="white" className="text-[#172554] font-bold">
                    Iniciar Sesión
                  </Button>
                </Link>
              )}
            </div>
          </div>
        </Collapse>
      </Navbar>
    </div>
  );
}