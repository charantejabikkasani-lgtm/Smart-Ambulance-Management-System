
import java.util.*;

public class DijkstraRouting {

    static class Road {
        String destination;
        int travelTime;

        Road(String destination, int travelTime) {
            this.destination = destination;
            this.travelTime = travelTime;
        }
    }

    static class State {
        String location;
        int cost;

        State(String location, int cost) {
            this.location = location;
            this.cost = cost;
        }
    }

    static Map<String, List<Road>> graph = new HashMap<>();

    static void addRoad(String from, String to, int time) {
        graph.computeIfAbsent(from, k -> new ArrayList<>())
             .add(new Road(to, time));

        graph.computeIfAbsent(to, k -> new ArrayList<>())
             .add(new Road(from, time));
    }

    static void updateRoad(String from, String to, int newTime) {
        updateDirection(from, to, newTime);
        updateDirection(to, from, newTime);

        System.out.println(
            "Traffic update: " + from + " <-> " + to
            + " now takes " + newTime + " minutes."
        );
    }

    static void updateDirection(String from, String to, int newTime) {
        List<Road> roads = graph.get(from);

        if (roads == null) {
            return;
        }

        for (Road road : roads) {
            if (road.destination.equals(to)) {
                road.travelTime = newTime;
                return;
            }
        }
    }

    static void findFastestRoute(String start, String hospital) {
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        PriorityQueue<State> queue =
            new PriorityQueue<>(Comparator.comparingInt(s -> s.cost));

        for (String location : graph.keySet()) {
            distance.put(location, Integer.MAX_VALUE);
        }

        distance.put(start, 0);
        queue.add(new State(start, 0));

        while (!queue.isEmpty()) {
            State current = queue.poll();

            if (current.cost > distance.get(current.location)) {
                continue;
            }

            if (current.location.equals(hospital)) {
                break;
            }

            for (Road road : graph.getOrDefault(
                    current.location, new ArrayList<>())) {

                int newCost = current.cost + road.travelTime;

                if (newCost < distance.getOrDefault(
                        road.destination, Integer.MAX_VALUE)) {

                    distance.put(road.destination, newCost);
                    previous.put(road.destination, current.location);

                    queue.add(new State(road.destination, newCost));
                }
            }
        }

        if (distance.getOrDefault(hospital, Integer.MAX_VALUE)
                == Integer.MAX_VALUE) {
            System.out.println("No route to the hospital was found.");
            return;
        }

        List<String> route = new ArrayList<>();
        String location = hospital;

        while (location != null) {
            route.add(location);
            location = previous.get(location);
        }

        Collections.reverse(route);

        System.out.println("FASTEST AMBULANCE ROUTE");
        System.out.println("-----------------------");
        System.out.println("Route: " + String.join(" -> ", route));
        System.out.println(
            "Estimated travel time: "
            + distance.get(hospital) + " minutes"
        );
    }

    public static void main(String[] args) {

        addRoad("Ambulance", "A", 5);
        addRoad("Ambulance", "B", 3);
        addRoad("A", "C", 4);
        addRoad("B", "C", 2);
        addRoad("C", "Hospital", 5);
        addRoad("A", "Hospital", 12);
        addRoad("B", "Hospital", 10);

        System.out.println("=== BEFORE TRAFFIC ===");
        findFastestRoute("Ambulance", "Hospital");

        System.out.println();

        updateRoad("B", "C", 15);

        System.out.println();
        System.out.println("=== AFTER TRAFFIC ===");
        findFastestRoute("Ambulance", "Hospital");
    }
}
