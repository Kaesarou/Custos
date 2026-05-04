package io.custos.node.core.application.port.in;

import io.custos.node.core.application.port.in.command.RetrieveSecretShareCommand;
import io.custos.node.core.domain.model.SecretShareDelivery;

public interface RetrieveSecretShareUseCase {
    SecretShareDelivery retrieve(RetrieveSecretShareCommand command);
}
