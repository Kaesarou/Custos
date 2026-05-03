import { ethers } from "hardhat";
import fs from "fs";

const ERC721_ABI = ["function ownerOf(uint256 tokenId) view returns (address)"];
const ERC1155_ABI = ["function balanceOf(address account, uint256 id) view returns (uint256)"];
const ERC20_ABI = ["function balanceOf(address account) view returns (uint256)"];

async function main() {
  const raw = fs.readFileSync("deployments/deployments.json", "utf8");
  const deployments = JSON.parse(raw);

  const [signer] = await ethers.getSigners();

  const erc1155 = new ethers.Contract(deployments.contracts.testERC1155, ERC1155_ABI, signer);
  const erc721 = new ethers.Contract(deployments.contracts.testERC721, ERC721_ABI, signer);
  const erc20 = new ethers.Contract(deployments.contracts.testERC20, ERC20_ABI, signer);

  const alice = deployments.accounts.alice;
  const bob = deployments.accounts.bob;

  const alice1155Token1 = await erc1155.balanceOf(alice, 1);
  const bob1155Token1 = await erc1155.balanceOf(bob, 1);
  const owner721Token1 = await erc721.ownerOf(1);
  const alice20 = await erc20.balanceOf(alice);

  console.log("ERC1155 alice tokenId=1 >= 1:", alice1155Token1 >= 1n);
  console.log("ERC1155 bob tokenId=1 >= 10:", bob1155Token1 >= 10n);
  console.log("ERC721 tokenId=1 owner is alice:", owner721Token1.toLowerCase() === alice.toLowerCase());
  console.log("ERC20 alice balance >= 100 tokens:", alice20 >= ethers.parseEther("100"));
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
