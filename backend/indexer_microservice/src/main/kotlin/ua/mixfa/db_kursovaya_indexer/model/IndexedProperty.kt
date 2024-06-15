package ua.mixfa.db_kursovaya_indexer.model

import org.springframework.data.annotation.Id

data class IndexedProperty(
    @Id val prop: String,
    val values: Map<String, Long>
) {
    data class Dto(
        val prop: String,
        val values: Collection<String>
    ) {
        constructor(indexedProperty: IndexedProperty) :
                this(indexedProperty.prop, indexedProperty.values.keys)
    }
}