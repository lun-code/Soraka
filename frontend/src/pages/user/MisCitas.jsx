import { SaludoUsuario } from "../../components/user/SaludoUsuario";
import { NavbarUsuario } from "../../components/user/NavbarUsuario";
import { CitasReservadas } from "../../components/user/CitasReservadas";

export function MisCitas() {

  return (
    <div>
      <NavbarUsuario />

      <main className="max-w-5xl mx-auto px-6 py-10">
        <SaludoUsuario />

        <CitasReservadas />
      </main>
    </div>
  );
}
