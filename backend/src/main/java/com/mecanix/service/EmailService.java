package com.mecanix.service;

import com.mecanix.dto.OrdemResponse;
import com.mecanix.dto.ItemServicoResponse;
import com.mecanix.dto.ItemPecaResponse;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mecanix.mail.remetente}")
    private String remetente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarOS(OrdemResponse os, String destinatario) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // From com encoding correto para evitar erro de conversão
        helper.setFrom(new InternetAddress(remetente, "MECANIX Oficina", "UTF-8"));
        helper.setTo(destinatario);
        helper.setSubject("Ordem de Servico " + osNum(os.getId()) + " - MECANIX");
        helper.setText(buildHtml(os), true);

        mailSender.send(message);
    }

    private String osNum(Long id) {
        return "#" + String.format("%04d", id);
    }

    private String fmtCur(BigDecimal v) {
        if (v == null) return "R$ 0,00";
        return "R$ " + String.format("%.2f", v).replace(".", ",");
    }

    private String fmtStatus(String s) {
        if (s == null) return "—";
        return switch (s) {
            case "ANDAMENTO"  -> "Em andamento";
            case "AGUARDANDO" -> "Aguardando peca";
            case "AGENDADO"   -> "Agendado";
            case "CONCLUIDO"  -> "Concluido";
            case "CANCELADO"  -> "Cancelado";
            default -> s;
        };
    }

    private String corStatus(String s) {
        if (s == null) return "#f59e0b";
        return switch (s) {
            case "CONCLUIDO"  -> "#22c55e";
            case "CANCELADO"  -> "#ef4444";
            case "AGUARDANDO" -> "#3b82f6";
            default           -> "#f59e0b";
        };
    }

    private String esc(String s) {
        if (s == null) return "—";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }

    private String buildHtml(OrdemResponse os) {
        StringBuilder svcs = new StringBuilder();
        if (os.getServicos() != null) {
            for (ItemServicoResponse s : os.getServicos()) {
                svcs.append("<tr>")
                    .append("<td style=\"padding:10px 0;border-bottom:1px solid #2a2a2a;color:#e0e0e0\">").append(esc(s.getDescricao())).append("</td>")
                    .append("<td style=\"padding:10px 0;border-bottom:1px solid #2a2a2a;text-align:right;font-weight:700;color:#f59e0b\">").append(fmtCur(s.getValor())).append("</td>")
                    .append("</tr>");
            }
        }
        if (svcs.length() == 0) svcs.append("<tr><td colspan=\"2\" style=\"color:#6b7280;padding:10px 0\">Nenhum servico.</td></tr>");

        StringBuilder pecas = new StringBuilder();
        if (os.getPecas() != null) {
            for (ItemPecaResponse p : os.getPecas()) {
                pecas.append("<tr>")
                    .append("<td style=\"padding:10px 0;border-bottom:1px solid #2a2a2a;color:#e0e0e0\">").append(esc(p.getNomePeca())).append("</td>")
                    .append("<td style=\"padding:10px 0;border-bottom:1px solid #2a2a2a;text-align:center;color:#9ca3af\">").append(p.getQuantidade()).append("</td>")
                    .append("<td style=\"padding:10px 0;border-bottom:1px solid #2a2a2a;text-align:right;color:#9ca3af\">").append(fmtCur(p.getPrecoUnitario())).append("</td>")
                    .append("<td style=\"padding:10px 0;border-bottom:1px solid #2a2a2a;text-align:right;font-weight:700;color:#f59e0b\">").append(fmtCur(p.getSubtotal())).append("</td>")
                    .append("</tr>");
            }
        }
        if (pecas.length() == 0) pecas.append("<tr><td colspan=\"4\" style=\"color:#6b7280;padding:10px 0\">Nenhuma peca.</td></tr>");

        String obs = (os.getObservacoes() != null && !os.getObservacoes().isBlank())
            ? "<div style=\"margin:12px 0;padding:12px;background:#1a1a1a;border-radius:6px;color:#9ca3af;font-size:13px\">Obs: " + esc(os.getObservacoes()) + "</div>"
            : "";

        String cor = corStatus(os.getStatus());
        String data = os.getData() != null ? os.getData().toString() : "—";
        String mecanico = os.getMecanico() != null ? esc(os.getMecanico()) : "—";

        return "<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\"></head><body style=\"margin:0;padding:0;background:#0f0f0f;font-family:Arial,sans-serif\">"
            + "<div style=\"max-width:620px;margin:0 auto;padding:24px 16px\">"

            // Header
            + "<div style=\"background:#1a1a1a;border-radius:12px 12px 0 0;padding:24px;text-align:center;border-bottom:2px solid #f59e0b\">"
            + "<div style=\"font-size:26px;font-weight:800;color:#f59e0b;letter-spacing:2px\">MECANIX</div>"
            + "<div style=\"font-size:12px;color:#6b7280;margin-top:2px\">Sistema de Gestao de Oficinas</div>"
            + "</div>"

            // OS + Status
            + "<div style=\"background:#111;padding:20px 24px;display:flex;justify-content:space-between;align-items:center\">"
            + "<div><div style=\"font-size:28px;font-weight:800;color:#f59e0b\">" + osNum(os.getId()) + "</div>"
            + "<div style=\"font-size:12px;color:#6b7280;margin-top:2px\">Emitida em " + data + "</div></div>"
            + "<div style=\"background:" + cor + "22;color:" + cor + ";border:1px solid " + cor + ";padding:6px 14px;border-radius:20px;font-size:13px;font-weight:600\">"
            + fmtStatus(os.getStatus()) + "</div>"
            + "</div>"

            // Info grid
            + "<div style=\"background:#141414;padding:20px 24px;border-top:1px solid #222\">"
            + "<table style=\"width:100%\"><tr>"
            + "<td style=\"padding:8px 16px 8px 0;vertical-align:top;width:33%\">"
            + "<div style=\"font-size:10px;color:#4b5563;text-transform:uppercase;letter-spacing:1px;margin-bottom:4px\">Cliente</div>"
            + "<div style=\"font-weight:600;color:#f3f4f6\">" + esc(os.getClienteNome()) + "</div>"
            + "</td>"
            + "<td style=\"padding:8px 16px 8px 0;vertical-align:top;width:33%\">"
            + "<div style=\"font-size:10px;color:#4b5563;text-transform:uppercase;letter-spacing:1px;margin-bottom:4px\">Veiculo</div>"
            + "<div style=\"font-weight:600;color:#f3f4f6\">" + esc(os.getVeiculoDesc()) + "</div>"
            + "<div style=\"font-size:12px;color:#6b7280\">" + esc(os.getVeiculoPlaca()) + "</div>"
            + "</td>"
            + "<td style=\"padding:8px 0;vertical-align:top;width:33%\">"
            + "<div style=\"font-size:10px;color:#4b5563;text-transform:uppercase;letter-spacing:1px;margin-bottom:4px\">Mecanico</div>"
            + "<div style=\"font-weight:600;color:#f3f4f6\">" + mecanico + "</div>"
            + "</td>"
            + "</tr></table>"
            + obs
            + "</div>"

            // Servicos
            + "<div style=\"background:#141414;padding:20px 24px;margin-top:2px\">"
            + "<div style=\"font-size:13px;font-weight:700;color:#f3f4f6;margin-bottom:12px\">SERVICOS</div>"
            + "<table style=\"width:100%;border-collapse:collapse\">"
            + "<thead><tr><th style=\"font-size:11px;color:#4b5563;text-align:left;padding-bottom:8px\">DESCRICAO</th>"
            + "<th style=\"font-size:11px;color:#4b5563;text-align:right;padding-bottom:8px\">VALOR</th></tr></thead>"
            + "<tbody>" + svcs + "</tbody>"
            + "<tfoot><tr><td style=\"padding-top:12px;font-size:12px;color:#6b7280\">Total servicos</td>"
            + "<td style=\"padding-top:12px;text-align:right;font-weight:700;color:#f59e0b\">" + fmtCur(os.getTotalServicos()) + "</td></tr></tfoot>"
            + "</table></div>"

            // Pecas
            + "<div style=\"background:#141414;padding:20px 24px;margin-top:2px\">"
            + "<div style=\"font-size:13px;font-weight:700;color:#f3f4f6;margin-bottom:12px\">PECAS</div>"
            + "<table style=\"width:100%;border-collapse:collapse\">"
            + "<thead><tr>"
            + "<th style=\"font-size:11px;color:#4b5563;text-align:left;padding-bottom:8px\">PECA</th>"
            + "<th style=\"font-size:11px;color:#4b5563;text-align:center;padding-bottom:8px\">QTD</th>"
            + "<th style=\"font-size:11px;color:#4b5563;text-align:right;padding-bottom:8px\">UNIT.</th>"
            + "<th style=\"font-size:11px;color:#4b5563;text-align:right;padding-bottom:8px\">SUBTOTAL</th></tr></thead>"
            + "<tbody>" + pecas + "</tbody>"
            + "<tfoot><tr><td colspan=\"3\" style=\"padding-top:12px;font-size:12px;color:#6b7280\">Total pecas</td>"
            + "<td style=\"padding-top:12px;text-align:right;font-weight:700;color:#f59e0b\">" + fmtCur(os.getTotalPecas()) + "</td></tr></tfoot>"
            + "</table></div>"

            // Total
            + "<div style=\"background:#1a1a1a;border-top:2px solid #f59e0b;padding:20px 24px;border-radius:0 0 12px 12px\">"
            + "<table style=\"width:100%\"><tr>"
            + "<td style=\"font-size:15px;color:#9ca3af\">Total geral</td>"
            + "<td style=\"text-align:right;font-size:28px;font-weight:800;color:#f59e0b\">" + fmtCur(os.getTotal()) + "</td>"
            + "</tr></table></div>"

            // Footer
            + "<div style=\"text-align:center;padding:20px;color:#374151;font-size:12px\">MECANIX - Gestao de Oficinas - E-mail automatico</div>"
            + "</div></body></html>";
    }
}
