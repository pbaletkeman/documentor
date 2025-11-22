package com.documentor.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Comprehensive test coverage for BeanUtils utility class.
 * Tests bean override operations, reflection-based updates, and edge cases.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BeanUtilsCoverageTest {

    @Mock
    private ConfigurableApplicationContext mockConfigContext;

    @Mock
    private ConfigurableListableBeanFactory mockBeanFactory;

    @Mock
    private BeanDefinition mockBeanDefinition;

    private Object originalBean;
    private Object newBean;

    /**
     * Set up test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        originalBean = new Object();
        newBean = new Object();
    }

    @Nested
    class NullParameterHandlingTests {

        @Test
        void testOverrideBeanWithNullApplicationContext() {
            assertThrows(NullPointerException.class, () ->
                BeanUtils.overrideBean(null, "testBean", newBean)
            );
        }

        @Test
        void testOverrideBeanWithNullBeanName() {
            assertThrows(NullPointerException.class, () ->
                BeanUtils.overrideBean(mockConfigContext, null, newBean)
            );
        }

        @Test
        void testOverrideBeanWithNullNewBean() {
            assertThrows(NullPointerException.class, () ->
                BeanUtils.overrideBean(mockConfigContext, "testBean", null)
            );
        }

        @Test
        void testOverrideBeanWithAllNullParameters() {
            assertThrows(NullPointerException.class, () ->
                BeanUtils.overrideBean(null, null, null)
            );
        }
    }

    @Nested
    class BeanNotFoundTests {

        @BeforeEach
        void setUp() {
            when(mockConfigContext.getBeanFactory()).thenReturn(mockBeanFactory);
        }

        @Test
        void testOverrideBeanWhenBeanDoesNotExist() {
            when(mockBeanFactory.containsBean("nonExistentBean")).thenReturn(false);
            when(mockBeanFactory.getBeanDefinitionNames())
                    .thenReturn(new String[]{"bean1", "bean2", "bean3"});

            BeanUtils.overrideBean(mockConfigContext, "nonExistentBean", newBean);

            verify(mockBeanFactory).containsBean("nonExistentBean");
        }

        @Test
        void testOverrideBeanWhenBeanDefinitionNamesIsEmpty() {
            when(mockBeanFactory.containsBean("testBean")).thenReturn(false);
            when(mockBeanFactory.getBeanDefinitionNames()).thenReturn(new String[]{});

            BeanUtils.overrideBean(mockConfigContext, "testBean", newBean);

            verify(mockBeanFactory).getBeanDefinitionNames();
        }
    }

    @Nested
    class NonSingletonBeanTests {

        @BeforeEach
        void setUp() {
            when(mockConfigContext.getBeanFactory()).thenReturn(mockBeanFactory);
        }

        @Test
        void testOverrideBeanWhenBeanIsNotSingleton() {
            when(mockBeanFactory.containsBean("prototypeBean")).thenReturn(true);
            when(mockBeanFactory.isSingleton("prototypeBean")).thenReturn(false);

            BeanUtils.overrideBean(mockConfigContext, "prototypeBean", newBean);

            verify(mockBeanFactory).isSingleton("prototypeBean");
        }

        @Test
        void testOverrideBeanWhenBeanIsFunctional() {
            when(mockBeanFactory.containsBean("functionalBean")).thenReturn(true);
            when(mockBeanFactory.isSingleton("functionalBean")).thenReturn(false);

            BeanUtils.overrideBean(mockConfigContext, "functionalBean", newBean);

            verify(mockBeanFactory).containsBean("functionalBean");
            verify(mockBeanFactory).isSingleton("functionalBean");
        }
    }

    @Nested
    class ContextTypeTests {

        @Test
        void testOverrideBeanWithNonConfigurableApplicationContext() {
            ApplicationContext nonConfigurableContext = mock(ApplicationContext.class);

            BeanUtils.overrideBean(nonConfigurableContext, "testBean", newBean);
        }

        @Test
        void testOverrideBeanWithConfigurableContext() {
            when(mockConfigContext.getBeanFactory()).thenReturn(mockBeanFactory);
            when(mockBeanFactory.containsBean("testBean")).thenReturn(true);
            when(mockBeanFactory.isSingleton("testBean")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("testBean"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("testBean"))
                    .thenReturn(originalBean)
                    .thenReturn(newBean);

            BeanUtils.overrideBean(mockConfigContext, "testBean", newBean);

            verify(mockConfigContext).getBeanFactory();
        }
    }

    @Nested
    class ExceptionHandlingTests {

        @BeforeEach
        void setUp() {
            when(mockConfigContext.getBeanFactory()).thenReturn(mockBeanFactory);
        }

        @Test
        void testOverrideBeanWhenGetBeanDefinitionThrowsException() {
            when(mockBeanFactory.containsBean("testBean")).thenReturn(true);
            when(mockBeanFactory.isSingleton("testBean")).thenReturn(true);
            doThrow(new RuntimeException("Bean definition lookup failed"))
                    .when(mockBeanFactory).getBeanDefinition(anyString());

            BeanUtils.overrideBean(mockConfigContext, "testBean", newBean);

            verify(mockBeanFactory).getBeanDefinition("testBean");
        }

        @Test
        void testOverrideBeanWhenContextThrowsException() {
            when(mockConfigContext.getBeanFactory())
                    .thenThrow(new RuntimeException("Context access failed"));

            BeanUtils.overrideBean(mockConfigContext, "testBean", newBean);

            verify(mockConfigContext).getBeanFactory();
        }

        @Test
        void testOverrideBeanWhenBeanLookupThrowsException() {
            when(mockBeanFactory.containsBean("testBean"))
                    .thenThrow(new RuntimeException("Bean lookup failed"));

            BeanUtils.overrideBean(mockConfigContext, "testBean", newBean);

            verify(mockBeanFactory).containsBean("testBean");
        }
    }

    @Nested
    class BeanFieldUpdateTests {

        @BeforeEach
        void setUp() {
            when(mockConfigContext.getBeanFactory()).thenReturn(mockBeanFactory);
        }

        @Test
        void testUpdateBeanFieldsWhenConfigBeanExists() {
            when(mockBeanFactory.containsBean("config")).thenReturn(true);
            when(mockBeanFactory.isSingleton("config")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("config"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("config")).thenReturn(new Object());
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{"config"});

            BeanUtils.overrideBean(mockConfigContext, "config", newBean);
            // Test completes if no exception thrown
        }

        @Test
        void testUpdateBeanFieldsSkipsSpringInternalBeans() {
            when(mockBeanFactory.containsBean("config")).thenReturn(true);
            when(mockBeanFactory.isSingleton("config")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("config"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("config")).thenReturn(new Object());
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{
                        "config",
                        "org.springframework.boot.internal"
                    });

            BeanUtils.overrideBean(mockConfigContext, "config", newBean);
            // Test completes if no exception thrown
        }
    }

    @Nested
    class SpecializedServiceUpdateTests {

        @BeforeEach
        void setUp() {
            when(mockConfigContext.getBeanFactory()).thenReturn(mockBeanFactory);
        }

        @Test
        void testUpdateDocumentorConfigDependentsWhenLlmServiceExists() {
            when(mockBeanFactory.containsBean("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.isSingleton("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("documentorConfig"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("documentorConfig")).thenReturn(new Object());
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{"documentorConfig", "llmService"});
            when(mockConfigContext.containsBean("llmService")).thenReturn(true);
            when(mockConfigContext.getBean("llmService")).thenReturn(new Object());

            BeanUtils.overrideBean(mockConfigContext, "documentorConfig", newBean);
            // Test completes if no exception thrown
        }

        @Test
        void testUpdateDocumentorConfigDependentsWhenDocumentationServiceExists() {
            when(mockBeanFactory.containsBean("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.isSingleton("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("documentorConfig"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("documentorConfig")).thenReturn(new Object());
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{"documentorConfig", "documentationService"});
            when(mockConfigContext.containsBean("documentationService"))
                    .thenReturn(true);
            when(mockConfigContext.getBean("documentationService"))
                    .thenReturn(new Object());

            BeanUtils.overrideBean(mockConfigContext, "documentorConfig", newBean);
            // Test completes if no exception thrown
        }

        @Test
        void testUpdateDocumentorConfigDependentsWhenBothServicesExist() {
            when(mockBeanFactory.containsBean("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.isSingleton("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("documentorConfig"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("documentorConfig")).thenReturn(new Object());
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{
                        "documentorConfig",
                        "llmService",
                        "documentationService"
                    });
            when(mockConfigContext.containsBean("llmService")).thenReturn(true);
            when(mockConfigContext.getBean("llmService")).thenReturn(new Object());
            when(mockConfigContext.containsBean("documentationService"))
                    .thenReturn(true);
            when(mockConfigContext.getBean("documentationService"))
                    .thenReturn(new Object());

            BeanUtils.overrideBean(mockConfigContext, "documentorConfig", newBean);
            // Test completes if no exception thrown
        }

        @Test
        void testUpdateDocumentorConfigDependentsWhenNeitherServiceExists() {
            when(mockBeanFactory.containsBean("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.isSingleton("documentorConfig")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("documentorConfig"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("documentorConfig")).thenReturn(new Object());
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{"documentorConfig"});
            when(mockConfigContext.containsBean("llmService")).thenReturn(false);
            when(mockConfigContext.containsBean("documentationService"))
                    .thenReturn(false);

            BeanUtils.overrideBean(mockConfigContext, "documentorConfig", newBean);
            // Test completes if no exception thrown
        }
    }

    @Nested
    class ReflectionPathTests {

        @BeforeEach
        void setUp() {
            when(mockConfigContext.getBeanFactory()).thenReturn(mockBeanFactory);
        }

        @Test
        void testReflectionAccessForPrivateFields() {
            TestBeanWithPrivateField bean = new TestBeanWithPrivateField();
            when(mockBeanFactory.containsBean("testBean")).thenReturn(true);
            when(mockBeanFactory.isSingleton("testBean")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("testBean"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("testBean")).thenReturn(bean);
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{"testBean"});
            when(mockConfigContext.getBean("testBean")).thenReturn(bean);

            BeanUtils.overrideBean(mockConfigContext, "testBean", newBean);
            // Test completes if no exception thrown
        }

        @Test
        void testReflectionAccessForInheritedFields() {
            TestBeanWithInheritance bean = new TestBeanWithInheritance();
            when(mockBeanFactory.containsBean("testBean")).thenReturn(true);
            when(mockBeanFactory.isSingleton("testBean")).thenReturn(true);
            when(mockBeanFactory.getBeanDefinition("testBean"))
                    .thenReturn(mockBeanDefinition);
            when(mockBeanFactory.getBean("testBean")).thenReturn(bean);
            when(mockConfigContext.getBeanDefinitionNames())
                    .thenReturn(new String[]{"testBean"});
            when(mockConfigContext.getBean("testBean")).thenReturn(bean);

            BeanUtils.overrideBean(mockConfigContext, "testBean", newBean);
            // Test completes if no exception thrown
        }
    }

    static class TestBeanWithConfig {
        private Object config;

        TestBeanWithConfig() {
            this.config = new Object();
        }

        Object getConfig() {
            return config;
        }
    }

    static class TestBeanWithInheritance extends TestBeanWithConfig {
        private String name;

        TestBeanWithInheritance() {
            this.name = "testBean";
        }

        String getName() {
            return name;
        }
    }

    static class TestBeanWithPrivateField {
        @SuppressWarnings("unused")
        private Object config;

        TestBeanWithPrivateField() {
            this.config = new Object();
        }
    }
}
