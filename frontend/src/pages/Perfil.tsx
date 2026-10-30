import { useState, useEffect, useRef, type ChangeEvent } from 'react'
import Layout from '../components/Layout'
import { useAuth } from '../context/AuthContext'
import { useModal } from '../components/Modal'
import {
  authAPI, equipeAPI, emailConfigAPI, planosAPI, assinaturasAPI, fmtCur, fmtDate, maskCartao, maskValidadeCartao,
  ROLE_LABELS, ROLE_COLORS, type Plano, type MembroEquipe, type EmailConfig,
} from '../api'

const ASSINATURA_LABELS: Record<string, string> = {
  TRIAL: 'Em período grátis', PENDENTE: 'Pagamento pendente', ATIVA: 'Ativa',
  INADIMPLENTE: 'Inadimplente', CANCELADA: 'Cancelada',
}
const ASSINATURA_BADGE: Record<string, string> = {
  TRIAL: 'bbl', PENDENTE: 'byw', ATIVA: 'bgn', INADIMPLENTE: 'brd2', CANCELADA: 'bgy',
}

const TABS = [
  { id: 'perfil', label: 'Perfil' },
  { id: 'seguranca', label: 'Segurança' },
  { id: 'plano', label: 'Plano' },
  { id: 'equipe', label: 'Equipe' },
  { id: 'email', label: 'E-mail' },
] as const

type TabId = typeof TABS[number]['id']

const FOTO_MAX_BYTES = 1.5 * 1024 * 1024 // limite do arquivo original (antes do base64 inflar ~33%)

export default function Perfil() {
  const { user } = useAuth()
  const [tab, setTab] = useState<TabId>('perfil')

  if (!user) return null

  // "Equipe" (criar/gerenciar acessos de funcionário) e "E-mail" (conta que
  // envia as OS) são só pro admin — quem tem acesso restrito não vê essas abas
  const tabs = TABS.filter(t => (t.id !== 'equipe' && t.id !== 'email') || user.perfil === 'ADMIN')

  return (
    <Layout title="Meu Perfil" pageId="perfil">
      <div className="tabstrip">
        {tabs.map(t => (
          <div key={t.id} className={`tb${tab === t.id ? ' on' : ''}`} onClick={() => setTab(t.id)}>{t.label}</div>
        ))}
      </div>

      {tab === 'perfil' && <AbaPerfil />}
      {tab === 'seguranca' && <AbaSeguranca />}
      {tab === 'plano' && <AbaPlano />}
      {tab === 'equipe' && user.perfil === 'ADMIN' && <AbaEquipe />}
      {tab === 'email' && user.perfil === 'ADMIN' && <AbaEmail />}
    </Layout>
  )
}

