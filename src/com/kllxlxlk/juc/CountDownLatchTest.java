package com.kllxlxlk.juc;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class CountDownLatchTest {

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(10);
        System.out.println("总数量：" + countDownLatch.getCount());

        // 开启 10 个线程处理事务
        for (int i = 1; i <= 10; i++) {
            int finalI = i;
            Thread thread = new Thread(() -> {
                // 睡眠，假装在处理什么事情
                try {
                    Thread.sleep(finalI * 1000);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " 被中断");
                }

                System.out.println(Thread.currentThread().getName() + " 执行完毕");
                countDownLatch.countDown();
            }, "线程" + i);

            thread.start();
        }

        // 当 10 个线程都完成或者等待够 3s，才会执行
        new Thread(() -> {
            try {
                countDownLatch.await(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 被中断");
            }
            System.out.println("已等待够 3s，当前未完成线程数：" + countDownLatch.getCount());
        }, "计时监控线程").start();

        // 当 10 个线程都完成后，才会执行
        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " 被中断");
            }
            System.out.println("所有线程执行完毕");
        }, "监控线程").start();
    }
}
