package space.norb.llvm.structure

import space.norb.llvm.values.globals.GlobalVariable

/**
 * LLVM module containing functions, global variables, and metadata.
 */
class Module(val name: String) {
    val functions: MutableList<Function> = mutableListOf()
    val globalVariables: MutableList<GlobalVariable> = mutableListOf()
    val namedMetadata: MutableMap<String, Metadata> = mutableMapOf()
    var targetTriple: String? = null
    var dataLayout: String? = null
}