package space.norb.llvm.utils

class Renamer {
    companion object {
        private val nameCountMap = mutableMapOf<String?, Int>()

        fun another(baseName: String? = null): String {
            if (baseName !in nameCountMap) {
                nameCountMap[baseName] = 0
            } else {
                nameCountMap[baseName] = nameCountMap[baseName]!! + 1
            }
            val serial = nameCountMap[baseName]!!
            return if (baseName == null)
                serial.toString()
            else
                "${baseName}.$serial"
        }

        fun current(baseName: String? = null): String {
            val serial = nameCountMap[baseName] ?: error("No name registered for base name: $baseName")
            return if (baseName == null)
                serial.toString()
            else
                "$baseName.$serial"
        }
    }
}