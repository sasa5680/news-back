package com.example.news.auth;

import com.example.news.entity.UserEntity;
import com.example.news.exception.ErrorCode;
import com.example.news.exception.NoAuthException;
import com.example.news.repo.UserRepo;
import com.example.news.token.TokenProvider;
import com.example.news.types.UserRole;
import org.apache.catalina.connector.Response;
import org.apache.http.protocol.ResponseConnControl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

//Auth 어노테이션 구현체
@Component
@Aspect
public class AuthClass {

    @Autowired
    UserRepo userRepo;

    @Autowired
    TokenProvider tokenProvider;

    @Pointcut("@annotation(Auth)") // 3
    public void onRequest() {
    }

    @Around("onRequest()")
    public Object processCustomAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

        //해당 메서드의 http 요청을 가져온다,
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Auth annotation = methodSignature.getMethod().getAnnotation(Auth.class);

        UserEntity userEntity = userRepo.findByUserId(tokenProvider.getUserIdFromRequest(request));

        //해당하는 유저가 없으면 에러처리
        if (userEntity == null)
            throw new NoAuthException(ErrorCode.NO_AUTHORIZATION_ERROR);

        //role 권한에 맞지 않으면 에러처리한다. (Admin은 모든 권한을 가진다.)
        String role = tokenProvider.getRoleFromRequest(request);

        boolean isAdmin = role.equals(UserRole.ADMIN.getUserRoleType());
        boolean isRightAuth = role.equals(annotation.userRole().getUserRoleType());

        if ((!isAdmin) && (!isRightAuth))
            throw new NoAuthException(ErrorCode.NO_AUTHORIZATION_ERROR);

        //해당 메서드의 파라미터들을 가져온다.
        Object[] args = joinPoint.getArgs();

        //파라미터에 붙은 어노테이션을 전부 가져온다.
        //[annotation type][annotation이 붙은 파라 파라미터]
        Annotation[][] parameterAnnotations = methodSignature.getMethod().getParameterAnnotations();

        //검사해서 @User가 붙어있으면 객체를 넣어준다.
        for (int argIndex = 0; argIndex < args.length; argIndex++) {
            for (Annotation paramAnnotation : parameterAnnotations[argIndex]) {
                if (paramAnnotation instanceof User) {
                    args[argIndex] = UserDetails.from(userEntity);
                }
            }
        }


        //토큰 기한이 많이 안 남으면 새 토큰 헤더에 남아서 추가
//        HttpServletResponse httpServletResponse= ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getResponse();
//        httpServletResponse.addHeader("sample", "sample");
//        System.out.println(httpServletResponse);

        Object result = joinPoint.proceed(args);
        return result;


    }
}
