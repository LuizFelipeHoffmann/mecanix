import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import { ordensAPI, clientesAPI, veiculosAPI, estoqueAPI, fmtCur, type OrdemServico, type Cliente, type Veiculo, type EstoqueItem } from '../api'

// Gráfico de barras vertical em SVG puro
function BarChart({ data }: { data: { label: string; value: number }[] }) {
  const max = Math.max(...data.map(d => d.value), 1)
  const W = 60, H = 160
  return (
    <svg viewBox={`0 0 ${data.length * W} ${H + 40}`} style={{ width: '100%', height: 'auto', display: 'block' }} role="img" aria-label="Faturamento por mês">
      {data.map((d, i) => {
        const h = Math.round(d.value / max * H)
        return (
          <g key={d.label}>
            <rect x={i * W + 12} y={H - h + 16} width={W - 24} height={Math.max(h, 2)} rx={5} fill="var(--or)" opacity={i === data.length - 1 ? 1 : .55} />
            <text x={i * W + W / 2} y={H - h + 10} textAnchor="middle" fontSize="11" fontWeight="700" fill="var(--tx)">
              {d.value ? d.value.toLocaleString('pt-BR', { maximumFractionDigits: 0 }) : ''}
            </text>
            <text x={i * W + W / 2} y={H + 34} textAnchor="middle" fontSize="12" fill="var(--tx2)">{d.label}</text>
          </g>
        )
      })}
    </svg>
  )
}

// Gráfico de rosca em SVG puro: cada fatia é um arco com stroke-dasharray
function Donut({ data }: { data: { label: string; value: number; color: string }[] }) {
  const total = data.reduce((a, d) => a + d.value, 0)
  const R = 60, C = 2 * Math.PI * R
  let acc = 0
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 20, flexWrap: 'wrap', justifyContent: 'center' }}>
      <svg viewBox="0 0 160 160" width={160} height={160} role="img" aria-label="OS por status">
        <circle cx={80} cy={80} r={R} fill="none" stroke="var(--bg3)" strokeWidth={22} />
        {total > 0 && data.filter(d => d.value).map(d => {
          const len = d.value / total * C
          const el = <circle key={d.label} cx={80} cy={80} r={R} fill="none" stroke={d.color} strokeWidth={22}
            strokeDasharray={`${len} ${C - len}`} strokeDashoffset={-acc} transform="rotate(-90 80 80)" />
          acc += len
          return el
        })}
        <text x={80} y={78} textAnchor="middle" fontSize="26" fontWeight="800" fill="var(--tx)">{total}</text>
        <text x={80} y={98} textAnchor="middle" fontSize="12" fill="var(--tx2)">OS</text>
      </svg>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
        {data.map(d => (
          <div key={d.label} style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 14 }}>
            <span style={{ width: 12, height: 12, borderRadius: 3, background: d.color, flexShrink: 0 }} />
            <span style={{ color: 'var(--tx2)', flex: 1 }}>{d.label}</span>
            <b>{d.value}</b>
          </div>
        ))}
      </div>
    </div>
  )
}

