package guru.springframework.spring6aiintro.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(RequestLoggingConfig.RequestLoggingProperties.class)
public class RequestLoggingConfig {

    @Bean
    @ConditionalOnProperty(prefix = "appconfig.request-logging", name = "enabled", havingValue = "true",
            matchIfMissing = true)
    public RequestLoggingFilter logFilter(RequestLoggingProperties requestLoggingProperties) {
        RequestLoggingFilter filter = new RequestLoggingFilter(requestLoggingProperties.getExcludePaths());
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA: ");
        return filter;
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "appconfig.request-logging")
    public static class RequestLoggingProperties {

        /**
         * Whether the request logging filter is registered at all. When set to false no
         * filter is added to the servlet filter chain.
         */
        private boolean enabled = true;

        /**
         * Request URI prefixes that are excluded from request logging (e.g.
         * /actuator/health).
         */
        private List<String> excludePaths = new ArrayList<>();

    }

}
