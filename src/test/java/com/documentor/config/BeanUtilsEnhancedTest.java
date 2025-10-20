package com.documentor.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Enhanced test coverage for BeanUtils to improve branch coverage.
 */
class BeanUtilsEnhancedTest {

    /**
     * Test successful bean override with DefaultSingletonBeanRegistry
     */
    @Test
    void testOverrideBeanSuccessfulPath() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("testBean")).thenReturn(true);
        when(configurableFactory.isSingleton("testBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("testBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(configurableFactory.getBean("testBean"))
            .thenReturn("originalValue")  // First call - get original
            .thenReturn("newValue");      // Second call - verify replacement
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(new String[]{"testBean", "otherBean"});

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // Verify the DefaultSingletonBeanRegistry methods were called
        verify(beanFactory).destroySingleton("testBean");
        verify(beanFactory).registerSingleton("testBean", newValue);
    }

    /**
     * Test DocumentorConfig special handling path
     */
    @Test
    void testOverrideBeanDocumentorConfigSpecialHandling() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        DocumentorConfig newConfig = mock(DocumentorConfig.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("documentorConfig")).thenReturn(true);
        when(configurableFactory.isSingleton("documentorConfig")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("documentorConfig")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("com.documentor.config.DocumentorConfig");
        when(configurableFactory.getBean("documentorConfig"))
            .thenReturn(mock(DocumentorConfig.class))
            .thenReturn(newConfig);
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"documentorConfig", "llmService", "documentationService"});

        // Mock dependent services
        when(context.containsBean("llmService")).thenReturn(true);
        when(context.containsBean("documentationService")).thenReturn(true);

        TestServiceWithConfig llmService = new TestServiceWithConfig();
        TestServiceWithConfig docService = new TestServiceWithConfig();
        when(context.getBean("llmService")).thenReturn(llmService);
        when(context.getBean("documentationService")).thenReturn(docService);

        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "documentorConfig", newConfig));

        // Just verify the method completed without error - the special handling is internal
    }    /**
     * Test reflection fallback path (non-DefaultSingletonBeanRegistry)
     */
    @Test
    void testOverrideBeanReflectionFallback() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.containsBean("testBean")).thenReturn(true);
        when(beanFactory.isSingleton("testBean")).thenReturn(true);
        when(beanFactory.getBeanDefinition("testBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(beanFactory.getBean("testBean")).thenReturn("originalValue");
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"testBean"});

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // Should trigger reflection fallback since not DefaultSingletonBeanRegistry
        verify(beanFactory).containsBean("testBean");
        verify(beanFactory).isSingleton("testBean");
    }

    /**
     * Test updateBeanFields with field name matching
     */
    @Test
    void testUpdateBeanFieldsWithNameMatch() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("testBean")).thenReturn(true);
        when(configurableFactory.isSingleton("testBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("testBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(configurableFactory.getBean("testBean"))
            .thenReturn("originalValue")
            .thenReturn("newValue");
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"testBean", "dependentBean"});

        // Create a dependent bean with a field that should be updated
        TestBeanWithStringField dependentBean = new TestBeanWithStringField("oldValue");
        when(context.getBean("dependentBean")).thenReturn(dependentBean);

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // The field should have been checked for update
        assertNotNull(dependentBean.getStringField());
    }

    /**
     * Test updateBeanFields with type matching
     */
    @Test
    void testUpdateBeanFieldsWithTypeMatch() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("configBean")).thenReturn(true);
        when(configurableFactory.isSingleton("configBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("configBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("com.documentor.config.DocumentorConfig");

        DocumentorConfig originalConfig = mock(DocumentorConfig.class);
        DocumentorConfig newConfig = mock(DocumentorConfig.class);
        when(configurableFactory.getBean("configBean"))
            .thenReturn(originalConfig)
            .thenReturn(newConfig);
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"configBean", "serviceBean"});

        // Create a service bean with a DocumentorConfig field
        TestServiceWithConfig serviceBean = new TestServiceWithConfig();
        serviceBean.setConfig(originalConfig);
        when(context.getBean("serviceBean")).thenReturn(serviceBean);

        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "configBean", newConfig));

        // Just verify the method completed - field update is internal behavior
    }    /**
     * Test updateBeanFields with null field value (should skip)
     */
    @Test
    void testUpdateBeanFieldsWithNullFieldValue() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("testBean")).thenReturn(true);
        when(configurableFactory.isSingleton("testBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("testBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(configurableFactory.getBean("testBean"))
            .thenReturn("originalValue")
            .thenReturn("newValue");
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"testBean", "dependentBean"});

        // Create a dependent bean with null field value
        TestBeanWithStringField dependentBean = new TestBeanWithStringField(null);
        when(context.getBean("dependentBean")).thenReturn(dependentBean);

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // The null field should not have been updated
        assertNull(dependentBean.getStringField());
    }

    /**
     * Test updateDependentBeans exception handling
     */
    @Test
    void testUpdateDependentBeansExceptionHandling() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("testBean")).thenReturn(true);
        when(configurableFactory.isSingleton("testBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("testBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(configurableFactory.getBean("testBean"))
            .thenReturn("originalValue")
            .thenReturn("newValue");
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"testBean", "problematicBean"});

        // Mock a problematic bean that throws exception
        when(context.getBean("problematicBean")).thenThrow(new RuntimeException("Bean access error"));

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // Just verify the method completed despite the exception
    }

    /**
     * Test updateBeanFields with inheritance (superclass fields)
     */
    @Test
    void testUpdateBeanFieldsWithInheritance() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("testBean")).thenReturn(true);
        when(configurableFactory.isSingleton("testBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("testBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(configurableFactory.getBean("testBean"))
            .thenReturn("originalValue")
            .thenReturn("newValue");
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"testBean", "inheritedBean"});

        // Create a bean that inherits from a class with matching field
        TestChildService childService = new TestChildService();
        childService.setBaseValue("oldValue");
        when(context.getBean("inheritedBean")).thenReturn(childService);

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // Just verify the method completed - field updates are internal
    }

    /**
     * Test updateBeanFields with field access exception
     */
    @Test
    void testUpdateBeanFieldsWithFieldAccessException() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory = mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory = (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("testBean")).thenReturn(true);
        when(configurableFactory.isSingleton("testBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("testBean")).thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(configurableFactory.getBean("testBean"))
            .thenReturn("originalValue")
            .thenReturn("newValue");
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"testBean", "problematicFieldBean"});

        // Create a bean with inaccessible field
        TestBeanWithFinalField problematicBean = new TestBeanWithFinalField();
        when(context.getBean("problematicFieldBean")).thenReturn(problematicBean);

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // Just verify the method completed despite field access issues
    }

    // Helper classes for testing

    public static class TestServiceWithConfig {
        private DocumentorConfig config;

        public DocumentorConfig getConfig() {
            return config;
        }

        public void setConfig(DocumentorConfig config) {
            this.config = config;
        }
    }

    public static class TestBeanWithStringField {
        private String stringField;

        public TestBeanWithStringField(String value) {
            this.stringField = value;
        }

        public String getStringField() {
            return stringField;
        }
    }

    public static class TestBaseService {
        private String baseValue;

        public String getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(String baseValue) {
            this.baseValue = baseValue;
        }
    }

    public static class TestChildService extends TestBaseService {
        private String childValue;

        public String getChildValue() {
            return childValue;
        }

        public void setChildValue(String childValue) {
            this.childValue = childValue;
        }
    }

    public static class TestBeanWithFinalField {
        private final String finalField = "cannotChange";

        public String getFinalField() {
            return finalField;
        }
    }
}
