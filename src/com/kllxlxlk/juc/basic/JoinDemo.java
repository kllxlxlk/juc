package com.kllxlxlk.juc.basic;

import org.junit.Test;

/**
 * 1、当前线程等待指定线程运行结束后运行。
 * 2、如果设置了超时时间，即使指定线程未能运行结束，当前线程也会继续往下执行。
 * 3、当前线程等待指定线程实际上是调用了当前线程对象的 wait 方法。如果当前线程被调用了 interrupt 方法会产生中断异常。
 * 4、如果等待的线程还未开始执行，当前线程会直接继续往下执行。
 */
public class JoinDemo {

    @Test
    public void test01() {
        Runnable runnable = () -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(i);
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        // 等待线程执行完毕，如果 3s 内没有执行完毕，则放弃等待
        try {
            thread.join(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("已经等待了 3s 钟");

        // 等待线程执行完毕
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("线程执行完毕");
    }


    /**
     * 等待其他线程执行完毕的线程会进入等待状态，如果被调用 interrupt 方法会产生中断异常
     */
    @Test
    public void test02() {
        Thread thread1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " 开始");
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + i);
            }
            System.out.println(Thread.currentThread().getName() + " 结束");
        }, "线程一");

        Thread thread2 = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + " 开始等待");
                thread1.join();
                System.out.println(Thread.currentThread().getName() + " 等待完毕");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "线程二");

        thread1.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread2.start();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread2.interrupt();
    }


    /**
     * 如果等待的线程还未开始执行，当前线程会直接继续往下执行。
     */
    @Test
    public void test03() throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " 开始");
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + i);
            }
            System.out.println(Thread.currentThread().getName() + " 结束");
        }, "线程一");

        System.out.println("等待线程一");
        thread.join();

        thread.start();

        System.out.println("主线程执行完毕");
    }

}
