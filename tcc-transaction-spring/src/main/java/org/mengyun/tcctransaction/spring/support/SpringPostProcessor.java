package org.mengyun.tcctransaction.spring.support;

import org.mengyun.tcctransaction.support.BeanFactory;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * {@link ContextRefreshedEvent} 事件监听器
 * spring 容器初始化完成时回调，注册{@link BeanFactory} 到 {@link FactoryBuilder#beanFactories}
 *
 * Created by changmingxie on 11/20/15.
 */
public class SpringPostProcessor implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

        if (applicationContext.getParent() == null) {
            FactoryBuilder.registerBeanFactory(applicationContext.getBean(BeanFactory.class));
        }
    }
}
