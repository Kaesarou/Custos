package io.custos.node.application.port.in;

import io.custos.node.application.port.in.command.RegisterSecretShareCommand;

public interface RegisterSecretShareUseCase {
    void register(RegisterSecretShareCommand command);
}
