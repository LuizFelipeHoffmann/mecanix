const API_BASE = 'http://localhost:8080/api';

export interface User {
  id: number;
  nome: string;
  email: string;
  perfil: 'ADMIN' | 'SERVICOS' | 'ESTOQUE';
  iniciais: string;
}

export interface Cliente {
  id: number;
  nome: string;
  cpf: string;
  telefone?: string;
  email?: string;
  endereco?: string;
  totalVeiculos: number;
  totalOrdens: number;
}

export interface Veiculo {
  id: number;
  clienteId: number;
  clienteNome?: string;
  marca: string;
  modelo: string;
  ano?: string;
  cor?: string;
  placa: string;
  tipo: string;
  quilometragem?: number;
  observacoes?: string;
  totalOrdens: number;
}

export interface EstoqueItem {
  id: number;
  codigo: string;
  nome: string;
  categoria?: string;
  quantidade: number;
  quantidadeMinima: number;
  precoUnitario: number;
  tipos?: string[];
  alertaEstoque: boolean;
}

export interface Servico {
  descricao: string;
  valor: number;
}

export interface Peca {
  estoqueId: number;
  nomePeca?: string;
  quantidade: number;
  precoUnitario: number;
  subtotal?: number;
}

export interface OrdemServico {
  id: number;
  clienteId: number;
  clienteNome?: string;
  clienteEmail?: string;
  veiculoId: number;
  veiculoDesc?: string;
  veiculoPlaca?: string;
  status: string;
  mecanico?: string;
  data?: string;
  observacoes?: string;
  servicos?: Servico[];
  pecas?: Peca[];
  total: number;
  totalServicos: number;
  totalPecas: number;
}

export interface DashboardData {
  ordensAbertas: number;
  ordensConcluidas: number;
  totalClientes: number;
  faturamentoConcluido: number;
  ticketMedio: number;
}

async function apiFetch<T>(method: string, path: string, body?: unknown): Promise<T> {
  const opts: RequestInit = {
    method,
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
  };
  if (body) opts.body = JSON.stringify(body);

  const res = await fetch(API_BASE + path, opts);

  if (res.status === 401) {
    sessionStorage.removeItem('mecanix_user');
    window.location.href = '/login';
    return null as T;
  }

  if (!res.ok) {
    const err = await res.json().catch(() => ({ erro: 'Erro na requisição' }));
    throw new Error(err.erro || 'Erro na requisição');
  }

  if (res.status === 204) return null as T;
  return res.json();
}

export const authAPI = {
  login:  (email: string, senha: string) => apiFetch<User>('POST', '/auth/login', { email, senha }),
  logout: () => apiFetch<void>('POST', '/auth/logout'),
  me:     () => apiFetch<User>('GET', '/auth/me'),
};

export const clientesAPI = {
  listar:    () => apiFetch<Cliente[]>('GET', '/clientes'),
  buscar:    (id: number) => apiFetch<Cliente>('GET', `/clientes/${id}`),
  criar:     (dados: Partial<Cliente>) => apiFetch<Cliente>('POST', '/clientes', dados),
  atualizar: (id: number, dados: Partial<Cliente>) => apiFetch<Cliente>('PUT', `/clientes/${id}`, dados),
  deletar:   (id: number) => apiFetch<void>('DELETE', `/clientes/${id}`),
};

export const veiculosAPI = {
  listar:           () => apiFetch<Veiculo[]>('GET', '/veiculos'),
  listarPorCliente: (cid: number) => apiFetch<Veiculo[]>('GET', `/veiculos?clienteId=${cid}`),
  buscar:    (id: number) => apiFetch<Veiculo>('GET', `/veiculos/${id}`),
  criar:     (dados: Partial<Veiculo>) => apiFetch<Veiculo>('POST', '/veiculos', dados),
  atualizar: (id: number, dados: Partial<Veiculo>) => apiFetch<Veiculo>('PUT', `/veiculos/${id}`, dados),
  deletar:   (id: number) => apiFetch<void>('DELETE', `/veiculos/${id}`),
};

