package io.custos.node.core.application.port.in;

import io.custos.node.core.application.port.in.command.RequestShareCommand;
import io.custos.node.core.domain.model.ShareDelivery;

public interface RequestShareService {
    ShareDelivery requestShare(RequestShareCommand command);
}
