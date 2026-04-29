package com.example.loyalty.infrastructure.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalIntegration { String value(); }
