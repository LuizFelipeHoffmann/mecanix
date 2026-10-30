import { createContext, useCallback, useContext, useState, type ReactNode } from 'react'

interface ModalState {
  open: boolean
  title: string
  message: string
}

interface ModalContextType {
  showAlert: (message: string, title?: string) => void
}

const ModalContext = createContext<ModalContextType>({ showAlert: () => {} })

// Substitui o window.alert() nativo por um modal no estilo do app (ao invés
// de um popup do navegador, que não pode ser estilizado e trava a aba).
// Uso: const { showAlert } = useModal(); showAlert('mensagem', 'Erro opcional')
export function ModalProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<ModalState>({ open: false, title: '', message: '' })

  const showAlert = useCallback((message: string, title: string = 'Erro') => {
    setState({ open: true, title, message })
  }, [])

  const close = () => setState(s => ({ ...s, open: false }))

  return (
    <ModalContext.Provider value={{ showAlert }}>
      {children}
      {state.open && (
        <div className="modal-overlay" onClick={close}>
          <div className="modal-box" onClick={e => e.stopPropagation()}>
            <div className="modal-hd">
              <div className="modal-ico">
                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2L1 21h22L12 2zm1 15h-2v-2h2v2zm0-4h-2V9h2v4z" /></svg>
              </div>
              <div className="modal-title">{state.title}</div>
            </div>
            <div className="modal-body">{state.message}</div>
            <div className="modal-actions">
              <button className="btn btn-p" onClick={close} autoFocus>OK</button>
            </div>
          </div>
        </div>
      )}
    </ModalContext.Provider>
  )
}

export function useModal() {
  return useContext(ModalContext)
}
