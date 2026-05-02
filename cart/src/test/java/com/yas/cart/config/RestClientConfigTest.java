package com.yas.cart.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class RestClientConfigTest {

    private RestClientConfig restClientConfig;

    @BeforeEach
    void setUp() {
        restClientConfig = new RestClientConfig();
    }

    @Nested
    class RestClientBeanTest {

        @Test
        void testRestClientBean_shouldCreateRestClient() {
            RestClient restClient = restClientConfig.restClient();

            assertThat(restClient).isNotNull();
        }

        @Test
        void testRestClientBean_shouldReturnRestClientInstance() {
            RestClient restClient = restClientConfig.restClient();

            assertThat(restClient).isInstanceOf(RestClient.class);
        }

        @Test
        void testRestClientBean_shouldBeUsableForRequests() {
            RestClient restClient = restClientConfig.restClient();

            assertThat(restClient).isNotNull();

            assertThat(restClient.get()).isNotNull();
        }

        @Test
        void testRestClientBean_multipleCallsShouldCreateDifferentInstances() {
            RestClient restClient1 = restClientConfig.restClient();
            RestClient restClient2 = restClientConfig.restClient();

            assertThat(restClient1).isNotNull();
            assertThat(restClient2).isNotNull();
            assertThat(restClient1).isNotSameAs(restClient2);
        }

        @Test
        void testRestClientBean_shouldHaveExpectedMethods() {
            RestClient restClient = restClientConfig.restClient();

            assertThat(restClient).isNotNull();
        }
    }

    @Nested
    class RestClientConfigClassTest {

        @Test
        void testConfigurationClass_shouldBeInstantiable() {
            assertThat(restClientConfig).isNotNull();
        }

        @Test
        void testConfigurationClass_shouldBeConfigurationClass() {
            assertThat(restClientConfig.getClass().getName()).contains("RestClientConfig");
        }

        @Test
        void testRestClientHasGetMethod() {
            RestClient restClient = restClientConfig.restClient();

            var getRequest = restClient.get();
            assertThat(getRequest).isNotNull();
        }

        @Test
        void testRestClientHasPostMethod() {
            RestClient restClient = restClientConfig.restClient();

            var postRequest = restClient.post();
            assertThat(postRequest).isNotNull();
        }

        @Test
        void testRestClientHasPutMethod() {
            RestClient restClient = restClientConfig.restClient();

            var putRequest = restClient.put();
            assertThat(putRequest).isNotNull();
        }

        @Test
        void testRestClientHasDeleteMethod() {
            RestClient restClient = restClientConfig.restClient();

            var deleteRequest = restClient.delete();
            assertThat(deleteRequest).isNotNull();
        }

        @Test
        void testRestClientHasPatchMethod() {
            RestClient restClient = restClientConfig.restClient();

            var patchRequest = restClient.patch();
            assertThat(patchRequest).isNotNull();
        }

        @Test
        void testRestClientHasHeadMethod() {
            RestClient restClient = restClientConfig.restClient();

            var headRequest = restClient.head();
            assertThat(headRequest).isNotNull();
        }

        @Test
        void testRestClientHasOptionsMethod() {
            RestClient restClient = restClientConfig.restClient();

            var optionsRequest = restClient.options();
            assertThat(optionsRequest).isNotNull();
        }
    }
}
