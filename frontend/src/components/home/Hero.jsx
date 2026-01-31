import { Shield, Clock, User, ArrowRight } from 'lucide-react'; // Iconos limpios
import clinica from "../../assets/clinica.png";
import { Link } from "react-router-dom";

export function Hero()  {

  return (
    <div className="bg-white">
      {/* 1. SECCIÓN HERO */}
      <section className="relative bg-blue-50 py-16 lg:py-24">
        <div className="max-w-10xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col lg:flex-row items-center">
          <div className="lg:w-1/2 mb-10 mx-10 lg:mb-0">
            <h1 className="text-4xl md:text-5xl font-extrabold text-blue-900 mb-6 leading-tight">
              <p>Tu salud es lo primero,</p> <span className="text-blue-600">reserva tu cita online.</span>
            </h1>
            <p className="text-lg text-gray-600 mb-8">
              Accede a los mejores especialistas desde la comodidad de tu casa. 
              Gestión rápida, segura y sin llamadas telefónicas.
            </p>
            <div className="flex flex-col sm:flex-row gap-4">
              <Link to="/login">
                <button className="bg-blue-600 text-white px-8 py-3 rounded-lg font-semibold hover:bg-blue-700 transition flex items-center justify-center">
                  Pedir Cita Ahora <ArrowRight className="ml-2 h-5 w-5" />
                </button>
              </Link>
              <Link to="/especialistas">
                <button className="bg-white text-blue-600 border border-blue-600 px-8 py-3 rounded-lg font-semibold hover:bg-gray-300 transition">
                  Ver Especialistas
                </button>
              </Link>
            </div>
          </div>
          <div className="lg:w-1/2 flex justify-center">
            {/* Aquí puedes poner una imagen médica */}
            <div className="w-full h-64 md:h-96 bg-blue-200 rounded-2xl shadow-xl flex items-center justify-center text-blue-400 italic">
              <img 
                src={clinica}
                alt="clinica"
                className='w-full h-full object-cover'
              />
            </div>
          </div>
        </div>
      </section>

      {/* 2. SECCIÓN DE SERVICIOS (TARJETAS) */}
      <section className="py-20 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl font-bold text-gray-900">¿Por qué elegir nuestro portal?</h2>
          <p className="mt-4 text-gray-600">Diseñado para facilitar tu bienestar y el de tu familia.</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {[
            { title: 'Gestión 24/7', desc: 'Reserva tu cita en cualquier momento del día.', icon: <Clock className="text-blue-500" /> },
            { title: 'Seguridad Total', desc: 'Tus datos médicos están protegidos bajo normativa GDPR.', icon: <Shield className="text-blue-500" /> },
            { title: 'Especialistas', desc: 'Más de 50 profesionales de diversas áreas a tu disposición.', icon: <User className="text-blue-500" /> }
          ].map((item, index) => (
            <div key={index} className="rounded-xl p-[3px] bg-gradient-to-t from-purple-900 to-blue-800 hover:shadow-xl transition hover:-translate-y-1">
              <div className="p-8 bg-white rounded-[10px] text-center">
                <div className="flex justify-center mb-4">{item.icon}</div>
                <h3 className="text-xl font-bold mb-2 text-gray-800">{item.title}</h3>
                <p className="text-gray-600">{item.desc}</p>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 3. SECCIÓN DE ESTADÍSTICAS RÁPIDAS */}
      <section className="bg-blue-900 py-12 text-white">
        <div className="max-w-7xl mx-auto px-4 grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div>
            <div className="text-4xl font-bold">+15k</div>
            <div className="text-blue-200 uppercase text-sm mt-2">Pacientes Felices</div>
          </div>
          <div>
            <div className="text-4xl font-bold">50+</div>
            <div className="text-blue-200 uppercase text-sm mt-2">Médicos</div>
          </div>
          <div>
            <div className="text-4xl font-bold">12</div>
            <div className="text-blue-200 uppercase text-sm mt-2">Clínicas</div>
          </div>
          <div>
            <div className="text-4xl font-bold">4.9/5</div>
            <div className="text-blue-200 uppercase text-sm mt-2">Valoración</div>
          </div>
        </div>
      </section>
    </div>
  );
};