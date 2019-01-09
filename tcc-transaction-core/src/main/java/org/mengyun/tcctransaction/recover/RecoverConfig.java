package org.mengyun.tcctransaction.recover;

import java.util.Set;

/**
 * 事务恢复配置接口.
 *
 * Created by changming.xie on 6/1/16.
 */
public interface RecoverConfig {

    /**
     * 获取最大重试次数
     * @return
     */
    public int getMaxRetryCount();

    /**
     * 事务恢复的时间间隔,单位：秒.
     * @return
     */
    public int getRecoverDuration();

    /**
     * 获取定时任务规则表达式.
     * @return
     */
    public String getCronExpression();

    /**
     * @return 延迟取消异常集合
     */
    public Set<Class<? extends Exception>> getDelayCancelExceptions();

    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayRecoverExceptions);

    public int getAsyncTerminateThreadPoolSize();
}
