package io.github.joaovale23.hexfolio.application.port.out;

import io.github.joaovale23.hexfolio.domain.architecture.OutboundPort;
import io.github.joaovale23.hexfolio.domain.model.ContactMessage;

@OutboundPort("Avisa o João que chegou uma mensagem")
public interface Notifier {

    void notify(ContactMessage message);
}
