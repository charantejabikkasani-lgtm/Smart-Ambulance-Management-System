
import java.util.*;

public class WeightedRoadNetwork {

    static class Road {
        String destination;
        int travelTime;

        Road(String destination, int travelTime) {
            this.destination = destination;
            this.travelTime = travelTime;
        }
    }

    public static void main(String[] args) {

        Map<String, List<Road>> graph = new HashMap<>();

        graph.put("Ambulance", Arrays.asList(
                new Road("A", 5),
                new Road("B", 3)
        ));

        graph.put("A", Arrays.asList(
                new Road("Ambulance", 5),
                new Road("C", 4),
                new Road("Hospital", 12)
        ));

        graph.put("B", Arrays.asList(
                new Road("Ambulance", 3),
                new Road("C", 2),
                new Road("Hospital", 10)
        ));

        graph.put("C", Arrays.asList(
                new Road("A", 4),
                new Road("B", 2),
                new Road("Hospital", 5)
        ));

        graph.put("Hospital", Arrays.asList(
                new Road("A", 12),
                new Road("B", 10),
                new Road("C", 5)
        ));

        System.out.println("ROAD NETWORK WITH TRAVEL TIMES");

        for (String location : graph.keySet()) {
            for (Road road : graph.get(location)) {
                System.out.println(
                        location + " -> " + road.destination
                        + " : " + road.travelTime + " minutes"
                );
            }
        }
    }
}
