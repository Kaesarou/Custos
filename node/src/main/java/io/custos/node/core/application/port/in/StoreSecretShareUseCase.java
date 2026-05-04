package io.custos.node.core.application.port.in;

import io.custos.node.core.application.port.in.command.StoreSecretShareCommand;

public interface StoreSecretShareUseCase {
    void store(StoreSecretShareCommand command);
}
