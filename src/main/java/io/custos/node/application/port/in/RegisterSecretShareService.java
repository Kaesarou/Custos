package io.custos.node.application.port.in;

import io.custos.node.application.port.in.command.RegisterSecretShareCommand;

public interface RegisterSecretShareService {
    void register(RegisterSecretShareCommand command);
}
