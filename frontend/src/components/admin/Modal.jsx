import { X } from "lucide-react";

/**
 * Modal genérico reutilizable.
 * Props:
 *  - open: boolean
 *  - onClose: fn
 *  - title: string
 *  - children: ReactNode
 *  - footer: ReactNode (opcional, botones personalizados)
 */
export function Modal({ open, onClose, title, children, footer }) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 px-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b">
          <h2 className="text-lg font-semibold text-gray-800">{title}</h2>
          <button
            onClick={onClose}
            className="p-1 rounded-lg hover:bg-gray-100 transition"
          >
            <X size={20} className="text-gray-500" />
          </button>
        </div>

        {/* Body */}
        <div className="px-6 py-4 overflow-y-auto flex-1">{children}</div>

        {/* Footer */}
        {footer && (
          <div className="px-6 py-4 border-t flex justify-end gap-3">
            {footer}
          </div>
        )}
      </div>
    </div>
  );
}

/**
 * Modal de confirmación de eliminación.
 */
export function ModalConfirmar({ open, onClose, onConfirm, nombre }) {
  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Confirmar eliminación"
      footer={
        <>
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-lg border border-gray-300 text-gray-700 text-sm font-medium hover:bg-gray-50 transition"
          >
            Cancelar
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 rounded-lg bg-red-600 text-white text-sm font-medium hover:bg-red-700 transition"
          >
            Eliminar
          </button>
        </>
      }
    >
      <p className="text-gray-600 text-sm">
        ¿Estás seguro de que quieres eliminar{" "}
        <span className="font-semibold text-gray-800">{nombre}</span>? Esta
        acción no se puede deshacer.
      </p>
    </Modal>
  );
}