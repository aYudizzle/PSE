package dev.ayupi.penguinstorageexplorer.data.builder

import dev.ayupi.penguinstorageexplorer.domain.model.DateOfExpiryState
import dev.ayupi.penguinstorageexplorer.domain.model.PSESortOrder

data class ApiOptionsBuilder(val url: String, private val options: MutableMap<String, String> = mutableMapOf()) {

    fun sort(sort: PSESortOrder): ApiOptionsBuilder {
        options["sort"] = sort.name.lowercase()
        return this
    }

    fun locationId(locationId: Int?): ApiOptionsBuilder {
        if (locationId != null) {
            options["locationId"] = locationId.toString()
        }
        return this
    }

    fun filterExpiry(filterExpiry: DateOfExpiryState?): ApiOptionsBuilder {
        if (filterExpiry != null) {
            options["filterExpiry"] = filterExpiry.name.lowercase()
        }
        return this
    }

    fun query(query: String?): ApiOptionsBuilder {
        if (query != null) {
            options["query"] = query
        }
        return this
    }

    fun build(): String {
        val optionList = options.map { "${it.key}=${it.value}" }
        return "$url?${optionList.joinToString("&")}"
    }
}
