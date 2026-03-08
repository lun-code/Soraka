import logo from "../../assets/descarga.png";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { CalendarDays, LogOut } from "lucide-react";
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
  { name: "Mis citas", path: "/medico", icon: <CalendarDays size={18} /> },
];

function NavList({ location }) {
  return (
    <ul className="flex flex-col gap-2 lg:mb-0 lg:mt-0 lg:flex-row lg:items-center lg:gap-12">
      {navItems.map((item) => (
        <Typography
          key={item.name}
          as="li"
          variant="small"
          color="white"
          className="flex justify-center"
        >
          <Link
            to={item.path}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-bold transition-all ${
              location.pathname === item.path
                ? "bg-white text-[#172554]"
                : "text-white hover:text-blue-200"
            }`}
          >
            {item.icon}
            {item.name}
          </Link>
        </Typography>
      ))}
    </ul>
  );
}

export function NavbarMedico() {
  const [openNav, setOpenNav] = useState(false);
  const { logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
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

  useEffect(() => {
    setOpenNav(false);
  }, [location.pathname]);

  return (
    <div className="w-full">
      <Navbar className="w-full max-w-full rounded-none border-none bg-[#172554] bg-opacity-100 py-0 lg:h-28">
        <div className="flex h-full items-center justify-between text-white">

          <Link to="/medico" className="cursor-pointer">
            <div className="flex items-center gap-4">
              <img
                src={logo}
                alt="logo"
                className="h-14 sm:h-16 md:h-20 lg:h-24 w-auto shrink-0 rounded-full border-2 border-white/20 shadow-sm"
                loading="lazy"
              />
              <span className="text-2xl font-bold hidden sm:block tracking-tight">
                Clínica Virtual
              </span>
            </div>
          </Link>

          <div className="hidden lg:flex items-center">
            <NavList location={location} />
          </div>

          <div className="flex items-center gap-x-1">
            <Button
              variant="gradient"
              size="lg"
              color="pink"
              className="hidden lg:inline-block px-10 py-3 text-gray-200 text-base shadow-xl hover:scale-105 transition-transform"
              onClick={handleLogout}
            >
              <span className="flex items-center gap-2">
                <LogOut size={16} />
                Cerrar sesión
              </span>
            </Button>
          </div>

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

        <Collapse open={openNav} className="bg-[#172554] px-4">
          <NavList location={location} />
          <Button
            fullWidth
            variant="gradient"
            size="lg"
            className="mt-4 text-[#172554] font-bold"
            color="white"
            onClick={handleLogout}
          >
            Cerrar sesión
          </Button>
        </Collapse>
      </Navbar>
    </div>
  );
}