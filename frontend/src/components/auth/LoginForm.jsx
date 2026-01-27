import { Input, Button, Typography } from "@material-tailwind/react";
import { Link } from "react-router-dom";

export function LoginForm() {
  return (
    <form className="flex flex-col gap-6 w-80 max-w-screen-lg sm:w-96">
      <div>
        <Typography variant="small" color="blue-gray" className="mb-2 font-medium">
          Correo Electrónico
        </Typography>
        <Input 
          size="lg" 
          placeholder="nombre@correo.com" 
          className="!border-t-blue-gray-200 focus:!border-t-gray-900"
          labelProps={{ className: "before:content-none after:content-none" }}
        />
      </div>
      <div>
        <Typography variant="small" color="blue-gray" className="mb-2 font-medium">
          Contraseña
        </Typography>
        <Input 
          type="password"
          size="lg" 
          placeholder="********"
          className="!border-t-blue-gray-200 focus:!border-t-gray-900"
          labelProps={{ className: "before:content-none after:content-none" }}
        />
      </div>
      
      <Button className="bg-[#172554] py-4 shadow-md" fullWidth>
        Iniciar Sesión
      </Button>
      
      <div className="flex justify-between items-center mt-2">
         <Typography variant="small" className="font-medium text-blue-500 cursor-pointer">
           ¿Olvidaste tu contraseña?
         </Typography>
         <Link to="/">
           <Typography variant="small" className="font-medium text-gray-600 hover:text-[#172554]">
             Volver al inicio
           </Typography>
         </Link>
      </div>
    </form>
  );
}