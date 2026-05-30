package es.codeurjc.shopventory.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.email.enabled:false}")
    private boolean enabled;

    @Value("${app.email.from:shopventory.alerts@gmail.com}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendWelcomeEmail(String to, String name) {
        if (!enabled) return;
        String subject = "Welcome to Shopventory";
        String body = """
                <div style="font-family:sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#0d6efd">Welcome to Shopventory, %s!</h2>
                  <p>Your account has been created successfully.</p>
                  <p>A system administrator will review and approve your account shortly.
                  Once approved you will have full access to the inventory management system.</p>
                  <hr>
                  <p style="color:#6c757d;font-size:12px">This is an automated message — please do not reply.</p>
                </div>
                """.formatted(name);
        send(to, subject, body);
    }

    @Async
    public void sendLowStockAlert(List<String> adminEmails, String productName, String sku,
                                   int currentStock, int threshold) {
        if (!enabled || adminEmails.isEmpty()) return;
        String subject = "[Shopventory] Low Stock Alert: " + productName;
        String body = """
                <div style="font-family:sans-serif;max-width:600px;margin:auto">
                  <h2 style="color:#dc3545">&#9888; Low Stock Alert</h2>
                  <p>The following product has fallen below its minimum stock threshold:</p>
                  <table style="border-collapse:collapse;width:100%%">
                    <tr><td style="padding:8px;font-weight:bold;background:#f8f9fa">Product</td>
                        <td style="padding:8px">%s</td></tr>
                    <tr><td style="padding:8px;font-weight:bold;background:#f8f9fa">SKU</td>
                        <td style="padding:8px">%s</td></tr>
                    <tr><td style="padding:8px;font-weight:bold;background:#f8f9fa">Current Stock</td>
                        <td style="padding:8px;color:#dc3545;font-weight:bold">%d units</td></tr>
                    <tr><td style="padding:8px;font-weight:bold;background:#f8f9fa">Minimum Threshold</td>
                        <td style="padding:8px">%d units</td></tr>
                  </table>
                  <p style="margin-top:16px">Please restock this product as soon as possible.</p>
                  <hr>
                  <p style="color:#6c757d;font-size:12px">This is an automated message from Shopventory.</p>
                </div>
                """.formatted(productName, sku != null ? sku : "—", currentStock, threshold);

        for (String email : adminEmails) {
            send(email, subject, body);
        }
    }

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (Exception e) {
            // Log but never propagate — a mail failure must not break the main operation
            org.slf4j.LoggerFactory.getLogger(EmailService.class)
                    .error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }
}
