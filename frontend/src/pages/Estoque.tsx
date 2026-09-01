import { useEffect, useState } from 'react'
import { useNavigate, useParams, Routes, Route } from 'react-router-dom'
import Layout from '../components/Layout'
import { estoqueAPI, fmtCur, TIPO_LABELS, type EstoqueItem } from '../api'

const CATS = ['Filtros', 'Freios', 'Ignição', 'Lubrificantes', 'Suspensão', 'Motor', 'Elétrico', 'Transmissão', 'Outros']
const TIPOS_EST = ['SEDAN', 'HATCH', 'SUV', 'PICKUP', 'ELETRICO']

function EstoqueList() {
  const navigate = useNavigate()
  const [itens, setItens] = useState<EstoqueItem[]>([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')

  useEffect(() => {
    estoqueAPI.listar().then(setItens).finally(() => setLoading(false))
  }, [])

  const alertaCount = itens.filter(e => e.alertaEstoque).length
  const q = search.toLowerCase().trim()
  const filtrados = q
    ? itens.filter(e =>
        e.codigo.toLowerCase().includes(q) ||
        e.nome.toLowerCase().includes(q) ||
        (e.categoria || '').toLowerCase().includes(q) ||
        (e.tipos || []).some(t => (TIPO_LABELS[t] || t).toLowerCase().includes(q))
      )
    : itens

  const actions = <button className="btn btn-p sm" onClick={() => navigate('/estoque/new')}>+ Novo item</button>

  if (loading) return <Layout title="Estoque" pageId="estoque" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  return (
    <Layout title="Estoque" pageId="estoque" actions={actions}>
      {alertaCount > 0 && (
        <div style={{ background: 'rgba(239,68,68,.08)', border: '1px solid rgba(239,68,68,.2)', borderRadius: 10, padding: '12px 16px', marginBottom: 16, color: 'var(--rd)', fontSize: 13 }}>
          ⚠️ <strong>{alertaCount} item(ns)</strong> com estoque abaixo do mínimo.
        </div>
      )}
      <div className="card">
        <div className="chd">
          <div className="ctitle">Estoque ({filtrados.length}{q ? ` de ${itens.length}` : ''} itens)</div>
          <input
            type="search"
            placeholder="Buscar por código, nome, categoria..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            style={{ background: 'var(--bg3)', border: '1px solid var(--brd)', borderRadius: 8, color: 'var(--tx)', padding: '6px 11px', fontSize: 13, outline: 'none', width: 'min(100%, 320px)' }}
          />
        </div>
        <div className="tbl-wrap tbl-desktop">
          <table className="tbl">
            <thead><tr><th>Código</th><th>Nome</th><th>Categoria</th><th>Quantidade</th><th>Preço Unit.</th><th>Compatível</th><th /></tr></thead>
            <tbody>
              {filtrados.length ? filtrados.map(e => (
                <tr key={e.id}>
                  <td className="osnum">{e.codigo}</td>
                  <td style={{ fontWeight: 500 }}>{e.nome}</td>
                  <td style={{ color: 'var(--tx2)' }}>{e.categoria || '—'}</td>
                  <td>
                    <span style={{ fontWeight: 600, color: e.alertaEstoque ? 'var(--rd)' : 'var(--gn)' }}>{e.quantidade}</span>
                    <span style={{ color: 'var(--tx3)', fontSize: 11 }}> / mín {e.quantidadeMinima}</span>
                    {e.alertaEstoque && <span className="badge brd2" style={{ marginLeft: 6, fontSize: 10 }}>Baixo</span>}
                  </td>
                  <td style={{ fontWeight: 600, color: 'var(--or)' }}>{fmtCur(e.precoUnitario)}</td>
                  <td style={{ color: 'var(--tx2)', fontSize: 12 }}>{(e.tipos || []).join(', ')}</td>
                  <td>
                    <div className="ib" onClick={() => navigate(`/estoque/${e.id}/edit`)}>
                      <svg viewBox="0 0 24 24"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z" /></svg>
                    </div>
                  </td>
                </tr>
              )) : <tr><td colSpan={7} style={{ textAlign: 'center', color: 'var(--tx3)', padding: 24 }}>{q ? 'Nenhum item encontrado.' : 'Nenhum item no estoque.'}</td></tr>}
            </tbody>
          </table>
        </div>
        <div className="mob-list cbd">
          {filtrados.map(e => (
            <div key={e.id} className="mob-card">
              <div className="mob-card-top">
                <span className="osnum">{e.codigo}</span>
                {e.alertaEstoque && <span className="badge brd2">Estoque baixo</span>}
              </div>
              <div style={{ fontWeight: 600, fontSize: 15, color: 'var(--tx)', marginBottom: 4 }}>{e.nome}</div>
              <div className="mob-card-row">
                <span style={{ color: e.alertaEstoque ? 'var(--rd)' : 'var(--gn)', fontWeight: 600 }}>{e.quantidade} un.</span>
                <span style={{ color: 'var(--or)', fontWeight: 600 }}>{fmtCur(e.precoUnitario)}</span>
                <div className="ib" onClick={() => navigate(`/estoque/${e.id}/edit`)}>
                  <svg viewBox="0 0 24 24"><path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25z" /></svg>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Layout>
  )
}

function EstoqueForm() {
  const navigate = useNavigate()
  const { id } = useParams()
  const editId = id ? parseInt(id) : null
  const title = editId ? 'Editar Item' : 'Novo Item'

  const [codigo, setCodigo] = useState('')
  const [nome, setNome] = useState('')
  const [categoria, setCategoria] = useState('')
  const [quantidade, setQuantidade] = useState('')
  const [quantidadeMinima, setQuantidadeMinima] = useState('')
  const [precoUnitario, setPrecoUnitario] = useState('')
  const [tipos, setTipos] = useState<string[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!editId) return
    setLoading(true)
    estoqueAPI.buscar(editId).then(e => {
      setCodigo(e.codigo); setNome(e.nome); setCategoria(e.categoria || '');
      setQuantidade(String(e.quantidade)); setQuantidadeMinima(String(e.quantidadeMinima));
      setPrecoUnitario(String(e.precoUnitario)); setTipos(e.tipos || [])
    }).finally(() => setLoading(false))
  }, [editId])

  function toggleTipo(t: string) {
    setTipos(prev => prev.includes(t) ? prev.filter(x => x !== t) : [...prev, t])
  }

  async function save() {
    if (!codigo) { setError('Informe o código'); return }
    if (!nome) { setError('Informe o nome'); return }
    setSaving(true); setError('')
    const dados = { codigo: codigo.toUpperCase(), nome, categoria, quantidade: parseInt(quantidade) || 0, quantidadeMinima: parseInt(quantidadeMinima) || 0, precoUnitario: parseFloat(precoUnitario) || 0, tipos }
    try {
      if (editId) await estoqueAPI.atualizar(editId, dados)
      else await estoqueAPI.criar(dados)
      navigate('/estoque')
    } catch (e: unknown) { setError(e instanceof Error ? e.message : 'Erro ao salvar'); setSaving(false) }
  }

  async function del() {
    if (!editId || !confirm('Excluir este item?')) return
    try { await estoqueAPI.deletar(editId); navigate('/estoque') }
    catch (e: unknown) { alert('Erro: ' + (e instanceof Error ? e.message : '')) }
  }

  const actions = <button className="btn btn-g sm" onClick={() => navigate('/estoque')}>← Voltar</button>

  if (loading) return <Layout title={title} pageId="estoque" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  return (
    <Layout title={title} pageId="estoque" actions={actions}>
      <div className="card">
        <div className="chd"><div className="ctitle">{title}</div></div>
        <div className="cbd">
          <div className="fgrid">
            <div className="fg">
              <label>Código *</label>
              <input className="finp" type="text" placeholder="Ex: FO-920" value={codigo} onChange={e => setCodigo(e.target.value.toUpperCase())} />
            </div>
            <div className="fg">
              <label>Categoria</label>
              <select className="finp" value={categoria} onChange={e => setCategoria(e.target.value)}>
                <option value="">Selecione...</option>
                {CATS.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </div>
            <div className="fg full">
              <label>Nome *</label>
              <input className="finp" type="text" placeholder="Ex: Filtro de óleo W920" value={nome} onChange={e => setNome(e.target.value)} />
            </div>
            <div className="fg">
              <label>Quantidade *</label>
              <input className="finp" type="number" placeholder="0" min={0} value={quantidade} onChange={e => setQuantidade(e.target.value)} />
            </div>
            <div className="fg">
              <label>Qtd. mínima *</label>
              <input className="finp" type="number" placeholder="5" min={0} value={quantidadeMinima} onChange={e => setQuantidadeMinima(e.target.value)} />
            </div>
            <div className="fg">
              <label>Preço unitário *</label>
              <input className="finp" type="number" placeholder="0.00" step={0.01} min={0} value={precoUnitario} onChange={e => setPrecoUnitario(e.target.value)} />
            </div>
            <div className="fg full">
              <label>Compatível com</label>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 14, marginTop: 8 }}>
                {TIPOS_EST.map(t => (
                  <label key={t} style={{ display: 'flex', alignItems: 'center', gap: 6, fontSize: 13, cursor: 'pointer' }}>
                    <input type="checkbox" checked={tipos.includes(t)} onChange={() => toggleTipo(t)} />
                    {TIPO_LABELS[t] || t}
                  </label>
                ))}
              </div>
            </div>
          </div>
          {error && <div style={{ background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 12 }}>{error}</div>}
          <div className="factions">
            <button className="btn btn-g" onClick={() => navigate('/estoque')}>Cancelar</button>
            {editId && <button className="btn" style={{ background: 'var(--rdd)', color: 'var(--rd)', border: '1px solid rgba(239,68,68,.3)' }} onClick={del}>Excluir</button>}
            <button className="btn btn-p" disabled={saving} onClick={save}>{saving ? 'Salvando...' : editId ? 'Salvar' : 'Cadastrar'}</button>
          </div>
        </div>
      </div>
    </Layout>
  )
}

export default function Estoque() {
  return (
    <Routes>
      <Route index element={<EstoqueList />} />
      <Route path="new" element={<EstoqueForm />} />
      <Route path=":id/edit" element={<EstoqueForm />} />
    </Routes>
  )
}
