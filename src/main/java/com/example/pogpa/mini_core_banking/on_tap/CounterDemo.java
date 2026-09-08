package com.example.pogpa.mini_core_banking.on_tap;
/*
* Tạo chương trình có 10 thread, mỗi thread tăng counter lên 1 10.000 lần.
* */
public class CounterDemo {
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++){
            threads[i] = new Thread( () -> {
                for (int j = 0; j < 10_000; j++) {
                    synchronized (CounterDemo.class) { counter++; }
                }
            });
            threads[i].start();
        }

        for(Thread thread : threads){
            thread.join();
        }


        System.out.println("Expected: 100000");
        System.out.println("Actual:   " + counter);

    }
}
