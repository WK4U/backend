package com.workforyou.backend.service;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private SendGrid sendGrid;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testaEnviarEmail() throws Exception{

        Response responseMock = new Response();
        responseMock.setStatusCode(202);
        when(sendGrid.api(any(Request.class))).thenReturn(responseMock);

        assertDoesNotThrow(() ->
            emailService.send("pessoa@gmail.com", "Assunto Teste", "Corpo do e-mail")
        );

        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    void testaEnviarEmailNegatico() throws Exception {
        when(sendGrid.api(any(Request.class)))
                .thenThrow(new RuntimeException("Erro de conexão com o SendGrid"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.send("pessoa@gmail.com", "Assunto TesteFalha", "Corpo do e-mail");
        });

        System.out.println("Mensagem exceção: " + exception.getMessage());

        assertNotNull(exception.getMessage());
        assertTrue(
                exception.getMessage().contains("Falha ao se comunicar com a API do SendGrid")
                || exception.getMessage().contains("Erro de conexão com o SendGrid")
        );
    }


}
