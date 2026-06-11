import { useState, useEffect, type ReactNode } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ordensAPI, estoqueAPI, ROLE_LABELS, ROLE_COLORS } from '../api'

interface NavItem {
  id: string
  path: string
  label: string
  roles: string[]
  icon: ReactNode
}

const NAV: NavItem[] = [
  {
    id: 'dashboard', path: '/dashboard', label: 'Dashboard', roles: ['ADMIN', 'SERVICOS', 'ESTOQUE'],
    icon: <path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z" />,
  },
  {
    id: 'os', path: '/os', label: 'Ordens de Serviço', roles: ['ADMIN', 'SERVICOS'],
    icon: <path d="M14 2H6c-1.1 0-2 .9-2 2v16h16V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z" />,
  },
  {
    id: 'clientes', path: '/clientes', label: 'Clientes', roles: ['ADMIN', 'SERVICOS'],
    icon: <path d="M16 11c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z" />,
  },
  {
    id: 'veiculos', path: '/veiculos', label: 'Veículos', roles: ['ADMIN', 'SERVICOS'],
    icon: <path d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8h2v1h2v-1h12v1h2v-1h2v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z" />,
  },
  {
    id: 'estoque', path: '/estoque', label: 'Estoque', roles: ['ADMIN', 'ESTOQUE'],
    icon: <path d="M20 6h-2.18c.07-.44.18-.87.18-1.3C18 2.12 15.88 0 13.3 0c-1.3 0-2.49.52-3.35 1.36L8 4.18V2H2v6h4.18L9 10.07V20c0 1.1.9 2 2 2h4c1.1 0 2-.9 2-2v-3h1c1.1 0 2-.9 2-2v-1h1c1.1 0 2-.9 2-2v-4c0-1.1-.9-2-2-2zM4 6V4h2v4H4V6z" />,
  },
  {
    id: 'relatorios', path: '/relatorios', label: 'Relatórios', roles: ['ADMIN'],
    icon: <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z" />,
  },
]

interface LayoutProps {
  title: string
  pageId: string
  actions?: ReactNode
  children: ReactNode
}

export default function Layout({ title, pageId, actions, children }: LayoutProps) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [sbOpen, setSbOpen] = useState(false)
  const [osBadge, setOsBadge] = useState(0)
  const [estBadge, setEstBadge] = useState(0)

  useEffect(() => {
    if (!user) return
    if (user.perfil === 'ADMIN' || user.perfil === 'SERVICOS') {
      ordensAPI.listar().then(ordens => {
        setOsBadge(ordens.filter(o => o.status === 'ANDAMENTO' || o.status === 'AGUARDANDO').length)
      }).catch(() => {})
    }
    if (user.perfil === 'ADMIN' || user.perfil === 'ESTOQUE') {
      estoqueAPI.listarAlertas().then(al => setEstBadge(al.length)).catch(() => {})
    }
  }, [user, location.pathname])

  const date = new Date().toLocaleDateString('pt-BR', { day: '2-digit', month: 'short', year: 'numeric' })

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  if (!user) return null

  return (
    <>
      <div className="email-toast" id="email-toast">
        <div className="toast-hd">
          <div className="toast-ico">
            <svg viewBox="0 0 24 24"><path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z" /></svg>
          </div>
          <div className="toast-title">E-mail enviado ao cliente</div>
          <div className="toast-close" onClick={() => document.getElementById('email-toast')?.classList.remove('show')}>×</div>
        </div>
        <div className="toast-body" id="toast-body"></div>
      </div>

      {sbOpen && <div className="sb-overlay show" onClick={() => setSbOpen(false)} />}

      <div className="app">
        <div className={`sb${sbOpen ? ' open' : ''}`}>
          <div className="brand">
            <div className="brand-r">
              <div className="brand-i">
                <svg viewBox="0 0 24 24"><path d="M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z" /></svg>
              </div>
              <span className="brand-n">MECANIX</span>
            </div>
            <div className="brand-s">Gestão de Oficinas</div>
          </div>

          <nav className="nav">
            <div className="nav-sec">Módulos</div>
            {NAV.filter(n => n.roles.includes(user.perfil)).map(n => (
              <div
                key={n.id}
                className={`nv${pageId === n.id ? ' on' : ''}`}
                onClick={() => { navigate(n.path); setSbOpen(false) }}
              >
                <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">{n.icon}</svg>
                {n.label}
                {n.id === 'os' && osBadge > 0 && <span className="nb">{osBadge}</span>}
                {n.id === 'estoque' && estBadge > 0 && <span className="nb r">{estBadge}</span>}
              </div>
            ))}
          </nav>

          <div className="sfooter">
            <div className="u-r">
              <div className="ava">{user.iniciais || '??'}</div>
              <div style={{ minWidth: 0 }}>
                <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--tx)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{user.nome}</div>
                <div style={{ fontSize: 11, color: ROLE_COLORS[user.perfil] || 'var(--or)' }}>{ROLE_LABELS[user.perfil] || user.perfil}</div>
              </div>
              <div style={{ marginLeft: 'auto', cursor: 'pointer', color: 'var(--tx3)', padding: 6 }} onClick={handleLogout} title="Sair">
                <svg width="17" height="17" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z" />
                </svg>
              </div>
            </div>
          </div>
        </div>

        <div className="mn">
          <div className="topbar">
            <div className="menu-btn" onClick={() => setSbOpen(s => !s)}>
              <svg viewBox="0 0 24 24"><path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z" /></svg>
            </div>
            <div className="ptitle">{title}</div>
            <div className="pdate">{date}</div>
            <div>{actions}</div>
          </div>
          <div className="ct">
            {children}
          </div>
        </div>
      </div>
    </>
  )
}
