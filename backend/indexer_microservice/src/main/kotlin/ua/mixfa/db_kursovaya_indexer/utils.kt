package ua.mixfa.db_kursovaya_indexer

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import kotlin.reflect.KProperty

fun fieldName(field: KProperty<*>): String = field.name

inline fun <reified T> MongoTemplate.findPageable(q: Query, pageable: Pageable, collection: String): Page<T> {
    val total = this.count(q, collection)
    val elements = this.find(q.with(pageable), T::class.java, collection)

    return PageImpl(elements, pageable, total)
}
