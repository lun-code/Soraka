import { ChevronLeft, ChevronRight } from "lucide-react";
import { useState } from "react";

const FILAS_POR_PAGINA = 8;

/**
 * Tabla admin genérica con paginación.
 * Props:
 *  - columns: [{ key, label, render? }]
 *  - data: array de objetos
 *  - acciones: fn(row) → ReactNode
 *  - loading: boolean
 *  - emptyMsg: string
 */
export function TablaAdmin({ columns, data, acciones, loading, emptyMsg = "No hay datos." }) {
  const [pagina, setPagina] = useState(1);

  const totalPaginas = Math.ceil((data?.length || 0) / FILAS_POR_PAGINA);
  const filas = data?.slice((pagina - 1) * FILAS_POR_PAGINA, pagina * FILAS_POR_PAGINA) ?? [];

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20 text-blue-600 font-semibold">
        Cargando...
      </div>
    );
  }

  return (
    <div>
      <div className="overflow-x-auto rounded-xl border border-gray-200">
        <table className="w-full text-sm">
          <thead>
            <tr className="bg-gray-800 text-white text-left text-xs uppercase tracking-wide">
              {columns.map((col) => (
                <th key={col.key} className="px-4 py-3 first:rounded-tl-xl last:rounded-tr-xl">
                  {col.label}
                </th>
              ))}
              {acciones && <th className="px-4 py-3 text-center rounded-tr-xl">Acciones</th>}
            </tr>
          </thead>
          <tbody>
            {filas.length === 0 ? (
              <tr>
                <td
                  colSpan={columns.length + (acciones ? 1 : 0)}
                  className="px-4 py-10 text-center text-gray-400"
                >
                  {emptyMsg}
                </td>
              </tr>
            ) : (
              filas.map((row, i) => (
                <tr
                  key={row.id ?? i}
                  className="border-t border-gray-100 hover:bg-blue-50 transition-colors"
                >
                  {columns.map((col) => (
                    <td key={col.key} className="px-4 py-3 text-gray-700">
                      {col.render ? col.render(row) : row[col.key] ?? "—"}
                    </td>
                  ))}
                  {acciones && (
                    <td className="px-4 py-3 text-center">
                      <div className="flex items-center justify-center gap-2">
                        {acciones(row)}
                      </div>
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Paginación */}
      {totalPaginas > 1 && (
        <div className="flex items-center justify-between mt-4 text-sm text-gray-600">
          <span>
            Página {pagina} de {totalPaginas} ({data.length} registros)
          </span>
          <div className="flex gap-2">
            <button
              onClick={() => setPagina((p) => Math.max(1, p - 1))}
              disabled={pagina === 1}
              className="p-1 rounded hover:bg-gray-100 disabled:opacity-40 transition"
            >
              <ChevronLeft size={18} />
            </button>
            <button
              onClick={() => setPagina((p) => Math.min(totalPaginas, p + 1))}
              disabled={pagina === totalPaginas}
              className="p-1 rounded hover:bg-gray-100 disabled:opacity-40 transition"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}