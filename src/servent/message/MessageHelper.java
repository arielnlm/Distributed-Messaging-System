package servent.message;

import app.ServentInfo;
import networking.Address;
import networking.ServentNeighbours;
import networking.SystemState;

import java.util.*;

public class MessageHelper {

    public static Queue<ServentInfo> findShortestPath(ServentInfo start, ServentInfo end) {
        Queue<ServentInfo> path = new LinkedList<>();

        // BFS uses a queue to keep track of the next nodes to visit
        Queue<ServentInfo> toVisit = new LinkedList<>();
        toVisit.add(start);

        // HashMap to keep track of visited nodes and their previous nodes in the path
        Map<ServentInfo, ServentInfo> visitedNodes = new HashMap<>();
        visitedNodes.put(start, null);

        while (!toVisit.isEmpty()) {
            ServentInfo current = toVisit.remove();

            if (current.equals(end)) {
                // Found the destination, reconstruct the path
                ServentInfo node = end;
                while (node != null) {
                    path.add(node);
                    node = visitedNodes.get(node);
                }

                // The path is from end to start at this point, so reverse it
                Collections.reverse((List<?>) path);
                return path;
            }

            // Not at the destination, add all unvisited neighbours to the queue
            ServentNeighbours neighbours = SystemState.getInstance().getNeighbours().get(current);
            if (neighbours != null) {
                for (ServentInfo neighbour : Arrays.asList(neighbours.getFirstLeft(), neighbours.getFirstRight(),
                        neighbours.getSecondLeft(), neighbours.getSecondRight())) {
                    if (neighbour != null && !visitedNodes.containsKey(neighbour)) {
                        toVisit.add(neighbour);
                        visitedNodes.put(neighbour, current);
                    }
                }
            }

        }

        // No path found
        return null;
    }

}

