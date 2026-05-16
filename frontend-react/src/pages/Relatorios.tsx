import { useEffect, useState } from 'react'
import Layout from '../components/Layout'
import { ordensAPI, clientesAPI, veiculosAPI, estoqueAPI, fmtCur, type OrdemServico, type Cliente, type Veiculo, type EstoqueItem } from '../api'

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

  return (
    <Layout title="Relatórios" pageId="relatorios" actions={actions}>
      <div className="metrics" style={{ marginBottom: 20 }}>
        {[
          { lbl: 'Faturamento concluído', val: fmtCur(rev), sub: `${done.length} OS concluída(s)`, ico: 'var(--or)', bg: 'var(--ord)' },
          { lbl: 'Ticket médio', val: fmtCur(ticket), sub: 'por OS concluída', ico: 'var(--bl)', bg: 'var(--bld)', mac: 'var(--bl)' },
          { lbl: 'Em aberto (potencial)', val: fmtCur(revOp), sub: `${open.length} OS em andamento`, ico: 'var(--yw)', bg: 'var(--ywd)', mac: 'var(--yw)' },
          { lbl: 'Total de clientes', val: String(clientes.length), sub: `${clientes.length} cadastrado(s)`, ico: 'var(--gn)', bg: 'var(--gnd)', mac: 'var(--gn)' },
        ].map(m => (
          <div key={m.lbl} className="met" style={{ '--mac': m.mac } as React.CSSProperties}>
            <div className="mlbl">{m.lbl}</div>
            <div className="mval" style={{ fontSize: 16 }}>{m.val}</div>
            <div className="msub">{m.sub}</div>
          </div>
        ))}
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
                  <span style={{ fontSize: 13, color: 'var(--tx2)' }}>{l}</span>
                  <span style={{ fontWeight: 700, color: c as string }}>{v}</span>
                </div>
              ))}
            </div>
          </div>
          <div className="card">
            <div className="chd"><div className="ctitle">Situação das OS</div></div>
            <div className="cbd">
              {[
                ['Concluídas', done.length, 'var(--gn)'],
                ['Em andamento', open.filter(o => o.status === 'ANDAMENTO').length, 'var(--yw)'],
                ['Aguardando peça', open.filter(o => o.status === 'AGUARDANDO').length, 'var(--bl)'],
                ['Agendadas', sched.length, 'var(--or)'],
                ['Total', ordens.length, 'var(--tx)'],
              ].map(([l, v, c]) => (
                <div key={l as string} style={{ display: 'flex', justifyContent: 'space-between', padding: '9px 0', borderBottom: '1px solid var(--brd)' }}>
                  <span style={{ fontSize: 13, color: 'var(--tx2)' }}>{l}</span>
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
                  <div style={{ fontSize: 12, color: 'var(--tx2)', width: 100, flexShrink: 0, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{l}</div>
                  <div className="pbar"><div className="pfill" style={{ width: `${Math.round(n / maxSvc * 100)}%` }} /></div>
                  <div style={{ fontSize: 12, color: 'var(--tx2)', width: 24, textAlign: 'right' }}>{n}</div>
                </div>
              )) : <div style={{ color: 'var(--tx3)', fontSize: 12 }}>Sem dados suficientes.</div>}
            </div>
          </div>
          <div className="card">
            <div className="chd"><div className="ctitle">Top clientes por receita</div></div>
            <div className="cbd">
              {topCli.length ? topCli.map(([nome, val], i) => (
                <div key={nome} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '8px 0', borderBottom: '1px solid var(--brd)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                    <div style={{ width: 22, height: 22, borderRadius: '50%', background: 'var(--ord)', color: 'var(--or)', fontSize: 11, fontWeight: 700, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{i + 1}</div>
                    <span style={{ fontSize: 13, fontWeight: 500 }}>{nome}</span>
                  </div>
                  <span style={{ fontWeight: 700, color: 'var(--or)' }}>{fmtCur(val)}</span>
                </div>
              )) : <div style={{ color: 'var(--tx3)', fontSize: 12 }}>Sem dados.</div>}
            </div>
          </div>
          {alertas.length > 0 && (
            <div className="card" style={{ borderColor: 'rgba(239,68,68,.2)' }}>
              <div className="chd"><div className="ctitle" style={{ color: 'var(--rd)' }}>Alertas de estoque</div></div>
              <div className="cbd">
                {alertas.map(e => (
                  <div key={e.id} className="ai">
                    <svg viewBox="0 0 24 24" fill="currentColor" width="14" height="14"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z" /></svg>
                    <div style={{ fontSize: 13, flex: 1 }}>{e.nome}</div>
                    <div style={{ fontSize: 12, color: 'var(--rd)', fontWeight: 600 }}>{e.quantidade} un.</div>
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
