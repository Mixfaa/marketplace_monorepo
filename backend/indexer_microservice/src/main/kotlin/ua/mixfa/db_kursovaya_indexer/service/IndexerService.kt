package ua.mixfa.db_kursovaya_indexer.service


import org.bson.types.ObjectId
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import ua.mixfa.db_kursovaya_indexer.fieldName
import ua.mixfa.db_kursovaya_indexer.findPageable
import ua.mixfa.db_kursovaya_indexer.model.*

@Service
class IndexerService(
    private val mongoTemplate: MongoTemplate,
) {
    @RabbitListener(queues = ["\${rabbitmq.product-created-queue}"])
    fun listenProductCreated(event: ProductCreatedEvent) {
        onProductCreated(event)
    }

    @RabbitListener(queues = ["\${rabbitmq.category-created-queue}"])
    fun listenCategoryCreated(event: CategoryCreatedEvent) {
        onCategoryCreated(event)
    }

    fun findValues(categoryId: ObjectId, pageable: Pageable): Page<IndexedProperty> {
        val indexCollectionRef = mongoTemplate.findOne(
            Query(Criteria.where(fieldName(IndexCollectionRef::categoriesIds)).`in`(categoryId)),
            IndexCollectionRef::class.java,
            INDEX_COLLECTION_MONGO_COLLECTION
        ) ?: return Page.empty()

        return mongoTemplate.findPageable<IndexedProperty>(
            Query(),
            pageable,
            indexCollectionRef.collectionName
        )
    }

    fun findValues(categoryId: ObjectId, prop: String): Collection<String> {
        val indexCollectionRef = mongoTemplate.findOne(
            Query(Criteria.where(fieldName(IndexCollectionRef::categoriesIds)).`in`(categoryId)),
            IndexCollectionRef::class.java,
            INDEX_COLLECTION_MONGO_COLLECTION
        ) ?: return emptyList()

        return mongoTemplate.findOne(
            Query(Criteria.where("_id").`is`(prop)),
            IndexedProperty::class.java,
            indexCollectionRef.collectionName
        )?.values?.keys ?: emptyList()
    }

    fun onProductCreated(event: ProductCreatedEvent) {
        val indexCollectionRef = mongoTemplate.findOne(
            Query(
                Criteria.where(fieldName(IndexCollectionRef::categoriesIds))
                    .all(event.allRelatedCategoriesIds.map(::ObjectId))
            ),
            IndexCollectionRef::class.java,
            INDEX_COLLECTION_MONGO_COLLECTION,
        ) ?: throw Exception("Can`t find collection for product ${event.id}")

        for ((prop, value) in event.characteristics) {
            mongoTemplate.updateFirst(
                Query(Criteria.where("_id").`is`(prop)),
                Update().inc("${fieldName(IndexedProperty::values)}.$value", 1),
                indexCollectionRef.collectionName
            )
        }
    }

    fun onCategoryCreated(event: CategoryCreatedEvent) {
        if (event.parentCategoryId == null) {
            val indexCollectionRef = IndexCollectionRef(listOf(event.id))
            mongoTemplate.insert(indexCollectionRef, INDEX_COLLECTION_MONGO_COLLECTION)

            for (requiredProp in event.requiredProps)
                mongoTemplate.insert(IndexedProperty(requiredProp, emptyMap()), indexCollectionRef.collectionName)
        } else { // expand cluster
            mongoTemplate.updateMulti(
                Query(
                    Criteria.where(fieldName(IndexCollectionRef::categoriesIds))
                        .`in`(event.parentCategoryId)
                ),
                Update().addToSet(fieldName(IndexCollectionRef::categoriesIds), event.id),
                INDEX_COLLECTION_MONGO_COLLECTION
            )
        }
    }
}