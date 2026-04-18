package io.custos.node.application.port.in;

import io.custos.node.application.port.in.command.RequestShareCommand;
import io.custos.node.domain.model.ShareDelivery;

public interface RequestShareUseCase {
    ShareDelivery requestShare(RequestShareCommand command);
}
