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
 * Comprehensive test coverage for BeanUtils to achieve 82%+ coverage for
 * config package.
 */
class BeanUtilsComprehensiveTest {

    /**
     * Test successful bean override with DefaultSingletonBeanRegistry
     */
    @Test
    void testOverrideBeanSuccessfulPath() {
        ConfigurableApplicationContext context =
            mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory =
            mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory =
            (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("testBean")).thenReturn(true);
        when(configurableFactory.isSingleton("testBean")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("testBean"))
            .thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName()).thenReturn("java.lang.String");
        when(configurableFactory.getBean("testBean"))
            .thenReturn("originalValue")  // First call - get original
            .thenReturn("newValue");      // Second call - verify replacement
        when(configurableFactory.getBeanDefinitionNames())
            .thenReturn(new String[]{"testBean"});
        when(context.getBeanDefinitionNames())
            .thenReturn(new String[]{"testBean"});

        String newValue = "newValue";
        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(context, "testBean", newValue));

        // Verify the DefaultSingletonBeanRegistry methods were called
        verify(beanFactory).destroySingleton("testBean");
        verify(beanFactory).registerSingleton("testBean", newValue);
    }

    /**
     * Test DocumentorConfig special handling with dependent services
     */
    @Test
    void testDocumentorConfigSpecialHandling() {
        ConfigurableApplicationContext context =
            mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory =
            mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory =
            (ConfigurableListableBeanFactory) beanFactory;
        BeanDefinition beanDefinition = mock(BeanDefinition.class);
        DocumentorConfig newConfig = mock(DocumentorConfig.class);

        when(context.getBeanFactory()).thenReturn(configurableFactory);
        when(configurableFactory.containsBean("documentorConfig")).thenReturn(true);
        when(configurableFactory.isSingleton("documentorConfig")).thenReturn(true);
        when(configurableFactory.getBeanDefinition("documentorConfig"))
            .thenReturn(beanDefinition);
        when(beanDefinition.getBeanClassName())
            .thenReturn("com.documentor.config.DocumentorConfig");
        when(configurableFactory.getBean("documentorConfig"))
            .thenReturn(mock(DocumentorConfig.class))
            .thenReturn(newConfig);
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(
            new String[]{"documentorConfig", "llmService",
                        "documentationService"});
        when(context.getBeanDefinitionNames()).thenReturn(
            new String[]{"documentorConfig", "llmService",
                        "documentationService"});

        // Mock dependent services
        when(context.containsBean("llmService")).thenReturn(true);
        when(context.containsBean("documentationService")).thenReturn(true);

        TestServiceWithConfig llmService = new TestServiceWithConfig();
        llmService.setConfig(mock(DocumentorConfig.class));
        TestServiceWithConfig docService = new TestServiceWithConfig();
        docService.setConfig(mock(DocumentorConfig.class));
        when(context.getBean("llmService")).thenReturn(llmService);
        when(context.getBean("documentationService")).thenReturn(docService);

        assertDoesNotThrow(() ->
            BeanUtils.overrideBean(context, "documentorConfig", newConfig));

        // Verify the services were updated
        assertEquals(newConfig, llmService.getConfig());
        assertEquals(newConfig, docService.getConfig());
    }

    /**
     * Test updateBeanFields with type and name matching
     */
    @Test
    void testUpdateBeanFieldsComprehensive() {
        ConfigurableApplicationContext context =
            mock(ConfigurableApplicationContext.class);
        DefaultSingletonBeanRegistry beanFactory =
            mock(DefaultSingletonBeanRegistry.class,
            withSettings().extraInterfaces(ConfigurableListableBeanFactory.class));
        ConfigurableListableBeanFactory configurableFactory =
            (ConfigurableListableBeanFactory) beanFactory;
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
            new String[]{"configBean", "serviceBean", "inheritedBean"});
        when(context.getBeanDefinitionNames()).thenReturn(
            new String[]{"configBean", "serviceBean", "inheritedBean"});

        // Create beans with fields to update
        TestServiceWithConfig serviceBean = new TestServiceWithConfig();
        serviceBean.setConfig(originalConfig);
        TestChildService inheritedBean = new TestChildService();
        inheritedBean.setBaseValue("oldValue");

        when(context.getBean("serviceBean")).thenReturn(serviceBean);
        when(context.getBean("inheritedBean")).thenReturn(inheritedBean);

        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "configBean", newConfig));

        // Fields should have been updated
        assertEquals(newConfig, serviceBean.getConfig());
    }

    /**
     * Test edge cases: null values, bean not found, non-singleton
     */
    @Test
    void testEdgeCases() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);

        when(context.getBeanFactory()).thenReturn(beanFactory);

        // Test bean not found
        when(beanFactory.containsBean("missingBean")).thenReturn(false);
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"otherBean"});
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "missingBean", "newValue"));

        // Test non-singleton bean
        when(beanFactory.containsBean("prototypeBean")).thenReturn(true);
        when(beanFactory.isSingleton("prototypeBean")).thenReturn(false);
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "prototypeBean", "newValue"));

        // Verify appropriate methods were called
        verify(beanFactory).containsBean("missingBean");
        verify(beanFactory).containsBean("prototypeBean");
        verify(beanFactory).isSingleton("prototypeBean");
    }

    /**
     * Test null parameter validation
     */
    @Test
    void testNullParameterValidation() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        String value = "test";

        assertThrows(NullPointerException.class,
            () -> BeanUtils.overrideBean(null, "beanName", value));
        assertThrows(NullPointerException.class,
            () -> BeanUtils.overrideBean(context, null, value));
        assertThrows(NullPointerException.class,
            () -> BeanUtils.overrideBean(context, "beanName", null));
    }

    /**
     * Test non-configurable application context
     */
    @Test
    void testNonConfigurableContext() {
        ApplicationContext context = mock(ApplicationContext.class);
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", "newValue"));
        // No interactions should occur with non-configurable context
        verifyNoInteractions(context);
    }

    /**
     * Test reflection fallback failure
     */
    @Test
    void testReflectionFallbackFailure() {
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
        when(context.getBeanDefinitionNames()).thenReturn(new String[]{"testBean"});

        // This should complete without throwing, even though reflection fails
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", "newValue"));
    }

    /**
     * Test exception handling during bean operations
     */
    @Test
    void testExceptionHandling() {
        ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
        ConfigurableListableBeanFactory beanFactory = mock(ConfigurableListableBeanFactory.class);

        when(context.getBeanFactory()).thenReturn(beanFactory);
        when(beanFactory.containsBean("testBean")).thenThrow(new RuntimeException("Database error"));

        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", "newValue"));
        verify(beanFactory).containsBean("testBean");
    }

    /**
     * Test updateBeanFields with null field values
     */
    @Test
    void testUpdateBeanFieldsWithNullValues() {
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
            new String[]{"testBean", "nullBean"});
        when(context.getBeanDefinitionNames()).thenReturn(
            new String[]{"testBean", "nullBean"});

        // Test with bean that has null field value
        TestBeanWithStringField nullFieldBean = new TestBeanWithStringField(null);
        when(context.getBean("nullBean")).thenReturn(nullFieldBean);

        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", "newValue"));

        // Null field should remain null
        assertNull(nullFieldBean.getStringField());
    }

    /**
     * Test updateDocumentorConfigDependents with missing services
     */
    @Test
    void testUpdateDocumentorConfigMissingServices() {
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
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(new String[]{"documentorConfig"});
        when(context.getBeanDefinitionNames()).thenReturn(new String[]{"documentorConfig"});

        // Mock services as NOT present
        when(context.containsBean("llmService")).thenReturn(false);
        when(context.containsBean("documentationService")).thenReturn(false);

        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "documentorConfig", newConfig));

        // Verify containsBean calls were made
        verify(context).containsBean("llmService");
        verify(context).containsBean("documentationService");
    }

    /**
     * Test bean identity verification failure
     */
    @Test
    void testBeanIdentityVerificationFailure() {
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
            .thenReturn("originalValue")    // First call - get original
            .thenReturn("differentValue");  // Second call - verification fails
        when(configurableFactory.getBeanDefinitionNames()).thenReturn(new String[]{"testBean"});
        when(context.getBeanDefinitionNames()).thenReturn(new String[]{"testBean"});

        String newValue = "newValue";
        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", newValue));

        // Verify registry methods were still called despite verification failure
        verify(beanFactory).destroySingleton("testBean");
        verify(beanFactory).registerSingleton("testBean", newValue);
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
        when(context.getBeanDefinitionNames()).thenReturn(
            new String[]{"testBean", "problematicBean"});

        // Mock a problematic bean that throws exception
        when(context.getBean("problematicBean")).thenThrow(new RuntimeException("Bean access error"));

        assertDoesNotThrow(() -> BeanUtils.overrideBean(context, "testBean", "newValue"));

        // Verify the exception was handled gracefully
        verify(context).getBean("problematicBean");
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
}
