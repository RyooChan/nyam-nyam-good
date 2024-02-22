package com.example.nyamnyamgood.config;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Utils {
    public static <T> T getBean(Class<?> classType) {
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        return (T) applicationContext.getBean(classType);
    }
}