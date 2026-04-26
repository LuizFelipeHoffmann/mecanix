// ================================================================
//  MECANIX — sidebar.js  (versão API)
//  Sidebar compartilhada + controle de autenticação
// ================================================================

const TIPO_LABELS = {SEDAN:'Sedan',HATCH:'Hatch',SUV:'SUV',PICKUP:'Pickup',ELETRICO:'Elétrico'};
const TIPO_COLORS = {SEDAN:'#3b82f6',HATCH:'#8b5cf6',SUV:'#0ea5e9',PICKUP:'#f59e0b',ELETRICO:'#22c55e'};
const ROLE_LABELS = {ADMIN:'Acesso total',SERVICOS:'Atendimento',ESTOQUE:'Estoque'};
const ROLE_COLORS = {ADMIN:'var(--or)',SERVICOS:'var(--bl)',ESTOQUE:'var(--gn)'};

const NAV = [
  {id:'dashboard', file:'dashboard.html', lbl:'Dashboard',         roles:['ADMIN','SERVICOS','ESTOQUE'],
   icon:'<path d="M3 13h8V3H3v10zm0 8h8v-6H3v6zm10 0h8V11h-8v10zm0-18v6h8V3h-8z"/>'},
  {id:'os',        file:'os.html',        lbl:'Ordens de Serviço', roles:['ADMIN','SERVICOS'],
   icon:'<path d="M14 2H6c-1.1 0-2 .9-2 2v16h16V8l-6-6zm2 16H8v-2h8v2zm0-4H8v-2h8v2zm-3-5V3.5L18.5 9H13z"/>'},
  {id:'clientes',  file:'clientes.html',  lbl:'Clientes',          roles:['ADMIN','SERVICOS'],
   icon:'<path d="M16 11c1.66 0 3-1.34 3-3s-1.34-3-3-3-3 1.34-3 3 1.34 3 3 3zm-8 0c1.66 0 3-1.34 3-3S9.66 5 8 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/>'},
  {id:'veiculos',  file:'veiculos.html',  lbl:'Veículos',          roles:['ADMIN','SERVICOS'],
   icon:'<path d="M18.92 6.01C18.72 5.42 18.16 5 17.5 5h-11c-.66 0-1.21.42-1.42 1.01L3 12v8h2v1h2v-1h12v1h2v-1h2v-8l-2.08-5.99zM6.5 16c-.83 0-1.5-.67-1.5-1.5S5.67 13 6.5 13s1.5.67 1.5 1.5S7.33 16 6.5 16zm11 0c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zM5 11l1.5-4.5h11L19 11H5z"/>'},
  {id:'estoque',   file:'estoque.html',   lbl:'Estoque',           roles:['ADMIN','ESTOQUE'],
   icon:'<path d="M20 6h-2.18c.07-.44.18-.87.18-1.3C18 2.12 15.88 0 13.3 0c-1.3 0-2.49.52-3.35 1.36L8 4.18V2H2v6h4.18L9 10.07V20c0 1.1.9 2 2 2h4c1.1 0 2-.9 2-2v-3h1c1.1 0 2-.9 2-2v-1h1c1.1 0 2-.9 2-2v-4c0-1.1-.9-2-2-2zM4 6V4h2v4H4V6z"/>'},
  {id:'relatorios',file:'relatorios.html',lbl:'Relatórios',        roles:['ADMIN'],
   icon:'<path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zM9 17H7v-7h2v7zm4 0h-2V7h2v10zm4 0h-2v-4h2v4z"/>'},
];

async function initPage(pageId, pageTitle) {
  const user = requireAuth();
  if (!user) return null;

  const nav = NAV.find(n => n.id === pageId);
  if (nav && !nav.roles.includes(user.perfil)) {
    window.location.href = 'dashboard.html'; return null;
  }

  const pt = document.getElementById('ptitle');
  if (pt) pt.textContent = pageTitle || '';
  const pd = document.getElementById('pdate');
  if (pd) pd.textContent = new Date().toLocaleDateString('pt-BR', {day:'2-digit',month:'short',year:'numeric'});

  await renderSidebar(pageId, user);
  return user;
}

async function renderSidebar(activeId, user) {
  const sb = document.getElementById('sb');
  if (!sb) return;

  // Busca alertas de estoque e OS abertas para badges
  let oq = 0, aq = 0;
  try {
    if (user.perfil === 'ADMIN' || user.perfil === 'SERVICOS') {
      const ordens = await ordensAPI.listar();
      oq = ordens.filter(o => o.status === 'ANDAMENTO' || o.status === 'AGUARDANDO').length;
    }
    if (user.perfil === 'ADMIN' || user.perfil === 'ESTOQUE') {
      const alertas = await estoqueAPI.listarAlertas();
      aq = alertas.length;
    }
  } catch {}

  let navHtml = '<div class="nav-sec">Módulos</div>';
  NAV.forEach(n => {
    if (!n.roles.includes(user.perfil)) return;
    let badge = '';
    if (n.id === 'os'      && oq > 0) badge = `<span class="nb">${oq}</span>`;
    if (n.id === 'estoque' && aq > 0) badge = `<span class="nb r">${aq}</span>`;
    navHtml += `<div class="nv${activeId===n.id?' on':''}" onclick="window.location.href='${n.file}'">
      <svg viewBox="0 0 24 24" fill="currentColor" width="16" height="16">${n.icon}</svg>
      ${n.lbl}${badge}
    </div>`;
  });

  sb.innerHTML = `
    <div class="brand">
      <div class="brand-r">
        <div class="brand-i"><svg viewBox="0 0 24 24"><path d="M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z"/></svg></div>
        <span class="brand-n">MECANIX</span>
      </div>
      <div class="brand-s">Gestão de Oficinas</div>
    </div>
    <nav class="nav">${navHtml}</nav>
    <div class="sfooter">
      <div class="u-r">
        <div class="ava">${user.iniciais||'??'}</div>
        <div style="min-width:0">
          <div style="font-size:13px;font-weight:600;color:var(--tx);overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${user.nome}</div>
          <div style="font-size:11px;color:${ROLE_COLORS[user.perfil]||'var(--or)'}">${ROLE_LABELS[user.perfil]||user.perfil}</div>
        </div>
        <div style="margin-left:auto;cursor:pointer;color:var(--tx3);padding:6px" onclick="logout()" title="Sair">
          <svg width="17" height="17" viewBox="0 0 24 24" fill="currentColor">
            <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.58L17 17l5-5zM4 5h8V3H4c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h8v-2H4V5z"/>
          </svg>
        </div>
      </div>
    </div>`;
}

function toggleSB() {
  document.getElementById('sb')?.classList.toggle('open');
  document.getElementById('sb-overlay')?.classList.toggle('show');
}
function closeSB() {
  document.getElementById('sb')?.classList.remove('open');
  document.getElementById('sb-overlay')?.classList.remove('show');
}
