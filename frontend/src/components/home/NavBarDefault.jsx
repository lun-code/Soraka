import logo from "../../assets/descarga.png";
import { Link } from "react-router-dom";
import { Home, Users, Info, Phone } from "lucide-react";
import { useState, useEffect } from "react";
import {
  Navbar,
  MobileNav,
  Typography,
  Button,
  IconButton,
} from "@material-tailwind/react";

function NavList() {
  const navItems = [
    { name: "Inicio", path: "/", icon: <Home size={18} /> },
    {
      name: "Especialistas",
      path: "/especialistas",
      icon: <Users size={18} />,
    },
    { name: "Sobre Nosotros", path: "#", icon: <Info size={18} /> },
    { name: "Contacto", path: "#", icon: <Phone size={18} /> },
  ];

  return (
    <ul className="flex flex-col gap-2 lg:mb-0 lg:mt-0 lg:flex-row lg:items-center lg:gap-12">
      {navItems.map((item) => (
        <Typography
          key={item.name}
          as="li"
          variant="small"
          color="white"
          className="font-bold text-lg hover:text-blue-200 transition-all flex justify-center"
        >
          <Link to={item.path} className="flex items-center gap-2">
            {item.icon}
            {item.name}
          </Link>
        </Typography>
      ))}
    </ul>
  );
}

export function NavBarDefault() {
  const [openNav, setOpenNav] = useState(false);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 960) setOpenNav(false);
    };

    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <div className="w-full">
      <Navbar className="w-full max-w-full rounded-none border-none bg-[#172554] bg-opacity-100 py-0 lg:h-28">
        <div className="flex h-full items-center justify-between text-white">
          {/* LOGO: Aumentado de tamaño */}
          <Link to="/" className="cursor-pointer">
            <div className="flex items-center gap-4">
              <img
                src={logo}
                alt="logo"
                className="h-14 sm:h-16 md:h-20 lg:h-24 w-auto shrink-0 rounded-full border-2 border-white/20 shadow-sm"
                loading="lazy"
              />
              {/* Opcional: Nombre de la clínica al lado del logo */}
              <span className="text-2xl font-bold hidden sm:block tracking-tight">
                Clínica Virtual
              </span>
            </div>
          </Link>

          <div className="hidden lg:flex items-center">
            <NavList />
          </div>

          {/* BOTÓN LOGIN: Más grande y con mejor fuente */}
          <div className="flex items-center gap-x-1">
            <Link to="/login">
              <Button
                variant="gradient"
                size="lg" // Cambiado de sm a lg
                color="white"
                className="hidden lg:inline-block px-10 py-3 text-[#172554] text-base font-bold shadow-xl hover:scale-105 transition-transform"
              >
                <span>Iniciar sesión</span>
              </Button>
            </Link>
          </div>

          {/* Icono menú móvil aumentado */}
          <IconButton
            variant="text"
            className="ml-auto h-10 w-10 text-white hover:bg-white/10 lg:hidden"
            ripple={false}
            onClick={() => setOpenNav(!openNav)}
          >
            {openNav ? (
              <svg
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                className="h-8 w-8"
                viewBox="0 0 24 24"
                stroke="currentColor"
                strokeWidth={2.5}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            ) : (
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-8 w-8"
                fill="none"
                stroke="currentColor"
                strokeWidth={2.5}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            )}
          </IconButton>
        </div>

        {/* Menú móvil ajustado */}
        <MobileNav open={openNav} className="bg-[#172554] px-4">
          <NavList />

          <Link to="/login">
            <Button
              fullWidth
              variant="gradient"
              size="lg"
              className="mt-4 text-[#172554] font-bold"
              color="white"
            >
              Iniciar sesión
            </Button>
          </Link>
        </MobileNav>
      </Navbar>
    </div>
  );
}
