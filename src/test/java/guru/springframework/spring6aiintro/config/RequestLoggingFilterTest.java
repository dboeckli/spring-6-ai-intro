package guru.springframework.spring6aiintro.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingFilterTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(RequestLoggingConfig.class);

    @ParameterizedTest
    @ValueSource(strings = { "/actuator/health", "/actuator/health/readiness", "/actuator/health/liveness" })
    void excludesRequestsMatchingAnExcludedPath(String requestUri) {
        RequestLoggingFilter filter = new RequestLoggingFilter(List.of("/actuator/health"));

        assertThat(filter.shouldNotFilter(request(requestUri))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "/api/v1/beer", "/api/v1/customer", "/actuator/info", "/actuator/healthcheck" })
    void logsRequestsNotMatchingAnExcludedPath(String requestUri) {
        RequestLoggingFilter filter = new RequestLoggingFilter(List.of("/actuator/health"));

        assertThat(filter.shouldNotFilter(request(requestUri))).isFalse();
    }

    @Test
    void logsEverythingWhenNoExcludedPathsAreConfigured() {
        RequestLoggingFilter filter = new RequestLoggingFilter(List.of());

        assertThat(filter.shouldNotFilter(request("/actuator/health"))).isFalse();
    }

    @Test
    void logsEverythingWhenExcludedPathsIsNull() {
        RequestLoggingFilter filter = new RequestLoggingFilter(null);

        assertThat(filter.shouldNotFilter(request("/actuator/health"))).isFalse();
    }

    @Test
    void registersFilterByDefault() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(RequestLoggingFilter.class));
    }

    @Test
    void doesNotRegisterFilterWhenDisabled() {
        contextRunner.withPropertyValues("appconfig.request-logging.enabled=false")
            .run(context -> assertThat(context).doesNotHaveBean(RequestLoggingFilter.class));
    }

    @Test
    void bindsExcludedPathsFromConfiguration() {
        contextRunner.withPropertyValues("appconfig.request-logging.exclude-paths[0]=/actuator/health").run(context -> {
            RequestLoggingFilter filter = context.getBean(RequestLoggingFilter.class);

            assertThat(filter.shouldNotFilter(request("/actuator/health/readiness"))).isTrue();
            assertThat(filter.shouldNotFilter(request("/api/v1/beer"))).isFalse();
        });
    }

    private static MockHttpServletRequest request(String requestUri) {
        return new MockHttpServletRequest("GET", requestUri);
    }

}