export default function Relatorios() {
  const [ordens, setOrdens] = useState<OrdemServico[]>([])
  const [clientes, setClientes] = useState<Cliente[]>([])
  const [veiculos, setVeiculos] = useState<Veiculo[]>([])
  const [estoque, setEstoque] = useState<EstoqueItem[]>([])
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    Promise.all([ordensAPI.listar(), clientesAPI.listar(), veiculosAPI.listar(), estoqueAPI.listar()])
      .then(([o, c, v, e]) => { setOrdens(o); setClientes(c); setVeiculos(v); setEstoque(e) })
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  const actions = <button className="btn btn-g sm" onClick={() => window.print()}>🖨️ Imprimir</button>

  if (loading) return <Layout title="Relatórios" pageId="relatorios" actions={actions}><div style={{ padding: 40, textAlign: 'center', color: 'var(--tx3)' }}>Carregando...</div></Layout>
  if (error) return <Layout title="Relatórios" pageId="relatorios" actions={actions}><div style={{ padding: 20, color: 'var(--rd)' }}>Erro: {error}</div></Layout>

  const done  = ordens.filter(o => o.status === 'CONCLUIDO')
  const open  = ordens.filter(o => o.status === 'ANDAMENTO' || o.status === 'AGUARDANDO')
  const sched = ordens.filter(o => o.status === 'AGENDADO')
  const rev   = done.reduce((a, o) => a + (parseFloat(String(o.total)) || 0), 0)
  const revOp = open.reduce((a, o) => a + (parseFloat(String(o.total)) || 0), 0)
  const maoO  = done.reduce((a, o) => a + (parseFloat(String(o.totalServicos)) || 0), 0)
  const pecOb = done.reduce((a, o) => a + (parseFloat(String(o.totalPecas)) || 0), 0)
  const ticket = done.length ? rev / done.length : 0
  const alertas = estoque.filter(e => e.alertaEstoque)

  const svcMap: Record<string, number> = {}
  ordens.forEach(o => (o.servicos || []).forEach(s => {
    const k = (s.descricao || '').trim().toLowerCase()
    const cat = k.includes('óleo') || k.includes('oleo') ? 'Troca de óleo'
      : k.includes('frei') ? 'Serviço de freios'
      : k.includes('elétri') ? 'Elétrica'
      : k.includes('suspens') || k.includes('amort') ? 'Suspensão'
      : k.includes('alinha') ? 'Alinhamento'
      : k.includes('diagnos') ? 'Diagnóstico'
      : 'Outros'
    svcMap[cat] = (svcMap[cat] || 0) + 1
  }))
  const topSvcs = Object.entries(svcMap).sort((a, b) => b[1] - a[1]).slice(0, 6)
  const maxSvc = topSvcs[0]?.[1] || 1

  const cliRev: Record<string, number> = {}
  done.forEach(o => {
    const k = o.clienteNome || 'Desconhecido'
    cliRev[k] = (cliRev[k] || 0) + (parseFloat(String(o.total)) || 0)
  })
  const topCli = Object.entries(cliRev).sort((a, b) => b[1] - a[1]).slice(0, 5)

  void veiculos

  // Faturamento das OS concluídas nos últimos 6 meses (inclui o atual)
  const hoje = new Date()
  const meses = Array.from({ length: 6 }, (_, i) => {
    const d = new Date(hoje.getFullYear(), hoje.getMonth() - 5 + i, 1)
    const chave = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    const value = done.filter(o => (o.data || '').startsWith(chave)).reduce((a, o) => a + (parseFloat(String(o.total)) || 0), 0)
    return { label: d.toLocaleDateString('pt-BR', { month: 'short' }).replace('.', ''), value }
  })

  const porStatus = [
    { label: 'Concluídas', value: done.length, color: 'var(--gn)' },
    { label: 'Em andamento', value: ordens.filter(o => o.status === 'ANDAMENTO').length, color: 'var(--yw)' },
    { label: 'Aguardando peça', value: ordens.filter(o => o.status === 'AGUARDANDO').length, color: 'var(--bl)' },
    { label: 'Agendadas', value: sched.length, color: 'var(--vi)' },
    { label: 'Canceladas', value: ordens.filter(o => o.status === 'CANCELADO').length, color: 'var(--rd)' },
  ]

  return (
    <Layout title="Relatórios" pageId="relatorios" actions={actions}>
      <div className="metrics" style={{ marginBottom: 20 }}>
        {[
          { lbl: 'Faturamento concluído', val: fmtCur(rev), sub: `${done.length} OS concluída(s)`, ico: 'var(--or)', bg: 'var(--ord)', mac: 'var(--or)' },
          { lbl: 'Ticket médio', val: fmtCur(ticket), sub: 'por OS concluída', ico: 'var(--bl)', bg: 'var(--bld)', mac: 'var(--bl)' },
          { lbl: 'Em aberto (potencial)', val: fmtCur(revOp), sub: `${open.length} OS em andamento`, ico: 'var(--yw)', bg: 'var(--ywd)', mac: 'var(--yw)' },
          { lbl: 'Total de clientes', val: String(clientes.length), sub: `${clientes.length} cadastrado(s)`, ico: 'var(--gn)', bg: 'var(--gnd)', mac: 'var(--gn)' },
        ].map(m => (
          <div key={m.lbl} className="met" style={{ '--mac': m.mac, '--macd': m.bg } as React.CSSProperties}>
            <div className="mlbl">{m.lbl}</div>
            <div className="mval" style={{ fontSize: 20 }}>{m.val}</div>
            <div className="msub">{m.sub}</div>
          </div>
        ))}
      </div>

      <div className="g2">
        <div className="card" style={{ marginBottom: 0 }}>
          <div className="chd"><div className="ctitle">Faturamento por mês (R$)</div><span style={{ fontSize: 13, color: 'var(--tx3)' }}>OS concluídas · últimos 6 meses</span></div>
          <div className="cbd"><BarChart data={meses} /></div>
        </div>
        <div className="card" style={{ marginBottom: 0 }}>
          <div className="chd"><div className="ctitle">OS por status</div></div>
          <div className="cbd"><Donut data={porStatus} /></div>
        </div>
      </div>

      <div className="g2">
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="card">
            <div className="chd"><div className="ctitle">Resumo financeiro</div></div>
            <div className="cbd">
              {[
                ['Receita total (concluídas)', fmtCur(rev), 'var(--gn)'],
                ['Mão de obra', fmtCur(maoO), 'var(--bl)'],
                ['Peças', fmtCur(pecOb), 'var(--or)'],
                ['Ticket médio', fmtCur(ticket), 'var(--tx)'],
                ['Em aberto (potencial)', fmtCur(revOp), 'var(--yw)'],
              ].map(([l, v, c]) => (
                <div key={l as string} style={{ display: 'flex', justifyContent: 'space-between', padding: '9px 0', borderBottom: '1px solid var(--brd)' }}>
                  <span style={{ fontSize: 14, color: 'var(--tx2)' }}>{l}</span>
                  <span style={{ fontWeight: 700, color: c as string }}>{v}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <div className="card">
            <div className="chd"><div className="ctitle">Serviços mais realizados</div></div>
            <div className="cbd">
              {topSvcs.length ? topSvcs.map(([l, n]) => (
                <div key={l} className="prow">
                  <div style={{ fontSize: 13, color: 'var(--tx2)', width: 100, flexShrink: 0, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{l}</div>
                  <div className="pbar"><div className="pfill" style={{ width: `${Math.round(n / maxSvc * 100)}%` }} /></div>
                  <div style={{ fontSize: 13, color: 'var(--tx2)', width: 24, textAlign: 'right' }}>{n}</div>
                </div>
              )) : <div style={{ color: 'var(--tx3)', fontSize: 13 }}>Sem dados suficientes.</div>}
            </div>
          </div>
          <div className="card">
            <div className="chd"><div className="ctitle">Top clientes por receita</div></div>
            <div className="cbd">
              {topCli.length ? topCli.map(([nome, val], i) => (
                <div key={nome} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0', borderBottom: '1px solid var(--brd)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div style={{ width: 22, height: 22, borderRadius: '50%', background: 'var(--ord)', color: 'var(--or)', fontSize: 12, fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{i + 1}</div>
                    <span style={{ fontSize: 14, fontWeight: 500 }}>{nome}</span>
                  </div>
                  <span style={{ fontWeight: 700, color: 'var(--or)' }}>{fmtCur(val)}</span>
                </div>
              )) : <div style={{ color: 'var(--tx3)', fontSize: 13 }}>Sem dados.</div>}
            </div>
          </div>
          {alertas.length > 0 && (
            <div className="card" style={{ borderColor: 'rgba(239,68,68,.2)' }}>
              <div className="chd"><div className="ctitle" style={{ color: 'var(--rd)' }}>Alertas de estoque</div></div>
              <div className="cbd">
                {alertas.map(e => (
                  <div key={e.id} className="ai">
                    <svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" /></svg>
                    <div style={{ fontSize: 14, flex: 1 }}>{e.nome}</div>
                    <div style={{ fontSize: 13, color: 'var(--rd)', fontWeight: 600 }}>{e.quantidade} un.</div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  )
}
