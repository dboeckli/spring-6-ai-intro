package guru.springframework.spring6aiintro.log;

import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class ConfigChangeListener {

    private static final List<String> PASSWORD_KEY_LIST = Arrays.asList("jwt.key-value", "password", "credentials",
            "secret");

    @EventListener
    @Observed(name = "config.change.listener", contextualName = "handle-context-refresh")
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        StreamSupport.stream(sources.spliterator(), false)
            .filter(EnumerablePropertySource.class::isInstance)
            .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
            .flatMap(Arrays::stream)
            .distinct()
            .forEach(prop -> {
                String propertyValue = env.getProperty(prop);
                if (propertyValue != null) {

                    if (PASSWORD_KEY_LIST.stream().anyMatch(prop.toLowerCase()::contains)
                            || PASSWORD_KEY_LIST.stream().anyMatch(propertyValue.toLowerCase()::contains)) {

                        log.info("{}: {}", prop, "**************************"); // hide
                                                                                // password
                    }
                    else {
                        log.info("{}: {}", prop, propertyValue);
                    }

                }
                else {
                    log.warn("null propertyValue encountered in {}: {}", prop, propertyValue);
                }
            });
    }

}
