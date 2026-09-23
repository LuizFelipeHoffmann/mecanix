import { useState, type KeyboardEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function doLogin() {
    if (!email || !senha) { setError('Preencha e-mail e senha'); return }
    setError('')
    setLoading(true)
    try {
      await login(email.trim(), senha)
      navigate('/dashboard')
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'E-mail ou senha incorretos.')
    } finally {
      setLoading(false)
    }
  }

  function onKey(e: KeyboardEvent) {
    if (e.key === 'Enter') doLogin()
  }

  return (
    <div className="lw">
      <div className="lbox">
        <div className="llogo">
          <div className="lico">
            <svg viewBox="0 0 24 24"><path d="M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z" /></svg>
          </div>
          <div className="lnm">MECANIX</div>
          <div className="lsub">Sistema de Gestão de Oficinas</div>
        </div>

        {error && (
          <div style={{ display: 'block', background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
            {error}
          </div>
        )}

        <div className="fg" style={{ marginBottom: 13 }}>
          <label>E-mail institucional</label>
          <input className="finp" type="email" placeholder="email@exemplo.com"
            value={email} onChange={e => setEmail(e.target.value)} onKeyDown={onKey} />
        </div>
        <div className="fg" style={{ marginBottom: 18 }}>
          <label>Senha</label>
          <input className="finp" type="password" placeholder="••••••••"
            value={senha} onChange={e => setSenha(e.target.value)} onKeyDown={onKey} />
        </div>
        <button className="btn btn-p btn-full" disabled={loading} onClick={doLogin}>
          {loading ? 'Entrando...' : 'Entrar no sistema'}
        </button>

        <div className="hint" style={{ marginTop: 16 }}>
          <div className="hint-t">Perfis de demonstração</div>
          <div className="hint-r"><b>admin@mecanix.com</b> / admin123 — Acesso total</div>
          <div className="hint-r"><b>servicos@mecanix.com</b> / serv123 — Atendimento</div>
          <div className="hint-r"><b>estoque@mecanix.com</b> / est123 — Estoque</div>
        </div>
      </div>
    </div>
  )
}
