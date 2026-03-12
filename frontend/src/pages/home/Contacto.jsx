import { NavBarPublica } from "../../components/home/NavBarPublica";
import { Github, Linkedin, Mail, Code2 } from "lucide-react";

const LINKS = [
  {
    icon: <Linkedin size={22} />,
    label: "LinkedIn",
    valor: "Manuel José Esteban Valdivia",
    href: "https://www.linkedin.com/in/manuel-jos%C3%A9-esteban-valdivia-91a3803b1/",
    color: "hover:bg-blue-50 hover:border-blue-300 hover:text-blue-700",
  },
  {
    icon: <Github size={22} />,
    label: "GitHub",
    valor: "lun-code",
    href: "https://github.com/lun-code",
    color: "hover:bg-blue-50 hover:border-blue-300 hover:text-blue-700",
  },
  {
    icon: <Mail size={22} />,
    label: "Email",
    valor: "lun.code01@gmail.com",
    href: "mailto:lun.code01@gmail.com",
    color: "hover:bg-blue-50 hover:border-blue-300 hover:text-blue-700",
  },
];

export function Contacto() {
  return (
    <div>
      <NavBarPublica />
      <main className="max-w-2xl mx-auto px-6 py-16">

        {/* Cabecera */}
        <div className="flex flex-col items-center text-center mb-12">
        <div className="h-20 w-20 rounded-full bg-[#172554] flex items-center justify-center mb-5 shadow-lg">
          <span className="text-white text-2xl font-bold">MJ</span>
        </div>
          <h1 className="text-3xl font-bold text-gray-800">Manuel José Esteban Valdivia</h1>
          <p className="text-gray-500 mt-2">Desarrollador fullstack</p>
        </div>

        {/* Descripción del proyecto */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8 mb-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-3">Sobre el proyecto</h2>
          <p className="text-gray-600 text-sm leading-relaxed">
            <span className="font-semibold text-[#172554]">Soraka</span> es una aplicación web fullstack para la gestión de citas médicas hospitalarias.
            Los pacientes pueden consultar especialistas disponibles y reservar citas.
            Los médicos gestionan su agenda. Los administradores tienen control total del sistema.
          </p>
          <p className="text-gray-500 text-sm leading-relaxed mt-3">
            Desarrollado con <span className="font-medium">Spring Boot</span> en el backend,
            <span className="font-medium"> React</span> en el frontend,
            <span className="font-medium"> MySQL</span> como base de datos y
            <span className="font-medium"> Docker</span> para el despliegue.
          </p>
        </div>

        {/* Links de contacto */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          <h2 className="text-lg font-semibold text-gray-800 mb-5">Contacto</h2>
          <div className="flex flex-col gap-3">
            {LINKS.map((link) => (
              <a
                key={link.label}
                href={link.href}
                target="_blank"
                rel="noopener noreferrer"
                className={`
                  flex items-center gap-4 p-4 rounded-xl border border-gray-100
                  transition-all duration-150 group ${link.color}
                `}
              >
                <div className="shrink-0 text-gray-500 group-hover:text-inherit transition-colors">
                  {link.icon}
                </div>
                <div>
                  <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">{link.label}</p>
                  <p className="text-sm text-gray-800 font-medium mt-0.5 group-hover:text-inherit transition-colors">
                    {link.valor}
                  </p>
                </div>
              </a>
            ))}
          </div>
        </div>

      </main>
    </div>
  );
}