import java.util.LinkedList;
import java.util.List;

import static java.lang.Thread.sleep;

public class QuicksortEngine {
    private final int maxThreads;
    private final List<WorkerThread> threadPool;
    private final LinkedList<SortJob> jobQueue;
    private boolean isShutdown;

    public QuicksortEngine(int maxThreads) {
        this.maxThreads = maxThreads;
        this.threadPool = new LinkedList<>();
        this.jobQueue = new LinkedList<>();
        this.isShutdown = false;
    }

    public void quickSort(List<Car> carList) {
        submitJob(new SortJob(carList, 0, carList.size() - 1, this));
    }

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

    public void awaitTermination() {
        while (!jobQueue.isEmpty() || !areThreadsWaiting()) {
            try {
                System.out.println("Waiting for threads to finish...");
                Thread.sleep(100); // Check periodically
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void shutdown() {
        System.out.println("All threads have finished.");
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