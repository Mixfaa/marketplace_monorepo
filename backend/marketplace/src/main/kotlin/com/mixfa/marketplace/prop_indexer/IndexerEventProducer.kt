package com.mixfa.marketplace.prop_indexer

import com.mixfa.marketplace.marketplace.model.Category
import com.mixfa.marketplace.marketplace.model.Product
import com.mixfa.marketplace.marketplace.service.CategoryService
import com.mixfa.marketplace.marketplace.service.ProductService
import com.mixfa.shared.model.MarketplaceEvent
import org.bson.types.ObjectId
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service

private data class CategoryCreatedEvent(
    val id: ObjectId,
    val parentCategoryId: ObjectId?,
    val requiredProps: Collection<String>
) {
    constructor(category: Category) : this(category.id, category.parentCategoryId, category.requiredProps)
}

private data class ProductCreatedEvent(
    val id: ObjectId,
    val characteristics: Map<String, String>,
    val allRelatedCategoriesIds: Collection<String>
) {
    constructor(product: Product) : this(product.id, product.characteristics, product.allRelatedCategoriesIds)
}

@Service
class IndexerEventProducer(
    private val amqpTemplate: AmqpTemplate,
    @Value("\${rabbitmq.product-created-queue}") private val productCreatedQueue: String,
    @Value("\${rabbitmq.category-created-queue}") private val categoryCreatedQueue: String
) : ApplicationListener<MarketplaceEvent> {
    override fun onApplicationEvent(event: MarketplaceEvent) {
        when (event) {
            is ProductService.Event.ProductRegister -> event.product.let { product ->
                amqpTemplate.convertAndSend(
                    productCreatedQueue,
                    ProductCreatedEvent(product)
                )
            }

            is CategoryService.Event.CategoryRegister -> event.category.let { category ->
                amqpTemplate.convertAndSend(
                    categoryCreatedQueue,
                    CategoryCreatedEvent(category)
                )
            }

            else -> {}
        }
    }
}