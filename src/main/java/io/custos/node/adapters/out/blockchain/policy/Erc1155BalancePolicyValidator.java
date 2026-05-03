package io.custos.node.adapters.out.blockchain.policy;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.custos.node.core.application.service.ChainRpcResolver;
import io.custos.node.core.domain.Erc1155BalancePolicyData;
import io.custos.node.core.domain.PolicyValidationResult;
import io.custos.node.core.application.port.out.AccessPolicyValidator;
import io.custos.node.core.domain.model.AccessPolicy;
import io.custos.node.core.domain.model.PolicyType;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;
import java.util.List;

@Service
public class Erc1155BalancePolicyValidator implements AccessPolicyValidator {

    private final ObjectMapper objectMapper;
    private final ChainRpcResolver chainRpcResolver;

    public Erc1155BalancePolicyValidator(
            ObjectMapper objectMapper,
            ChainRpcResolver chainRpcResolver
    ) {
        this.objectMapper = objectMapper;
        this.chainRpcResolver = chainRpcResolver;
    }

    @Override
    public PolicyType supportedType() {
        return PolicyType.EVM_ERC1155_BALANCE;
    }

    @Override
    public PolicyValidationResult validate(AccessPolicy policy, String walletAddress) {
        if (policy == null || policy.type() != PolicyType.EVM_ERC1155_BALANCE) {
            return PolicyValidationResult.invalid("UNSUPPORTED_POLICY_TYPE");
        }

        if (!WalletUtils.isValidAddress(walletAddress)) {
            return PolicyValidationResult.invalid("INVALID_WALLET");
        }

        if (!WalletUtils.isValidAddress(policy.validatorContract())) {
            return PolicyValidationResult.invalid("INVALID_VALIDATOR_CONTRACT");
        }

        var rpcUrl = chainRpcResolver.resolveRpcUrl(policy.chainId());

        if (rpcUrl.isEmpty()) {
            return PolicyValidationResult.invalid("CHAIN_NOT_CONFIGURED");
        }

        Erc1155BalancePolicyData policyData;

        try {
            policyData = objectMapper.readValue(policy.policyData(), Erc1155BalancePolicyData.class);
        } catch (Exception e) {
            return PolicyValidationResult.invalid("INVALID_POLICY_DATA");
        }

        BigInteger tokenId;
        BigInteger minBalance;

        try {
            tokenId = new BigInteger(policyData.tokenId());
            minBalance = new BigInteger(policyData.minBalance());
        } catch (Exception e) {
            return PolicyValidationResult.invalid("INVALID_POLICY_DATA");
        }

        if (tokenId.signum() < 0 || minBalance.signum() <= 0) {
            return PolicyValidationResult.invalid("INVALID_POLICY_DATA");
        }

        try {
            BigInteger balance = callErc1155BalanceOf(
                    rpcUrl.get(),
                    policy.validatorContract(),
                    walletAddress,
                    tokenId
            );

            if (balance.compareTo(minBalance) >= 0) {
                return PolicyValidationResult.valid();
            }

            return PolicyValidationResult.invalid("INSUFFICIENT_BALANCE");

        } catch (Exception e) {
            return PolicyValidationResult.invalid("ON_CHAIN_CALL_FAILED");
        }
    }

    private BigInteger callErc1155BalanceOf(
            String rpcUrl,
            String contractAddress,
            String walletAddress,
            BigInteger tokenId
    ) throws Exception {

        Web3j web3j = Web3j.build(new HttpService(rpcUrl));

        Function function = new Function(
                "balanceOf",
                List.of(
                        new Address(walletAddress),
                        new Uint256(tokenId)
                ),
                List.of(new TypeReference<Uint256>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);

        Transaction transaction = Transaction.createEthCallTransaction(
                walletAddress,
                contractAddress,
                encodedFunction
        );

        var response = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();

        if (response.hasError()) {
            throw new IllegalStateException(response.getError().getMessage());
        }

        String value = response.getValue();

        if (value == null || value.equals("0x")) {
            throw new IllegalStateException("Empty response from contract");
        }

        var decoded = FunctionReturnDecoder.decode(value, function.getOutputParameters());

        if (decoded.isEmpty()) {
            throw new IllegalStateException("Unable to decode balanceOf response");
        }

        return (BigInteger) decoded.getFirst().getValue();
    }
}