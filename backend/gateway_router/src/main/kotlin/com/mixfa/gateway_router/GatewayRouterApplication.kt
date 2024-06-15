package com.mixfa.gateway_router

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean

@ConfigurationProperties("gateway")
data class MarketplaceRoutesConfig(
    val routes: List<RouteConfig>
) {
    data class RouteConfig(
        val path: String,
        val uri: String
    ) {
        fun apply(builder: RouteLocatorBuilder.Builder): RouteLocatorBuilder.Builder = builder.route {
            it.path(path)
            it.uri(uri)
        }
    }
}

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan(
    "com.mixfa.gateway_router",
)
class GatewayRouterApplication(
    private val config: MarketplaceRoutesConfig
) {

    @Bean
    fun router(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .let {
                var routeBuilder = it

                for (routeConfig in config.routes)
                    routeBuilder = routeConfig.apply(routeBuilder)

                routeBuilder
            }
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<GatewayRouterApplication>(*args)
}
