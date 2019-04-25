package com.renewable.gateway.task.config;

import com.renewable.gateway.task.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.ScheduledMethodRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description：
 * @Author: jarry
 */
@Slf4j
public class ScheduleConfig implements SchedulingConfigurer, AsyncConfigurer {

    //实现ScheduledTaskRegistrar中的configureTasks方法，设置调度器
//    public void configureTask(ScheduledTaskRegistrar taskRegistrar){
//        //创建一个线程池调度器
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        //设置线程池容量
//        scheduler.setPoolSize(20);
//        //线程名前缀
//        scheduler.setThreadNamePrefix("DataTaskDispatch_");
//        //等待时长
//        scheduler.setAwaitTerminationSeconds(60);
//        //当线程池被调用shutdown时对线程的策略
//        scheduler.setWaitForTasksToCompleteOnShutdown(true);
//        //设置任务注册器的调度器
//        taskRegistrar.setTaskScheduler(scheduler);
//    }

    //维持一个自增长的任务id（Atomic确保原子性）
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();
    //全局调度器
    private ThreadPoolTaskScheduler taskScheduler;
    //维护一个CronTask和ScheduleFuture的Map，用于管理Spring托管的cronTask     //之后还应该同时取出triggerTasks、cronTasks、fixedRateTasks、fixedDelayTasks 以保证托管任务的完整性
    private Map<CronTask, ScheduledFuture> cronTaskScheduledFutureMap;
    //通过自定义的Task(pojo)，提供用户查看。（VO可用)
    private List<Task> taskList;

    /**
     * 提取出创建线程池调度器的代码
     *
     * @return 线程池
     */
    public ThreadPoolTaskScheduler taskScheduler() {
        //创建一个线程池调度器
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        //设置线程池容量
        scheduler.setPoolSize(20);
        //线程名前缀
        scheduler.setThreadNamePrefix("DataTaskDispatch_");
        //等待时长
        scheduler.setAwaitTerminationSeconds(60);
        //当线程池被调用shutdown时对线程的策略
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    //实现ScheduledTaskRegistrar中的configureTasks方法，设置调度器
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskScheduler = new ThreadPoolTaskScheduler();//taskScheduler();
        cronTaskScheduledFutureMap = new HashMap<>();
        taskList = new ArrayList<>();
    }


    /**
     * 获取任务列表（用户查看的）
     *
     * @return
     */
    public List<Task> getTaskList() {
        return taskList;
    }

    /**
     * 进行任务的编辑。以传入任务的triggerName作为唯一标识编辑已经存在的任务
     *
     * @param
     */
    public void editTask(Task task) {
        //如果当前的任务调度器或者说任务列表为空，那么便不需要进行进行任何操作（因为这两者为空，意味着不存在可运行任务
        if (taskScheduler == null || cronTaskScheduledFutureMap == null)
            return;

        cronTaskScheduledFutureMap.
                //对任务队列进行遍历。    //对每一个当前已经参与调度的任务进行判断，取消triggerName与传入Task的triggerName相同的任务
                        forEach((
                        //采用lambda函数进行同时多个数据传入处理
                        (cronTask, scheduledFuture) -> {
                            //确保cronTask为ScheduledMethodRunnable类型
                            if (cronTask.getRunnable() instanceof ScheduledMethodRunnable) {
                                ScheduledMethodRunnable scheduledMethodRunnable = (ScheduledMethodRunnable) cronTask.getRunnable();
                                log.info(scheduledMethodRunnable.getMethod().toGenericString());
                                String methodName = scheduledMethodRunnable.getMethod().toGenericString();
                                if (task.getTriggerName().equals(methodName) && scheduledFuture.cancel(true)) {
                                    switch (task.getState()) {
                                        //当前传入的任务要求处于调度状态时，立即启动调度
                                        case RUN:
                                            cronTaskScheduledFutureMap.put(cronTask, taskScheduler.schedule(scheduledMethodRunnable, new CronTrigger(task.getCron())));
                                            break;
                                    }
                                }
                            }
                        }
                ));
        System.out.println("cronTask:" + task.getCron());
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskScheduler();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}
