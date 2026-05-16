import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Layout from '../components/Layout'
import { useAuth } from '../context/AuthContext'
import { dashboardAPI, ordensAPI, estoqueAPI, fmtCur, osNum, fmtDate, STATUS_BADGE, STATUS_LABELS, type DashboardData, type OrdemServico, type EstoqueItem } from '../api'

function Badge({ status }: { status: string }) {
  const cls = STATUS_BADGE[status] || 'bgy'
  return <span className={`badge ${cls}`}><span className="bdot" />{STATUS_LABELS[status] || status}</span>
}

export default function Dashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [dash, setDash] = useState<DashboardData | null>(null)
  const [ordens, setOrdens] = useState<OrdemServico[]>([])
  const [alertas, setAlertas] = useState<EstoqueItem[]>([])
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([dashboardAPI.get(), ordensAPI.listar(), estoqueAPI.listarAlertas()])
      .then(([d, o, a]) => { setDash(d); setOrdens(o); setAlertas(a) })
      .catch(e => setError(e.message))
  }, [])

  if (error) return <Layout title="Dashboard" pageId="dashboard"><div style={{ padding: 20, color: 'var(--rd)' }}>Erro ao carregar: {error}</div></Layout>
  if (!dash) return <Layout title="Dashboard" pageId="dashboard"><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>

  const recentes = ordens.slice(0, 5)

  const sf: Record<string, number> = {}
  ordens.forEach(o => (o.servicos || []).forEach(s => {
    const d = (s.descricao || '').toLowerCase()
    const k = d.includes('óleo') ? 'Troca de óleo' : d.includes('frei') ? 'Freios' :
      d.includes('elétri') ? 'Elétrica' : d.includes('suspens') || d.includes('amort') ? 'Suspensão' :
      d.includes('alinha') ? 'Alinhamento' : 'Outros'
    sf[k] = (sf[k] || 0) + 1
  }))
  const srt = Object.entries(sf).sort((a, b) => b[1] - a[1]).slice(0, 5)
  const mx = srt[0]?.[1] || 1

  const actions = user?.perfil !== 'ESTOQUE' ? (
    <button className="btn btn-p sm" onClick={() => navigate('/os/new')}>
      <svg viewBox="0 0 24 24" fill="currentColor"><path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z" /></svg>Nova OS
    </button>
  ) : undefined

  return (
    <Layout title="Dashboard" pageId="dashboard" actions={actions}>
      <div className="metrics">
        <div className="met" onClick={() => navigate('/os')}>
          <div className="mlbl">Atendimentos abertos</div>
          <div className="mval">{dash.ordensAbertas}</div>
          <div className="msub">em andamento / aguardando</div>
          <div className="mico" style={{ background: 'var(--ord)' }}>
            <svg viewBox="0 0 24 24" fill="var(--or)" width="14" height="14"><path d="M14 2H6c-1.1 0-2 .9-2 2v16h16V8z" /></svg>
          </div>
        </div>
        <div className="met" style={{ '--mac': 'var(--gn)' } as React.CSSProperties} onClick={() => navigate('/os?filter=CONCLUIDO')}>
          <div className="mlbl">Serviços concluídos</div>
          <div className="mval">{dash.ordensConcluidas}</div>
          <div className="msub">total no histórico</div>
          <div className="mico" style={{ background: 'var(--gnd)' }}>
            <svg viewBox="0 0 24 24" fill="var(--gn)" width="14" height="14"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" /></svg>
          </div>
        </div>
        <div className="met" style={{ '--mac': 'var(--bl)' } as React.CSSProperties} onClick={() => navigate('/clientes')}>
          <div className="mlbl">Clientes ativos</div>
          <div className="mval">{dash.totalClientes}</div>
          <div className="msub">cadastrados no sistema</div>
          <div className="mico" style={{ background: 'var(--bld)' }}>
            <svg viewBox="0 0 24 24" fill="var(--bl)" width="14" height="14"><path d="M16 11c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3z" /></svg>
          </div>
        </div>
        <div className="met" style={{ '--mac': 'var(--yw)' } as React.CSSProperties} onClick={() => navigate('/relatorios')}>
          <div className="mlbl">Faturamento concluído</div>
          <div className="mval" style={{ fontSize: 14 }}>{fmtCur(dash.faturamentoConcluido)}</div>
          <div className="msub">ticket médio {fmtCur(dash.ticketMedio)}</div>
          <div className="mico" style={{ background: 'var(--ywd)' }}>
            <svg viewBox="0 0 24 24" fill="var(--yw)" width="14" height="14"><path d="M11.8 10.9c-2.27-.59-3-1.2-3-2.15 0-1.09 1.01-1.85 2.7-1.85 1.78 0 2.44.85 2.5 2.1h2.21c-.07-1.72-1.12-3.3-3.21-3.81V3h-3v2.16c-1.94.42-3.5 1.68-3.5 3.61 0 2.31 1.91 3.46 4.7 4.13 2.5.6 3 1.48 3 2.41 0 .69-.49 1.79-2.7 1.79-2.06 0-2.87-.92-2.98-2.1h-2.2c.12 2.19 1.76 3.42 3.68 3.83V21h3v-2.15c1.95-.37 3.5-1.5 3.5-3.55 0-2.84-2.43-3.81-4.7-4.4z" /></svg>
          </div>
        </div>
      </div>

      <div className="g2">
        <div className="card">
          <div className="chd">
            <div className="ctitle">Ordens recentes</div>
            <button className="btn btn-g sm" onClick={() => navigate('/os')}>Ver todas →</button>
          </div>
          <div className="tbl-wrap tbl-desktop">
            <table className="tbl">
              <thead><tr><th>OS</th><th>Cliente</th><th>Veículo</th><th>Status</th><th style={{ textAlign: 'right' }}>Total</th></tr></thead>
              <tbody>
                {recentes.map(o => (
                  <tr key={o.id} style={{ cursor: 'pointer' }} onClick={() => navigate(`/os/${o.id}`)}>
                    <td className="osnum">{osNum(o.id)}</td>
                    <td>{o.clienteNome || '—'}</td>
                    <td style={{ color: 'var(--tx2)' }}>{o.veiculoDesc || '—'}</td>
                    <td><Badge status={o.status} /></td>
                    <td style={{ fontWeight: 700, textAlign: 'right', color: 'var(--or)' }}>{fmtCur(o.total)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <div className="mob-list cbd">
            {recentes.map(o => (
              <div key={o.id} className="mob-card" onClick={() => navigate(`/os/${o.id}`)}>
                <div className="mob-card-top"><span className="osnum">{osNum(o.id)}</span><Badge status={o.status} /></div>
                <div className="mob-card-sub">{o.clienteNome || '—'} · {o.veiculoDesc || '—'}</div>
                <div style={{ fontWeight: 700, color: 'var(--or)', fontSize: 15 }}>{fmtCur(o.total)}</div>
              </div>
            ))}
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
          <div className="card">
            <div className="chd"><div className="ctitle">Serviços mais comuns</div></div>
            <div className="cbd">
              {srt.length ? srt.map(([l, n]) => (
                <div key={l} className="prow">
                  <div style={{ fontSize: 12, color: 'var(--tx2)', width: 88, flexShrink: 0 }}>{l}</div>
                  <div className="pbar"><div className="pfill" style={{ width: `${Math.round(n / mx * 100)}%` }} /></div>
                  <div style={{ fontSize: 12, color: 'var(--tx2)', width: 20, textAlign: 'right' }}>{n}</div>
                </div>
              )) : <div style={{ color: 'var(--tx3)', fontSize: 12 }}>Sem dados.</div>}
            </div>
          </div>

          {alertas.length ? (
            <div className="card" style={{ borderColor: 'rgba(239,68,68,.2)' }}>
              <div className="chd">
                <div className="ctitle" style={{ color: 'var(--rd)' }}>Alertas de estoque</div>
                {user?.perfil !== 'SERVICOS' && <button className="btn btn-g sm" onClick={() => navigate('/estoque')}>Gerenciar</button>}
              </div>
              <div className="cbd" style={{ paddingTop: 10 }}>
                {alertas.map(e => (
                  <div key={e.id} className="ai">
                    <svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" /></svg>
                    <div style={{ fontSize: 13, flex: 1 }}>{e.nome}</div>
                    <div style={{ fontSize: 12, color: 'var(--rd)', fontWeight: 600 }}>{e.quantidade} un.</div>
                  </div>
                ))}
              </div>
            </div>
          ) : (
            <div className="card">
              <div className="chd"><div className="ctitle" style={{ color: 'var(--gn)' }}>Estoque em dia</div></div>
              <div className="cbd"><div style={{ fontSize: 12, color: 'var(--tx2)' }}>Todos os itens acima do mínimo.</div></div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  )
}

// suppress unused import warning
void fmtDate
