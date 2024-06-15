package ua.mixfa.db_kursovaya_indexer.controller

import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ua.mixfa.db_kursovaya_indexer.CheckedPageable
import ua.mixfa.db_kursovaya_indexer.service.IndexerService

@RestController
@RequestMapping("/v2/marketplace/indexer")
class IndexerController(
    private val indexerService: IndexerService
) {
    @GetMapping("/{categoryId}/{prop}")
    fun findIndexes(@PathVariable categoryId: String, @PathVariable prop: String): Collection<String> {
        return indexerService.findValues(ObjectId(categoryId), prop)
    }

    @GetMapping("/{categoryId}")
    fun findIndexes(@PathVariable categoryId: String, page: Int, pageSize: Int) = indexerService.findValues(
        ObjectId(categoryId),
        CheckedPageable(page, pageSize)
    )
}