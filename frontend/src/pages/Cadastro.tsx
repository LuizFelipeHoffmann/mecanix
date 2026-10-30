import { useState, useEffect, type KeyboardEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useModal } from '../components/Modal'
import {
  maskCnpj, maskCpf, maskCep, maskPhone, maskCartao, maskValidadeCartao, isPhoneValid,
  UF_LIST, fmtCur, planosAPI, assinaturasAPI, type Plano,
} from '../api'

export default function Cadastro() {
  const { cadastrar } = useAuth()
  const { showAlert } = useModal()
  const navigate = useNavigate()

  // dados da oficina
  const [tipoDocumento, setTipoDocumento] = useState<'CNPJ' | 'CPF'>('CNPJ')
  const [nomeFantasia, setNomeFantasia] = useState('')
  const [razaoSocial, setRazaoSocial] = useState('')
  const [documento, setDocumento] = useState('')
  const [telefoneEmpresa, setTelefoneEmpresa] = useState('')
  const [cepEmpresa, setCepEmpresa] = useState('')
  const [ruaEmpresa, setRuaEmpresa] = useState('')
  const [numeroEmpresa, setNumeroEmpresa] = useState('')
  const [bairroEmpresa, setBairroEmpresa] = useState('')
  const [cidadeEmpresa, setCidadeEmpresa] = useState('')
  const [ufEmpresa, setUfEmpresa] = useState('')
  const [aceiteTermos, setAceiteTermos] = useState(false)

  // plano escolhido — os três têm os mesmos recursos, só muda a cobrança
  // (ver PlanoService no backend): Grátis (3 meses, depois cobra o mensal
  // sozinho), Mensal, ou Anual (mais barato que pagar o mensal 12x).
  const [planos, setPlanos] = useState<Plano[]>([])
  const [planoTipo, setPlanoTipo] = useState<'GRATIS' | 'MENSAL' | 'ANUAL'>('GRATIS')

  useEffect(() => {
    // p pode vir null (ex: backend ainda não reiniciado com a rota /planos
    // liberada sem sessão) — nunca deixa planos virar algo que não seja array,
    // senão o planos.length lá embaixo quebra a tela inteira.
    planosAPI.listar().then(p => setPlanos(p || [])).catch(() => setPlanos([]))
  }, [])

  // forma de pagamento — exigida pra qualquer plano, inclusive o Grátis
  // (precisa ter cartão ou PIX cadastrado pra quando os 3 meses acabarem).
  const [formaPagamento, setFormaPagamento] = useState<'CARTAO' | 'PIX'>('CARTAO')
  const [cartaoNumero, setCartaoNumero] = useState('')
  const [cartaoNome, setCartaoNome] = useState('')
  const [cartaoValidade, setCartaoValidade] = useState('')
  const [cartaoCvv, setCartaoCvv] = useState('')

  const [nomeUsuario, setNomeUsuario] = useState('')
  const [emailUsuario, setEmailUsuario] = useState('')
  const [senha, setSenha] = useState('')
  const [confirmarSenha, setConfirmarSenha] = useState('')
  const [verSenha, setVerSenha] = useState(false)
  const [verConfirmarSenha, setVerConfirmarSenha] = useState(false)

  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  function onTipoDocumentoChange(t: 'CNPJ' | 'CPF') {
    setTipoDocumento(t)
    setDocumento('') // troca o formato, então limpa pra não ficar mascarado errado
  }

  async function doCadastro() {
    if (!nomeFantasia || !razaoSocial || !documento) { setError('Preencha os dados da oficina'); return }

    // a quantidade de dígitos já diz se é CNPJ (14) ou CPF (11) — valida e
    // manda pro backend por isso, em vez de confiar só no botão marcado, pra
    // não dar "CNPJ inválido" se a pessoa digitou um CPF e esqueceu de trocar
    const digitosDocumento = documento.replace(/\D/g, '')
    const tipoDocFinal: 'CNPJ' | 'CPF' = digitosDocumento.length === 11 ? 'CPF' : 'CNPJ'
    if (tipoDocFinal === 'CNPJ' && digitosDocumento.length !== 14) { setError('CNPJ inválido'); return }
    if (tipoDocFinal === 'CPF' && digitosDocumento.length !== 11) { setError('CPF inválido'); return }
    if (!nomeUsuario || !emailUsuario || !senha) { setError('Preencha seus dados de acesso'); return }
    if (senha.length < 6) { setError('A senha deve ter no mínimo 6 caracteres'); return }
    if (senha !== confirmarSenha) { setError('As senhas não coincidem'); return }
    if (!aceiteTermos) { setError('É necessário aceitar os Termos de Uso e a Política de Privacidade'); return }

    // forma de pagamento é obrigatória pra qualquer plano, inclusive o Grátis
    if (formaPagamento === 'CARTAO') {
      const digitosCartao = cartaoNumero.replace(/\D/g, '')
      if (digitosCartao.length < 13) { setError('Número do cartão inválido'); return }
      if (!cartaoNome.trim()) { setError('Informe o nome impresso no cartão'); return }
      if (!/^\d{2}\/\d{2}$/.test(cartaoValidade)) { setError('Validade do cartão inválida (MM/AA)'); return }
      if (!/^\d{3,4}$/.test(cartaoCvv)) { setError('CVV inválido'); return }
    }

    setError('')
    setLoading(true)
    try {
      await cadastrar({
        nomeFantasia, razaoSocial, cnpj: documento, tipoDocumento: tipoDocFinal, planoTipo,
        telefoneEmpresa, cepEmpresa, ruaEmpresa, numeroEmpresa, bairroEmpresa, cidadeEmpresa, ufEmpresa,
        aceiteTermos, nomeUsuario, emailUsuario: emailUsuario.trim(), senha,
      })

      // conta já criada (e logada) — agora registra a forma de pagamento.
      // Isso é obrigatório pros três planos, então sempre chama o checkout
      // (antes só chamava pra plano pago, e por isso parecia que "não
      // acontecia nada" ao escolher um plano: a conta ia direto pro
      // dashboard sem nenhuma confirmação na tela).
      try {
        const chk = await assinaturasAPI.checkout({
          plano: planoTipo,
          formaPagamento,
          cartaoNumero: formaPagamento === 'CARTAO' ? cartaoNumero : undefined,
          cartaoNome: formaPagamento === 'CARTAO' ? cartaoNome : undefined,
          cartaoValidade: formaPagamento === 'CARTAO' ? cartaoValidade : undefined,
          cartaoCvv: formaPagamento === 'CARTAO' ? cartaoCvv : undefined,
        })
        if (chk.checkoutUrl && !chk.sandbox) { window.location.href = chk.checkoutUrl; return }
        if (chk.mensagem) showAlert(chk.mensagem, 'Conta criada!')
      } catch (e: unknown) {
        // a conta já foi criada — não trava o cadastro se o pagamento falhar,
        // só avisa que vai precisar resolver isso depois.
        showAlert('Sua conta foi criada, mas não conseguimos registrar a forma de pagamento agora' +
          (e instanceof Error ? ': ' + e.message : '') + '. Você pode configurar isso depois pelo sistema.', 'Atenção')
      }
      navigate('/dashboard')
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Não foi possível criar a conta.')
    } finally {
      setLoading(false)
    }
  }

  function onKey(e: KeyboardEvent) {
    if (e.key === 'Enter') doCadastro()
  }

  return (
    <div className="lw">
      <div className="lbox lbox-cad">
        <div className="llogo">
          <div className="lico">
            <svg viewBox="0 0 24 24"><path d="M22.7 19l-9.1-9.1c.9-2.3.4-5-1.5-6.9-2-2-5-2.4-7.4-1.3L9 6 6 9 1.6 4.7C.4 7.1.9 10.1 2.9 12.1c1.9 1.9 4.6 2.4 6.9 1.5l9.1 9.1c.4.4 1 .4 1.4 0l2.3-2.3c.5-.4.5-1.1.1-1.4z" /></svg>
          </div>
          <div className="lnm">MECANIX</div>
          <div className="lsub">Criar conta da sua oficina</div>
        </div>

        {error && (
          <div style={{ display: 'block', background: 'var(--rdd)', border: '1px solid rgba(239,68,68,.3)', borderRadius: 8, padding: '10px 14px', fontSize: 13, color: 'var(--rd)', marginBottom: 14 }}>
            {error}
          </div>
        )}

        <div className="hint-t" style={{ marginBottom: 8 }}>Dados da oficina</div>
        <div className="fgrid">
          <div className="fg full">
            <label>Nome fantasia *</label>
            <input className="finp" type="text" placeholder="Ex: Auto Center do João"
              value={nomeFantasia} onChange={e => setNomeFantasia(e.target.value)} />
          </div>
          <div className="fg full">
            <label>Razão social *</label>
            <input className="finp" type="text" placeholder="Ex: João Silva Serviços Automotivos LTDA"
              value={razaoSocial} onChange={e => setRazaoSocial(e.target.value)} />
          </div>

          <div className="fg full" style={{ flexDirection: 'row', alignItems: 'center', gap: 16 }}>
            <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
              <input type="radio" name="tipoDoc" checked={tipoDocumento === 'CNPJ'} onChange={() => onTipoDocumentoChange('CNPJ')} />
              Tenho CNPJ
            </label>
            <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
              <input type="radio" name="tipoDoc" checked={tipoDocumento === 'CPF'} onChange={() => onTipoDocumentoChange('CPF')} />
              Ainda não tenho CNPJ (usar CPF)
            </label>
          </div>

          <div className="fg">
            <label>{tipoDocumento} *</label>
            <input className="finp" type="text"
              placeholder={tipoDocumento === 'CNPJ' ? '00.000.000/0000-00' : '000.000.000-00'}
              maxLength={tipoDocumento === 'CNPJ' ? 18 : 14}
              value={documento}
              onChange={e => setDocumento(tipoDocumento === 'CNPJ' ? maskCnpj(e.target.value) : maskCpf(e.target.value))} />
          </div>
          <div className="fg">
            <label>Telefone</label>
            <input className="finp" type="tel" placeholder="(41) 99999-0000" maxLength={15}
              value={telefoneEmpresa} onChange={e => setTelefoneEmpresa(maskPhone(e.target.value))} />
            {!isPhoneValid(telefoneEmpresa) && (
              <div style={{ fontSize: 12, color: '#f59e0b', marginTop: 4 }}>Telefone inválido</div>
            )}
          </div>

          <div className="fg">
            <label>CEP</label>
            <input className="finp" type="text" placeholder="00000-000" maxLength={9}
              value={cepEmpresa} onChange={e => setCepEmpresa(maskCep(e.target.value))} />
          </div>
          <div className="fg">
            <label>Cidade</label>
            <input className="finp" type="text" placeholder="Curitiba"
              value={cidadeEmpresa} onChange={e => setCidadeEmpresa(e.target.value)} />
          </div>
          <div className="fg">
            <label>Estado</label>
            <select className="finp" value={ufEmpresa} onChange={e => setUfEmpresa(e.target.value)}>
              <option value="">UF</option>
              {UF_LIST.map(uf => <option key={uf} value={uf}>{uf}</option>)}
            </select>
          </div>
          <div className="fg">
            <label>Bairro</label>
            <input className="finp" type="text" placeholder="Centro"
              value={bairroEmpresa} onChange={e => setBairroEmpresa(e.target.value)} />
          </div>
          <div className="fg" style={{ flex: 2 }}>
            <label>Rua</label>
            <input className="finp" type="text" placeholder="Rua das Flores"
              value={ruaEmpresa} onChange={e => setRuaEmpresa(e.target.value)} />
          </div>
          <div className="fg">
            <label>Número</label>
            <input className="finp" type="text" placeholder="100"
              value={numeroEmpresa} onChange={e => setNumeroEmpresa(e.target.value)} />
          </div>
        </div>

        {planos.length > 0 && (
          <>
            <div className="hint-t" style={{ margin: '18px 0 2px' }}>Escolha seu plano</div>
            <div style={{ fontSize: 12, color: 'var(--tx3)', marginBottom: 8 }}>
              Os três planos têm exatamente os mesmos recursos — a única diferença é a cobrança.
            </div>
            <div className="planos-grid" style={{ marginBottom: 18 }}>
              {planos.map(p => (
                <div key={p.tipo} className={`plano-card${planoTipo === p.tipo ? ' on' : ''}`} onClick={() => setPlanoTipo(p.tipo)}>
                  <div className="plano-nome">{p.nome}</div>
                  <div className="plano-preco">
                    {p.trialMeses > 0 ? (
                      <>Grátis<small> por {p.trialMeses} meses</small></>
                    ) : p.cicloMeses >= 12 ? (
                      <>{fmtCur(p.precoCobranca)}<small> /ano</small></>
                    ) : (
                      <>{fmtCur(p.precoCobranca)}<small> /mês</small></>
                    )}
                  </div>
                  {p.trialMeses > 0 && (
                    <div style={{ fontSize: 12, color: 'var(--tx3)', marginTop: -4, marginBottom: 6 }}>
                      depois, {fmtCur(p.precoCobranca)}/mês
                    </div>
                  )}
                  {p.cicloMeses >= 12 && p.trialMeses === 0 && (
                    <div style={{ fontSize: 12, color: 'var(--gn)', marginTop: -4, marginBottom: 6 }}>
                      equivale a {fmtCur(p.precoEquivalenteMensal)}/mês — mais barato que o mensal
                    </div>
                  )}
                  {(p.destaques || []).map((d, i) => <div key={i} className="plano-feat">✓ {d}</div>)}
                </div>
              ))}
            </div>
          </>
        )}

        <div className="hint-t" style={{ margin: '18px 0 2px' }}>Forma de pagamento</div>
        <div style={{ fontSize: 12, color: 'var(--tx3)', marginBottom: 10 }}>
          {planoTipo === 'GRATIS'
            ? 'Mesmo no plano grátis pedimos um cartão ou PIX cadastrado — você só é cobrado depois dos 3 meses.'
            : 'Dados de cobrança da sua assinatura.'}
        </div>
        <div style={{ display: 'flex', gap: 16, marginBottom: 14 }}>
          <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
            <input type="radio" name="formaPag" checked={formaPagamento === 'CARTAO'} onChange={() => setFormaPagamento('CARTAO')} />
            Cartão de crédito
          </label>
          <label style={{ display: 'flex', alignItems: 'center', gap: 6, fontWeight: 500, fontSize: 13, color: 'var(--tx2)', cursor: 'pointer' }}>
            <input type="radio" name="formaPag" checked={formaPagamento === 'PIX'} onChange={() => setFormaPagamento('PIX')} />
            PIX
          </label>
        </div>

        {formaPagamento === 'CARTAO' ? (
          <div className="fgrid" style={{ marginBottom: 4 }}>
            <div className="fg full">
              <label>Número do cartão *</label>
              <input className="finp" type="text" placeholder="0000 0000 0000 0000" maxLength={19}
                value={cartaoNumero} onChange={e => setCartaoNumero(maskCartao(e.target.value))} />
            </div>
            <div className="fg full">
              <label>Nome impresso no cartão *</label>
              <input className="finp" type="text" placeholder="Como está no cartão"
                value={cartaoNome} onChange={e => setCartaoNome(e.target.value.toUpperCase())} />
            </div>
            <div className="fg">
              <label>Validade *</label>
              <input className="finp" type="text" placeholder="MM/AA" maxLength={5}
                value={cartaoValidade} onChange={e => setCartaoValidade(maskValidadeCartao(e.target.value))} />
            </div>
            <div className="fg">
              <label>CVV *</label>
              <input className="finp" type="text" placeholder="123" maxLength={4}
                value={cartaoCvv} onChange={e => setCartaoCvv(e.target.value.replace(/\D/g, '').slice(0, 4))} />
            </div>
          </div>
        ) : (
          <div style={{ fontSize: 13, color: 'var(--tx2)', marginBottom: 4 }}>
            A chave PIX pra cobrança recorrente é gerada assim que a conta for criada.
          </div>
        )}

        <div className="hint-t" style={{ margin: '18px 0 8px' }}>Seus dados de acesso (administrador)</div>
        <div className="fgrid">
          <div className="fg full">
            <label>Seu nome *</label>
            <input className="finp" type="text" placeholder="Ex: João Silva"
              value={nomeUsuario} onChange={e => setNomeUsuario(e.target.value)} />
          </div>
          <div className="fg full">
            <label>E-mail *</label>
            <input className="finp" type="email" placeholder="email@exemplo.com"
              value={emailUsuario} onChange={e => setEmailUsuario(e.target.value)} onKeyDown={onKey} />
          </div>
          <div className="fg">
            <label>Senha *</label>
            <div style={{ position: 'relative' }}>
              <input className="finp" type={verSenha ? 'text' : 'password'} placeholder="mínimo 6 caracteres" style={{ paddingRight: 40 }}
                value={senha} onChange={e => setSenha(e.target.value)} onKeyDown={onKey} />
              <button type="button" onClick={() => setVerSenha(v => !v)} aria-label={verSenha ? 'Ocultar senha' : 'Mostrar senha'}
                style={{ position: 'absolute', right: 10, top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', color: 'var(--tx3)', cursor: 'pointer', padding: 4 }}>
                {verSenha ? (
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" /><path d="M1 1l22 22" /></svg>
                ) : (
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" /></svg>
                )}
              </button>
            </div>
          </div>
          <div className="fg">
            <label>Confirmar senha *</label>
            <div style={{ position: 'relative' }}>
              <input className="finp" type={verConfirmarSenha ? 'text' : 'password'} placeholder="••••••••" style={{ paddingRight: 40 }}
                value={confirmarSenha} onChange={e => setConfirmarSenha(e.target.value)} onKeyDown={onKey} />
              <button type="button" onClick={() => setVerConfirmarSenha(v => !v)} aria-label={verConfirmarSenha ? 'Ocultar senha' : 'Mostrar senha'}
                style={{ position: 'absolute', right: 10, top: '50%', transform: 'translateY(-50%)', background: 'none', border: 'none', color: 'var(--tx3)', cursor: 'pointer', padding: 4 }}>
                {verConfirmarSenha ? (
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" /><path d="M1 1l22 22" /></svg>
                ) : (
                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" /></svg>
                )}
              </button>
            </div>
            {confirmarSenha && senha !== confirmarSenha && (
              <div style={{ fontSize: 12, color: 'var(--rd)', marginTop: 4 }}>As senhas não coincidem</div>
            )}
          </div>
        </div>

        <div className="fg" style={{ flexDirection: 'row', alignItems: 'center', gap: 8, margin: '16px 0 18px' }}>
          <input type="checkbox" id="aceite" checked={aceiteTermos} onChange={e => setAceiteTermos(e.target.checked)} style={{ width: 16, height: 16 }} />
          <label htmlFor="aceite" style={{ fontSize: 13, color: 'var(--tx2)', fontWeight: 400 }}>
            Li e aceito os Termos de Uso e a Política de Privacidade do MECANIX
          </label>
        </div>

        <button className="btn btn-p btn-full" disabled={loading} onClick={doCadastro}>
          {loading ? 'Criando conta...' : 'Criar conta'}
        </button>

        <div style={{ textAlign: 'center', marginTop: 14, fontSize: 13, color: 'var(--tx2)' }}>
          Já tem uma conta?{' '}
          <span style={{ color: 'var(--or)', fontWeight: 600, cursor: 'pointer' }} onClick={() => navigate('/login')}>
            Entrar
          </span>
        </div>
      </div>
    </div>
  )
}
