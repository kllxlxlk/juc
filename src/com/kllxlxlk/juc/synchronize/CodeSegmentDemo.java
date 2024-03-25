package com.kllxlxlk.juc.synchronize;

/**
 * 锁住一段代码
 * <p>
 * 1、测试是否能锁住
 * 2、测试是否是可重入锁
 */
public class CodeSegmentDemo {

    public void countdown(String name, int times) {
        synchronized (this) {
            if (times == 0) {
                return;
            }
            System.out.println(name + "倒计时：" + times);
            countdown(name, times - 1);
        }
    }

    public void repeat(String content, int times) {
        synchronized (this) {
            if (times == 0) {
                return;
            }
            System.out.println("重复：" + content + "（剩余 " + (times - 1) + " 次）");
            repeat(content, times - 1);
        }
    }

    public static void main(String[] args) {
        CodeSegmentDemo demo = new CodeSegmentDemo();

        Runnable runnable1 = () -> {
            demo.countdown("火箭发射", 100);
        };

        Runnable runnable2 = () -> {
            demo.repeat("今天全体加班", 100);
        };

        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable1);
        Thread thread3 = new Thread(runnable2);
        Thread thread4 = new Thread(runnable2);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
