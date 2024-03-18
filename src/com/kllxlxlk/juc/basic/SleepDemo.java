package com.kllxlxlk.juc.basic;


/**
 * 1、sleep 不会释放锁
 * 2、sleep 时被调用 interrupt 方法，会抛出中断异常，并且会清除线程的中断状态
 */
public class SleepDemo {

    public static int zhangAccountBalance = 100;

    public static int liAccountBalance = 100;

    public static void main(String[] args) throws InterruptedException {
        Runnable transfer = () -> {
            synchronized (SleepDemo.class) {
                System.out.println("transfer 开始运行");

                while (zhangAccountBalance > 0) {
                    // 监控中断状态，如果被设置为中断状态，终止线程
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }

                    zhangAccountBalance--;

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        System.out.println("线程睡眠期间被调用了 interrupt 方法，此时中断标志为：" + Thread.currentThread().isInterrupted());
                        zhangAccountBalance++;
                        break;
                    }

                    liAccountBalance++;

                    System.out.println("转账成功，张三 " + zhangAccountBalance + "，李四 " + liAccountBalance);
                }

                System.out.println("transfer 运行结束");
            }
        };

        Runnable r2 = () -> {
            synchronized (SleepDemo.class) {
                // 在 transfer 线程结束后才会开始运行，表明 sleep 不会释放锁
                System.out.println("r2 开始运行");
                System.out.println("r2 结束运行");
            }
        };

        Thread thread1 = new Thread(transfer);
        Thread thread2 = new Thread(r2);

        thread1.start();
        thread2.start();

        Thread.sleep(3000);

        thread1.interrupt();
    }
}
