package com.kllxlxlk.juc;

/**
 * stop 方法会直接终止线程
 * stop 已废弃，不建议再使用
 */
public class StopThreadDemo {

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            System.out.println("线程开始执行");

            for (int i = 0; i < 100; i++) {
                System.out.println("执行第 " + i + " 次循环");

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("线程结束执行");
        };

        Thread thread = new Thread(runnable);
        thread.start();

        Thread.sleep(3 * 1000);

        thread.stop();
    }

}
