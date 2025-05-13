import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

public class QuicksortEngine {
    private final int maxThreads;
    private final List<WorkerThread> threadPool;
    private final LinkedList<SortJob> jobQueue;

    public QuicksortEngine(int maxThreads) {
        this.maxThreads = maxThreads;
        this.threadPool = new LinkedList<>();
        this.jobQueue = new LinkedList<>();
    }

    public void quickSort(List<Car> carList) {
        submitJob(new SortJob(carList, 0, carList.size() - 1, this));
//        long startTime;
//        synchronized (this) {
//            startTime = System.currentTimeMillis();
//
//        }
//        long endTime = System.currentTimeMillis();
//        System.out.println("List " + " sorted in " + (endTime - startTime) + " ms.");
    }

    /*
        * Adds SortJob to queue and notifies threads waiting on it, initializes new threads if there is room in the pool
     */
    public synchronized void submitJob(SortJob job) {
        synchronized (jobQueue) {
            jobQueue.add(job);
            jobQueue.notify();
        }
        if (threadPool.size() < maxThreads) {
            WorkerThread worker = new WorkerThread();
            threadPool.add(worker);
            worker.start();
        }
    }

    /*
        * Waits for all jobs to finish
     */
    public void waitForCompletion() {
        while (!jobQueue.isEmpty() || !areThreadsWaiting()) {
            try {
                System.out.println("Waiting...");
                Thread.sleep(100); // Check periodically
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
        * Waits for all jobs to finish and then clears the thread pool
     */
    public synchronized void shutdown() {
//        for (WorkerThread thread : threadPool) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (WorkerThread thread : threadPool) {
//            thread.interrupt();
//        }

        System.out.println("Shutting down thread pool...");
        threadPool.clear();
    }

    private boolean areThreadsWaiting() {
        for (WorkerThread thread : threadPool) {
            if (thread.isAlive() && thread.getState() != Thread.State.WAITING) {
                return false;
            }
        }
        return true;
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            SortJob job;
            while (true) {
                synchronized (jobQueue) {
                    while (jobQueue.isEmpty()) {
                        try {
                            jobQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    job = jobQueue.poll();
                }
                try {
                    job.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}