// ================================================================
//  MECANIX — api.js
//  Comunicação com a API Java/Spring Boot
//
//  IMPORTANTE: usa "localhost" (não 127.0.0.1) para que o cookie
//  de sessão seja enviado corretamente pelo navegador.
// ================================================================

const API_BASE = 'http://localhost:8080/api';

// ── FETCH BASE ──
async function apiFetch(method, path, body = null) {
    const opts = {
        method,
        credentials: 'include', // Envia o cookie de sessão
        headers: { 'Content-Type': 'application/json' },
    };
    if (body) opts.body = JSON.stringify(body);

    const res = await fetch(API_BASE + path, opts);

    if (res.status === 401) {
        // Sessão expirada
        sessionStorage.removeItem('mecanix_user');
        window.location.href = 'index.html';
        return null;
    }

    if (!res.ok) {
        const err = await res.json().catch(() => ({ erro: 'Erro na requisição' }));
        throw new Error(err.erro || 'Erro na requisição');
    }

    if (res.status === 204) return null; // DELETE retorna vazio
    return res.json();
}

// ── APIs ──
const authAPI = {
    login:  (email, senha) => apiFetch('POST', '/auth/login', { email, senha }),
    logout: ()             => apiFetch('POST', '/auth/logout'),
    me:     ()             => apiFetch('GET',  '/auth/me'),
};

const clientesAPI = {
    listar:    ()          => apiFetch('GET',    '/clientes'),
    buscar:    (id)        => apiFetch('GET',    `/clientes/${id}`),
    criar:     (dados)     => apiFetch('POST',   '/clientes', dados),
    atualizar: (id, dados) => apiFetch('PUT',    `/clientes/${id}`, dados),
    deletar:   (id)        => apiFetch('DELETE', `/clientes/${id}`),
};

const veiculosAPI = {
    listar:           ()    => apiFetch('GET', '/veiculos'),
    listarPorCliente: (cid) => apiFetch('GET', `/veiculos?clienteId=${cid}`),
    buscar:     (id)        => apiFetch('GET',    `/veiculos/${id}`),
    criar:      (dados)     => apiFetch('POST',   '/veiculos', dados),
    atualizar:  (id, dados) => apiFetch('PUT',    `/veiculos/${id}`, dados),
    deletar:    (id)        => apiFetch('DELETE', `/veiculos/${id}`),
};

const ordensAPI = {
    listar:          ()       => apiFetch('GET', '/ordens'),
    listarPorStatus: (status) => apiFetch('GET', `/ordens?status=${status}`),
    buscar:     (id)          => apiFetch('GET',    `/ordens/${id}`),
    criar:      (dados)       => apiFetch('POST',   '/ordens', dados),
    atualizar:  (id, dados)   => apiFetch('PUT',    `/ordens/${id}`, dados),
    deletar:    (id)          => apiFetch('DELETE', `/ordens/${id}`),
};

const estoqueAPI = {
    listar:        ()       => apiFetch('GET', '/estoque'),
    listarAlertas: ()       => apiFetch('GET', '/estoque/alertas'),
    listarPorTipo: (tipo)   => apiFetch('GET', `/estoque?tipo=${tipo}`),
    buscar:     (id)        => apiFetch('GET',    `/estoque/${id}`),
    criar:      (dados)     => apiFetch('POST',   '/estoque', dados),
    atualizar:  (id, dados) => apiFetch('PUT',    `/estoque/${id}`, dados),
    deletar:    (id)        => apiFetch('DELETE', `/estoque/${id}`),
};

const dashboardAPI = {
    get: () => apiFetch('GET', '/dashboard'),
};

// ── SESSÃO ──
const SESSION_KEY = 'mecanix_user';

function getUser() {
    try { return JSON.parse(sessionStorage.getItem(SESSION_KEY)); }
    catch { return null; }
}

function setUser(user) {
    sessionStorage.setItem(SESSION_KEY, JSON.stringify(user));
}

function requireAuth() {
    const u = getUser();
    if (!u) { window.location.href = 'index.html'; return null; }
    return u;
}

async function logout() {
    try { await authAPI.logout(); } catch {}
    sessionStorage.removeItem(SESSION_KEY);
    window.location.href = 'index.html';
}

// ── HELPERS ──
function osNum(id) {
    return '#' + String(id).padStart(4, '0');
}

function fmtCur(v) {
    return 'R$\u00A0' + Number(v || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 });
}

function fmtDate(d) {
    if (!d) return '—';
    try { return new Date(d + 'T12:00').toLocaleDateString('pt-BR'); }
    catch { return d; }
}

function sBadge(s) {
    const m = {
        ANDAMENTO:  ['byw', 'Em andamento'],
        AGUARDANDO: ['bbl', 'Aguardando peça'],
        CONCLUIDO:  ['bgn', 'Concluído'],
        AGENDADO:   ['bgy', 'Agendado'],
        CANCELADO:  ['brd2', 'Cancelado'],
    };
    const [c, l] = m[s] || ['bgy', s];
    return `<span class="badge ${c}"><span class="bdot"></span>${l}</span>`;
}

function highlightMatch(text, q) {
    if (!q) return text;
    try {
        const re = new RegExp('(' + q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&') + ')', 'gi');
        return text.replace(re, '<strong style="color:var(--or)">$1</strong>');
    } catch { return text; }
}

function showDrop(id)  { document.getElementById(id)?.classList.add('show'); }
function hideDrop(id)  { document.getElementById(id)?.classList.remove('show'); }
function getParam(name){ return new URLSearchParams(window.location.search).get(name); }

function maskPhone(inp) {
    let v = inp.value.replace(/\D/g, '').slice(0, 11);
    if (!v.length) { inp.value = ''; return; }
    if (v.length <= 2)       v = '(' + v;
    else if (v.length <= 7)  v = '(' + v.slice(0,2) + ') ' + v.slice(2);
    else if (v.length <= 10) v = '(' + v.slice(0,2) + ') ' + v.slice(2,6) + '-' + v.slice(6);
    else                     v = '(' + v.slice(0,2) + ') ' + v.slice(2,7) + '-' + v.slice(7);
    inp.value = v;
}

function maskPlate(inp) {
    let v = inp.value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 7);
    if (v.length >= 5 && /[A-Z]/.test(v[4])) inp.value = v;
    else { if (v.length > 3) v = v.slice(0,3) + '-' + v.slice(3); inp.value = v; }
}

function showError(msg) {
    const el = document.getElementById('f-err');
    if (el) { el.textContent = msg; el.style.display = 'block'; }
    else alert(msg);
}

function showEmailToast(nome, email, osId) {
    const t = document.getElementById('email-toast');
    const b = document.getElementById('toast-body');
    if (!t || !b) return;
    b.innerHTML = `<strong>${nome}</strong> notificado em <strong>${email}</strong> — OS ${osNum(osId)}.`;
    t.classList.add('show');
    setTimeout(closeToast, 6000);
}

function closeToast() {
    document.getElementById('email-toast')?.classList.remove('show');
}
