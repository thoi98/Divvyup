package in.divvyup.controller.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import in.divvyup.config.AuthConfig;
import in.divvyup.controller.view.ExceptionView;
import in.divvyup.exception.AuthenticationException;
import in.divvyup.exception.ProcessingException;
import in.divvyup.persistence.dao.UserSessionDao;
import in.divvyup.persistence.model.User;
import in.divvyup.persistence.model.UserSession;
import in.divvyup.pojo.RequestExecutionContext;
import in.divvyup.service.UserService;
import in.divvyup.util.AuthUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@WebFilter ("/*")
@Slf4j
public class RequestProcessingFilter implements Filter {
    public static final ThreadLocal<RequestExecutionContext> REQUEST_EXECUTION_CONTEXT = new ThreadLocal<>();

    @Autowired
    private UserSessionDao userSessionDao;

    @Autowired
    private AuthConfig authConfig;

    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException {
        try {
            if (request instanceof HttpServletRequest servletRequest) {
                if (!Arrays.asList("GET", "POST", "PUT", "DELETE").contains(servletRequest.getMethod())) {
                    setHeadersForCORS((HttpServletResponse) response, (HttpServletRequest) request);
                } else {
                    RequestExecutionContext.RequestExecutionContextBuilder executionContextBuilder = RequestExecutionContext.builder();
                    if (isExcludedForAuth(servletRequest)) {
                        REQUEST_EXECUTION_CONTEXT.set(executionContextBuilder.build());
                        chain.doFilter(request, response);
                    } else {
                        UserSession userSession = null;
                        String sessionId = getSessionIdFromCookie(servletRequest);
                        if (StringUtils.isNotBlank(sessionId)) {
                            userSession = getUserSession(sessionId);
                            if (Objects.nonNull(userSession)) {
                                String userId = userSession.getUserId();
                                User user = userService.getUserById(userId);
                                executionContextBuilder.user(user).sessionId(userSession.getSessionId());
                            }
                        }
                        REQUEST_EXECUTION_CONTEXT.set(executionContextBuilder.build());
                        if (sessionId != null && Objects.nonNull(userSession)) {
                            chain.doFilter(request, response);
                        } else {
                            throw new AuthenticationException("Unauthorized access");
                        }
                    }
                    setHeadersForCORS((HttpServletResponse) response, (HttpServletRequest) request);
                }
            }
        } catch (AuthenticationException exception) {
            log.error("Exception processing request", exception);
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED_401);
            httpResponse.getWriter()
                        .print(ExceptionView.builder().errorCode(exception.getErrorCode()).errorMessage(exception.getErrorMessage()).build());
            setHeadersForCORS(httpResponse, (HttpServletRequest) request);
        } catch (IOException | ServletException exception) {
            log.error("Exception processing request", exception);
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
            httpResponse.getWriter().print(new ProcessingException("Something went wrong. Please try again later"));
            setHeadersForCORS(httpResponse, (HttpServletRequest) request);
        } finally {
            REQUEST_EXECUTION_CONTEXT.remove();
        }
    }

    private void setHeadersForCORS(HttpServletResponse httpResponse, HttpServletRequest request) {
        String header = request.getHeader("Origin");
        if (StringUtils.isNotBlank(header)) {
            httpResponse.setHeader("Access-Control-Allow-Origin", header);
        }
        httpResponse.setHeader("Access-Control-Allow-Methods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        httpResponse.setHeader("Access-Control-Allow-Headers",
                "Cache-Control,X-Requested-With,Content-Type,Accept,Origin,Authorization,Access-Control-Allow-Origin,Access-Control-Allow" +
                        "-Credentials");
        httpResponse.addHeader("Vary", "Origin");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setContentType("application/json");
    }

    private UserSession getUserSession(String encodedSessionId) {
        return userSessionDao.getBySessionId(new String(Base64.decodeBase64(encodedSessionId)));
    }

    private String getSessionIdFromCookie(HttpServletRequest servletRequest) {
        return getCookieValue(servletRequest, authConfig.getCookieName());
    }

    private String getCookieValue(HttpServletRequest servletRequest, String cookieName) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            Optional<Cookie> profileCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(cookieName)).findFirst();
            if (profileCookie.isPresent()) {
                return profileCookie.get().getValue();
            }
        }
        return null;
    }

    private boolean isExcludedForAuth(HttpServletRequest request) {
        return AuthUtil.AUTH_EXCLUDED_URL_PATTERNS.stream().anyMatch(p -> p.getPattern().matcher(request.getRequestURI()).matches());
    }
}
