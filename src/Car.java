import java.util.List;

public class Car implements Comparable<Car> {
    private final long recId;
    private final String serial;
    private final Color color;
    private final String destination;

    private static final List<String> DEST_ORDER = List.of(
            "Los Angeles",
            "Houston",
            "New Orleans",
            "Miami",
            "New York");

    public Car(long recId, String serial, Color color, String destination) {
        this.recId = recId;
        this.serial = serial;
        this.color = color;
        this.destination = destination;
    }

    @Override
    public int compareTo(Car other) {
        // First criteria: Destination
        int destCompare = Integer.compare(DEST_ORDER.indexOf(this.destination), DEST_ORDER.indexOf(other.destination));
        if (destCompare != 0) return destCompare;

        // Second criteria: Color
        int colorCompare = this.color.ordinal() - other.color.ordinal();
        if (colorCompare != 0) return colorCompare;

        // Third criteria: Serial (alphabetical order)
        return this.serial.compareTo(other.serial);
    }

    @Override
    public String toString() {
        return destination + "\t" + color + "\t" + serial + "\t" + recId;
    }
}
