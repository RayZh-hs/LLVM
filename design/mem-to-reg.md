# Mem to Reg Pass

The Mem2Reg pass applies utilities the SSA property of LLVM IR to promote memory-resident variables to register-resident variables. In practice, this means that for each named `alloca` instruction, we would traverse its use chain. If the `alloca`-ed pointer is only used:

1. As a destination in `store`;
2. As a source in `load`;

Then we deem this SSA Value as promotable. We would then replace all `load` instructions with the value being loaded, and remove all `store` instructions. Instead, `phi` nodes would be inserted where `load` sources span multiple basic blocks.