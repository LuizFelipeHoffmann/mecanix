import { useEffect, useState, useRef } from 'react'
import { useNavigate, useParams, useSearchParams, Routes, Route } from 'react-router-dom'
import Layout from '../components/Layout'
import { veiculosAPI, clientesAPI, maskPlate, TIPO_LABELS, TIPO_COLORS, type Veiculo, type Cliente } from '../api'
import { searchMarcas, searchModelos } from '../data/marcas'

function VeiculoList() {
  const navigate = useNavigate()
  const [veiculos, setVeiculos] = useState<Veiculo[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  useEffect(() => {
    veiculosAPI.listar().then(setVeiculos).finally(() => setLoading(false))
  }, [])

  const q = search.toLowerCase().trim()
  const filtrados = q
    ? veiculos.filter(v =>
        v.placa.toLowerCase().includes(q) ||
        v.marca.toLowerCase().includes(q) ||
        v.modelo.toLowerCase().includes(q) ||
        (v.cor || '').toLowerCase().includes(q) ||
        (v.clienteNome || '').toLowerCase().includes(q) ||
        (TIPO_LABELS[v.tipo] || v.tipo).toLowerCase().includes(q) ||
        (v.ano || '').includes(q)
      )
    : veiculos

  const actions = <button className="btn btn-p sm" onClick={() => navigate('/veiculos/new')}>+ Novo veículo</button>

  if (loading) return <Layout title="Veículos" pageId="veiculos" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  return (
    <Layout title="Veículos" pageId="veiculos" actions={actions}>
      <div className="card">
        <div className="chd">
          <div className="ctitle">Veículos ({filtrados.length}{q ? ` de ${veiculos.length}` : ''})</div>
          <input
            type="search"
            placeholder="Buscar por placa, marca, modelo, proprietário..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ background: 'var(--bg3)', border: '1px solid var(--brd)', borderRadius: 8, color: 'var(--tx)', padding: '6px 11px', fontSize: 13, outline: 'none', width: 'min(100%, 320px)' }}
          />
        </div>
        <div className="tbl-wrap tbl-desktop">
          <table className="tbl">
            <thead><tr><th>Placa</th><th>Marca/Modelo</th><th>Ano</th><th>Cor</th><th>Tipo</th><th>Proprietário</th><th>OS</th><th /></tr></thead>
            <tbody>
              {filtrados.length ? filtrados.map(v => {
                const col = TIPO_COLORS[v.tipo] || '#7a8099'
                return (
                  <tr key={v.id}>
                    <td className="osnum">{v.placa}</td>
                    <td style={{ fontWeight: 500 }}>{v.marca} {v.modelo}</td>
                    <td style={{ color: 'var(--tx2)' }}>{v.ano || '—'}</td>
                    <td style={{ color: 'var(--tx2)' }}>{v.cor || '—'}</td>
                    <td><span style={{ background: col + '22', color: col, fontSize: 10, fontWeight: 600, padding: '2px 7px', borderRadius: 4 }}>{TIPO_LABELS[v.tipo] || v.tipo}</span></td>
                    <td>{v.clienteNome || '—'}</td>
                    <td><span className="badge bbl">{v.totalOrdens} OS</span></td>
                    <td><div className="ib" onClick={() => navigate(`/veiculos/${v.id}/edit`)}><svg viewBox="0 0 24 24"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z" /></svg></div></td>
                  </tr>
                )
              }) : <tr><td colSpan={8} style={{ textAlign: 'center', color: 'var(--tx3)', padding: 24 }}>{q ? 'Nenhum veículo encontrado.' : 'Nenhum veículo cadastrado.'}</td></tr>}
            </tbody>
          </table>
        </div>
        <div className="mob-list cbd">
          {filtrados.map(v => {
            const col = TIPO_COLORS[v.tipo] || '#7a8099'
            return (
              <div key={v.id} className="mob-card">
                <div className="mob-card-top">
                  <span className="osnum">{v.placa}</span>
                  <span style={{ background: col + '22', color: col, fontSize: 11, fontWeight: 600, padding: '3px 8px', borderRadius: 5 }}>{TIPO_LABELS[v.tipo] || v.tipo}</span>
                </div>
                <div style={{ fontWeight: 600, fontSize: 15, color: 'var(--tx)', marginBottom: 4 }}>{v.marca} {v.modelo} {v.ano || ''} · {v.cor || ''}</div>
                <div className="mob-card-row">
                  <span style={{ color: 'var(--tx2)', fontSize: 13 }}>{v.clienteNome || '—'}</span>
                  <div className="ib" onClick={() => navigate(`/veiculos/${v.id}/edit`)}><svg viewBox="0 0 24 24"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z" /></svg></div>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </Layout>
  )
}

function VeiculoForm() {
  const navigate = useNavigate()
  const { id } = useParams()
  const [searchParams] = useSearchParams()
  const editId = id ? parseInt(id) : null
  const preCliId = searchParams.get('clienteId') ? parseInt(searchParams.get('clienteId')!) : null
  const title = editId ? 'Editar Veículo' : 'Novo Veículo'

  const [clientes, setClientes] = useState<Cliente[]>([])
  const [clienteId, setClienteId] = useState('')
  const [marca, setMarca] = useState('')
  const [modelo, setModelo] = useState('')
  const [ano, setAno] = useState('')
  const [cor, setCor] = useState('')
  const [placa, setPlaca] = useState('')
  const [tipo, setTipo] = useState('SEDAN')
  const [km, setKm] = useState('')
  const [obs, setObs] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  const [marcaSugg, setMarcaSugg] = useState<string[]>([])
  const [modeloSugg, setModeloSugg] = useState<string[]>([])
  const marcaRef = useRef<HTMLDivElement>(null)
  const modeloRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    Promise.all([
      clientesAPI.listar(),
      editId ? veiculosAPI.buscar(editId) : Promise.resolve(null),
    ]).then(([cls, v]) => {
      setClientes(cls)
      if (v) {
        setClienteId(String(v.clienteId)); setMarca(v.marca); setModelo(v.modelo)
        setAno(v.ano || ''); setCor(v.cor || ''); setPlaca(v.placa); setTipo(v.tipo)
        setKm(v.quilometragem ? String(v.quilometragem) : ''); setObs(v.observacoes || '')
      } else if (preCliId) {
        setClienteId(String(preCliId))
      }
    }).finally(() => setLoading(false))
  }, [editId, preCliId])

  function onMarcaInput(val: string) {
    setMarca(val)
    setModelo('')
    setMarcaSugg(val ? searchMarcas(val) : [])
    setModeloSugg([])
  }

  function selectMarca(m: string) {
    setMarca(m); setModelo(''); setMarcaSugg([]); setModeloSugg([])
  }

  function onModeloInput(val: string) {
    setModelo(val)
    setModeloSugg(val ? searchModelos(marca, val) : [])
  }

  function selectModelo(m: string) {
    setModelo(m); setModeloSugg([])
  }

  async function save() {
    if (!clienteId) { setError('Selecione o proprietário'); return }
    if (!marca) { setError('Informe a marca'); return }
    if (!modelo) { setError('Informe o modelo'); return }
    if (!placa) { setError('Informe a placa'); return }
    setSaving(true); setError('')
    const dados = { clienteId: parseInt(clienteId), marca, modelo, ano, cor, placa: placa.toUpperCase(), tipo, quilometragem: km ? parseInt(km) : undefined, observacoes: obs }
    try {
      if (editId) await veiculosAPI.atualizar(editId, dados)
      else await veiculosAPI.criar(dados)
      navigate('/veiculos')
    } catch (e: unknown) { setError(e instanceof Error ? e.message : 'Erro ao salvar'); setSaving(false) }
  }

  async function del() {
    if (!editId || !confirm('Excluir este veículo?')) return
    try { await veiculosAPI.deletar(editId); navigate('/veiculos') }
    catch (e: unknown) { alert('Erro: ' + (e instanceof Error ? e.message : '')) }
  }

  const actions = <button className="btn btn-g sm" onClick={() => navigate('/veiculos')}>← Voltar</button>

  if (loading) return <Layout title={title} pageId="veiculos" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  const TIPOS = ['SEDAN', 'HATCH', 'SUV', 'PICKUP', 'ELETRICO']

  return (
    <Layout title={title} pageId="veiculos" actions={actions}>
      <div className="card">
        <div className="chd"><div className="ctitle">{title}</div></div>
        <div className="cbd">
          <div className="fgrid">
            <div className="fg full">
              <label>Proprietário *</label>
              <select className="finp" value={clienteId} onChange={e => setClienteId(e.target.value)}>
                <option value="">Selecione...</option>
                {clientes.map(c => <option key={c.id} value={c.id}>{c.nome} — {c.cpf}</option>)}
              </select>
            </div>
            <div className="fg" ref={marcaRef} style={{ position: 'relative' }}>
              <label>Marca *</label>
              <input className="finp" type="text" placeholder="Ex: Honda" autoComplete="off"
                value={marca} onChange={e => onMarcaInput(e.target.value)}
                onBlur={() => setTimeout(() => setMarcaSugg([]), 200)} />
              {marcaSugg.length > 0 && (
                <div className="ac-drop show">
                  {marcaSugg.map(m => <div key={m} className="ac-item" onMouseDown={() => selectMarca(m)}><span className="ac-name">{m}</span></div>)}
                </div>
              )}
            </div>
            <div className="fg" ref={modeloRef} style={{ position: 'relative' }}>
              <label>Modelo *</label>
              <input className="finp" type="text" placeholder="Ex: Civic" autoComplete="off"
                value={modelo} onChange={e => onModeloInput(e.target.value)}
                onBlur={() => setTimeout(() => setModeloSugg([]), 200)} />
              {modeloSugg.length > 0 && (
                <div className="ac-drop show">
                  {modeloSugg.map(m => <div key={m} className="ac-item" onMouseDown={() => selectModelo(m)}><span className="ac-name">{m}</span></div>)}
                </div>
              )}
            </div>
            <div className="fg"><label>Ano</label><input className="finp" type="text" placeholder="2022" maxLength={4} value={ano} onChange={e => setAno(e.target.value)} /></div>
            <div className="fg"><label>Cor</label><input className="finp" type="text" placeholder="Prata" value={cor} onChange={e => setCor(e.target.value)} /></div>
            <div className="fg"><label>Placa *</label><input className="finp" type="text" placeholder="ABC-1234" maxLength={8} value={placa} onChange={e => setPlaca(maskPlate(e.target.value))} style={{ textTransform: 'uppercase' }} /></div>
            <div className="fg">
              <label>Tipo</label>
              <select className="finp" value={tipo} onChange={e => setTipo(e.target.value)}>
                {TIPOS.map(t => <option key={t} value={t}>{TIPO_LABELS[t] || t}</option>)}
              </select>
            </div>
            <div className="fg"><label>Km</label><input className="finp" type="number" placeholder="45000" value={km} onChange={e => setKm(e.target.value)} /></div>
            <div className="fg full"><label>Observações</label><input className="finp" type="text" placeholder="Observações sobre o veículo" value={obs} onChange={e => setObs(e.target.value)} /></div>
          </div>
          {error && <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 12 }}>{error}</div>}
          <div className="factions">
            <button className="btn btn-g" onClick={() => navigate('/veiculos')}>Cancelar</button>
            {editId && <button className="btn" style={{ background: 'var(--rdd)', color: 'var(--rd)', border: '1px solid rgba(239,68,68,.3)' }} onClick={del}>Excluir</button>}
            <button className="btn btn-p" disabled={saving} onClick={save}>{saving ? 'Salvando...' : editId ? 'Salvar' : 'Cadastrar'}</button>
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default function Veiculos() {
  return (
    <Routes>
      <Route index element={<VeiculoList />} />
      <Route path="new" element={<VeiculoForm />} />
      <Route path=":id/edit" element={<VeiculoForm />} />
    </Routes>
  )
}
