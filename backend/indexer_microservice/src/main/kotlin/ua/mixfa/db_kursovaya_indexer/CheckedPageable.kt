package ua.mixfa.db_kursovaya_indexer

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

const val MAX_PAGE_SIZE = 15

fun Pageable.isNotInBound(): Boolean {
    return !(pageSize >= 0 && pageSize <= MAX_PAGE_SIZE)
}

private val largePageSizeException = Exception("Page size is too big, should be <= $MAX_PAGE_SIZE")

fun Pageable.throwIfNotInBound() {
    if (this.isNotInBound()) throw largePageSizeException
}


@JvmInline
value class CheckedPageable(
    private val pageable: Pageable
) : Pageable by pageable {
    init {
        pageable.throwIfNotInBound()
    }

    constructor(page: Int, pageSize: Int, sort: Sort) : this(PageRequest.of(page, pageSize, sort))
    constructor(page: Int, pageSize: Int) : this(PageRequest.of(page, pageSize))

    override fun next(): CheckedPageable = CheckedPageable(pageable.next())
    override fun previousOrFirst(): CheckedPageable = CheckedPageable(pageable.previousOrFirst())
    override fun first(): CheckedPageable = CheckedPageable(pageable.first())
    override fun withPage(pageNumber: Int): CheckedPageable = CheckedPageable(pageable.withPage(pageNumber))
}