package com.kllxlxlk.juc.basic;


import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1、interrupt()     中断线程
 * 2、isInterrupted() 判断线程是否被中断
 * 3、interrupted()   判断线程是否被中断，清除线程的中断状态
 */
public class InterruptDemo {

    public static int zhangAccountBalance = 10000000;

    public static int liAccountBalance = 10000000;

    public static void main(String[] args) throws InterruptedException {
        Runnable transfer = () -> {
            System.out.println("transfer 开始运行");

            while (zhangAccountBalance > 0) {
                // 如果被设置中断状态，睡眠 3 秒后继续执行
                if (Thread.interrupted()) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }

                zhangAccountBalance--;

                liAccountBalance++;

                System.out.println("转账成功，张三 " + zhangAccountBalance + "，李四 " + liAccountBalance);
            }

            System.out.println("transfer 运行结束");
        };

        Thread thread1 = new Thread(transfer);

        thread1.start();

        Thread.sleep(2000);

        // 中断线程
        thread1.interrupt();

        // 判断线程中断状态
        System.out.println("transfer 线程是否中断：" + thread1.isInterrupted());
    }
}
