import { NavBarPublica } from "../../components/home/NavBarPublica";
import { Hero } from "../../components/home/Hero";
import { Footer } from "../../components/home/Footer";
import { useAuth } from "../../contexts/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

export function Home() {

  const { usuario } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {

    if(!usuario) {
      return;
    }

    const roleRoutes = {
      PACIENTE: "/dashboard",
      ADMIN: "/admin",
      MEDICO: "/medico",
    };

    const destination = roleRoutes[usuario.rol];

    if(destination) {
      navigate(destination, { replace: true });
    }

  }, [usuario, navigate]);

  return (
    <div>
      <NavBarPublica />
      <Hero />
      <Footer />
    </div>
  );
}