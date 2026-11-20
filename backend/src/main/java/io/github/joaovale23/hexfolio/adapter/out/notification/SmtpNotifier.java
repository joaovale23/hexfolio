package io.github.joaovale23.hexfolio.adapter.out.notification;

import io.github.joaovale23.hexfolio.application.port.out.Notifier;
import io.github.joaovale23.hexfolio.domain.architecture.OutboundAdapter;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Envia o aviso por e-mail. O "responder" vai direto para quem escreveu. */
@Component
@OutboundAdapter("Envia o aviso de novo contato por e-mail (SMTP)")
class SmtpNotifier implements Notifier {

    private final JavaMailSender mail;
    private final String to;
    private final String from;

    SmtpNotifier(JavaMailSender mail,
                 @Value("${hexfolio.contact.notify-to}") String to,
                 @Value("${hexfolio.contact.from}") String from) {
        this.mail = mail;
        this.to = to;
        this.from = from;
    }

    @Override
    public void notify(ContactMessage message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setFrom(from);
        email.setReplyTo(message.email());
        email.setSubject("[hexfolio] Nova mensagem de " + message.name());
        email.setText("""
                %s <%s> escreveu pelo portfólio:

                %s

                ---
                id: %s
                recebida em: %s
                """.formatted(message.name(), message.email(), message.message(), message.id(), message.receivedAt()));
        mail.send(email);
    }
}
