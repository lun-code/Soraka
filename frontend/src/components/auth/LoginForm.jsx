import { Input, Button, Typography } from "@material-tailwind/react";
import { Link } from "react-router-dom";

export function LoginForm() {
  return (
    <form className="flex flex-col gap-6 w-80 max-w-screen-lg sm:w-96">
      <div>
        <Input 
          size="lg" 
          placeholder="nombre@correo.com" 
          color="light-blue"
          variant="static"
          label="Email"
          className="border-solid"
        />
      </div>
      <div>
        <Input 
          type="password"
          size="lg" 
          placeholder="********"
          label="Contraseña"
          color="light-blue"
          variant="static"
          className="border-solid"
        />
      </div>
      
      <Button className="bg-[#172554] py-4 shadow-md" fullWidth>
        Iniciar Sesión
      </Button>
      
      <div className="flex justify-between items-center mt-2">
         <Typography variant="small" className="font-medium text-blue-500 hover:text-black">
           ¿Olvidaste tu contraseña?
         </Typography>
         <Link to="/">
           <Typography variant="small" className="font-medium text-gray-900 hover:text-blue-500">
             Volver al inicio
           </Typography>
         </Link>
      </div>
    </form>
  );
}