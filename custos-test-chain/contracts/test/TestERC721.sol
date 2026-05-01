// SPDX-License-Identifier: MIT
pragma solidity ^0.8.28;

import "@openzeppelin/contracts/token/ERC721/ERC721.sol";

contract TestERC721 is ERC721 {
    uint256 private _nextTokenId;

    constructor() ERC721("Custos Test ERC721", "CT721") {}

    function mint(address to) external returns (uint256) {
        _nextTokenId += 1;
        uint256 tokenId = _nextTokenId;
        _mint(to, tokenId);
        return tokenId;
    }
}
