package com.example.demo.service;

import com.example.demo.enums.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender javaMailSender, @Value("${app.mail.from}") String fromEmail) {
        this.javaMailSender = javaMailSender;
        this.fromEmail = fromEmail;
    }

    @Async
    public void sendOrderCreatedEmail(String toEmail, Long orderId, String originAddress, String destinationAddress, LocalDate scheduledPickUpDate, BigDecimal cost) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);
            messageHelper.setSubject("Замовлення створено №" + orderId);

            String htmlContent = buildOrderCreatedEmailTemplate(orderId, originAddress, destinationAddress, scheduledPickUpDate, cost);

            messageHelper.setText(htmlContent, true);
            javaMailSender.send(message);

            log.info("Order created email sent successfully to: {}", toEmail);


        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send order creation email" + e.getMessage());
        }
    }

    @Async
    public void sendOrderStatusChangedEmail(String toEmail, Long orderId, OrderStatus oldStatus, OrderStatus newStatus, String originAddress, String destinationAddress) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(toEmail);
            messageHelper.setSubject("Зміна статусу замовлення №" + orderId);

            String htmlContent = buildOrderStatusChangedEmailTemplate(
                    orderId, oldStatus, newStatus, originAddress, destinationAddress
            );

            messageHelper.setText(htmlContent, true);
            javaMailSender.send(message);

            log.info("Order status changed email sent successfully to: {}", toEmail);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send order status changed email" + e.getMessage());
        }
    }

    private String buildOrderCreatedEmailTemplate(
            Long orderId,
            String originAddress,
            String destinationAddress,
            LocalDate scheduledPickupDate,
            BigDecimal cost
    ) {
        String pickupDateFormatted = scheduledPickupDate != null
                ? scheduledPickupDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "Не вказано";

        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <style>
                                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                                .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                                .content { background-color: #f9f9f9; padding: 20px; }
                                .info-row { margin: 10px 0; padding: 10px; background-color: white; border-left: 4px solid #4CAF50; }
                                .label { font-weight: bold; color: #555; }
                                .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <h1>✅ Замовлення створено</h1>
                                </div>
                                <div class="content">
                                    <p>Вітаємо! Ваше замовлення успішно створено.</p>
                        
                                    <div class="info-row">
                                        <span class="label">Номер замовлення:</span> #%d
                                    </div>
                        
                                    <div class="info-row">
                                        <span class="label">Звідки:</span> %s
                                    </div>
                        
                                    <div class="info-row">
                                        <span class="label">Куди:</span> %s
                                    </div>
                        
                                    <div class="info-row">
                                        <span class="label">Дата підбору:</span> %s
                                    </div>
                        
                                    <div class="info-row">
                                        <span class="label">Вартість:</span> %s грн
                                    </div>
                        
                                    <p style="margin-top: 20px;">Наша команда незабаром призначить водія та транспорт для вашого замовлення.</p>
                                    <p>Ви отримаєте email-сповіщення про зміну статусу.</p>
                                </div>
                                <div class="footer">
                                    <p>Це автоматичне повідомлення. Будь ласка, не відповідайте на нього.</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                orderId,
                originAddress,
                destinationAddress,
                pickupDateFormatted,
                cost
        );
    }

    private String buildOrderStatusChangedEmailTemplate(
            Long orderId,
            OrderStatus oldStatus,
            OrderStatus newStatus,
            String originAddress,
            String destinationAddress
    ) {
        String statusMessage = getStatusMessage(newStatus);
        String statusColor = getStatusColor(newStatus);
        String statusDescription = getStatusDescription(newStatus);

        return String.format("""
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <style>
                                body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                                .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                                .header { background-color: %s; color: white; padding: 20px; text-align: center; }
                                .content { background-color: #f9f9f9; padding: 20px; }
                                .status-badge { display: inline-block; padding: 8px 16px; border-radius: 20px;
                                               background-color: %s; color: white; font-weight: bold; }
                                .info-row { margin: 10px 0; padding: 10px; background-color: white; }
                                .label { font-weight: bold; color: #555; }
                                .footer { text-align: center; margin-top: 20px; color: #777; font-size: 12px; }
                            </style>
                        </head>
                        <body>
                            <div class="container">
                                <div class="header">
                                    <h1>📦 %s</h1>
                                </div>
                                <div class="content">
                                    <p>Статус вашого замовлення змінився:</p>
                        
                                    <div class="info-row">
                                        <span class="label">Номер замовлення:</span> #%d
                                    </div>
                        
                                    <div class="info-row">
                                        <span class="label">Попередній статус:</span> %s
                                    </div>
                        
                                    <div class="info-row">
                                        <span class="label">Новий статус:</span>
                                        <span class="status-badge">%s</span>
                                    </div>
                        
                                    <div class="info-row">
                                        <span class="label">Маршрут:</span> %s → %s
                                    </div>
                        
                                    <p style="margin-top: 20px; padding: 15px; background-color: #e8f5e9; border-left: 4px solid #4CAF50;">
                                        %s
                                    </p>
                                </div>
                                <div class="footer">
                                    <p>Це автоматичне повідомлення. Будь ласка, не відповідайте на нього.</p>
                                </div>
                            </div>
                        </body>
                        </html>
                        """,
                statusColor,
                statusColor,
                statusMessage,
                orderId,
                oldStatus != null ? translateStatus(oldStatus) : "Створено",
                translateStatus(newStatus),
                originAddress,
                destinationAddress,
                statusDescription
        );
    }

    private String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case ASSIGNED -> "Призначено водія";
            case IN_TRANSIT -> "Замовлення в дорозі";
            case COMPLETED -> "Замовлення виконано";
            case CANCELLED -> "Замовлення скасовано";
            default -> "Зміна статусу замовлення";
        };
    }

    private String getStatusColor(OrderStatus status) {
        return switch (status) {
            case ASSIGNED -> "#2196F3";
            case IN_TRANSIT -> "#FF9800";
            case COMPLETED -> "#4CAF50";
            case CANCELLED -> "#F44336";
            default -> "#9E9E9E";
        };
    }

    private String getStatusDescription(OrderStatus status) {
        return switch (status) {
            case ASSIGNED -> "Ми призначили водія та транспорт для вашого замовлення. Очікуйте на підбір вантажу.";
            case IN_TRANSIT -> "Ваш вантаж у дорозі! Водій прямує до пункту призначення.";
            case COMPLETED -> "Замовлення успішно виконано. Дякуємо за співпрацю!";
            case CANCELLED -> "На жаль, замовлення було скасовано. Зверніться до служби підтримки для деталей.";
            default -> "Статус вашого замовлення змінився.";
        };
    }

    private String translateStatus(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Очікує";
            case ASSIGNED -> "Призначено";
            case IN_TRANSIT -> "В дорозі";
            case COMPLETED -> "Виконано";
            case CANCELLED -> "Скасовано";
        };
    }
}
