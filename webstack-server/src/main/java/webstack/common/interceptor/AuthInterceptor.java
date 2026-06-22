package webstack.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import webstack.common.annotation.RequireRole;
import webstack.common.constant.RoleConstant;
import webstack.common.exception.BizException;
import webstack.common.result.ResultCode;
import webstack.common.util.JwtUtil;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_USER_ID = "userId";
    public static final String ATTR_ROLE = "role";

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String token = resolveToken(request);
        boolean authenticated = token != null && jwtUtil.validateToken(token);

        if (authenticated) {
            request.setAttribute(ATTR_USER_ID, jwtUtil.getUserIdFromToken(token));
            request.setAttribute(ATTR_ROLE, jwtUtil.getRoleFromToken(token));
        } else {
            request.setAttribute(ATTR_ROLE, RoleConstant.USER);
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }

        if (requireRole == null) {
            return true;
        }

        String[] allowedRoles = requireRole.value();

        // value 为空 → 任意已登录用户
        if (allowedRoles.length == 0) {
            if (!authenticated) {
                throw new BizException(ResultCode.UNAUTHORIZED);
            }
            return true;
        }

        // value 非空 → 当前角色须命中其一
        String currentRole = (String) request.getAttribute(ATTR_ROLE);
        if (!Arrays.asList(allowedRoles).contains(currentRole)) {
            throw new BizException(authenticated ? ResultCode.FORBIDDEN : ResultCode.UNAUTHORIZED);
        }
        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
