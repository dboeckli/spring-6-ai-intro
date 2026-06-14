package guru.springframework.spring6aiintro.test.config;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Locale;

public class LocaleExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(@NonNull ExtensionContext context) {
        Locale.setDefault(Locale.US);
    }

}