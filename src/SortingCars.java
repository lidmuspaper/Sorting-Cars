import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.security.SecureRandom;

public class SortingCars {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String[] DESTINATIONS = {"Los Angeles", "Houston", "New Orleans", "Miami", "New York"};
    private static final String VALID_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static void main(String[] args) {
        int numberOfLists = 3;
        int carsPerList = 100000;
        int maxThreads = 10;

        QuicksortEngine engine = new QuicksortEngine(maxThreads);
        List<List<Car>> carLists = new ArrayList<>();

        for (int i = 0; i < numberOfLists; i++) {
            List<Car> cars = generateCars(carsPerList);
            carLists.add(cars);
            saveToFile(cars, "cars-" + (i + 1) + ".txt");
        }

        System.out.println("Begin sorting async...");
        for (List<Car> carList : carLists) {
            engine.quickSort(carList);
        }

        System.out.println("Waiting for sorting to finish...");
        engine.waitForCompletion();

        engine.shutdown();

        System.out.println("Finished sorting async...");
        for (List<Car> carList : carLists) {
            // If item is in compareTo order, item, otherwise null, if null eventually appears, the list is not sorted
            System.out.println("List " + (carLists.indexOf(carList) + 1) + " sorted: " + carList.stream()
                    .reduce((prev, curr) -> curr.compareTo(prev) >= 0 ? curr : null)
                    .isPresent());
        }

//        System.out.println("Begin sorting sync...");
//        for (List<Car> carList : carLists) {
//            long startTime = System.currentTimeMillis();
//            engine.quickSortTraditional(carList, 0, carList.size() - 1);
//            long endTime = System.currentTimeMillis();
//            System.out.println("List " + (carLists.indexOf(carList) + 1) + " sorted in " + (endTime - startTime) + " ms.");
//        }
//        System.out.println("Finished sorting sync...");

        for (int i = 0; i < numberOfLists; i++) {
            saveToFile(carLists.get(i),"cars-" + (i + 1) + "-sorted.txt");
        }
    }

    private static List<Car> generateCars(int count) {
        List<Car> cars = new ArrayList<>();
        Set<String> serials = new HashSet<>();

        for (int i = 1; i <= count; i++) {
            String serial;
            do {
                serial = randomSerial();
            } while (!serials.add(serial)); // hashset for unique serials

            Color color = Color.values()[RANDOM.nextInt(Color.values().length)];
            String dest = DESTINATIONS[RANDOM.nextInt(DESTINATIONS.length)];

            cars.add(new Car(i, serial, color, dest));
        }
        return cars;
    }

    private static String randomSerial() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(VALID_CHAR.charAt(RANDOM.nextInt(VALID_CHAR.length())));
        }
        return sb.toString();
    }

    private static void saveToFile(List<Car> carList, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Car car : carList) {
                writer.write(car.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
