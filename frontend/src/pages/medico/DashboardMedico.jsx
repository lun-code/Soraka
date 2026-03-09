import { NavbarMedico } from "../../components/medico/NavbarMedico";
import { TablaCitasMedico } from "../../components/medico/TablaCitasMedico";
import { useAuth } from "../../contexts/AuthContext";
import { useCitasMedico } from "../../hooks/useCitasMedico";

export function DashboardMedico() {
  const { apiFetch, usuario } = useAuth();
  const { citas, loading } = useCitasMedico(apiFetch);

  return (
    <div>
      <NavbarMedico />
      <main className="max-w-5xl mx-auto px-6 py-10">
        <h2 className="text-2xl font-semibold text-gray-800 mb-1">Hola, {usuario?.nombre}</h2>
        <p className="text-gray-500 text-sm mb-8">Estas son tus próximas citas</p>

        <section className="bg-white rounded-2xl shadow-lg p-8">
          <h3 className="text-xl font-semibold text-gray-800 mb-6">Mis citas</h3>
          <TablaCitasMedico citas={citas} loading={loading} />
        </section>
      </main>
    </div>
  );
}