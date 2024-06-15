package ua.mixfa.db_kursovaya_indexer.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

const val INDEX_COLLECTION_MONGO_COLLECTION = "index_collection_ref"

@Document(INDEX_COLLECTION_MONGO_COLLECTION)
data class IndexCollectionRef(
    val categoriesIds: List<ObjectId>,
    @Id val id: ObjectId = ObjectId(),
    val collectionName: String = "index_collection_${ObjectId()}",
)