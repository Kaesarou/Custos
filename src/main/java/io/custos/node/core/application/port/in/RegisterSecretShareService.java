package io.custos.node.core.application.port.in;

import io.custos.node.core.application.port.in.command.RegisterSecretShareCommand;

public interface RegisterSecretShareService {
    void register(RegisterSecretShareCommand command);
}
