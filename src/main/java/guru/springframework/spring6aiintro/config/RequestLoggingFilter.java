package guru.springframework.spring6aiintro.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.List;

@RequiredArgsConstructor
public class RequestLoggingFilter extends CommonsRequestLoggingFilter {

    private final List<String> excludePaths;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (excludePaths == null || excludePaths.isEmpty()) {
            return false;
        }
        String requestUri = request.getRequestURI();
        return excludePaths.stream().anyMatch(path -> requestUri.equals(path) || requestUri.startsWith(path + "/"));
    }

}
