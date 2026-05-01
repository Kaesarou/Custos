// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract TestERC20 is ERC20 {
    constructor() ERC20("Custos Test ERC20", "CT20") {}

    function mint(address to, uint256 amount) external {
        _mint(to, amount);
    }
}
