package com.kllxlxlk.juc;

import org.junit.Test;

/**
 * suspend() 方法会使线程进入阻塞状态，在调用 resume() 方法后会重新开始执行。
 *
 * 此方法目前已废弃，主要有两个原因：
 * 1、线程阻塞后不会释放资源，如果 resume() 方法在 suspend() 方法后执行，阻塞的线程会一直持有资源，其它需要该资源的线程都会被阻塞
 * 2、线程在阻塞后状态仍是 Runnable 状态
 */
public class SuspendDemo {

    private final Object object = new Object();

    private Runnable suspend = () -> {
        synchronized (object) {
            System.out.println(Thread.currentThread().getName() + " 开始");
            System.out.println(Thread.currentThread().getName() + " 进入阻塞");
            Thread.currentThread().suspend();
            System.out.println(Thread.currentThread().getName() + " 重新开始执行");
            System.out.println(Thread.currentThread().getName() + " 结束");
        }
    };


    /**
     * suspend 会阻塞线程但不会释放资源
     */
    @Test
    public void test01() throws InterruptedException {
        Thread thread1 = new Thread(suspend, "线程一");
        Thread thread2 = new Thread(suspend, "线程二");

        thread1.start();
        thread2.start();

        Thread.sleep(3 * 1000);
        thread1.resume();
        thread2.resume();

        Thread.sleep(3 * 1000);
        thread2.resume();

        thread1.join();
        thread2.join();
    }
}
