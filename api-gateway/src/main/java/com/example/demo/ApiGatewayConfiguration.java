package com.example.demo;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

	@Autowired
	AuthenticationFilter authenticationFilter;

	@Bean
	public RouteLocator gateWayLocator(RouteLocatorBuilder builder) {
		Function<PredicateSpec, Buildable<Route>> masterEmployeeroute = p -> p.path("/api/v1/fields/insert-desig",
				"/api/v1/fields/insert-depart", "/api/v1/fields/insert-sub-depart/",
				"/api/v1/fields/insert-employee-type", "/api/v1/fields/insert-adsress-types",
				"/api/v1/fields/get-all-subDepart", "/api/v1/fields/get-all-Depart", "/api/v1/fields/get-all-addType",
				"/api/v1/fields/get-all-desg", "/api/v1/fields/get-all-empTypes", "/api/v1/fields/technologies",
				"/api/v1/emp/insert-emp-details", "/api/v1/emp/repotees", "/api/v1/emp/get-under-emps",
				"/api/v1/emp/get-emp-crosspnd-details", "/api/v1/emp/get-supvisor-dropdown", "/api/v1/emp/update-emp",
				"/api/v1/emp/download-photo", "/api/v1/emp/photo-upload", "/api/v1/emp/update-photo",
				"/api/v1/emp/photo-delete", "/api/v1/emp/update-emp-req", "/api/v1/emp/subordinate-manager",
				"/api/v1/hr/update-desg-hierarchy", "/api/v1/hr/Hr-dropDown",
				"/api/v1/hr/update-supervisor-id/{empId}/{newSupId}", "/api/v1/hr/card-detail",
				"/api/v1/hr/card-detail-by-desg", "/api/v1/hr/search","/api/v1/auth/change-password")
				.filters(f -> f.filter(authenticationFilter))
				// .circuitBreaker(c ->
				// c.setName("custom").setFallbackUri("/fallback/testService")))

				.uri("lb://MASTER-DETAILS-SERVICE");

		Function<PredicateSpec, Buildable<Route>> productRoute = p -> p.path("/api/admin/addProducts",
				"/api/admin/getonecategory", "/api/admin/addCategory", "/api/admin/updateProd",
				"/api/admin/getCategories", "/api/admin/getAllProducts", "/api/user/searchProduct")
//				.filters(f -> f.filter(authenticationFilter)
//						.circuitBreaker(c -> c.setName("custom").setFallbackUri("/fallback/testService")))
				.uri("lb://2-SERVICE");

		Function<PredicateSpec, Buildable<Route>> cartRoute = p -> p
				.path("/api/user/addToCart", "/api/user/getMyCartList", "/api/user/delete", "/api/user/updateMyCart",
						"/api/user/findByCartListByUserName/", "/api/user/deleteMyCartList", "/user/deletecart")
//				.filters(f -> f.filter(authenticationFilter)
//						.circuitBreaker(c -> c.setName("custom").setFallbackUri("/fallback/testService")))
				.uri("lb://3-SERVICE");

		Function<PredicateSpec, Buildable<Route>> inventory = p -> p
				.path("/api/admin/getAllInventory", "/api/admin/addInventory")
//				.filters(f -> f.filter(authenticationFilter)
//						.circuitBreaker(c -> c.setName("custom").setFallbackUri("/fallback/testService")))
				.uri("lb://4-SERVICE");

		Function<PredicateSpec, Buildable<Route>> ordersRoute = p -> p.path("/api/user/payWithCod",
				"/api/user/getMyOrders", "/api/user/generatePdf/**", "/api/user/cancelOrders")
//				.filters(f -> f.filter(authenticationFilter)
//						.circuitBreaker(c -> c.setName("custom").setFallbackUri("/fallback/testService")))
				.uri("lb://5-SERVICE");

		Function<PredicateSpec, Buildable<Route>> paymentRoute = p -> p.path("/user/findMyOrders")
//				.filters(f -> f.filter(authenticationFilter)
//						.circuitBreaker(c -> c.setName("custom").setFallbackUri("/fallback/testService")))
				.uri("lb://6-SERVICE");

		return builder.routes().route(masterEmployeeroute).route(productRoute).route(cartRoute).route(inventory)
				.route(ordersRoute).route(paymentRoute).build();

	}

//	
//	@Bean
//	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(
//			CircuitBreakerRegistry circuitBreakerRegistry, TimeLimiterRegistry timeLimiterRegistry) {
//		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
//				.circuitBreakerConfig(circuitBreakerRegistry.getConfiguration("backendB")
//						.orElse(circuitBreakerRegistry.getDefaultConfig()))
//				.timeLimiterConfig(timeLimiterRegistry.getConfiguration("backendB")
//						.orElse(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(30000)).build()))
//				.build());
//	}
//	

//    @Bean
//    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer()
//    {
//        return factory->factory.configureDefault(id ->new Resilience4JConfigBuilder(id)
//                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
//                .timeLimiterConfig(TimeLimiterConfig.custom()
//                        .timeoutDuration(Duration.ofSeconds(20)).build()).build());
//    }

}
