import { NavbarUsuario } from "../../components/user/NavbarUsuario";
import { useMiPerfil } from "../../hooks/user/useMiPerfil";
import { User, Mail, ShieldCheck } from "lucide-react";

const COLORES_ROL = {
  ADMIN:    "bg-purple-100 text-purple-700",
  MEDICO:   "bg-blue-100 text-blue-700",
  PACIENTE: "bg-green-100 text-green-700",
};

function Campo({ icon, label, valor }) {
  return (
    <div className="flex items-center gap-4 p-4 rounded-xl bg-gray-50 min-w-0">
      <div className="shrink-0">{icon}</div>
      <div>
        <p className="text-xs text-gray-400 font-medium uppercase tracking-wide">{label}</p>
        <p className="text-sm text-gray-800 font-medium mt-0.5 break-all">{valor}</p>
      </div>
    </div>
  );
}

export function MiPerfil() {
  const { perfil, usuario, loading } = useMiPerfil();

  return (
    <div>
      <NavbarUsuario />
      <main className="max-w-2xl mx-auto px-6 py-10">
        <h2 className="text-2xl font-semibold text-gray-800 mb-1">Mi perfil</h2>
        <p className="text-gray-500 text-sm mb-8">Tu información personal</p>

        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-8">
          {loading ? (
            <p className="text-gray-400 text-sm">Cargando...</p>
          ) : (
            <div className="flex flex-col gap-6">

              <div className="flex items-center gap-4">
                <div className="h-16 w-16 rounded-full bg-[#172554] flex items-center justify-center text-white text-2xl font-bold select-none">
                  {(perfil?.nombre ?? usuario?.nombre ?? "?")[0].toUpperCase()}
                </div>
                <div>
                  <p className="text-lg font-semibold text-gray-800">
                    {perfil?.nombre ?? usuario?.nombre ?? "—"}
                  </p>
                  <span className={`px-2 py-0.5 rounded-full text-xs font-semibold ${COLORES_ROL[perfil?.rol ?? usuario?.rol] ?? ""}`}>
                    {perfil?.rol ?? usuario?.rol ?? "—"}
                  </span>
                </div>
              </div>

              <hr className="border-gray-100" />

              <div className="flex flex-col gap-4">
                <Campo icon={<User size={18} className="text-gray-400" />}        label="Nombre"        valor={perfil?.nombre ?? usuario?.nombre ?? "—"} />
                <Campo icon={<Mail size={18} className="text-gray-400" />}         label="Email"         valor={perfil?.email ?? usuario?.Email ?? "—"} />
                <Campo icon={<ShieldCheck size={18} className="text-gray-400" />}  label="Rol"           valor={perfil?.rol ?? usuario?.rol ?? "—"} />
              </div>

            </div>
          )}
        </div>
      </main>
    </div>
  );
}