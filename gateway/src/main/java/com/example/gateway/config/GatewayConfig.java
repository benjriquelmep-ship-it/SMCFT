package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // 1. Auth Service (Puerto: 2022)
                .route("auth_service_route", r -> r.path("/api/v1/auth/**")
                        .uri("lb://AUTHSERVICE"))

                // 2. User Service (Puerto: 2031)
                .route("user_service_route", r -> r.path("/api/v1/users/**")
                        .uri("lb://USERSERVICE"))

                // 3. Vehicle Service (Puerto: 2032)
                .route("vehicle_service_route", r -> r.path("/api/v1/vehicles/**")
                        .uri("lb://VEHICLESERVICE"))

                // 4. Border Crossing Service (Puerto: 2023)
                .route("border_crossing_route", r -> r.path("/api/v1/border-crossings/**")
                        .uri("lb://BORDERCROSSINGSERVICE"))

                // 5. Entry Service (Puerto: 2025)
                .route("entry_service_route", r -> r.path("/api/v1/entries/**")
                        .uri("lb://ENTRYSERVICE"))

                // 6. Sanitary Service (Puerto: 2029)
                .route("sanitary_service_route", r -> r.path("/api/v1/sanitary/**")
                        .uri("lb://SANITARYSERVICE"))

                // 7. Deadline Service (Puerto: 2024)
                .route("deadline_service_route", r -> r.path("/api/v1/deadlines/**", "/api/v1/deadline-alerts/**")
                        .uri("lb://DEADLINESERVICE"))

                // 8. Item Category Service (Puerto: 2026)
                .route("item_category_route", r -> r.path("/api/v1/item-categories/**", "/api/v1/items/**")
                        .uri("lb://ITEMCATEGORYSERVICE"))

                // 9. Transaction Service (Puerto: 2040)
                .route("transaction_service_route", r -> r.path("/api/v1/transactions/**", "/api/v1/transaction-details/**")
                        .uri("lb://TRANSACTIONSERVICE"))

                // 10. Report Service (Puerto: 2028)
                .route("report_service_route", r -> r.path("/api/v1/reports/**", "/api/v1/report-details/**")
                        .uri("lb://REPORTSERVICE"))

                // 11. Notification Service (Puerto: 2027)
                .route("notification_service_route", r -> r.path("/api/v1/notifications/**", "/api/v1/notification-recipients/**")
                        .uri("lb://NOTIFICATIONSERVICE"))

                // 12. Audit Service (Puerto: 2021)
                .route("audit_service_route", r -> r.path("/api/v1/audits/**", "/api/v1/audit-details/**")
                        .uri("lb://AUDITSERVICE"))

                .build();
    }
}