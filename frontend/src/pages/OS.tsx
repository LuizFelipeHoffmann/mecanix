import { useEffect, useState } from 'react'
import { useNavigate, useParams, useSearchParams, Routes, Route } from 'react-router-dom'
import Layout from '../components/Layout'
import { ordensAPI, clientesAPI, veiculosAPI, estoqueAPI, fmtCur, fmtDate, osNum, STATUS_LABELS, STATUS_BADGE, type OrdemServico, type Cliente, type Veiculo, type EstoqueItem } from '../api'

function Badge({ status }: { status: string }) {
  return <span className={`badge ${STATUS_BADGE[status] || 'bgy'}`}><span className="bdot" />{STATUS_LABELS[status] || status}</span>
}

// ── LISTA ──
function OSList() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const filter = searchParams.get('filter') || ''
  const [ordens, setOrdens] = useState<OrdemServico[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  useEffect(() => { ordensAPI.listar().then(setOrdens).finally(() => setLoading(false)) }, [])

  const porStatus = filter ? ordens.filter(o => o.status === filter) : ordens
  const q = search.toLowerCase().trim()
  const filtradas = q
    ? porStatus.filter(o =>
        osNum(o.id).toLowerCase().includes(q) ||
        (o.clienteNome || '').toLowerCase().includes(q) ||
        (o.veiculoDesc || '').toLowerCase().includes(q) ||
        (o.veiculoPlaca || '').toLowerCase().includes(q) ||
        (o.mecanico || '').toLowerCase().includes(q) ||
        (STATUS_LABELS[o.status] || o.status).toLowerCase().includes(q)
      )
    : porStatus
  const STATUS_LIST = ['', 'ANDAMENTO', 'AGUARDANDO', 'AGENDADO', 'CONCLUIDO', 'CANCELADO']

  const actions = <button className="btn btn-p sm" onClick={() => navigate('/os/new')}>+ Nova OS</button>

  if (loading) return <Layout title="Ordens de Serviço" pageId="os" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  return (
    <Layout title="Ordens de Serviço" pageId="os" actions={actions}>
      <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 16 }}>
        {STATUS_LIST.map(s => {
          const cnt = s ? ordens.filter(o => o.status === s).length : ordens.length
          const lbl = s ? STATUS_LABELS[s] || s : 'Todas'
          return (
            <button key={s} className={`btn ${filter === s ? 'btn-p' : 'btn-g'} sm`}
              onClick={() => navigate(s ? `/os?filter=${s}` : '/os')}>
              {lbl} <span style={{ opacity: .7 }}>({cnt})</span>
            </button>
          )
        })}
      </div>
      <div className="card">
        <div className="chd">
          <div className="ctitle">Ordens de Serviço ({filtradas.length}{q ? ` de ${porStatus.length}` : ''})</div>
          <input
            type="search"
            placeholder="Buscar por OS, cliente, veículo, placa, mecânico..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ background: 'var(--bg3)', border: '1px solid var(--brd)', borderRadius: 8, color: 'var(--tx)', padding: '6px 11px', fontSize: 13, outline: 'none', width: 'min(100%, 320px)' }}
          />
        </div>
        <div className="tbl-wrap tbl-desktop">
          <table className="tbl">
            <thead><tr><th>OS</th><th>Cliente</th><th>Veículo</th><th>Placa</th><th>Mecânico</th><th>Status</th><th style={{ textAlign: 'right' }}>Total</th><th>Data</th></tr></thead>
            <tbody>
              {filtradas.length ? filtradas.map(o => (
                <tr key={o.id} style={{ cursor: 'pointer' }} onClick={() => navigate(`/os/${o.id}`)}>
                  <td className="osnum">{osNum(o.id)}</td>
                  <td style={{ fontWeight: 500 }}>{o.clienteNome || '—'}</td>
                  <td style={{ color: 'var(--tx2)' }}>{o.veiculoDesc || '—'}</td>
                  <td style={{ color: 'var(--tx2)' }}>{o.veiculoPlaca || '—'}</td>
                  <td style={{ color: 'var(--tx2)' }}>{o.mecanico || '—'}</td>
                  <td><Badge status={o.status} /></td>
                  <td style={{ fontWeight: 700, color: 'var(--or)', textAlign: 'right' }}>{fmtCur(o.total)}</td>
                  <td>{fmtDate(o.data)}</td>
                </tr>
              )) : <tr><td colSpan={8} style={{ textAlign: 'center', color: 'var(--tx3)', padding: 24 }}>Nenhuma OS encontrada.</td></tr>}
            </tbody>
          </table>
        </div>
        <div className="mob-list cbd">
          {filtradas.map(o => (
            <div key={o.id} className="mob-card" onClick={() => navigate(`/os/${o.id}`)}>
              <div className="mob-card-top"><span className="osnum">{osNum(o.id)}</span><Badge status={o.status} /></div>
              <div style={{ fontWeight: 600, fontSize: 15, color: 'var(--tx)' }}>{o.clienteNome || '—'}</div>
              <div style={{ fontSize: 13, color: 'var(--tx2)', marginBottom: 6 }}>{o.veiculoDesc || '—'} · {o.veiculoPlaca || '—'}</div>
              <div style={{ fontWeight: 700, color: 'var(--or)' }}>{fmtCur(o.total)}</div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  )
}

// ── FORMULÁRIO ──
interface FormSvc { d: string; v: number }
interface FormPeca { id: number | null; nome: string; qtd: number; preco: number }

function OSForm() {
  const navigate = useNavigate()
  const { id } = useParams()
  const editId = id ? parseInt(id) : null
  const title = editId ? 'Editar OS' : 'Nova OS'

  const [clientes, setClientes] = useState<Cliente[]>([])
  const [veiculos, setVeiculos] = useState<Veiculo[]>([])
  const [estoqueCache, setEstoqueCache] = useState<EstoqueItem[]>([])
  const [clienteId, setClienteId] = useState('')
  const [veiculoId, setVeiculoId] = useState('')
  const [status, setStatus] = useState('ANDAMENTO')
  const [mecanico, setMecanico] = useState('')
  const [data, setData] = useState(new Date().toISOString().split('T')[0])
  const [obs, setObs] = useState('')
  const [svcs, setSvcs] = useState<FormSvc[]>([])
  const [pecas, setPecas] = useState<FormPeca[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    Promise.all([clientesAPI.listar(), estoqueAPI.listar()]).then(async ([cls, est]) => {
      setClientes(cls); setEstoqueCache(est)
      if (editId) {
        const os = await ordensAPI.buscar(editId)
        setClienteId(String(os.clienteId)); setStatus(os.status); setMecanico(os.mecanico || '')
        setData(os.data || new Date().toISOString().split('T')[0]); setObs(os.observacoes || '')
        setSvcs((os.servicos || []).map(s => ({ d: s.descricao, v: s.valor })))
        setPecas((os.pecas || []).map(p => ({ id: p.estoqueId, nome: p.nomePeca || '', qtd: p.quantidade, preco: p.precoUnitario })))
        if (os.clienteId) {
          const vl = await veiculosAPI.listarPorCliente(os.clienteId)
          setVeiculos(vl); setVeiculoId(String(os.veiculoId))
        }
      } else if (cls.length) {
        const first = cls[0]
        setClienteId(String(first.id))
        const vl = await veiculosAPI.listarPorCliente(first.id)
        setVeiculos(vl)
        if (vl.length) setVeiculoId(String(vl[0].id))
      }
    }).finally(() => setLoading(false))
  }, [editId])

  async function onClienteChange(cid: string) {
    setClienteId(cid); setVeiculoId(''); setVeiculos([])
    if (!cid) return
    try {
      const vl = await veiculosAPI.listarPorCliente(parseInt(cid))
      setVeiculos(vl)
      if (vl.length) setVeiculoId(String(vl[0].id))
    } catch {}
  }

  function addSvc() { setSvcs(s => [...s, { d: '', v: 0 }]) }
  function removeSvc(i: number) { setSvcs(s => s.filter((_, j) => j !== i)) }
  function updateSvc(i: number, field: 'd' | 'v', val: string | number) {
    setSvcs(s => s.map((x, j) => j === i ? { ...x, [field]: val } : x))
  }

  function addPeca() { setPecas(p => [...p, { id: null, nome: '', qtd: 1, preco: 0 }]) }
  function removePeca(i: number) { setPecas(p => p.filter((_, j) => j !== i)) }
  function selectPeca(i: number, estoqueId: string) {
    const item = estoqueCache.find(e => e.id === parseInt(estoqueId))
    if (item) setPecas(p => p.map((x, j) => j === i ? { ...x, id: item.id, nome: item.nome, preco: item.precoUnitario } : x))
  }
  function updatePeca(i: number, field: 'qtd' | 'preco', val: number) {
    setPecas(p => p.map((x, j) => j === i ? { ...x, [field]: val } : x))
  }

  const totalSvcs = svcs.reduce((a, s) => a + (parseFloat(String(s.v)) || 0), 0)
  const totalPecas = pecas.reduce((a, p) => a + (parseFloat(String(p.preco)) || 0) * (parseInt(String(p.qtd)) || 1), 0)

  async function save() {
    if (!clienteId) { setError('Selecione o cliente'); return }
    if (!veiculoId) { setError('Selecione o veículo'); return }
    setSaving(true); setError('')
    const dados = {
      clienteId: parseInt(clienteId), veiculoId: parseInt(veiculoId), status, mecanico, data, observacoes: obs,
      servicos: svcs.filter(s => s.d).map(s => ({ descricao: s.d, valor: parseFloat(String(s.v)) || 0 })),
      pecas: pecas.filter(p => p.id).map(p => ({ estoqueId: p.id, quantidade: parseInt(String(p.qtd)) || 1, precoUnitario: parseFloat(String(p.preco)) || 0 })),
    }
    try {
      const os = editId ? await ordensAPI.atualizar(editId, dados) : await ordensAPI.criar(dados)
      navigate(`/os/${os.id}`)
    } catch (e: unknown) { setError(e instanceof Error ? e.message : 'Erro ao salvar'); setSaving(false) }
  }

  async function del() {
    if (!editId || !confirm('Excluir esta OS?')) return
    try { await ordensAPI.deletar(editId); navigate('/os') }
    catch (e: unknown) { alert('Erro: ' + (e instanceof Error ? e.message : '')) }
  }

  const STATUS_LIST = ['ANDAMENTO', 'AGUARDANDO', 'AGENDADO', 'CONCLUIDO', 'CANCELADO']
  const actions = <button className="btn btn-g sm" onClick={() => navigate('/os')}>← Voltar</button>

  if (loading) return <Layout title={title} pageId="os" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  return (
    <Layout title={title} pageId="os" actions={actions}>
      <div className="card" style={{ marginBottom: 16 }}>
        <div className="chd"><div className="ctitle">Dados da OS</div></div>
        <div className="cbd">
          <div className="fgrid">
            <div className="fg full">
              <label>Cliente *</label>
              <select className="finp" value={clienteId} onChange={e => onClienteChange(e.target.value)}>
                <option value="">Selecione...</option>
                {clientes.map(c => <option key={c.id} value={c.id}>{c.nome}</option>)}
              </select>
            </div>
            <div className="fg full">
              <label>Veículo *</label>
              <select className="finp" value={veiculoId} onChange={e => setVeiculoId(e.target.value)}>
                {veiculos.length ? veiculos.map(v => <option key={v.id} value={v.id}>{v.placa} — {v.marca} {v.modelo} {v.ano || ''}</option>) : <option value="">Selecione o cliente primeiro...</option>}
              </select>
            </div>
            <div className="fg">
              <label>Status</label>
              <select className="finp" value={status} onChange={e => setStatus(e.target.value)}>
                {STATUS_LIST.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
              </select>
            </div>
            <div className="fg"><label>Mecânico</label><input className="finp" type="text" placeholder="Nome do mecânico" value={mecanico} onChange={e => setMecanico(e.target.value)} /></div>
            <div className="fg"><label>Data</label><input className="finp" type="date" value={data} onChange={e => setData(e.target.value)} /></div>
            <div className="fg full"><label>Observações</label><input className="finp" type="text" placeholder="Observações gerais" value={obs} onChange={e => setObs(e.target.value)} /></div>
          </div>
        </div>
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <div className="chd">
          <div className="ctitle">Serviços</div>
          <button className="btn btn-g sm" onClick={addSvc}>+ Adicionar serviço</button>
        </div>
        <div className="cbd">
          {svcs.length ? svcs.map((s, i) => (
            <div key={i} style={{ display: 'flex', gap: 10, alignItems: 'center', paddingBottom: 8, borderBottom: '1px solid var(--brd)', marginBottom: 8 }}>
              <input className="finp" type="text" placeholder="Descrição do serviço" style={{ flex: 2 }} value={s.d} onChange={e => updateSvc(i, 'd', e.target.value)} />
              <input className="finp" type="number" placeholder="Valor R$" style={{ width: 120, flexShrink: 0 }} step={0.01} value={s.v || ''} onChange={e => updateSvc(i, 'v', parseFloat(e.target.value) || 0)} />
              <button className="btn sm" style={{ background: 'var(--rdd)', color: 'var(--rd)', border: 'none' }} onClick={() => removeSvc(i)}>×</button>
            </div>
          )) : <div style={{ fontSize: 13, color: 'var(--tx3)' }}>Nenhum serviço adicionado.</div>}
          {svcs.length > 0 && <div style={{ textAlign: 'right', fontSize: 13, color: 'var(--tx2)', marginTop: 8 }}>Total serviços: {fmtCur(totalSvcs)}</div>}
        </div>
      </div>

      <div className="card" style={{ marginBottom: 16 }}>
        <div className="chd">
          <div className="ctitle">Peças</div>
          <button className="btn btn-g sm" onClick={addPeca}>+ Adicionar peça</button>
        </div>
        <div className="cbd">
          {pecas.length ? pecas.map((p, i) => (
            <div key={i} style={{ display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap', paddingBottom: 8, borderBottom: '1px solid var(--brd)', marginBottom: 8 }}>
              <select className="finp" style={{ flex: 2, minWidth: 160 }} value={p.id || ''} onChange={e => selectPeca(i, e.target.value)}>
                <option value="">Selecione...</option>
                {estoqueCache.map(e => <option key={e.id} value={e.id}>{e.nome} (est: {e.quantidade})</option>)}
              </select>
              <input className="finp" type="number" placeholder="Qtd" style={{ width: 70, flexShrink: 0 }} min={1} value={p.qtd} onChange={e => updatePeca(i, 'qtd', parseInt(e.target.value) || 1)} />
              <input className="finp" type="number" placeholder="R$ unit." style={{ width: 110, flexShrink: 0 }} step={0.01} value={p.preco || ''} onChange={e => updatePeca(i, 'preco', parseFloat(e.target.value) || 0)} />
              <button className="btn sm" style={{ background: 'var(--rdd)', color: 'var(--rd)', border: 'none' }} onClick={() => removePeca(i)}>×</button>
            </div>
          )) : <div style={{ fontSize: 13, color: 'var(--tx3)' }}>Nenhuma peça adicionada.</div>}
          {pecas.length > 0 && <div style={{ textAlign: 'right', fontSize: 13, color: 'var(--tx2)', marginTop: 8 }}>Total peças: {fmtCur(totalPecas)}</div>}
        </div>
      </div>

      <div className="card">
        <div className="cbd">
          {error && <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 12 }}>{error}</div>}
          <div className="factions">
            <button className="btn btn-g" onClick={() => navigate('/os')}>Cancelar</button>
            {editId && <button className="btn" style={{ background: 'var(--rdd)', color: 'var(--rd)', border: '1px solid rgba(239,68,68,.3)' }} onClick={del}>Excluir</button>}
            <button className="btn btn-p" disabled={saving} onClick={save}>{saving ? 'Salvando...' : editId ? 'Salvar' : 'Criar OS'}</button>
          </div>
        </div>
      </div>
    </Layout>
  )
}

// ── DETALHES ──
function OSDetail() {
  const navigate = useNavigate()
  const { id } = useParams()
  const osId = parseInt(id!)
  const [os, setOs] = useState<OrdemServico | null>(null)
  const [loading, setLoading] = useState(true)
  const [emailSending, setEmailSending] = useState(false)
  const [emailDone, setEmailDone] = useState(false)

  useEffect(() => {
    ordensAPI.buscar(osId).then(setOs).finally(() => setLoading(false))
  }, [osId])

  async function mudarStatus(novoStatus: string) {
    if (!os || !confirm(`Alterar status para "${STATUS_LABELS[novoStatus]}"?`)) return
    try {
      await ordensAPI.atualizar(osId, {
        clienteId: os.clienteId, veiculoId: os.veiculoId, status: novoStatus, mecanico: os.mecanico,
        data: os.data, observacoes: os.observacoes,
        servicos: (os.servicos || []).map(s => ({ descricao: s.descricao, valor: s.valor })),
        pecas: (os.pecas || []).map(p => ({ estoqueId: p.estoqueId, quantidade: p.quantidade, precoUnitario: p.precoUnitario })),
      })
      const updated = await ordensAPI.buscar(osId)
      setOs(updated)
    } catch (e: unknown) { alert('Erro: ' + (e instanceof Error ? e.message : '')) }
  }

  async function enviarEmail() {
    setEmailSending(true)
    try {
      await ordensAPI.enviarEmail(osId)
      setEmailDone(true)
      const t = document.getElementById('email-toast')
      const b = document.getElementById('toast-body')
      if (t && b) {
        b.innerHTML = `OS enviada com sucesso para <strong>${os?.clienteEmail}</strong>`
        t.classList.add('show')
        setTimeout(() => t.classList.remove('show'), 6000)
      }
      setTimeout(() => setEmailDone(false), 3000)
    } catch (e: unknown) {
      alert('Erro ao enviar e-mail: ' + (e instanceof Error ? e.message : '') + '\n\nVerifique se o Gmail e a senha de app estão configurados em application.properties')
    } finally { setEmailSending(false) }
  }

  const actions = (
    <div style={{ display: 'flex', gap: 8 }}>
      <button className="btn btn-g sm" onClick={() => navigate('/os')}>← Voltar</button>
      <button className="btn btn-p sm" onClick={() => window.print()}>🖨️ Imprimir</button>
    </div>
  )

  if (loading) return <Layout title="Detalhes da OS" pageId="os" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>
  if (!os) return <Layout title="Detalhes da OS" pageId="os" actions={actions}><div style={{ padding: 20, color: 'var(--rd)' }}>OS não encontrada.</div></Layout>

  return (
    <Layout title="Detalhes da OS" pageId="os" actions={actions}>
      <div className="card">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12, padding: '20px 20px 16px' }}>
          <div>
            <div style={{ fontSize: 22, fontWeight: 700, color: 'var(--or)' }}>{osNum(os.id)}</div>
            <div style={{ fontSize: 13, color: 'var(--tx2)', marginTop: 4 }}>Emitida em {fmtDate(os.data)}</div>
          </div>
          <div style={{ display: 'flex', gap: 8, alignItems: 'center', flexWrap: 'wrap' }}>
            <Badge status={os.status} />
            <button className="btn btn-g sm" onClick={() => navigate(`/os/${os.id}/edit`)}>✏️ Editar</button>
            {os.status !== 'CONCLUIDO' && <button className="btn sm" style={{ background: 'var(--gn)', color: '#fff', border: 'none' }} onClick={() => mudarStatus('CONCLUIDO')}>✅ Concluir</button>}
            {os.status === 'ANDAMENTO' && <button className="btn sm" style={{ background: 'var(--bl)', color: '#fff', border: 'none' }} onClick={() => mudarStatus('AGUARDANDO')}>⏸ Aguardar peça</button>}
            {os.status === 'AGUARDANDO' && <button className="btn sm" style={{ background: 'var(--yw)', color: '#000', border: 'none' }} onClick={() => mudarStatus('ANDAMENTO')}>▶ Retomar</button>}
            {os.status !== 'CANCELADO' && os.status !== 'CONCLUIDO' && <button className="btn sm" style={{ background: 'var(--rd)', color: '#fff', border: 'none' }} onClick={() => mudarStatus('CANCELADO')}>✖ Cancelar</button>}
            {os.status === 'CONCLUIDO' && <button className="btn sm" style={{ background: 'var(--yw)', color: '#000', border: 'none' }} onClick={() => mudarStatus('ANDAMENTO')}>↩ Reabrir</button>}
            {os.clienteEmail
              ? <button className="btn btn-g sm" disabled={emailSending} onClick={enviarEmail}>{emailDone ? '✅ E-mail enviado!' : emailSending ? 'Enviando...' : '📧 Enviar OS por e-mail'}</button>
              : <span style={{ fontSize: 11, color: 'var(--tx3)' }}>Cliente sem e-mail</span>}
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit,minmax(200px,1fr))', gap: 16, padding: '16px 20px', borderTop: '1px solid var(--brd)', borderBottom: '1px solid var(--brd)' }}>
          <div><div style={{ fontSize: 11, color: 'var(--tx3)', marginBottom: 4 }}>CLIENTE</div><div style={{ fontWeight: 600 }}>{os.clienteNome}</div></div>
          <div><div style={{ fontSize: 11, color: 'var(--tx3)', marginBottom: 4 }}>VEÍCULO</div><div style={{ fontWeight: 600 }}>{os.veiculoDesc}</div><div style={{ fontSize: 13, color: 'var(--tx2)' }}>{os.veiculoPlaca}</div></div>
          <div><div style={{ fontSize: 11, color: 'var(--tx3)', marginBottom: 4 }}>MECÂNICO</div><div style={{ fontWeight: 600 }}>{os.mecanico || '—'}</div></div>
          {os.observacoes && <div><div style={{ fontSize: 11, color: 'var(--tx3)', marginBottom: 4 }}>OBSERVAÇÕES</div><div style={{ fontSize: 13, color: 'var(--tx2)' }}>{os.observacoes}</div></div>}
        </div>

        <div style={{ padding: 20 }}>
          <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--tx)', marginBottom: 10 }}>Serviços</div>
          <table className="tbl" style={{ marginBottom: 20 }}>
            <thead><tr><th>Descrição</th><th style={{ textAlign: 'right' }}>Valor</th></tr></thead>
            <tbody>
              {(os.servicos || []).length ? os.servicos!.map((s, i) => (
                <tr key={i}><td>{s.descricao}</td><td style={{ textAlign: 'right', color: 'var(--or)', fontWeight: 600 }}>{fmtCur(s.valor)}</td></tr>
              )) : <tr><td colSpan={2} style={{ color: 'var(--tx3)' }}>Nenhum serviço.</td></tr>}
            </tbody>
            <tfoot><tr style={{ borderTop: '1px solid var(--brd)' }}><td style={{ color: 'var(--tx2)', fontSize: 12 }}>Total serviços</td><td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--or)' }}>{fmtCur(os.totalServicos)}</td></tr></tfoot>
          </table>

          <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--tx)', marginBottom: 10 }}>Peças</div>
          <table className="tbl">
            <thead><tr><th>Peça</th><th style={{ textAlign: 'center' }}>Qtd</th><th style={{ textAlign: 'right' }}>Unit.</th><th style={{ textAlign: 'right' }}>Subtotal</th></tr></thead>
            <tbody>
              {(os.pecas || []).length ? os.pecas!.map((p, i) => (
                <tr key={i}>
                  <td>{p.nomePeca}</td>
                  <td style={{ textAlign: 'center', color: 'var(--tx2)' }}>{p.quantidade}</td>
                  <td style={{ textAlign: 'right', color: 'var(--tx2)' }}>{fmtCur(p.precoUnitario)}</td>
                  <td style={{ textAlign: 'right', color: 'var(--or)', fontWeight: 600 }}>{fmtCur(p.subtotal)}</td>
                </tr>
              )) : <tr><td colSpan={4} style={{ color: 'var(--tx3)' }}>Nenhuma peça.</td></tr>}
            </tbody>
            <tfoot><tr style={{ borderTop: '1px solid var(--brd)' }}><td colSpan={3} style={{ color: 'var(--tx2)', fontSize: 12 }}>Total peças</td><td style={{ textAlign: 'right', fontWeight: 700, color: 'var(--or)' }}>{fmtCur(os.totalPecas)}</td></tr></tfoot>
          </table>

          <div style={{ marginTop: 20, paddingTop: 16, borderTop: '2px solid var(--brd)', display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: 12 }}>
            <span style={{ fontSize: 15, color: 'var(--tx2)' }}>Total geral</span>
            <span style={{ fontSize: 22, fontWeight: 700, color: 'var(--or)' }}>{fmtCur(os.total)}</span>
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default function OS() {
  return (
    <Routes>
      <Route index element={<OSList />} />
      <Route path="new" element={<OSForm />} />
      <Route path=":id" element={<OSDetail />} />
      <Route path=":id/edit" element={<OSForm />} />
    </Routes>
  )
}
