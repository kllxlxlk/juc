package com.kllxlxlk.juc.synchronize;

/**
 * 使用 synchronized 修饰普通方法，本质上和使用 synchronized (this) 锁住整个方法等价
 * 使用 synchronized 修饰静态方法，本质上和使用 synchronized (this.getClass()) 锁住整个方法等价
 */
public class MethodDemo {

    public static synchronized void staticCountdown(String name, int times) {
        if (times == 0) {
            return;
        }
        System.out.println("静态方法// " + name + "倒计时：" + times);
        staticCountdown(name, times - 1);
    }

    public static synchronized void staticRepeat(String content, int times) {
        if (times == 0) {
            return;
        }
        System.out.println("静态方法// " + "重复：" + content + "（剩余 " + (times - 1) + " 次）");
        staticRepeat(content, times - 1);
    }

    public synchronized void countdown(String name, int times) {
        if (times == 0) {
            return;
        }
        System.out.println(name + "倒计时：" + times);
        countdown(name, times - 1);
    }

    public synchronized void repeat(String content, int times) {
        if (times == 0) {
            return;
        }
        System.out.println("重复：" + content + "（剩余 " + (times - 1) + " 次）");
        repeat(content, times - 1);
    }

    public static void main(String[] args) {
        MethodDemo demo = new MethodDemo();

        Runnable runnable1 = () -> {
            demo.countdown("火箭发射", 100);
        };

        Runnable runnable2 = () -> {
            demo.repeat("今天全体加班", 100);
        };

        Runnable runnable3 = () -> {
            MethodDemo.staticCountdown("火箭发射", 100);
        };

        Runnable runnable4 = () -> {
            MethodDemo.staticRepeat("今天全体加班", 100);
        };

        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        Thread thread3 = new Thread(runnable3);
        Thread thread4 = new Thread(runnable4);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
