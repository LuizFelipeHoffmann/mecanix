import { useState, type KeyboardEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { maskCnpj, maskPhone } from '../api'

export default function Cadastro() {
  const { cadastrar } = useAuth()
  const navigate = useNavigate()

  // dados da oficina
  const [nomeFantasia, setNomeFantasia] = useState('')
  const [razaoSocial, setRazaoSocial] = useState('')
  const [cnpj, setCnpj] = useState('')
  const [telefoneEmpresa, setTelefoneEmpresa] = useState('')
  const [enderecoEmpresa, setEnderecoEmpresa] = useState('')
  const [aceiteTermos, setAceiteTermos] = useState(false)

  // dados do usuário administrador
  const [nomeUsuario, setNomeUsuario] = useState('')
  const [emailUsuario, setEmailUsuario] = useState('')
  const [senha, setSenha] = useState('')
  const [confirmarSenha, setConfirmarSenha] = useState('')

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function doCadastro() {
    if (!nomeFantasia || !razaoSocial || !cnpj) { setError('Preencha os dados da oficina'); return }
    if (!nomeUsuario || !emailUsuario || !senha) { setError('Preencha seus dados de acesso'); return }
    if (senha.length < 6) { setError('A senha deve ter no mínimo 6 caracteres'); return }
    if (senha !== confirmarSenha) { setError('As senhas não coincidem'); return }
    if (!aceiteTermos) { setError('É necessário aceitar os Termos de Uso e a Política de Privacidade'); return }

    setError('')
    setLoading(true)
    try {
      await cadastrar({
        nomeFantasia, razaoSocial, cnpj, telefoneEmpresa, enderecoEmpresa, aceiteTermos,
        nomeUsuario, emailUsuario: emailUsuario.trim(), senha,
      })
      navigate('/dashboard')
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Não foi possível criar a conta.')
    } finally {
      setLoading(false)
    }
  }

  function onKey(e: KeyboardEvent) {
    if (e.key === 'Enter') doCadastro()
  }

  return (
    <div className="lw">
      <div className="lbox" style={{ maxWidth: 520 }}>
        <div className="llogo">
          <div className="lico">
            <svg viewBox="0 0 24 24"><path d="M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z" /></svg>
          </div>
          <div className="lnm">MECANIX</div>
          <div className="lsub">Criar conta da sua oficina</div>
        </div>

        {error && (
          <div style={{ display: 'block', background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
            {error}
          </div>
        )}

        <div className="hint-t" style={{ marginBottom: 8 }}>Dados da oficina</div>
        <div className="fgrid">
          <div className="fg full">
            <label>Nome fantasia *</label>
            <input className="finp" type="text" placeholder="Ex: Auto Center do João"
              value={nomeFantasia} onChange={e => setNomeFantasia(e.target.value)} />
          </div>
          <div className="fg full">
            <label>Razão social *</label>
            <input className="finp" type="text" placeholder="Ex: João Silva Serviços Automotivos LTDA"
              value={razaoSocial} onChange={e => setRazaoSocial(e.target.value)} />
          </div>
          <div className="fg">
            <label>CNPJ *</label>
            <input className="finp" type="text" placeholder="00.000.000/0000-00" maxLength={18}
              value={cnpj} onChange={e => setCnpj(maskCnpj(e.target.value))} />
          </div>
          <div className="fg">
            <label>Telefone</label>
            <input className="finp" type="tel" placeholder="(41) 99999-0000" maxLength={15}
              value={telefoneEmpresa} onChange={e => setTelefoneEmpresa(maskPhone(e.target.value))} />
          </div>
          <div className="fg full">
            <label>Endereço</label>
            <input className="finp" type="text" placeholder="Rua, número, bairro, cidade - UF"
              value={enderecoEmpresa} onChange={e => setEnderecoEmpresa(e.target.value)} />
          </div>
        </div>

        <div className="hint-t" style={{ margin: '18px 0 8px' }}>Seus dados de acesso (administrador)</div>
        <div className="fgrid">
          <div className="fg full">
            <label>Seu nome *</label>
            <input className="finp" type="text" placeholder="Ex: João Silva"
              value={nomeUsuario} onChange={e => setNomeUsuario(e.target.value)} />
          </div>
          <div className="fg full">
            <label>E-mail *</label>
            <input className="finp" type="email" placeholder="email@exemplo.com"
              value={emailUsuario} onChange={e => setEmailUsuario(e.target.value)} onKeyDown={onKey} />
          </div>
          <div className="fg">
            <label>Senha *</label>
            <input className="finp" type="password" placeholder="mínimo 6 caracteres"
              value={senha} onChange={e => setSenha(e.target.value)} onKeyDown={onKey} />
          </div>
          <div className="fg">
            <label>Confirmar senha *</label>
            <input className="finp" type="password" placeholder="••••••••"
              value={confirmarSenha} onChange={e => setConfirmarSenha(e.target.value)} onKeyDown={onKey} />
          </div>
        </div>

        <div className="fg" style={{ flexDirection: 'row', alignItems: 'center', gap: 8, margin: '16px 0 18px' }}>
          <input type="checkbox" id="aceite" checked={aceiteTermos} onChange={e => setAceiteTermos(e.target.checked)} style={{ width: 16, height: 16 }} />
          <label htmlFor="aceite" style={{ fontSize: 13, color: 'var(--tx2)', fontWeight: 400 }}>
            Li e aceito os Termos de Uso e a Política de Privacidade do MECANIX
          </label>
        </div>

        <button className="btn btn-p btn-full" disabled={loading} onClick={doCadastro}>
          {loading ? 'Criando conta...' : 'Criar conta'}
        </button>

        <div style={{ textAlign: 'center', marginTop: 14, fontSize: 13, color: 'var(--tx2)' }}>
          Já tem uma conta?{' '}
          <span style={{ color: 'var(--or)', fontWeight: 600, cursor: 'pointer' }} onClick={() => navigate('/login')}>
            Entrar
          </span>
        </div>
      </div>
    </div>
  )
}