// ───────────────────────── ABA: DADOS DO PERFIL ─────────────────────────
function AbaPerfil() {
  const { user, atualizarUsuario } = useAuth()
  const { showAlert } = useModal()
  const fileRef = useRef<HTMLInputElement>(null)

  const [nome, setNome] = useState(user?.nome || '')
  const [foto, setFoto] = useState<string | undefined>(user?.foto)
  const [removerFoto, setRemoverFoto] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => { setNome(user?.nome || ''); setFoto(user?.foto); setRemoverFoto(false) }, [user?.nome, user?.foto])

  function onEscolherFoto(e: ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    e.target.value = ''
    if (!file) return
    if (!file.type.startsWith('image/')) { showAlert('Escolha um arquivo de imagem.'); return }
    if (file.size > FOTO_MAX_BYTES) { showAlert('A imagem é muito grande. Escolha uma foto de até 1,5 MB.'); return }
    const reader = new FileReader()
    reader.onload = () => { setFoto(reader.result as string); setRemoverFoto(false) }
    reader.readAsDataURL(file)
  }

  function onRemoverFoto() {
    setFoto(undefined)
    setRemoverFoto(true)
  }

  async function salvar() {
    if (!nome.trim()) { setError('Informe seu nome'); return }
    setError('')
    setLoading(true)
    try {
      const atualizado = await authAPI.atualizarPerfil({
        nome: nome.trim(),
        foto: removerFoto ? undefined : foto,
        removerFoto,
      })
      if (atualizado) atualizarUsuario(atualizado)
      setRemoverFoto(false)
      showAlert('Perfil atualizado com sucesso!', 'Tudo certo')
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Não foi possível salvar as alterações.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card">
      <div className="chd"><div className="ctitle">Dados do perfil</div></div>
      <div className="cbd">
        {error && (
          <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
            {error}
          </div>
        )}

        <div style={{ display: 'flex', alignItems: 'center', gap: 18, marginBottom: 22, flexWrap: 'wrap' }}>
          <div className="ava-edit-wrap">
            <div className="ava ava-lg">
              {foto ? <img src={foto} alt="" /> : (user?.iniciais || '??')}
            </div>
            <div className="ava-edit-btn" onClick={() => fileRef.current?.click()} title="Trocar foto">
              <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 9a3 3 0 100 6 3 3 0 000-6zm9-3h-3.17l-1.24-1.35A2 2 0 0015.12 4H8.88a2 2 0 00-1.47.65L6.17 6H3a1 1 0 00-1 1v12a1 1 0 001 1h18a1 1 0 001-1V7a1 1 0 00-1-1zm-9 11a5 5 0 110-10 5 5 0 010 10z" /></svg>
            </div>
            <input ref={fileRef} type="file" accept="image/*" style={{ display: 'none' }} onChange={onEscolherFoto} />
          </div>
          <div>
            <div style={{ fontSize: 13, color: 'var(--tx2)', marginBottom: 6 }}>
              Foto de perfil — aparece no menu lateral. Opcional.
            </div>
            {foto && (
              <span style={{ color: 'var(--rd)', fontSize: 13, cursor: 'pointer', fontWeight: 500 }} onClick={onRemoverFoto}>
                Remover foto
              </span>
            )}
          </div>
        </div>

        <div className="fgrid">
          <div className="fg">
            <label>Nome *</label>
            <input className="finp" type="text" value={nome} onChange={e => setNome(e.target.value)} />
          </div>
          <div className="fg">
            <label>E-mail</label>
            <input className="finp" type="email" value={user?.email || ''} disabled title="O e-mail de acesso não pode ser alterado aqui" />
          </div>
          <div className="fg">
            <label>Cargo</label>
            <input className="finp" type="text" value={user?.cargo || ROLE_LABELS[user?.perfil || ''] || ''} disabled />
          </div>
          <div className="fg">
            <label>Nível de acesso</label>
            <div style={{ display: 'flex', alignItems: 'center', height: 42 }}>
              <span className="badge" style={{ background: 'var(--bg3)', color: ROLE_COLORS[user?.perfil || ''] || 'var(--or)' }}>
                <span className="bdot" style={{ background: ROLE_COLORS[user?.perfil || ''] || 'var(--or)' }} />
                {ROLE_LABELS[user?.perfil || ''] || user?.perfil}
              </span>
            </div>
          </div>
        </div>

        <div className="factions">
          <button className="btn btn-p" disabled={loading} onClick={salvar}>
            {loading ? 'Salvando...' : 'Salvar alterações'}
          </button>
        </div>
      </div>
    </div>
  )
}

