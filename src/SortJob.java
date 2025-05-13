import java.util.List;

public class SortJob implements Runnable {
    private List<Car> cars;
    private int low;
    private int high;
    private QuicksortEngine engine;

    public SortJob(List<Car> cars, int low, int high, QuicksortEngine engine) {
        this.cars = cars;
        this.low = low;
        this.high = high;
        this.engine = engine;
    }

    @Override
    public void run() {
        if (low < high) {
            int pivotIndex = partition(cars, low, high);
            engine.submitJob(new SortJob(cars, low, pivotIndex - 1, engine));
            engine.submitJob(new SortJob(cars, pivotIndex + 1, high, engine));

//            System.out.println("Working from " + Thread.currentThread().getName());
        }
    }

    private int partition(List<Car> cars, int low, int high) {
        Car pivot = cars.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (cars.get(j).compareTo(pivot) <= 0) {
                i++;
                swap(cars, i, j);
            }
        }
        swap(cars, i + 1, high);
        return i + 1;
    }

    private void swap(List<Car> cars, int i, int j) {
        Car temp = cars.get(i);
        cars.set(i, cars.get(j));
        cars.set(j, temp);
    }
}