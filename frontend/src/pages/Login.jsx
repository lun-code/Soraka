import { Card, Typography } from "@material-tailwind/react";
import logo from "../assets/descarga.png";
import { LoginForm } from "../components/auth/LoginForm"; // Importamos el componente

export function Login() {
  return (
    <div className="min-h-screen bg-[#eceef0] flex items-center justify-center p-4">
      <Card
        color="white"
        shadow={true}
        className="p-8 flex flex-col items-center border-2 border-blue-500 border-solid shadow-blue-gray-500 shadow-lg"
      >
        {/* Cabecera del Login */}
        <div className="flex flex-col items-center mb-8 text-center">
          <img
            src={logo}
            alt="logo"
            className="h-20 w-20 mb-4 rounded-full border border-gray-100"
          />
          <Typography variant="h4" color="blue-gray" className="font-bold">
            Clínica Virtual
          </Typography>
          <Typography color="gray" className="mt-1 font-normal">
            Accede a tu portal.
          </Typography>
        </div>

        {/* Uso del componente que creamos antes */}
        <LoginForm />
      </Card>
    </div>
  );
}
