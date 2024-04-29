package com.kllxlxlk.juc;

import org.junit.Test;

import java.util.concurrent.Semaphore;

/**
 * 提供一组信号量，获取到信号量的线程才能执行。比如数据库连接就可以使用 Semaphore 控制连接数量。
 */
public class SemaphoreTest {

    /**
     * 获取到信号量后开始执行任务，执行完任务后归还信号量。
     */
    @Test
    public void normalUsage() {
        // 此信号量最多允许 3 个线程同时执行，可以观察日志是否如此。
        Semaphore semaphore = new Semaphore(3);

        // 创建一个需要获取信号量的任务
        Runnable task = () -> {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + ": 获取到许可证，执行任务...");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + ": 任务完成，释放许可证...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        };

        // 创建 50 个线程执行任务
        for (int i = 1; i <= 50; i++) {
            new Thread(task).start();
        }

        // 主线程等待一定时间
        try {
            Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监控信号量状态，包括：
     * hasQueuedThreads()，是否有线程在等待获取信号量
     * getQueueLength()，  等待获取信号量的线程数量
     * availablePermits()，当前可用的信号量个数
     */
    @Test
    public void monitorSemaphoreStatus() throws InterruptedException {
        // 此信号量最多允许 3 个线程同时执行，可以观察日志是否如此。
        Semaphore semaphore = new Semaphore(3);

        // 创建监控信号量的线程，并等待线程执行
        new Thread(() -> {
            while (true) {
                System.out.println("是否有线程在等待获取信号量：" + semaphore.hasQueuedThreads());
                System.out.println("等待获取信号量的线程数量：" + semaphore.getQueueLength());
                System.out.println("当前可用的信号量个数：" + semaphore.availablePermits());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(500);

        // 创建一个需要获取信号量的任务
        Runnable task = () -> {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + ": 获取到许可证，执行任务...");
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName() + ": 任务完成，释放许可证...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        };

        // 创建 50 个线程执行任务
        for (int i = 1; i <= 50; i++) {
            new Thread(task).start();
        }

        // 主线程等待一定时间
        Thread.sleep(30 * 1000);
    }

    /**
     * drainPermits()，将当前剩余的信号量全部回收，返回回收的信号量个数
     */
    @Test
    public void testDrainPermits() throws InterruptedException {
        // 此信号量最多允许 3 个线程同时执行，可以观察日志是否如此。
        Semaphore semaphore = new Semaphore(3);

        // 创建一个需要获取信号量的任务
        Runnable task = () -> {
            try {
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + ": 获取到许可证，执行任务...");
                Thread.sleep(3 * 1000);
                System.out.println(Thread.currentThread().getName() + ": 任务完成，释放许可证...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        };

        // 创建 2 个线程执行任务
        new Thread(task).start();
        new Thread(task).start();

        // 等待刚新建的两个线程执行起来
        Thread.sleep(1000);

        System.out.println("当前可用的信号量个数：" + semaphore.availablePermits());

        int a = semaphore.drainPermits();
        System.out.println("调用 drainPermits 将当前可用的信号量收回，收回了 " + a + " 个信号量");

        System.out.println("当前可用的信号量个数：" + semaphore.availablePermits());

        Thread.sleep(3 * 1000);

        System.out.println("当前可用的信号量个数：" + semaphore.availablePermits());
    }
}
