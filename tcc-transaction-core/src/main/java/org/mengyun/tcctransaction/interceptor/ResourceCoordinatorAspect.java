package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mengyun.tcctransaction.api.Compensable;

/**
 * 抽象的资源协调器切面（切面是通知和切点的结合）.
 * <p>拦截带@Compensable注解的可补偿事务方法.</p>
 *
 * Created by changmingxie on 11/8/15.
 */
@Aspect
public abstract class ResourceCoordinatorAspect {

    /**
     * 资源协调拦截器
     */
    private ResourceCoordinatorInterceptor resourceCoordinatorInterceptor;

    /**
     * 切入点:
     * <p>拦截注解 {@link Compensable}</p>
     */
    @Pointcut("@annotation(org.mengyun.tcctransaction.api.Compensable)")
    public void transactionContextCall() {

    }

    /**
     * 环绕通知
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("transactionContextCall()")
    public Object interceptTransactionContextMethod(ProceedingJoinPoint pjp) throws Throwable {
        return resourceCoordinatorInterceptor.interceptTransactionContextMethod(pjp);
    }

    public void setResourceCoordinatorInterceptor(ResourceCoordinatorInterceptor resourceCoordinatorInterceptor) {
        this.resourceCoordinatorInterceptor = resourceCoordinatorInterceptor;
    }

    public abstract int getOrder();
}
