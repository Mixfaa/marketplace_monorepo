package ua.mixfa.db_kursovaya_indexer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


const val PRODUCT_CREATED_QUEUE = "indexer-product-created"
const val CATEGORY_CREATED_QUEUE = "indexer-category-created"

@SpringBootApplication
class DbKursovayaIndexerApplication {

    @Bean
    fun queue1(): Queue = Queue(PRODUCT_CREATED_QUEUE)

    @Bean
    fun queue2(): Queue = Queue(CATEGORY_CREATED_QUEUE)

    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().registerKotlinModule()

    @Bean
    fun messageConverter(mapper: ObjectMapper): MessageConverter = Jackson2JsonMessageConverter(mapper)

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins("*")
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<DbKursovayaIndexerApplication>(*args)
}
