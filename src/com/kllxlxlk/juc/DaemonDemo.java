package com.kllxlxlk.juc;

/**
 * 当程序内只剩下守护线程，虚拟机会退出执行。
 * 守护线程必须在线程开始运行之前设置。否则程序会出现 IllegalThreadStateException 异常，并且线程以用户线程运行。
 */
public class DaemonDemo {

    public static Runnable daemon = () -> {
        while (true) {
            System.out.println("I'm alive");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public static void test01() throws InterruptedException {
        Thread thread = new Thread(daemon);
        thread.setDaemon(true);
        thread.start();

        Thread.sleep(5000);
    }

    public static void test02() throws InterruptedException {
        Thread thread = new Thread(daemon);
        thread.start();
        thread.setDaemon(true);

        Thread.sleep(5000);
    }

    public static void main(String[] args) throws InterruptedException {
        // test01();
        test02();
    }
}
