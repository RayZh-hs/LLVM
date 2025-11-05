package space.norb.llvm.structure

import space.norb.llvm.values.globals.GlobalVariable
import space.norb.llvm.values.Metadata

/**
 * LLVM module containing functions, global variables, and metadata.
 */
class Module(val name: String) {
    val functions: MutableList<Function> = mutableListOf()
    val globalVariables: MutableList<GlobalVariable> = mutableListOf()
    val namedMetadata: MutableMap<String, Metadata> = mutableMapOf()
    var targetTriple: String? = null
    var dataLayout: String? = null
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Module) return false
        return name == other.name
    }
    
    override fun hashCode(): Int {
        return name.hashCode()
    }
    
    override fun toString(): String {
        return "Module(name=$name)"
    }
}