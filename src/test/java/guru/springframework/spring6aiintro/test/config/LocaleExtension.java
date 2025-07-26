package guru.springframework.spring6aiintro.test.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Locale;

public class LocaleExtension implements BeforeAllCallback {
    @Override
    public void beforeAll(ExtensionContext context) {
        Locale.setDefault(Locale.US);
    }
}