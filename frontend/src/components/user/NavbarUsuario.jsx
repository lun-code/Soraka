import logo from "../../assets/descarga.png";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Home, User, Phone, CalendarFold, LogOut } from "lucide-react";
import { useState, useEffect } from "react";
import { useAuth } from "../../contexts/AuthContext";
import {
  Navbar,
  Collapse,
  Button,
  IconButton,
} from "@material-tailwind/react";

function NavList({ location, closeMenu }) {
  const navItems = [
    { name: "Inicio", path: "/dashboard", icon: <Home size={18} /> },
    { name: "Mis citas", path: "/mis-citas", icon: <CalendarFold size={18} /> },
    { name: "Mi perfil", path: "/mi-perfil", icon: <User size={18} /> },
    { name: "Contacto", path: "/contacto", icon: <Phone size={18} /> },
  ];

  return (
    <ul className="flex flex-col gap-2 mt-4 lg:mt-0 lg:flex-row lg:items-center lg:gap-6">
      {navItems.map((item) => (
        <li key={item.name} className="w-full lg:w-auto">
          <Link
            to={item.path}
            onClick={closeMenu}
            className={`flex items-center gap-3 px-4 py-4 lg:py-2 rounded-xl text-sm font-bold transition-all ${
              location.pathname === item.path
                ? "bg-white text-[#172554] shadow-md"
                : "text-white hover:bg-white/10"
            }`}
          >
            {item.icon}
            <span>{item.name}</span>
          </Link>
        </li>
      ))}
    </ul>
  );
}

export function NavbarUsuario() {
  const [openNav, setOpenNav] = useState(false);
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    setOpenNav(false);
    logout();
    navigate("/", { replace: true });
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
        className="max-w-full border-none bg-[#172554] px-4 py-3 lg:px-10 lg:h-24"
      >
        <div className="flex h-full items-center justify-between text-white">
          
          {/* Logo y Nombre */}
          <Link to="/dashboard" className="flex items-center gap-3 shrink-0">
            <img
              src={logo}
              alt="logo"
              className="h-12 lg:h-16 w-auto rounded-full border-2 border-white/20"
            />
            <span className="text-xl font-bold hidden sm:block tracking-tight">
              Clínica Virtual
            </span>
          </Link>

          {/* Navegación Desktop */}
          <div className="hidden lg:block ml-auto mr-8">
            <NavList location={location} />
          </div>

          {/* Botón Salir Desktop - AHORA EN ROJO */}
          <div className="hidden lg:flex items-center border-l border-white/20 pl-8">
            <Button
              variant="filled"
              size="sm"
              className="flex items-center gap-2 font-bold bg-red-500 hover:bg-red-600 text-white capitalize shadow-md px-5 py-2.5 rounded-xl transition-all"
              onClick={handleLogout}
            >
              <LogOut size={18} />
              Cerrar sesión
            </Button>
          </div>

          {/* Icono Hamburguesa */}
          <IconButton
            variant="text"
            className="ml-auto h-12 w-12 text-white lg:hidden"
            onClick={() => setOpenNav(!openNav)}
          >
            {openNav ? (
              <svg xmlns="http://www.w3.org/2000/svg" fill="none" className="h-8 w-8" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            ) : (
              <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8" fill="none" stroke="currentColor" strokeWidth={2.5}>
                <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
              </svg>
            )}
          </IconButton>
        </div>

        {/* Menú Desplegable Móvil */}
        <Collapse open={openNav} className="bg-[#172554]">
          <div className="py-4 flex flex-col gap-2 border-t border-white/10 mt-2">
            
            <NavList location={location} closeMenu={() => setOpenNav(false)} />
            
            {/* Botón Cerrar Sesión Móvil - ROJO Y CONSISTENTE */}
            <button
              onClick={handleLogout}
              className="flex items-center justify-center gap-3 px-4 py-4 mt-4 rounded-xl text-sm font-bold bg-red-500 text-white shadow-lg active:scale-95 transition-all w-full"
            >
              <LogOut size={18} className="stroke-[2.5px]" />
              <span>Cerrar sesión</span>
            </button>
            
          </div>
        </Collapse>
      </Navbar>
    </div>
  );
}