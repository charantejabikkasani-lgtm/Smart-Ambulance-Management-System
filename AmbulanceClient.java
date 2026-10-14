
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class AmbulanceClient {

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
    }

    static void updateDirection(String from, String to, int newTime) {
        for (Road road : graph.getOrDefault(from, new ArrayList<>())) {
            if (road.destination.equals(to)) {
                road.travelTime = newTime;
                return;
            }
        }
    }

    static String findFastestRoute(String start, String hospital) {
        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        PriorityQueue<State> queue =
            new PriorityQueue<>(Comparator.comparingInt(s -> s.cost));

        for (String location : graph.keySet()) {
            distance.put(location, Integer.MAX_VALUE);
        }

        if (!distance.containsKey(start) || !distance.containsKey(hospital)) {
            return "No route found.";
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

        if (distance.get(hospital) == Integer.MAX_VALUE) {
            return "No route found.";
        }

        List<String> route = new ArrayList<>();
        String location = hospital;

        while (location != null) {
            route.add(location);
            location = previous.get(location);
        }

        Collections.reverse(route);

        return String.join(" -> ", route)
            + " | Estimated travel time: "
            + distance.get(hospital) + " minutes";
    }

    public static void main(String[] args) {

        // Road network
        addRoad("Ambulance", "A", 5);
        addRoad("Ambulance", "B", 3);
        addRoad("A", "C", 4);
        addRoad("B", "C", 2);
        addRoad("C", "Hospital", 5);
        addRoad("A", "Hospital", 12);
        addRoad("B", "Hospital", 10);

        // Simulate traffic on the road between B and C
        updateRoad("B", "C", 15);

        // Calculate route after traffic update
        String route = findFastestRoute("Ambulance", "Hospital");

        String ambulanceId = "AMB101";
        String currentLocation = "Ambulance";
        String emergency = "Cardiac Emergency";

        String message =
            "Emergency Alert"
            + " | Ambulance ID: " + ambulanceId
            + " | Current Location: " + currentLocation
            + " | Emergency Type: " + emergency
            + " | Fastest Route: " + route;

        System.out.println("Calculated route: " + route);

        try (
            Socket socket = new Socket("localhost", 5000);
            PrintWriter output = new PrintWriter(
                socket.getOutputStream(), true)
        ) {
            output.println(message);
            System.out.println("Emergency details sent to hospital.");

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
            System.out.println("Start HospitalServer first.");
        }
    }
}