// ───────────────────────── ABA: SEGURANÇA (SENHA) ─────────────────────────
function AbaSeguranca() {
  const { showAlert } = useModal()
  const [senhaAtual, setSenhaAtual] = useState('')
  const [senhaNova, setSenhaNova] = useState('')
  const [confirmar, setConfirmar] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function salvar() {
    if (!senhaAtual) { setError('Informe sua senha atual'); return }
    if (senhaNova.length < 6) { setError('A nova senha deve ter no mínimo 6 caracteres'); return }
    if (senhaNova !== confirmar) { setError('As senhas não coincidem'); return }
    setError('')
    setLoading(true)
    try {
      await authAPI.alterarSenha({ senhaAtual, senhaNova })
      setSenhaAtual(''); setSenhaNova(''); setConfirmar('')
      showAlert('Senha alterada com sucesso!', 'Tudo certo')
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Não foi possível alterar a senha.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="card">
      <div className="chd"><div className="ctitle">Alterar senha</div></div>
      <div className="cbd">
        {error && (
          <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
            {error}
          </div>
        )}
        <div className="fgrid">
          <div className="fg full">
            <label>Senha atual *</label>
            <input className="finp" type="password" value={senhaAtual} onChange={e => setSenhaAtual(e.target.value)} />
          </div>
          <div className="fg">
            <label>Nova senha *</label>
            <input className="finp" type="password" placeholder="mínimo 6 caracteres" value={senhaNova} onChange={e => setSenhaNova(e.target.value)} />
          </div>
          <div className="fg">
            <label>Confirmar nova senha *</label>
            <input className="finp" type="password" value={confirmar} onChange={e => setConfirmar(e.target.value)} />
            {confirmar && senhaNova !== confirmar && (
              <div className="emsg">As senhas não coincidem</div>
            )}
          </div>
        </div>
        <div className="factions">
          <button className="btn btn-p" disabled={loading} onClick={salvar}>
            {loading ? 'Salvando...' : 'Alterar senha'}
          </button>
        </div>
      </div>
    </div>
  )
}

// ───────────────────────── ABA: PLANO / ASSINATURA ─────────────────────────
function AbaPlano() {
  const { user, atualizarUsuario } = useAuth()
  const { showAlert } = useModal()
  const [planos, setPlanos] = useState<Plano[]>([])
  const [planoEscolhido, setPlanoEscolhido] = useState<string | undefined>(undefined)
  const [formaPagamento, setFormaPagamento] = useState<'CARTAO' | 'PIX'>('CARTAO')
  const [cartaoNumero, setCartaoNumero] = useState('')
  const [cartaoNome, setCartaoNome] = useState('')
  const [cartaoValidade, setCartaoValidade] = useState('')
  const [cartaoCvv, setCartaoCvv] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    planosAPI.listar().then(p => setPlanos(p || [])).catch(() => setPlanos([]))
  }, [])

  const somenteAdmin = user?.perfil !== 'ADMIN'
  const planoAtual = user?.planoTipo
  const trocandoPlano = !!planoEscolhido && planoEscolhido !== planoAtual

  async function confirmarTroca() {
    if (!planoEscolhido) return
    if (formaPagamento === 'CARTAO') {
      const digitos = cartaoNumero.replace(/\D/g, '')
      if (digitos.length < 13) { setError('Número do cartão inválido'); return }
      if (!cartaoNome.trim()) { setError('Informe o nome impresso no cartão'); return }
      if (!/^\d{2}\/\d{2}$/.test(cartaoValidade)) { setError('Validade do cartão inválida (MM/AA)'); return }
      if (!/^\d{3,4}$/.test(cartaoCvv)) { setError('CVV inválido'); return }
    }
    setError('')
    setLoading(true)
    try {
      const chk = await assinaturasAPI.checkout({
        plano: planoEscolhido,
        formaPagamento,
        cartaoNumero: formaPagamento === 'CARTAO' ? cartaoNumero : undefined,
        cartaoNome: formaPagamento === 'CARTAO' ? cartaoNome : undefined,
        cartaoValidade: formaPagamento === 'CARTAO' ? cartaoValidade : undefined,
        cartaoCvv: formaPagamento === 'CARTAO' ? cartaoCvv : undefined,
      })
      // o checkout já atualiza a sessão no backend — busca o usuário fresco
      // pra refletir o plano novo sem precisar deslogar
      const fresco = await authAPI.me()
      if (fresco) atualizarUsuario(fresco)
      setPlanoEscolhido(undefined)
      setCartaoNumero(''); setCartaoNome(''); setCartaoValidade(''); setCartaoCvv('')
      showAlert(chk.mensagem || 'Plano atualizado com sucesso!', 'Tudo certo')
    } catch (e: unknown) {
      showAlert(e instanceof Error ? e.message : 'Não foi possível trocar de plano agora.', 'Erro')
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <div className="card">
        <div className="chd"><div className="ctitle">Sua assinatura</div></div>
        <div className="cbd">
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, flexWrap: 'wrap', marginBottom: user?.statusAssinatura === 'TRIAL' && user?.trialFim ? 6 : 0 }}>
            <span style={{ fontSize: 16, fontWeight: 700, color: 'var(--tx)' }}>
              {planos.find(p => p.tipo === planoAtual)?.nome || planoAtual || '—'}
            </span>
            {user?.statusAssinatura && (
              <span className={`badge ${ASSINATURA_BADGE[user.statusAssinatura] || 'bgy'}`}>
                <span className="bdot" />{ASSINATURA_LABELS[user.statusAssinatura] || user.statusAssinatura}
              </span>
            )}
          </div>
          {user?.statusAssinatura === 'TRIAL' && user?.trialFim && (
            <div style={{ fontSize: 13, color: 'var(--tx2)' }}>
              Seu período grátis termina em {fmtDate(user.trialFim)}.
            </div>
          )}
          {user?.statusAssinatura === 'PENDENTE' && (
            <div style={{ fontSize: 13, color: '#f59e0b', marginTop: 4 }}>
              Confirme sua forma de pagamento abaixo pra evitar interrupções no acesso.
            </div>
          )}
        </div>
      </div>

      {somenteAdmin ? (
        <div className="info-bar">
          <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z" /></svg>
          Apenas o administrador da oficina pode alterar o plano.
        </div>
      ) : (
        <div className="card">
          <div className="chd"><div className="ctitle">Trocar de plano</div></div>
          <div className="cbd">
            {error && (
              <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
                {error}
              </div>
            )}
            <div className="planos-grid" style={{ marginBottom: 14 }}>
              {planos.map(p => (
                <div
                  key={p.tipo}
                  className={`plano-card${(planoEscolhido ?? planoAtual) === p.tipo ? ' on' : ''}`}
                  onClick={() => setPlanoEscolhido(p.tipo)}
                  style={p.tipo === planoAtual ? { opacity: .85 } : undefined}
                >
                  {p.tipo === planoAtual && (
                    <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--gn)', marginBottom: 4 }}>PLANO ATUAL</div>
                  )}
                  <div className="plano-nome">{p.nome}</div>
                  <div className="plano-preco">
                    {p.trialMeses > 0 ? (
                      <>Grátis<small> por {p.trialMeses} meses</small></>
                    ) : p.cicloMeses >= 12 ? (
                      <>{fmtCur(p.precoCobranca)}<small> /ano</small></>
                    ) : (
                      <>{fmtCur(p.precoCobranca)}<small> /mês</small></>
                    )}
                  </div>
                  {p.cicloMeses >= 12 && p.trialMeses === 0 && (
                    <div style={{ fontSize: 12, color: 'var(--gn)', marginTop: -4, marginBottom: 6 }}>
                      equivale a {fmtCur(p.precoEquivalenteMensal)}/mês
                    </div>
                  )}
                  {(p.destaques || []).map((d, i) => <div key={i} className="plano-feat">✓ {d}</div>)}
                </div>
              ))}
            </div>

            {trocandoPlano && (
              <>
                <div className="hint-t" style={{ margin: '14px 0 8px' }}>Forma de pagamento</div>
                <div style={{ display: 'flex', gap: 16, marginBottom: 14 }}>
                  <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
                    <input type="radio" name="formaPagPerfil" checked={formaPagamento === 'CARTAO'} onChange={() => setFormaPagamento('CARTAO')} />
                    Cartão de crédito
                  </label>
                  <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
                    <input type="radio" name="formaPagPerfil" checked={formaPagamento === 'PIX'} onChange={() => setFormaPagamento('PIX')} />
                    PIX
                  </label>
                </div>
                {formaPagamento === 'CARTAO' && (
                  <div className="fgrid" style={{ marginBottom: 4 }}>
                    <div className="fg full">
                      <label>Número do cartão *</label>
                      <input className="finp" type="text" placeholder="0000 0000 0000 0000" maxLength={19}
                        value={cartaoNumero} onChange={e => setCartaoNumero(maskCartao(e.target.value))} />
                    </div>
                    <div className="fg full">
                      <label>Nome impresso no cartão *</label>
                      <input className="finp" type="text" value={cartaoNome} onChange={e => setCartaoNome(e.target.value.toUpperCase())} />
                    </div>
                    <div className="fg">
                      <label>Validade *</label>
                      <input className="finp" type="text" placeholder="MM/AA" maxLength={5}
                        value={cartaoValidade} onChange={e => setCartaoValidade(maskValidadeCartao(e.target.value))} />
                    </div>
                    <div className="fg">
                      <label>CVV *</label>
                      <input className="finp" type="text" placeholder="123" maxLength={4}
                        value={cartaoCvv} onChange={e => setCartaoCvv(e.target.value.replace(/\D/g, '').slice(0, 4))} />
                    </div>
                  </div>
                )}
                <div className="factions">
                  <button className="btn btn-g" disabled={loading} onClick={() => setPlanoEscolhido(undefined)}>Cancelar</button>
                  <button className="btn btn-p" disabled={loading} onClick={confirmarTroca}>
                    {loading ? 'Confirmando...' : 'Confirmar troca de plano'}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}
    </>
  )
}

