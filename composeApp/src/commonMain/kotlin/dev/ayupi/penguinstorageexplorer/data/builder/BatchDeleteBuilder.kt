package dev.ayupi.penguinstorageexplorer.data.builder

data class BatchDeleteBuilder(val url: String, private val ids: MutableSet<Int> = mutableSetOf()) {
    fun setIds(ids: Set<Int>): BatchDeleteBuilder {
        this.ids.addAll(ids)
        return this
    }

    fun build(): String {
        return "$url?ids=${ids.joinToString(",")}"
    }
}
