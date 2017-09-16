package com.lucianms.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author izarooni
 */
public final class TaskExecutor {

    private static final AtomicInteger runningTaskId = new AtomicInteger(0);
    private static final List<TaskRunnable> tasks = new ArrayList<>();
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        private int runningTaskId = 0;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ETask" + (runningTaskId++));
        }
    });

    public static synchronized void cancelTask(int taskId) {
        tasks.stream().filter(task -> (task.getTaskId() == taskId) && !task.isCanceled()).forEach(TaskRunnable::cancel);
    }

    public static synchronized TaskRunnable executeLater(Runnable runnable, long delay) {
        return setupId(executor.schedule(runnable, delay, TimeUnit.MILLISECONDS));
    }

    public static synchronized TaskRunnable executeRepeating(Runnable runnable, long delay, long repeatingDelay) {
        return setupId(executor.scheduleAtFixedRate(runnable, delay, repeatingDelay, TimeUnit.MILLISECONDS));
    }

    private static TaskRunnable setupId(ScheduledFuture<?> schedule) {
        final int taskId = runningTaskId.getAndIncrement();
        TaskRunnable task = new TaskRunnable(schedule) {
            @Override
            public int getTaskId() {
                return taskId;
            }
        };
        tasks.add(task);
        return task;
    }
}
