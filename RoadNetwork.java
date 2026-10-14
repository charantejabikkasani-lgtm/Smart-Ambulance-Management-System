
import java.util.*;

public class RoadNetwork {

    public static void main(String[] args) {

        Map<String, List<String>> roads = new HashMap<>();

        roads.put("Ambulance",
                Arrays.asList("A", "B"));

        roads.put("A",
                Arrays.asList("Ambulance", "C", "Hospital"));

        roads.put("B",
                Arrays.asList("Ambulance", "C", "Hospital"));

        roads.put("C",
                Arrays.asList("A", "B", "Hospital"));

        roads.put("Hospital",
                Arrays.asList("A", "B", "C"));

        System.out.println("SMART AMBULANCE ROAD NETWORK");
        System.out.println("-----------------------------");

        for (String location : roads.keySet()) {
            System.out.println(
                    location + " is connected to: "
                    + roads.get(location)
            );
        }
    }
}
