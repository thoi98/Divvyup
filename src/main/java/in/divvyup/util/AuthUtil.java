package in.divvyup.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import in.divvyup.annotation.NoLogin;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@SuppressWarnings ({ "deprecation" })
public class AuthUtil {
    public static final Set<UrlPattern> AUTH_EXCLUDED_URL_PATTERNS = new HashSet<>();

    static {
        Reflections reflections =
                new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()).setScanners(new MethodAnnotationsScanner()));
        // Auth excluded URL patterns
        Set<Method> noLoginMethods = reflections.getMethodsAnnotatedWith(NoLogin.class);
        noLoginMethods.forEach(method -> AUTH_EXCLUDED_URL_PATTERNS.add(getUrlPattern(method)));
    }

    private static UrlPattern getUrlPattern(Method method) {
        RequestMapping requestMappingAnnotatedClass = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        String classPathUrl = Optional.ofNullable(requestMappingAnnotatedClass).map(AuthUtil::getPath).orElse("");
        String methodPathUrl = "";
        String requestMethod = "GET";
        Annotation annotation = AnnotationUtils.getAnnotation(method, GetMapping.class);
        if (Objects.isNull(annotation)) {
            annotation = AnnotationUtils.getAnnotation(method, PostMapping.class);
            requestMethod = "POST";
        }
        if (Objects.isNull(annotation)) {
            annotation = AnnotationUtils.getAnnotation(method, PutMapping.class);
            requestMethod = "PUT";
        }
        if (Objects.isNull(annotation)) {
            annotation = AnnotationUtils.getAnnotation(method, DeleteMapping.class);
            requestMethod = "DELETE";
        }
        if (Objects.nonNull(annotation)) {
            methodPathUrl = getPath(annotation);
        }
        String url = "/" + trimSlash(trimSlash(classPathUrl) + "/" + trimSlash(methodPathUrl));
        return UrlPattern.builder().method(requestMethod).pattern(url.equals("/") ? Pattern.compile("/") : getUrlRegexPattern(url)).build();
    }

    private static String getPath(Annotation annotation) {
        String[] value = (String[]) AnnotationUtils.getValue(annotation, "value");
        String[] path = (String[]) AnnotationUtils.getValue(annotation, "path");
        if (Objects.nonNull(value) && value.length > 0) {
            return value[0];
        } else if (Objects.nonNull(path) && path.length > 0) {
            return path[0];
        }
        return "";
    }

    private static Pattern getUrlRegexPattern(String url) {
        return Pattern.compile(Arrays.stream(url.split("/")).map(s -> {
            if (s.startsWith("{")) {
                return "[a-zA-Z0-9-_]+";
            } else {
                return s;
            }
        }).collect(Collectors.joining("/")));
    }

    private static String trimSlash(String s) {
        while (s.startsWith("/")) {
            s = s.substring(1);
        }
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UrlPattern {
        private String method;

        private Pattern pattern;

        public boolean isMatch(HttpServletRequest request) {
            return this.pattern.matcher(request.getRequestURI()).matches() && method.equals(request.getMethod());
        }
    }
}
