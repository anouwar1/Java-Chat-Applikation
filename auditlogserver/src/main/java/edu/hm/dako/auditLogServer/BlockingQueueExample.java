package edu.hm.dako.auditLogServer;


import edu.hm.dako.common.AuditLogPDU;

import java.util.concurrent.LinkedBlockingQueue;

public class BlockingQueueExample {

    public static void main(String[] args) throws Exception {

        LinkedBlockingQueue<AuditLogPDU> queue = new LinkedBlockingQueue<AuditLogPDU>();

        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);

        new Thread(producer).start();
        new Thread(consumer).start();

        Thread.sleep(4000);
    }
}

class Producer implements Runnable {

    protected LinkedBlockingQueue<AuditLogPDU> queue = null;

    public Producer(LinkedBlockingQueue<AuditLogPDU> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            Thread.currentThread().setName("Producer");
            AuditLogPDU pdu = new AuditLogPDU();
            queue.put(pdu);
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + ", Queue-Laenge: " + queue.size());
            Thread.sleep(5000);
            queue.put(pdu);
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + ", Queue-Laenge: " + queue.size());
            Thread.sleep(5000);
            queue.put(pdu);
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + ", Queue-Laenge: " + queue.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {

    protected LinkedBlockingQueue<AuditLogPDU> queue = null;

    public Consumer(LinkedBlockingQueue<AuditLogPDU> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            Thread.currentThread().setName("Consumer");
            System.out.println(queue.take());
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + ", Queue-Laenge: " + queue.size());
            Thread.sleep(5000);
            System.out.println(queue.take());
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + ", Queue-Laenge: " + queue.size());
            Thread.sleep(5000);
            System.out.println(queue.take());
            System.out.println("Thread: " + Thread.currentThread().getName()
                    + ", Queue-Laenge: " + queue.size());
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}