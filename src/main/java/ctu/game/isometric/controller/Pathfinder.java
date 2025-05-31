package ctu.game.isometric.controller;

import com.badlogic.gdx.utils.Array;
import ctu.game.isometric.model.world.IsometricMap;

import java.util.*;

public class Pathfinder {
    private static class Node implements Comparable<Node> {
        int x, y;
        Node parent;
        float g; // cost from start
        float h; // heuristic (estimate to goal)
        float f; // total cost (g + h)

        Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public int compareTo(Node other) {
            return Float.compare(this.f, other.f);
        }
    }

    private final IsometricMap map;
    private final int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // Right, Up, Left, Down

    public Pathfinder(IsometricMap map) {
        this.map = map;
    }

    public Array<int[]> findPath(int startX, int startY, int goalX, int goalY, int maxLength) {
        // If the target is not walkable, find the closest walkable tile
        if (!map.isWalkable(goalX, goalY)) {
            int[] closestWalkable = findClosestWalkable(goalX, goalY);
            goalX = closestWalkable[0];
            goalY = closestWalkable[1];
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<String> closedSet = new HashSet<>();

        Node startNode = new Node(startX, startY);
        startNode.g = 0;
        startNode.h = heuristic(startX, startY, goalX, goalY);
        startNode.f = startNode.g + startNode.h;

        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Check if we reached the goal
//            Nếu đến đích thì reconstruct lại đường đi.

            if (current.x == goalX && current.y == goalY) {
                return reconstructPath(current, maxLength);
            }
            String key = current.x + "," + current.y;
            closedSet.add(key);

            // Check all neighbors
            // Nếu không thể đi hoặc đã duyệt -> bỏ qua
            for (int[] dir : directions) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];

                // Skip if outside map or unwalkable
                if (!map.isWalkable(nx, ny)) continue;

                String neighborKey = nx + "," + ny;
                if (closedSet.contains(neighborKey)) continue;

                float tentativeG = current.g + 1; // Cost is just distance of 1 per move

                Node neighbor = new Node(nx, ny);
                neighbor.parent = current;
                neighbor.g = tentativeG;
                neighbor.h = heuristic(nx, ny, goalX, goalY);
                neighbor.f = neighbor.g + neighbor.h;

                boolean found = false;
                for (Node node : openSet) {
                    if (node.x == nx && node.y == ny) {
                        found = true;
                        if (tentativeG < node.g) {
                            openSet.remove(node);
                            openSet.add(neighbor);
                        }
                        break;
                    }
                }

                if (!found) {
                    openSet.add(neighbor);
                }
            }
        }

        // No path found
        return new Array<>();
    }

    private int[] findClosestWalkable(int x, int y) {
//        Bắt đầu tìm kiếm từ khoảng cách (bán kính) là 1.
        int searchRadius = 1;
//        Giới hạn việc tìm kiếm trong bán kính tối đa là 10 ô để tránh kiểm tra vô hạn.
        int maxSearchRadius = 10;
        // Search in expanding squares around the original coordinates
        while (searchRadius <= maxSearchRadius) {
            for (int offsetY = -searchRadius; offsetY <= searchRadius; offsetY++) {
                for (int offsetX = -searchRadius; offsetX <= searchRadius; offsetX++) {
                    // Only check tiles on the perimeter of the square
                    if (Math.abs(offsetX) == searchRadius || Math.abs(offsetY) == searchRadius) {
                        int checkX = x + offsetX;
                        int checkY = y + offsetY;
                        if (map.isWalkable(checkX, checkY)) {
                            return new int[]{checkX, checkY};
                        }
                    }
                }
            }
            searchRadius++;
        }

        // Default to original coordinates if no walkable found
        return new int[]{x, y};
    }
//    khoảng cách Manhattan giữa hai điểm trên lưới (grid)
    private float heuristic(int x1, int y1, int x2, int y2) {
        // Manhattan distance for grid movement
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private Array<int[]> reconstructPath(Node endNode, int maxLength) {
        Array<int[]> path = new Array<>();
        Node current = endNode;

        while (current != null && path.size < maxLength) {
            path.insert(0, new int[]{current.x, current.y});
            current = current.parent;
        }

        return path;
    }
}