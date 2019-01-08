package org.mengyun.tcctransaction.spring;

import org.aspectj.lang.annotation.Aspect;
import org.mengyun.tcctransaction.interceptor.CompensableTransactionInterceptor;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorAspect;
import org.mengyun.tcctransaction.interceptor.ResourceCoordinatorInterceptor;
import org.mengyun.tcctransaction.support.TransactionConfigurator;
import org.springframework.core.Ordered;

/**
 * 协调器切面配置
 * <p>创建切面拦截器 {@link ResourceCoordinatorInterceptor}</p>
 *
 * Created by changmingxie on 11/8/15.
 */
@Aspect
public class ConfigurableCoordinatorAspect extends ResourceCoordinatorAspect implements Ordered {

    private TransactionConfigurator transactionConfigurator;

    public void init() {

        ResourceCoordinatorInterceptor resourceCoordinatorInterceptor = new ResourceCoordinatorInterceptor();
        resourceCoordinatorInterceptor.setTransactionManager(transactionConfigurator.getTransactionManager());
        this.setResourceCoordinatorInterceptor(resourceCoordinatorInterceptor);
    }

    /**
     * 通知顺序.
     * <p>最高优先级+1（值较低的那个有更高的优先级）</p>
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    public void setTransactionConfigurator(TransactionConfigurator transactionConfigurator) {
        this.transactionConfigurator = transactionConfigurator;
    }
}
