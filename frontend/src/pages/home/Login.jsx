import { Card, Typography } from "@material-tailwind/react";
import logo from "../../assets/descarga.png";
import { LoginForm } from "../../components/auth/LoginForm";

export function Login() {
  return (
    <div className="min-h-screen bg-slate-100 flex items-center justify-center p-4">
      <Card
        color="white"
        shadow={false}
        // Hemos suavizado el borde y la sombra para que parezca una app médica moderna
        className="w-full max-w-md p-6 lg:p-10 border border-slate-200 shadow-xl shadow-slate-200/50 rounded-3xl"
      >
        {/* Cabecera del Login */}
        <div className="flex flex-col items-center mb-8 text-center">
          <div className="p-1 bg-white rounded-full border border-slate-100 shadow-sm mb-4">
            <img
              src={logo}
              alt="logo"
              className="h-20 w-20 rounded-full object-cover"
            />
          </div>
          
          <Typography variant="h3" className="text-[#172554] font-black tracking-tight">
            Bienvenido
          </Typography>
          
          <Typography className="text-slate-500 font-medium mt-1">
            Accede a tu portal de <span className="text-blue-600 font-bold">Clínica Virtual</span>
          </Typography>
        </div>

        {/* Formulario */}
        <div className="w-full">
          <LoginForm />
        </div>
      </Card>
    </div>
  );
}