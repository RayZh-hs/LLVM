package space.norb.llvm.structure

import space.norb.llvm.core.Type
import space.norb.llvm.core.Value
import space.norb.llvm.types.FunctionType
import space.norb.llvm.visitors.IRVisitor
import space.norb.llvm.enums.LinkageType

/**
 * Function
 *
 * Represents a function in LLVM IR. Each function has a name, type, and is associated with a parent module.
 *
 * @param name The name of the function, which serves as its identifier within the module.
 * @param type The function type, which includes the return type and parameter types.
 * @param module The parent module to which this function belongs.
 * @param linkage The linkage type of the function, which determines its visibility and linkage behavior.
 * @param isDeclaration Indicates whether this function is a declaration (i.e., it has no body) or a definition (i.e., it has a body with basic blocks).
 */
class Function(
    override val name: String,
    override val type: FunctionType,
    val module: Module,
    val linkage: LinkageType = LinkageType.EXTERNAL,
    val isDeclaration: Boolean = false
) : Value {
    val returnType: Type = type.returnType
    val parameters: List<Argument> = type.paramTypes.mapIndexed { index, paramType ->
        Argument(type.getParameterName(index), paramType, this, index)
    }
    val basicBlocks: MutableList<BasicBlock> = mutableListOf()
    var entryBlock: BasicBlock? = null
    
    override fun <T> accept(visitor: IRVisitor<T>): T = visitor.visitFunction(this)
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Function) return false
        return name == other.name &&
            type == other.type &&
            module == other.module &&
            linkage == other.linkage &&
            isDeclaration == other.isDeclaration
    }
    
    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + module.hashCode()
        result = 31 * result + linkage.hashCode()
        result = 31 * result + isDeclaration.hashCode()
        return result
    }
    
    override fun toString(): String {
        return "Function(name=$name, type=$type, module=${module.name})"
    }
    
    override fun getParent(): Any? {
        // Functions belong to modules
        return module
    }

    fun setBasicBlock(block: BasicBlock): Function {
        this.entryBlock = block
        this.basicBlocks.clear()
        this.basicBlocks.add(block)
        return this
    }

    fun setBasicBlocks(blocks: List<BasicBlock>): Function {
        if (blocks.isNotEmpty()) {
            this.entryBlock = blocks[0]
        } else {
            throw IllegalArgumentException("Basic blocks list cannot be empty")
        }
        this.basicBlocks.clear()
        this.basicBlocks.addAll(blocks)
        return this
    }
    
    /**
     * Insert a new basic block into this function.
     *
     * @param name The name of the basic block
     * @param setAsEntrypoint Whether to set this block as the entry point.
     *                        If true, this block becomes the entryBlock.
     *                        If this is the first block being added, it defaults to being the entrypoint.
     * @return The created BasicBlock
     */
    fun insertBasicBlock(name: String, setAsEntrypoint: Boolean = false): BasicBlock {
        val block = BasicBlock(name, this)
        
        // Check if this is the first block being added
        val isFirstBlock = basicBlocks.isEmpty()
        
        // Add the block to the function's basic blocks list
        basicBlocks.add(block)
        
        // Set as entrypoint if requested or if this is the first block
        if (setAsEntrypoint || isFirstBlock) {
            entryBlock = block
        }
        
        return block
    }
}
