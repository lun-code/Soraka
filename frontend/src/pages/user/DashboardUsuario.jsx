import { useState } from "react";
import { SaludoUsuario } from "../../components/user/SaludoUsuario";
import { SelectorEspecialidad } from "../../components/user/SelectorEspecialidad";
import { TablaCitasDisponibles } from "../../components/user/TablaCitasDisponibles";
import { NavbarUsuario } from "../../components/user/NavbarUsuario";

export function DashboardUsuario() {
  const [especialidadSeleccionada, setEspecialidadSeleccionada] =
    useState(null);

  return (
    <div>
      <NavbarUsuario />

      <main className="max-w-5xl mx-auto px-6 py-10">
        <SaludoUsuario nombre="Juan" />

        <SelectorEspecialidad
          value={especialidadSeleccionada}
          onChange={setEspecialidadSeleccionada}
        />

        {especialidadSeleccionada && (
          <TablaCitasDisponibles especialidad={especialidadSeleccionada} />
        )}
      </main>
    </div>
  );
}
