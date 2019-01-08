package org.mengyun.tcctransaction.spring;

import org.aspectj.lang.annotation.Aspect;
import org.mengyun.tcctransaction.TransactionManager;
import org.mengyun.tcctransaction.interceptor.CompensableTransactionAspect;
import org.mengyun.tcctransaction.interceptor.CompensableTransactionInterceptor;
import org.mengyun.tcctransaction.spring.support.SpringTransactionConfigurator;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.springframework.core.Ordered;

/**
 * TCC补偿切面配置
 * <p>创建切面拦截器 {@link CompensableTransactionInterceptor}</p>
 *
 * Created by changmingxie on 10/30/15.
 */
@Aspect
public class ConfigurableTransactionAspect extends CompensableTransactionAspect implements Ordered {

    private TransactionConfigurator transactionConfigurator;

    /**
     * 初始化
     */
    public void init() {

        TransactionManager transactionManager = transactionConfigurator.getTransactionManager();

        CompensableTransactionInterceptor compensableTransactionInterceptor = new CompensableTransactionInterceptor();
        compensableTransactionInterceptor.setTransactionManager(transactionManager);
        compensableTransactionInterceptor.setDelayCancelExceptions(transactionConfigurator.getRecoverConfig().getDelayCancelExceptions());

        this.setCompensableTransactionInterceptor(compensableTransactionInterceptor);
    }

    /**
     * 通知顺序(默认：最高优先级).
     * 在“进入”连接点的情况下，最高优先级的通知会先执行（所以给定的两个前置通知中，优先级高的那个会先执行）。
     * 在“退出”连接点的情况下，最高优先级的通知会最后执行。
     * 当定义在不同的切面里的两个通知都需要在一个相同的连接点中运行， 那么除非你指定，否则执行的顺序是未知的，你可以通过指定优先级来控制执行顺序。
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 设置事务配置器
     *
     * @see SpringTransactionConfigurator
     * @param transactionConfigurator
     */
    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