// ───────────────────────── ABA: EQUIPE (ACESSOS DE FUNCIONÁRIO) ─────────────────────────
const NIVEL_LABELS: Record<string, string> = { SERVICOS: 'Atendimento', ESTOQUE: 'Estoque' }

function AbaEquipe() {
  const { showAlert } = useModal()
  const [membros, setMembros] = useState<MembroEquipe[]>([])
  const [loading, setLoading] = useState(true)
  const [editId, setEditId] = useState<number | null>(null) // null = form fechado, 0 = criando novo, id = editando
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [cargo, setCargo] = useState('')
  const [nivel, setNivel] = useState<'SERVICOS' | 'ESTOQUE'>('SERVICOS')
  const [error, setError] = useState('')
  const [salvando, setSalvando] = useState(false)

  function carregar() {
    equipeAPI.listar().then(m => setMembros(m || [])).catch(() => setMembros([])).finally(() => setLoading(false))
  }
  useEffect(() => { carregar() }, [])

  function abrirNovo() {
    setEditId(0); setNome(''); setEmail(''); setSenha(''); setCargo(''); setNivel('SERVICOS'); setError('')
  }

  function abrirEdicao(m: MembroEquipe) {
    setEditId(m.id); setNome(m.nome); setEmail(m.email); setSenha(''); setCargo(m.cargo || ''); setNivel(m.perfil); setError('')
  }

  function fecharForm() {
    setEditId(null)
  }

  async function salvar() {
    if (!nome.trim()) { setError('Informe o nome'); return }
    const criando = editId === 0
    if (criando) {
      if (!email.trim()) { setError('Informe o e-mail'); return }
      if (senha.length < 6) { setError('A senha deve ter no mínimo 6 caracteres'); return }
    } else if (senha && senha.length < 6) {
      setError('A nova senha deve ter no mínimo 6 caracteres'); return
    }
    setError('')
    setSalvando(true)
    try {
      if (criando) {
        await equipeAPI.criar({ nome: nome.trim(), email: email.trim(), senha, cargo: cargo.trim() || undefined, perfil: nivel })
        showAlert('Acesso criado com sucesso! A pessoa já pode entrar com o e-mail e a senha cadastrados.', 'Tudo certo')
      } else if (editId) {
        await equipeAPI.atualizar(editId, { nome: nome.trim(), cargo: cargo.trim() || undefined, perfil: nivel, senha: senha || undefined })
        showAlert('Acesso atualizado com sucesso!', 'Tudo certo')
      }
      setEditId(null)
      carregar()
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Não foi possível salvar o acesso.')
    } finally {
      setSalvando(false)
    }
  }

  async function remover(m: MembroEquipe) {
    if (!confirm(`Remover o acesso de ${m.nome}? A pessoa não vai conseguir mais entrar no sistema.`)) return
    try {
      await equipeAPI.remover(m.id)
      carregar()
    } catch (e: unknown) {
      showAlert(e instanceof Error ? e.message : 'Não foi possível remover este acesso.', 'Erro')
    }
  }

  const formAberto = editId !== null

  return (
    <>
      <div className="card">
        <div className="chd">
          <div className="ctitle">Acessos da equipe</div>
          {!formAberto && <button className="btn btn-p sm" onClick={abrirNovo}>+ Novo acesso</button>}
        </div>
        <div style={{ padding: '0 14px 14px', fontSize: 13, color: 'var(--tx3)' }}>
          Crie contas de Atendimento ou Estoque pra sua equipe — cada pessoa tem seu próprio login e pode usar o sistema
          ao mesmo tempo que os outros, todos vendo os dados da mesma oficina, mas só com acesso ao que precisam.
        </div>

        {loading ? (
          <div style={{ padding: 24, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div>
        ) : (
          <>
            <div className="tbl-wrap tbl-desktop">
              <table className="tbl">
                <thead><tr><th>Nome</th><th>E-mail</th><th>Cargo</th><th>Nível de acesso</th><th /></tr></thead>
                <tbody>
                  {membros.length ? membros.map(m => (
                    <tr key={m.id}>
                      <td style={{ fontWeight: 500 }}>{m.nome}</td>
                      <td style={{ color: 'var(--tx2)' }}>{m.email}</td>
                      <td style={{ color: 'var(--tx2)' }}>{m.cargo || '—'}</td>
                      <td><span className="badge bbl">{NIVEL_LABELS[m.perfil] || m.perfil}</span></td>
                      <td>
                        <div style={{ display: 'flex', gap: 6, justifyContent: 'flex-end' }}>
                          <div className="ib" onClick={() => abrirEdicao(m)} title="Editar">
                            <svg viewBox="0 0 24 24"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z" /></svg>
                          </div>
                          <div className="ib" onClick={() => remover(m)} title="Remover">
                            <svg viewBox="0 0 24 24"><path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" /></svg>
                          </div>
                        </div>
                      </td>
                    </tr>
                  )) : <tr><td colSpan={5} style={{ textAlign: 'center', color: 'var(--tx3)', padding: 24 }}>Nenhum acesso de equipe criado ainda.</td></tr>}
                </tbody>
              </table>
            </div>
            <div className="mob-list cbd">
              {membros.map(m => (
                <div key={m.id} className="mob-card">
                  <div className="mob-card-top">
                    <span style={{ fontWeight: 600, fontSize: 15, color: 'var(--tx)' }}>{m.nome}</span>
                    <span className="badge bbl">{NIVEL_LABELS[m.perfil] || m.perfil}</span>
                  </div>
                  <div className="mob-card-sub">{m.email}{m.cargo ? ` · ${m.cargo}` : ''}</div>
                  <div className="mob-card-row">
                    <span style={{ color: 'var(--or)', fontWeight: 600, fontSize: 13, cursor: 'pointer' }} onClick={() => abrirEdicao(m)}>Editar</span>
                    <span style={{ color: 'var(--rd)', cursor: 'pointer', fontSize: 13 }} onClick={() => remover(m)}>Remover</span>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}
      </div>

      {formAberto && (
        <div className="card">
          <div className="chd"><div className="ctitle">{editId === 0 ? 'Novo acesso' : 'Editar acesso'}</div></div>
          <div className="cbd">
            {error && (
              <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
                {error}
              </div>
            )}
            <div className="fgrid">
              <div className="fg">
                <label>Nome *</label>
                <input className="finp" type="text" value={nome} onChange={e => setNome(e.target.value)} />
              </div>
              <div className="fg">
                <label>Cargo</label>
                <input className="finp" type="text" placeholder="Ex: Atendente" value={cargo} onChange={e => setCargo(e.target.value)} />
              </div>
              <div className="fg">
                <label>E-mail {editId === 0 ? '*' : ''}</label>
                <input className="finp" type="email" value={email} disabled={editId !== 0}
                  title={editId !== 0 ? 'O e-mail de acesso não pode ser alterado aqui' : undefined}
                  onChange={e => setEmail(e.target.value)} />
              </div>
              <div className="fg">
                <label>{editId === 0 ? 'Senha *' : 'Nova senha'}</label>
                <input className="finp" type="password" placeholder={editId === 0 ? 'mínimo 6 caracteres' : 'deixe em branco para manter a atual'}
                  value={senha} onChange={e => setSenha(e.target.value)} />
              </div>
              <div className="fg full">
                <label>Nível de acesso *</label>
                <div style={{ display: 'flex', gap: 16, marginTop: 4 }}>
                  <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
                    <input type="radio" name="nivelEquipe" checked={nivel === 'SERVICOS'} onChange={() => setNivel('SERVICOS')} />
                    Atendimento — clientes, veículos e ordens de serviço
                  </label>
                  <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
                    <input type="radio" name="nivelEquipe" checked={nivel === 'ESTOQUE'} onChange={() => setNivel('ESTOQUE')} />
                    Estoque — peças e produtos
                  </label>
                </div>
              </div>
            </div>
            <div className="factions">
              <button className="btn btn-g" disabled={salvando} onClick={fecharForm}>Cancelar</button>
              <button className="btn btn-p" disabled={salvando} onClick={salvar}>
                {salvando ? 'Salvando...' : editId === 0 ? 'Criar acesso' : 'Salvar alterações'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

// ───────────────────────── ABA: E-MAIL (CONTA QUE ENVIA AS OS) ─────────────────────────
function AbaEmail() {
  const { user } = useAuth()
  const { showAlert } = useModal()
  const [config, setConfig] = useState<EmailConfig | null>(null)
  const [loading, setLoading] = useState(true)
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [nomeRemetente, setNomeRemetente] = useState('')
  const [destinatarioTeste, setDestinatarioTeste] = useState('')
  const [error, setError] = useState('')
  const [salvando, setSalvando] = useState(false)
  const [testando, setTestando] = useState(false)

  function carregar() {
    emailConfigAPI.buscar().then(c => {
      if (!c) return
      setConfig(c)
      setEmail(c.email || '')
      setNomeRemetente(c.nomeRemetente || '')
    }).finally(() => setLoading(false))
  }
  useEffect(() => { carregar(); setDestinatarioTeste(user?.email || '') }, [])

  async function salvar() {
    if (!email.trim()) { setError('Informe o e-mail da conta que vai enviar as mensagens'); return }
    setError('')
    setSalvando(true)
    try {
      const atualizado = await emailConfigAPI.atualizar({
        email: email.trim(),
        senha: senha.trim() || undefined,
        nomeRemetente: nomeRemetente.trim() || undefined,
      })
      if (atualizado) { setConfig(atualizado); setSenha('') }
      showAlert('Configuração salva! Use "Enviar teste" aqui abaixo pra confirmar que o e-mail e a senha funcionam antes de depender disso com clientes de verdade.', 'Tudo certo')
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Não foi possível salvar a configuração.')
    } finally {
      setSalvando(false)
    }
  }

  async function remover() {
    if (!confirm('Voltar a usar a conta padrão do MECANIX pra enviar as OS? A conta própria configurada será removida.')) return
    setSalvando(true)
    try {
      const atualizado = await emailConfigAPI.atualizar({ removerCredenciais: true })
      if (atualizado) { setConfig(atualizado); setEmail(''); setSenha(''); setNomeRemetente('') }
    } catch (e: unknown) {
      showAlert(e instanceof Error ? e.message : 'Não foi possível remover a configuração.', 'Erro')
    } finally {
      setSalvando(false)
    }
  }

  async function testar() {
    if (!destinatarioTeste.trim()) { showAlert('Informe um e-mail de destino pro teste.'); return }
    setTestando(true)
    try {
      await emailConfigAPI.testar(destinatarioTeste.trim())
      showAlert(`E-mail de teste enviado para ${destinatarioTeste.trim()}. Confira a caixa de entrada (e o spam).`, 'Enviado')
    } catch (e: unknown) {
      showAlert(e instanceof Error ? e.message : 'Não foi possível enviar o teste.', 'Erro')
    } finally {
      setTestando(false)
    }
  }

  if (loading) return <div className="card"><div style={{ padding: 24, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></div>

  const trocandoEmail = config?.email ? email.trim().toLowerCase() !== config.email.toLowerCase() : !!email.trim()

  return (
    <>
      <div className="card">
        <div className="chd"><div className="ctitle">Conta de e-mail da oficina</div></div>
        <div className="cbd">
          <div style={{ fontSize: 13, color: 'var(--tx2)', marginBottom: 14 }}>
            É essa conta que envia a Ordem de Serviço pro cliente quando você clica em "Enviar OS por e-mail". Hoje
            enviando como: <strong style={{ color: 'var(--tx)' }}>{config?.remetenteAtual}</strong>
            {config?.contaPropriaConfigurada
              ? <span className="badge bgn" style={{ marginLeft: 8 }}>Conta própria configurada</span>
              : <span className="badge bgy" style={{ marginLeft: 8 }}>Usando a conta padrão do MECANIX</span>}
          </div>

          <div className="info-bar">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z" /></svg>
            Se for usar uma conta do Gmail, a senha aqui precisa ser uma <strong>senha de app</strong> (não a senha normal da
            conta) — gerada em myaccount.google.com/apppasswords, depois de ativar a verificação em duas etapas.
          </div>

          {error && (
            <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
              {error}
            </div>
          )}

          <div className="fgrid">
            <div className="fg">
              <label>E-mail da conta *</label>
              <input className="finp" type="email" placeholder="suaoficina@gmail.com" value={email} onChange={e => setEmail(e.target.value)} />
            </div>
            <div className="fg">
              <label>Senha de app {trocandoEmail ? '*' : ''}</label>
              <input className="finp" type="password"
                placeholder={config?.contaPropriaConfigurada && !trocandoEmail ? 'deixe em branco para manter a atual' : 'senha de app, não a senha normal'}
                value={senha} onChange={e => setSenha(e.target.value)} />
            </div>
            <div className="fg full">
              <label>Nome exibido como remetente</label>
              <input className="finp" type="text" placeholder={user?.empresaNome || 'Nome da sua oficina'}
                value={nomeRemetente} onChange={e => setNomeRemetente(e.target.value)} />
            </div>
          </div>

          <div className="factions">
            {config?.contaPropriaConfigurada && (
              <button className="btn btn-g" disabled={salvando} onClick={remover}>Usar a conta padrão do MECANIX</button>
            )}
            <button className="btn btn-p" disabled={salvando} onClick={salvar}>
              {salvando ? 'Salvando...' : 'Salvar configuração'}
            </button>
          </div>
        </div>
      </div>

      {config?.contaPropriaConfigurada && (
        <div className="card">
          <div className="chd"><div className="ctitle">Testar envio</div></div>
          <div className="cbd">
            <div style={{ fontSize: 13, color: 'var(--tx2)', marginBottom: 12 }}>
              Manda um e-mail de teste usando a conta configurada acima, pra confirmar que está tudo certo antes de
              depender disso com clientes de verdade.
            </div>
            <div className="fgrid">
              <div className="fg full">
                <label>Enviar teste para</label>
                <input className="finp" type="email" value={destinatarioTeste} onChange={e => setDestinatarioTeste(e.target.value)} />
              </div>
            </div>
            <div className="factions">
              <button className="btn btn-p" disabled={testando} onClick={testar}>
                {testando ? 'Enviando...' : 'Enviar teste'}
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}
