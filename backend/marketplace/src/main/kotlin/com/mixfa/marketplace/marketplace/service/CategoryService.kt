package com.mixfa.marketplace.marketplace.service

import com.mixfa.marketplace.marketplace.model.Category
import com.mixfa.marketplace.marketplace.repository.CategoryRepository
import com.mixfa.shared.NotFoundException
import com.mixfa.shared.categoryNotFound
import com.mixfa.shared.model.CheckedPageable
import com.mixfa.shared.model.MarketplaceEvent
import com.mixfa.shared.orThrow
import jakarta.validation.Valid
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated
import java.util.*

@Service
@Validated
class CategoryService(
    private val categoryRepo: CategoryRepository,
    private val eventPublisher: ApplicationEventPublisher
) {
    fun findCategoryById(id: String): Optional<Category> = categoryRepo.findById(id)

    fun findCategoriesByIdOrThrow(ids: Collection<String>): List<Category> {
        if (ids.isEmpty()) return emptyList()
        val categories = categoryRepo.findAllById(ids)
        if (categories.size != ids.size) throw NotFoundException.categoryNotFound()
        return categories
    }

    @Transactional
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    open fun registerCategory(@Valid request: Category.RegisterRequest): Category {
        val parentCategory = request.parentCategory?.let { id -> categoryRepo.findById(id).orThrow() }

        return categoryRepo.save(
            Category(
                name = request.name,
                subcategoriesIds = emptySet(),
                parentCategoryId = parentCategory?.id,
                requiredProps = if (parentCategory != null) request.requiredProps + parentCategory.requiredProps else request.requiredProps,
            )
        ).also { newCategory ->
            if (parentCategory != null)
                categoryRepo.save(parentCategory.copy(subcategoriesIds = parentCategory.subcategoriesIds + newCategory.id.toString()))

            eventPublisher.publishEvent(Event.CategoryRegister(newCategory, this))
        }
    }

    fun findCategories(query: String, pageable: CheckedPageable): Page<Category> =
        categoryRepo.findAllByNameContainsIgnoreCase(query, pageable)

    fun listCategories(pageable: CheckedPageable): Page<Category> =
        categoryRepo.findAll(pageable)

    sealed class Event(src: Any) : MarketplaceEvent(src) {
        class CategoryRegister(val category: Category, src: Any) : Event(src)
    }
}


