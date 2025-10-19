package com.documentor.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Utility class for working with Spring beans.
 */
public final class BeanUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtils.class);

    private BeanUtils() {
        // Utility class, do not instantiate
    }

    /**
     * Override a singleton bean in the Spring context.
     * This allows configuration values to be updated at runtime.
     *
     * @param applicationContext the Spring application context
     * @param beanName the name of the bean to override
     * @param newBean the new bean instance to use
     * @param <T> the bean type
     */
    public static <T> void overrideBean(
            final ApplicationContext applicationContext,
            final String beanName,
            final T newBean) {

        Objects.requireNonNull(applicationContext, "Application context cannot be null");
        Objects.requireNonNull(beanName, "Bean name cannot be null");
        Objects.requireNonNull(newBean, "New bean instance cannot be null");

        LOGGER.info("Attempting to override bean: {}", beanName);

        try {
            if (applicationContext instanceof ConfigurableApplicationContext configContext) {
                ConfigurableListableBeanFactory beanFactory = configContext.getBeanFactory();

                // Log bean factory info
                LOGGER.info("Bean factory: {}", beanFactory.getClass().getName());

                // Check if bean exists
                if (!beanFactory.containsBean(beanName)) {
                    LOGGER.error("Bean with name '{}' not found", beanName);

                    // Log all available beans
                    String[] allBeans = beanFactory.getBeanDefinitionNames();
                    LOGGER.info("Available beans ({}): {}", allBeans.length, String.join(", ", allBeans));
                    return;
                }

                // Check if bean is singleton
                if (!beanFactory.isSingleton(beanName)) {
                    LOGGER.warn("Bean '{}' is not a singleton. Will not override.", beanName);
                    return;
                }

                // Get bean definition for logging
                BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
                LOGGER.info("Bean class: {}", beanDef.getBeanClassName());

                // Get original bean for comparison
                Object originalBean = beanFactory.getBean(beanName);
                LOGGER.info("Original bean class: {}", originalBean.getClass().getName());
                LOGGER.info("New bean class: {}", newBean.getClass().getName());

                // Replace singleton instance
                LOGGER.info("Overriding bean '{}' with new instance", beanName);

                // Use reflection to access protected methods if necessary
                if (beanFactory instanceof DefaultSingletonBeanRegistry registry) {
                    LOGGER.info("Using DefaultSingletonBeanRegistry direct method");
                    registry.destroySingleton(beanName);
                    registry.registerSingleton(beanName, newBean);
                } else {
                    // Try to use reflection as fallback
                    LOGGER.info("Using reflection fallback to override bean");
                    destroyAndRegisterSingletonViaReflection(beanFactory, beanName, newBean);
                }

                // Update any dependent beans
                LOGGER.info("Updating dependent beans that reference '{}'", beanName);
                updateDependentBeans(configContext, beanName, newBean);

                // Verify the bean was actually replaced
                Object updatedBean = beanFactory.getBean(beanName);
                if (updatedBean == newBean) {
                    LOGGER.info("Successfully replaced bean '{}' (identity verified)", beanName);
                } else {
                    LOGGER.warn("Bean replacement verification failed - objects are not identical");
                }

                LOGGER.info("Bean override operation completed for '{}'", beanName);
            } else {
                LOGGER.error("ApplicationContext is not configurable, cannot override bean");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to override bean '{}': {}", beanName, e.getMessage(), e);
        }
    }

    /**
     * Attempt to destroy and register a singleton using reflection.
     *
     * @param beanFactory the bean factory
     * @param beanName the name of the bean to override
     * @param newBean the new bean instance
     */
    private static void destroyAndRegisterSingletonViaReflection(
            final ConfigurableListableBeanFactory beanFactory,
            final String beanName,
            final Object newBean) throws Exception {

        // Try to get the singleton registry inside the bean factory
        Field registryField = null;
        for (Field field : beanFactory.getClass().getDeclaredFields()) {
            if (DefaultSingletonBeanRegistry.class.isAssignableFrom(field.getType())) {
                registryField = field;
                break;
            }
        }

        if (registryField != null) {
            registryField.setAccessible(true);
            DefaultSingletonBeanRegistry registry =
                    (DefaultSingletonBeanRegistry) registryField.get(beanFactory);
            registry.destroySingleton(beanName);
            registry.registerSingleton(beanName, newBean);
        } else {
            throw new IllegalStateException(
                    "Could not find singleton registry in bean factory: "
                    + beanFactory.getClass().getName());
        }
    }

    /**
     * Update any beans that depend on the overridden bean.
     *
     * @param context the application context
     * @param beanName the name of the bean that was overridden
     * @param newBean the new bean instance
     */
    private static void updateDependentBeans(
            final ConfigurableApplicationContext context,
            final String beanName,
            final Object newBean) {

        // Get all bean names
        String[] allBeanNames = context.getBeanDefinitionNames();
        LOGGER.info("Checking {} beans for dependencies on '{}'", allBeanNames.length, beanName);

        // Special handling for key service beans
        if ("documentorConfig".equals(beanName)) {
            try {
                updateDocumentorConfigDependents(context, newBean);
            } catch (Exception e) {
                LOGGER.error("Error updating key services with new config: {}", e.getMessage(), e);
            }
        }

        int updatedCount = 0;

        for (String name : allBeanNames) {
            try {
                // Skip the bean itself and Spring internal beans
                if (name.equals(beanName) || name.startsWith("org.springframework")) {
                    continue;
                }

                Object bean = context.getBean(name);
                boolean updated = updateBeanFields(bean, beanName, newBean);
                if (updated) {
                    updatedCount++;
                    LOGGER.info("Updated dependent bean: {}", name);
                }
            } catch (Exception e) {
                // Log but continue with other beans
                LOGGER.debug("Could not update dependent bean '{}': {}", name, e.getMessage());
            }
        }

        LOGGER.info("Updated {} dependent beans", updatedCount);
    }

    /**
     * Special handling for updating services that depend on DocumentorConfig.
     * This directly targets known services that need the updated config.
     *
     * @param context the application context
     * @param newConfig the new DocumentorConfig instance
     */
    private static void updateDocumentorConfigDependents(
            final ConfigurableApplicationContext context,
            final Object newConfig) {

        // Specifically update LlmService which we know uses DocumentorConfig
        if (context.containsBean("llmService")) {
            LOGGER.info("Found llmService bean - updating with new config");
            Object llmService = context.getBean("llmService");
            boolean updated = updateBeanFields(llmService, "config", newConfig);
            LOGGER.info("LlmService config update result: {}", updated ? "successful" : "failed");
        }

        // Update DocumentationService which also uses DocumentorConfig
        if (context.containsBean("documentationService")) {
            LOGGER.info("Found documentationService bean - updating with new config");
            Object docService = context.getBean("documentationService");
            boolean updated = updateBeanFields(docService, "config", newConfig);
            LOGGER.info("DocumentationService config update result: {}", updated ? "successful" : "failed");
        }
    }

    /**
     * Updates fields in a bean that match the overridden bean's type.
     *
     * @param bean the bean to update
     * @param overriddenBeanName the name of the bean that was overridden or field name
     * @param newValue the new value for fields of matching type
     * @return true if at least one field was updated, false otherwise
     */
    private static boolean updateBeanFields(
            final Object bean,
            final String overriddenBeanName,
            final Object newValue) {

        if (bean == null || newValue == null) {
            return false;
        }

        Class<?> targetClass = bean.getClass();
        Class<?> valueClass = newValue.getClass();
        boolean updated = false;

        LOGGER.debug("Updating fields in {} that match type {}",
                bean.getClass().getName(), valueClass.getName());

        // Check all declared fields
        while (targetClass != null) {
            for (Field field : targetClass.getDeclaredFields()) {
                try {
                    // Match by field name if provided, or by type
                    boolean nameMatch = field.getName().equals(overriddenBeanName);
                    boolean typeMatch = field.getType().isAssignableFrom(valueClass);

                    if (nameMatch || typeMatch) {
                        field.setAccessible(true);
                        Object currentValue = field.get(bean);

                        // Only update if field has a value (to avoid NPEs elsewhere)
                        if (currentValue != null) {
                            field.set(bean, newValue);
                            LOGGER.info("Updated field '{}' in bean of type {}",
                                    field.getName(), bean.getClass().getName());
                            updated = true;
                        } else {
                            LOGGER.debug("Field '{}' has null value, not updating", field.getName());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.debug("Could not update field {} in bean {}: {}",
                            field.getName(), bean.getClass().getName(), e.getMessage());
                }
            }
            targetClass = targetClass.getSuperclass();
        }

        return updated;
    }
}
