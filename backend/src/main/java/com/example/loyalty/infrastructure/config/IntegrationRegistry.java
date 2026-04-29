package com.example.loyalty.infrastructure.config;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IntegrationRegistry {
    private final Map<String, String> integrations;
    public IntegrationRegistry(ApplicationContext context) {
        this.integrations = context.getBeansWithAnnotation(ExternalIntegration.class).values().stream()
            .collect(Collectors.toMap(this::integrationName, bean -> ClassUtils.getUserClass(bean).getSimpleName()));
    }
    public Map<String, String> registeredIntegrations() { return integrations; }

    private String integrationName(Object bean) {
        ExternalIntegration annotation = AnnotationUtils.findAnnotation(ClassUtils.getUserClass(bean), ExternalIntegration.class);
        return annotation == null ? bean.getClass().getSimpleName() : annotation.value();
    }
}
