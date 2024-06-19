package ua.mixfa.db_kursovaya_indexer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer



@SpringBootApplication
class DbKursovayaIndexerApplication(
    @Value("\${rabbitmq.product-created-queue}") private val productCreatedQueue: String,
    @Value("\${rabbitmq.category-created-queue}") private val categoryCreatedQueue: String
) {
    @Bean
    fun productCreatedQueue(): Queue = Queue(productCreatedQueue)

    @Bean
    fun categoryCreatedQueue(): Queue = Queue(categoryCreatedQueue)

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
