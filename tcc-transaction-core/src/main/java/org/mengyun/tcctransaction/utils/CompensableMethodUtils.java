package org.mengyun.tcctransaction.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.common.MethodType;

import java.lang.reflect.Method;

/**
 * 可补偿方法工具类.
 *
 * Created by changmingxie on 11/21/15.
 */
public class CompensableMethodUtils {

    /**
     * 获取有{@link Compensable} 注解的{@code Method} 对象.
     * @param pjp
     * @return
     */
    public static Method getCompensableMethod(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        if (method.getAnnotation(Compensable.class) == null) {
            try {
                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method;
    }

    /**
     * 计算方法类型.
     * @param propagation
     * @param isTransactionActive
     * @param transactionContext
     * @return
     */
    public static MethodType calculateMethodType(Propagation propagation, boolean isTransactionActive, TransactionContext transactionContext) {

        if ((propagation.equals(Propagation.REQUIRED) && !isTransactionActive && transactionContext == null) ||
                propagation.equals(Propagation.REQUIRES_NEW)) {
            return MethodType.ROOT;
        } else if ((propagation.equals(Propagation.REQUIRED) || propagation.equals(Propagation.MANDATORY)) && !isTransactionActive && transactionContext != null) {
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }

    /**
     * 计算方法类型.
     * @param transactionContext
     * @param isCompensable
     * @return
     */
    public static MethodType calculateMethodType(TransactionContext transactionContext, boolean isCompensable) {

        if (transactionContext == null && isCompensable) {
            //isRootTransactionMethod
            return MethodType.ROOT;
        } else if (transactionContext == null && !isCompensable) {
            //isSoaConsumer
            return MethodType.CONSUMER;
        } else if (transactionContext != null && isCompensable) {
            //isSoaProvider
            return MethodType.PROVIDER;
        } else {
            return MethodType.NORMAL;
        }
    }

    /**
     * 获取事务上下文参数的位置。
     * @param parameterTypes
     * @return
     */
    public static int getTransactionContextParamPosition(Class<?>[] parameterTypes) {

        int position = -1;

        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(org.mengyun.tcctransaction.api.TransactionContext.class)) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * 从参数获取事务上下文.
     * @param args
     * @return
     */
    public static TransactionContext getTransactionContextFromArgs(Object[] args) {

        TransactionContext transactionContext = null;

        for (Object arg : args) {
            if (arg != null && org.mengyun.tcctransaction.api.TransactionContext.class.isAssignableFrom(arg.getClass())) {

                transactionContext = (org.mengyun.tcctransaction.api.TransactionContext) arg;
            }
        }

        return transactionContext;
    }
}
