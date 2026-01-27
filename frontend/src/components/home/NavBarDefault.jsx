import React from "react";
import logo from "../../assets/logo.png";
import { Link } from 'react-router-dom';
import {
  Navbar,
  MobileNav,
  Typography,
  Button,
  IconButton,
} from "@material-tailwind/react";
 
export function NavBarDefault() {
  const [openNav, setOpenNav] = React.useState(false);
 
  React.useEffect(() => {
    window.addEventListener(
      "resize",
      () => window.innerWidth >= 960 && setOpenNav(false),
    );
  }, []);
 
  return (
    // He quitado el borde rojo, pero he mantenido la estructura
    <div className="w-full"> 
      <Navbar className="w-full max-w-full rounded-none bg-[#172554] bg-opacity-100 border-none px-4 py-4 lg:px-8 lg:py-5">
        <div className="flex items-center justify-between text-white container mx-auto">
          
          {/* LOGO: Aumentado de tamaño */}
          <Typography
            as="a"
            href="#"
            className="cursor-pointer py-1.5"
          >
            <div className="flex items-center gap-4">
              <img
                src={logo}
                alt="logo"
                className="h-16 w-auto md:h-20 lg:h-24 shrink-0 rounded-full border-2 border-white/20 shadow-sm"
                loading="lazy"
              />
              {/* Opcional: Nombre de la clínica al lado del logo */}
              <span className="text-2xl font-bold hidden sm:block tracking-tight">
                Clínica Virtual
              </span>
            </div>
          </Typography>

          {/* BOTÓN LOGIN: Más grande y con mejor fuente */}
          <div className="flex items-center gap-x-1">
            <Link to="/login">
              <Button 
                variant="gradient" 
                size="lg" // Cambiado de sm a lg
                color="white"
                className="hidden lg:inline-block px-10 py-4 text-[#172554] text-base font-bold shadow-xl hover:scale-105 transition-transform"
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

        {/* Menú móvil ajustado */}
        <MobileNav open={openNav} className="bg-[#172554] px-4 pb-4">
          <div className="flex flex-col gap-2">
            <Button fullWidth variant="gradient" size="lg" className="mt-4">
              Acceso Pacientes
            </Button>
          </div>
        </MobileNav>
      </Navbar>
    </div>
  );
}