package com.kllxlxlk.juc;

import org.junit.Test;

public class ThreadGroupDemo {

    @Test
    public void test01() throws InterruptedException {
        Runnable runnable = () -> {
            while (true) {
                String threadGroupName = Thread.currentThread().getThreadGroup().getName();
                String threadName = Thread.currentThread().getName();
                System.out.println("I am " + threadGroupName + "-" + threadName);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ThreadGroup threadGroup = new ThreadGroup("PrintGroup");
        Thread t1 = new Thread(threadGroup, runnable, "T1");
        Thread t2 = new Thread(threadGroup, runnable, "T2");

        t1.start();
        t2.start();

        Thread.sleep(3000);
        System.out.println("threadGroupName 的活动中的线程数量：" + threadGroup.activeCount());
        threadGroup.list();

        threadGroup.stop();
        Thread.sleep(500);
        System.out.println("threadGroupName 的活动中的线程数量：" + threadGroup.activeCount());
        threadGroup.list();
    }

}
