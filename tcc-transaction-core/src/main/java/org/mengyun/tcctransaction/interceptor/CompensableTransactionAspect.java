package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.mengyun.tcctransaction.api.Compensable;

/**
 * 抽象的TCC补偿切面（切面是通知和切点的结合）.
 * <p>拦截带@Compensable注解的可补偿事务方法.</p>
 *
 * Created by changmingxie on 10/30/15.
 */
@Aspect
public abstract class CompensableTransactionAspect {

    /**
     * 可补偿事务拦截器
     */
    private CompensableTransactionInterceptor compensableTransactionInterceptor;

    public void setCompensableTransactionInterceptor(CompensableTransactionInterceptor compensableTransactionInterceptor) {
        this.compensableTransactionInterceptor = compensableTransactionInterceptor;
    }

    /**
     * 切入点:
     * <ul>
     *     <li>包含切入点表达式和切点签名</li>
     *     <li>切点用于准确定位应该在什么地方应用切面的通知</li>
     *     <li>切点可以被切面内的所有通知元素引用</li>
     * </ul>
     * <p>拦截注解 {@link Compensable}</p>
     */
    @Pointcut("@annotation(org.mengyun.tcctransaction.api.Compensable)")
    public void compensableService() {

    }

    /**
     * 环绕通知（在一个方法执行之前和执行之后运行
     * ，第一个参数必须是 {@link ProceedingJoinPoint}类型
     * ，pjp将包含切点拦截的方法的参数信息）
     * @param pjp
     * @throws Throwable
     */
    @Around("compensableService()")
    public Object interceptCompensableMethod(ProceedingJoinPoint pjp) throws Throwable {

        return compensableTransactionInterceptor.interceptCompensableMethod(pjp);
    }

    public abstract int getOrder();
}
