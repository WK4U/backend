package com.workforyou.backend.service;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private SendGrid sendGrid;

    public void send(String to, String subject, String text){
        Email from = new Email("workfooryu@gmail.com");
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", text);
        Mail mail = new Mail(from, subject, toEmail, content);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            // Envia a requisição via API HTTPS (não usa porta 587)
            Response response = sendGrid.api(request);

            // Opcional: Log para verificar se o envio foi bem-sucedido
            if (response.getStatusCode() >= 400) {
                // Lançar exceção ou logar erro
                System.err.println("Erro ao enviar e-mail (SendGrid). Status: " + response.getStatusCode() + ", Body: " + response.getBody());
            } else {
                System.out.println("E-mail enviado com sucesso via SendGrid. Status: " + response.getStatusCode());
            }

        } catch (Exception ex) {
            // Tratar falhas de comunicação com a API
            throw new RuntimeException("Falha ao se comunicar com a API do SendGrid", ex);
        }
    }
}

