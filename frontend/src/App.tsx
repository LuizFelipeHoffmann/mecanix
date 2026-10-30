import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import { ModalProvider } from './components/Modal'
import Login from './pages/Login'
import Cadastro from './pages/Cadastro'
import Dashboard from './pages/Dashboard'
import Clientes from './pages/Clientes'
import Veiculos from './pages/Veiculos'
import Estoque from './pages/Estoque'
import OS from './pages/OS'
import Relatorios from './pages/Relatorios'
import Perfil from './pages/Perfil'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  return <>{children}</>
}

export default function App() {
  return (
    <ModalProvider>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/dashboard" element={<PrivateRoute><Dashboard /></PrivateRoute>} />
        <Route path="/os/*" element={<PrivateRoute><OS /></PrivateRoute>} />
        <Route path="/clientes/*" element={<PrivateRoute><Clientes /></PrivateRoute>} />
        <Route path="/veiculos/*" element={<PrivateRoute><Veiculos /></PrivateRoute>} />
        <Route path="/estoque/*" element={<PrivateRoute><Estoque /></PrivateRoute>} />
        <Route path="/relatorios" element={<PrivateRoute><Relatorios /></PrivateRoute>} />
        <Route path="/perfil" element={<PrivateRoute><Perfil /></PrivateRoute>} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </ModalProvider>
  )
}
