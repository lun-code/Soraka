import logo from "../../assets/logo.png"; // Asegúrate que la ruta sea correcta
import { Typography } from "@material-tailwind/react";
import { Phone, Mail, MapPin } from "lucide-react"; // Mantenemos estos que son de UI

export function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-[#172554] text-white pt-16 pb-8">
      <div className="container mx-auto px-4 lg:px-8">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-12 mb-12">
          
          {/* Columna 1: Logo y Eslogan */}
          <div className="flex flex-col gap-4">
            <div className="flex items-center gap-3">
              <img src={logo} alt="Logo" className="h-12 w-12 rounded-full border border-white/20" />
              <Typography variant="h5" className="font-bold tracking-tight">
                Clínica Virtual
              </Typography>
            </div>
            <p className="text-blue-100/70 text-sm leading-relaxed">
              Cuidamos de ti y de los tuyos con la tecnología más avanzada y un equipo humano excepcional.
            </p>
          </div>

          {/* Columna 2: Enlaces Rápidos */}
          <div>
            <Typography className="font-bold mb-6 text-lg uppercase tracking-wider">Enlaces</Typography>
            <ul className="space-y-3 text-blue-100/80">
              <li><a href="#" className="hover:text-cyan-400 transition-colors">Cuadro Médico</a></li>
              <li><a href="#" className="hover:text-cyan-400 transition-colors">Especialidades</a></li>
              <li><a href="#" className="hover:text-cyan-400 transition-colors">Seguros Médicos</a></li>
              <li><a href="#" className="hover:text-cyan-400 transition-colors">Preguntas Frecuentes</a></li>
            </ul>
          </div>

          {/* Columna 3: Contacto */}
          <div>
            <Typography className="font-bold mb-6 text-lg uppercase tracking-wider">Contacto</Typography>
            <ul className="space-y-4 text-blue-100/80">
              <li className="flex items-center gap-3">
                <Phone className="h-5 w-5 text-cyan-400" /> <span>+34 900 123 456</span>
              </li>
              <li className="flex items-center gap-3">
                <Mail className="h-5 w-5 text-cyan-400" /> <span>contacto@clinicavirtual.com</span>
              </li>
              <li className="flex items-center gap-3">
                <MapPin className="h-5 w-5 text-cyan-400" /> <span>Calle Salud 123, Madrid</span>
              </li>
            </ul>
          </div>

          {/* Columna 4: RRSS (Con SVGs manuales para evitar errores) */}
          <div>
            <Typography className="font-bold mb-6 text-lg uppercase tracking-wider">Síguenos</Typography>
            <div className="flex gap-4 mb-6">
              {/* Facebook */}
              <a href="#" className="p-2 bg-white/10 rounded-full hover:bg-cyan-500 transition-all group">
                <svg 
                  xmlns="http://www.w3.org/2000/svg" 
                  width="20" height="20" 
                  viewBox="0 0 24 24" 
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2" 
                  strokeLinecap="round" 
                  strokeLinejoin="round"
                  className="group-hover:text-white"
                >
                  <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
                </svg>
              </a>

              {/* Instagram */}
              <a href="#" className="p-2 bg-white/10 rounded-full hover:bg-cyan-500 transition-all group">
                <svg 
                  xmlns="http://www.w3.org/2000/svg" 
                  width="20" height="20" 
                  viewBox="0 0 24 24" 
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2" 
                  strokeLinecap="round" 
                  strokeLinejoin="round"
                  className="group-hover:text-white"
                >
                  <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
                  <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
                  <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line>
                </svg>
              </a>

              {/* Twitter / X (Logo actualizado) */}
              <a href="#" className="p-2 bg-white/10 rounded-full hover:bg-cyan-500 transition-all group">
                <svg 
                  xmlns="http://www.w3.org/2000/svg" 
                  width="20" height="20" 
                  viewBox="0 0 24 24" 
                  fill="none" 
                  stroke="currentColor" 
                  strokeWidth="2" 
                  strokeLinecap="round" 
                  strokeLinejoin="round"
                  className="group-hover:text-white"
                >
                  <path d="M4 4l11.733 16h4.267l-11.733 -16z" />
                  <path d="M4 20l6.768 -6.768m2.46 -2.46l6.772 -6.772" />
                </svg>
              </a>
            </div>
            
            <Typography className="text-sm text-blue-100/60 uppercase font-bold">Horario de Atención</Typography>
            <p className="text-blue-100/80">Lunes a Viernes: 08:00 - 20:00</p>
          </div>
        </div>

        {/* Línea Divisoria */}
        <hr className="border-white/10 mb-8" />

        {/* Copyright y Legal */}
        <div className="flex flex-col md:flex-row justify-between items-center gap-4 text-blue-200/50 text-sm font-medium">
          <p>© {currentYear} Clínica Virtual. Todos los derechos reservados.</p>
          <div className="flex gap-6">
            <a href="#" className="hover:text-white transition-colors">Aviso Legal</a>
            <a href="#" className="hover:text-white transition-colors">Privacidad</a>
            <a href="#" className="hover:text-white transition-colors">Cookies</a>
          </div>
        </div>
      </div>
    </footer>
  );
}