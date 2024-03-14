package com.kllxlxlk.juc.avo.visibility;


/**
 * 此例子用来说明指令重排对 java 程序可见性的影响
 *
 * 如果 x = 0，则说明 x = b 在 b = 1 前面执行，则 y = 1。
 * 如果 y = 0，则说明 y = a 在 a = 1 前面执行，则 x = 1。
 * 所以（x，y）只可能出现三种情况：（0，1）（1，0）（1，1）
 *
 * 但重复运行代码，竟然会出现（x，y）=（0，0）的情况，简直不可思议
 *
 * 其实原因非常简单，但看线程一或者线程二，里面的两条语句交换执行顺序对程序没有任何影响，所以在执行的过程中，可能会出于性能方面的
 * 考虑，交换语句的执行顺序。语句在重排之后可能会出现如下执行情况：
 *
 * 线程一            线程二
 * x = b
 *                   y = a
 * a = 1
 *                   b = 0
 *
 * 在这种情况出现时，似乎线程一对变量 a 的修改对线程二是不可见的，当然你也可以说成线程二对变量 b 的修改似乎对线程一是不可见的，
 * 这就是指令重排导致的代码可见性问题。如果你想避免这个问题，在声明 x, y, a, b 时应该加上 volatile 关键字，这样就可以避免指令
 * 被重排
 */
public class ReorderingTest {

    private static int x, y, a, b;

    public static void main(String[] args) throws InterruptedException {
        int number = 1;

        while (true) {
            x = y = a = b = 0;

            Thread thread1 = new Thread(() -> {
                a = 1;
                x = b;
            });

            Thread thread2 = new Thread(() -> {
                b = 1;
                y = a;
            });

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

            if (x == 0 && y == 0) {
                System.out.println("第 " + number + " 次实验，x = " + x + "，y = " + y);
            }

            number++;
        }
    }
}
