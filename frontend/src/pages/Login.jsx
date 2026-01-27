import { Card, Typography } from "@material-tailwind/react";
import logo from "../assets/logo.png";
import { LoginForm } from "../components/auth/LoginForm"; // Importamos el componente

export function Login() {
  return (
    <div className="min-h-screen bg-[#f8fafc] flex items-center justify-center p-4">
      <Card color="white" shadow={true} className="p-8 flex flex-col items-center">
        {/* Cabecera del Login */}
        <div className="flex flex-col items-center mb-8 text-center">
          <img src={logo} alt="logo" className="h-20 w-20 mb-4 rounded-full border border-gray-100" />
          <Typography variant="h4" color="blue-gray" className="font-bold">
            Clínica Virtual
          </Typography>
          <Typography color="gray" className="mt-1 font-normal">
            Bienvenido de nuevo. Accede a tu portal.
          </Typography>
        </div>

        {/* Uso del componente que creamos antes */}
        <LoginForm />
        
      </Card>
    </div>
  );
}