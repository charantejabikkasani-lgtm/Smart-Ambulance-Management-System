
import java.util.*;

public class NetworkModel {

    private static final Map<String, Map<String, Integer>> roads =
            new LinkedHashMap<>();

    static {
        addRoad("Ambulance", "A", 5);
        addRoad("Ambulance", "B", 3);
        addRoad("A", "C", 4);
        addRoad("B", "C", 2);
        addRoad("C", "Hospital", 5);
        addRoad("A", "Hospital", 12);
        addRoad("B", "Hospital", 10);

        // Simulated congestion for the demo route.
        updateRoad("B", "C", 15);
    }

    public static synchronized void addRoad(
            String from, String to, int minutes) {
        roads.computeIfAbsent(from, k -> new LinkedHashMap<>())
             .put(to, minutes);
        roads.computeIfAbsent(to, k -> new LinkedHashMap<>())
             .put(from, minutes);
    }

    public static synchronized void updateRoad(
            String from, String to, int minutes) {
        if (minutes < 1) {
            throw new IllegalArgumentException(
                    "Travel time must be at least 1 minute.");
        }

        if (!roads.containsKey(from)
                || !roads.get(from).containsKey(to)) {
            throw new IllegalArgumentException(
                    "Road does not exist: " + from + " - " + to);
        }

        roads.get(from).put(to, minutes);
        roads.get(to).put(from, minutes);
    }

    public static synchronized int getRoadTime(
            String from, String to) {
        return roads.getOrDefault(from, Collections.emptyMap())
                    .getOrDefault(to, -1);
    }

    public static synchronized Map<String, Integer> getRoadsFrom(
            String from) {
        return new LinkedHashMap<>(
                roads.getOrDefault(from, Collections.emptyMap()));
    }

    public static synchronized Set<String> getLocations() {
        return new LinkedHashSet<>(roads.keySet());
    }

    public static synchronized String findFastestRoute(
            String start, String destination) {

        if (!roads.containsKey(start)
                || !roads.containsKey(destination)) {
            return "Unknown location.";
        }

        Map<String, Integer> distance = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        for (String node : roads.keySet()) {
            distance.put(node, Integer.MAX_VALUE);
        }

        PriorityQueue<String> queue = new PriorityQueue<>(
                Comparator.comparingInt(distance::get));

        distance.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(destination)) {
                break;
            }

            for (Map.Entry<String, Integer> edge :
                    roads.get(current).entrySet()) {

                String next = edge.getKey();
                int newCost = distance.get(current) + edge.getValue();

                if (newCost < distance.get(next)) {
                    distance.put(next, newCost);
                    previous.put(next, current);

                    // Rebuild queue so changed priorities are respected.
                    queue.remove(next);
                    queue.add(next);
                }
            }
        }

        if (distance.get(destination) == Integer.MAX_VALUE) {
            return "No route found.";
        }

        List<String> path = new ArrayList<>();
        String node = destination;

        while (node != null) {
            path.add(node);
            node = previous.get(node);
        }

        Collections.reverse(path);

        return String.join(" -> ", path)
                + " | Estimated travel time: "
                + distance.get(destination) + " minutes";
    }

    public static synchronized int getTravelTime(
            String start, String destination) {
        String result = findFastestRoute(start, destination);
        String marker = "Estimated travel time: ";

        int index = result.lastIndexOf(marker);
        if (index < 0) {
            return -1;
        }

        try {
            return Integer.parseInt(
                    result.substring(index + marker.length())
                          .replace(" minutes", "").trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static synchronized String[] getRouteNodes(
            String start, String destination) {
        String result = findFastestRoute(start, destination);
        int marker = result.indexOf(" | Estimated travel time:");

        if (marker >= 0) {
            result = result.substring(0, marker);
        }

        if (result.equals("No route found.")
                || result.equals("Unknown location.")) {
            return new String[0];
        }

        return result.split(" -> ");
    }
}
