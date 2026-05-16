import { useEffect, useState } from 'react'
import { useNavigate, useParams, Routes, Route } from 'react-router-dom'
import Layout from '../components/Layout'
import { clientesAPI, maskPhone, type Cliente } from '../api'

function ClienteList() {
  const navigate = useNavigate()
  const [clientes, setClientes] = useState<Cliente[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    clientesAPI.listar().then(setClientes).finally(() => setLoading(false))
  }, [])

  const actions = <button className="btn btn-p sm" onClick={() => navigate('/clientes/new')}>+ Novo cliente</button>

  if (loading) return <Layout title="Clientes" pageId="clientes" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  return (
    <Layout title="Clientes" pageId="clientes" actions={actions}>
      <div className="card">
        <div className="chd"><div className="ctitle">Clientes ({clientes.length})</div></div>
        <div className="tbl-wrap tbl-desktop">
          <table className="tbl">
            <thead><tr><th>Nome</th><th>CPF</th><th>Telefone</th><th>Veículos</th><th>OS</th><th /></tr></thead>
            <tbody>
              {clientes.map(c => (
                <tr key={c.id}>
                  <td style={{ fontWeight: 500 }}>{c.nome}</td>
                  <td style={{ color: 'var(--tx2)' }}>{c.cpf}</td>
                  <td style={{ color: 'var(--tx2)' }}>{c.telefone || '—'}</td>
                  <td><span className="badge bbl">{c.totalVeiculos} veíc.</span></td>
                  <td><span className="badge bgy">{c.totalOrdens} OS</span></td>
                  <td>
                    <div className="ib" onClick={() => navigate(`/clientes/${c.id}/edit`)}>
                      <svg viewBox="0 0 24 24"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z" /></svg>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div className="mob-list cbd">
          {clientes.map(c => (
            <div key={c.id} className="mob-card">
              <div className="mob-card-top">
                <span style={{ fontWeight: 600, fontSize: 15 }}>{c.nome}</span>
                <div className="ib" onClick={() => navigate(`/clientes/${c.id}/edit`)}>
                  <svg viewBox="0 0 24 24"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z" /></svg>
                </div>
              </div>
              <div className="mob-card-sub">{c.cpf} · {c.telefone || '—'}</div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  )
}

function ClienteForm() {
  const navigate = useNavigate()
  const { id } = useParams()
  const editId = id ? parseInt(id) : null
  const title = editId ? 'Editar Cliente' : 'Novo Cliente'

  const [nome, setNome] = useState('')
  const [cpf, setCpf] = useState('')
  const [telefone, setTelefone] = useState('')
  const [email, setEmail] = useState('')
  const [endereco, setEndereco] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!editId) return
    setLoading(true)
    clientesAPI.buscar(editId).then(c => {
      setNome(c.nome); setCpf(c.cpf); setTelefone(c.telefone || ''); setEmail(c.email || ''); setEndereco(c.endereco || '')
    }).finally(() => setLoading(false))
  }, [editId])

  async function save() {
    if (!nome || !cpf) { setError('Nome e CPF são obrigatórios'); return }
    setSaving(true); setError('')
    try {
      if (editId) await clientesAPI.atualizar(editId, { nome, cpf, telefone, email, endereco })
      else await clientesAPI.criar({ nome, cpf, telefone, email, endereco })
      navigate('/clientes')
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Erro ao salvar')
      setSaving(false)
    }
  }

  const actions = <button className="btn btn-g sm" onClick={() => navigate('/clientes')}>← Voltar</button>

  if (loading) return <Layout title={title} pageId="clientes" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  return (
    <Layout title={title} pageId="clientes" actions={actions}>
      <div className="card">
        <div className="chd"><div className="ctitle">{title}</div></div>
        <div className="cbd">
          <div className="fgrid">
            <div className="fg full">
              <label>Nome completo *</label>
              <input className="finp" type="text" placeholder="Ex: Maria da Silva" value={nome} onChange={e => setNome(e.target.value)} />
            </div>
            <div className="fg">
              <label>CPF *</label>
              <input className="finp" type="text" placeholder="000.000.000-00" maxLength={14} value={cpf} onChange={e => setCpf(e.target.value)} />
            </div>
            <div className="fg">
              <label>Telefone</label>
              <input className="finp" type="tel" placeholder="(41) 99999-0000" maxLength={15} value={telefone} onChange={e => setTelefone(maskPhone(e.target.value))} />
            </div>
            <div className="fg full">
              <label>E-mail</label>
              <input className="finp" type="email" placeholder="email@exemplo.com" value={email} onChange={e => setEmail(e.target.value)} />
            </div>
            <div className="fg full">
              <label>Endereço</label>
              <input className="finp" type="text" placeholder="Rua, número, bairro, cidade - UF" value={endereco} onChange={e => setEndereco(e.target.value)} />
            </div>
          </div>
          {error && <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 12 }}>{error}</div>}
          <div className="factions">
            <button className="btn btn-g" onClick={() => navigate('/clientes')}>Cancelar</button>
            <button className="btn btn-p" disabled={saving} onClick={save}>{saving ? 'Salvando...' : editId ? 'Salvar' : 'Cadastrar'}</button>
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default function Clientes() {
  return (
    <Routes>
      <Route index element={<ClienteList />} />
      <Route path="new" element={<ClienteForm />} />
      <Route path=":id/edit" element={<ClienteForm />} />
    </Routes>
  )
}