export const ordensAPI = {
  listar:          () => apiFetch<OrdemServico[]>('GET', '/ordens'),
  listarPorStatus: (status: string) => apiFetch<OrdemServico[]>('GET', `/ordens?status=${status}`),
  buscar:    (id: number) => apiFetch<OrdemServico>('GET', `/ordens/${id}`),
  criar:     (dados: unknown) => apiFetch<OrdemServico>('POST', '/ordens', dados),
  atualizar: (id: number, dados: unknown) => apiFetch<OrdemServico>('PUT', `/ordens/${id}`, dados),
  deletar:   (id: number) => apiFetch<void>('DELETE', `/ordens/${id}`),
  enviarEmail: (id: number) => apiFetch<void>('POST', `/ordens/${id}/enviar-email`),
};

export const estoqueAPI = {
  listar:        () => apiFetch<EstoqueItem[]>('GET', '/estoque'),
  listarAlertas: () => apiFetch<EstoqueItem[]>('GET', '/estoque/alertas'),
  listarPorTipo: (tipo: string) => apiFetch<EstoqueItem[]>('GET', `/estoque?tipo=${tipo}`),
  buscar:    (id: number) => apiFetch<EstoqueItem>('GET', `/estoque/${id}`),
  criar:     (dados: Partial<EstoqueItem>) => apiFetch<EstoqueItem>('POST', '/estoque', dados),
  atualizar: (id: number, dados: Partial<EstoqueItem>) => apiFetch<EstoqueItem>('PUT', `/estoque/${id}`, dados),
  deletar:   (id: number) => apiFetch<void>('DELETE', `/estoque/${id}`),
};

export const dashboardAPI = {
  get: () => apiFetch<DashboardData>('GET', '/dashboard'),
};

// ── HELPERS ──
export function osNum(id: number) {
  return '#' + String(id).padStart(4, '0');
}

export function fmtCur(v: number | string | null | undefined) {
  return 'R$ ' + Number(v || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2 });
}

export function fmtDate(d: string | null | undefined) {
  if (!d) return '—';
  try { return new Date(d + 'T12:00').toLocaleDateString('pt-BR'); }
  catch { return d; }
}

export function maskPhone(value: string): string {
  let v = value.replace(/\D/g, '').slice(0, 11);
  if (!v.length) return '';
  if (v.length <= 2)       return '(' + v;
  if (v.length <= 7)       return '(' + v.slice(0,2) + ') ' + v.slice(2);
  if (v.length <= 10)      return '(' + v.slice(0,2) + ') ' + v.slice(2,6) + '-' + v.slice(6);
  return '(' + v.slice(0,2) + ') ' + v.slice(2,7) + '-' + v.slice(7);
}

export function maskPlate(value: string): string {
  let v = value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 7);
  if (v.length >= 5 && /[A-Z]/.test(v[4])) return v;
  if (v.length > 3) return v.slice(0,3) + '-' + v.slice(3);
  return v;
}

export const STATUS_LABELS: Record<string, string> = {
  ANDAMENTO:  'Em andamento',
  AGUARDANDO: 'Aguardando peça',
  CONCLUIDO:  'Concluído',
  AGENDADO:   'Agendado',
  CANCELADO:  'Cancelado',
};

export const STATUS_BADGE: Record<string, string> = {
  ANDAMENTO:  'byw',
  AGUARDANDO: 'bbl',
  CONCLUIDO:  'bgn',
  AGENDADO:   'bgy',
  CANCELADO:  'brd2',
};

export const TIPO_LABELS: Record<string, string> = {
  SEDAN: 'Sedan', HATCH: 'Hatch', SUV: 'SUV', PICKUP: 'Pickup', ELETRICO: 'Elétrico',
};

export const TIPO_COLORS: Record<string, string> = {
  SEDAN: '#3b82f6', HATCH: '#8b5cf6', SUV: '#0ea5e9', PICKUP: '#f59e0b', ELETRICO: '#22c55e',
};

export const ROLE_LABELS: Record<string, string> = {
  ADMIN: 'Acesso total', SERVICOS: 'Atendimento', ESTOQUE: 'Estoque',
};

export const ROLE_COLORS: Record<string, string> = {
  ADMIN: 'var(--or)', SERVICOS: 'var(--bl)', ESTOQUE: 'var(--gn)',
};

// ── SESSION ──
const SESSION_KEY = 'mecanix_user';

export function getUser(): User | null {
  try { return JSON.parse(sessionStorage.getItem(SESSION_KEY) || 'null'); }
  catch { return null; }
}

export function setUser(user: User) {
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(user));
}

export function clearUser() {
  sessionStorage.removeItem(SESSION_KEY);
}
