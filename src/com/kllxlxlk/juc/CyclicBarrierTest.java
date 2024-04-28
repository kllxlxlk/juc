package com.kllxlxlk.juc;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 允许一组线程相互等待，达到一个共同的屏障点再继续执行
 */
public class CyclicBarrierTest {

    /**
     * 正常用法，一组线程相互等待，到达了同一个屏障点后继续执行
     */
    @Test
    public void testNormalUsage() {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("所有任务已抵达同一屏障点，继续向下执行"));

        System.out.println("预期线程数：" + barrier.getParties());

        Runnable task = () -> {
            try {
                String threadName = Thread.currentThread().getName();
                System.out.println("线程 " + threadName + " 正在工作");
                Thread.sleep((long) (Math.random() * 5000)); // 模拟工作
                System.out.println("线程 " + threadName + " 完成工作，等待其他线程...");
                barrier.await();  // 等待其他线程到达屏障
                System.out.println("线程 " + threadName + " 继续执行");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(task, "线程 1").start();
        new Thread(task, "线程 2").start();
        new Thread(task, "线程 3").start();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * isBroken()，判断 CyclicBarrier 是否处于损坏状态。
     * 当一个线程在等待时被中断或者等待超时，CyclicBarrier 会进入到损坏状态。
     * CyclicBarrier 被损坏时，正在等待的线程和后续进入等待的线程，都会获取到 CyclicBarrier 损坏异常
     */
    @Test
    public void testIsBroken() {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("所有任务已抵达同一屏障点，继续向下执行"));

        // 监控 CyclicBarrier 是否损坏
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("CyclicBarrier 是否损坏：" + barrier.isBroken());
            }
        }).start();

        // 睡眠 0.5s，等待监控线程正式启动
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 创建工作线程
        Runnable task = () -> {
            try {
                Thread.sleep((long) (Math.random() * 5000));
                System.out.println("线程 " + Thread.currentThread().getName() + " 完成工作，等待其他线程...");
                // 只等待 1s，所以很容易出现 CyclicBarrier 损坏的情况
                barrier.await(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 在等待的过程中被中断");
            } catch (TimeoutException e) {
                System.out.println(Thread.currentThread().getName() + " 等待时间超过 1s");
            } catch (BrokenBarrierException e) {
                System.out.println(Thread.currentThread().getName() + " 在等待的过程中出现 CyclicBarrier 损坏的情况");
            }
        };

        new Thread(task, "线程 1").start();
        new Thread(task, "线程 2").start();
        new Thread(task, "线程 3").start();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * getNumberWaiting()，到达屏障点的线程数量。当所有的线程都达到屏障点后，线程会继续执行，此时到达屏障点的线程数量会重新变回 0
     */
    @Test
    public void testGetNumberWaiting() {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("所有任务已抵达同一屏障点，继续向下执行"));

        // 监控到达屏障点的线程数量
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("到达屏障点的线程数量：" + barrier.getNumberWaiting());
            }
        }).start();

        // 睡眠 0.5s，等待监控线程正式启动
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 创建工作线程
        Runnable task = () -> {
            try {
                Thread.sleep((long) (Math.random() * 5000));
                System.out.println("线程 " + Thread.currentThread().getName() + " 完成工作，等待其他线程...");
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(task, "线程 1").start();
        new Thread(task, "线程 2").start();
        new Thread(task, "线程 3").start();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * reset()，重置 CyclicBarrier 的状态。
     * 当 CyclicBarrier 出现损坏或 CyclicBarrier 被设计为反复使用，则可以通过此方法重置状态。
     * 当 CyclicBarrier 的 reset() 方法被调用时，正处于等待状态的线程会收到 CyclicBarrier 损坏异常。
     */
    @Test
    public void testReset01() {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("所有任务已抵达同一屏障点，继续向下执行"));

        // 监控到达屏障点的线程数量
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("到达屏障点的线程数量：" + barrier.getNumberWaiting());
            }
        }).start();

        // 睡眠 0.5s，等待监控线程正式启动
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 创建工作线程
        Runnable task = () -> {
            try {
                Thread.sleep((long) (Math.random() * 5000));
                System.out.println("线程 " + Thread.currentThread().getName() + " 完成工作，等待其他线程...");
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(task, "线程 1").start();
        new Thread(task, "线程 2").start();
        new Thread(task, "线程 3").start();
        new Thread(task, "线程 4").start();
        new Thread(task, "线程 5").start();
        new Thread(task, "线程 6").start();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testReset02() {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> System.out.println("所有任务已抵达同一屏障点，继续向下执行"));

        // 创建工作线程
        Runnable task = () -> {
            try {
                Thread.sleep((long) (Math.random() * 5000));
                System.out.println("线程 " + Thread.currentThread().getName() + " 完成工作，等待其他线程...");
                barrier.await();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 在等待的过程中被中断");
            } catch (BrokenBarrierException e) {
                System.out.println(Thread.currentThread().getName() + " 在等待的过程中出现 CyclicBarrier 损坏的情况");
            }
        };

        new Thread(task, "线程 1").start();
        new Thread(task, "线程 2").start();

        // 两个线程到达屏障开始等待时，调用 reset 方法
        try {
            Thread.sleep(6 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        barrier.reset();

        new Thread(task, "线程 3").start();
        new Thread(task, "线程 4").start();
        new Thread(task, "线程 5").start();

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
