import java.util.*;

public class TrapLocator {
    public List<Colony> colonies;

    public TrapLocator(List<Colony> colonies) {
        this.colonies = colonies;
    }

    public List<List<Integer>> revealTraps() {
        List<List<Integer>> trapPositions = new ArrayList<>();

        for (Colony colony : colonies) {
            List<Integer> dangerousCities = new ArrayList<>();
            boolean[] visited = new boolean[colony.cities.size()];

            for (int i = 0; i < colony.cities.size(); i++) {
                if (!visited[i]) {
                    int city = colony.cities.get(i);
                    if (hasCycle(colony, city, visited, new HashSet<>(), new HashSet<>(), dangerousCities)) {

                        dangerousCities.remove(dangerousCities.size() - 1);
                    }
                }
            }

            trapPositions.add(dangerousCities);
        }

        return trapPositions;
    }

    private boolean hasCycle(Colony colony, int city, boolean[] visited, Set<Integer> stack, Set<Integer> visitedSet, List<Integer> dangerousCities) {
        int cityIndex = colony.cities.indexOf(city);
        visited[cityIndex] = true;
        stack.add(city);
        visitedSet.add(city);
        boolean cycleFound = false;
        Set<Integer> cycleCities = new HashSet<>();

        for (int neighbor : colony.roadNetwork.get(city)) {
            int neighborIndex = colony.cities.indexOf(neighbor);
            if (!visited[neighborIndex]) {
                if (hasCycle(colony, neighbor, visited, stack, visitedSet, dangerousCities)) {
                    cycleFound = true;
                    cycleCities.add(city);
                    cycleCities.add(neighbor);
                    break;
                }
            } else if (stack.contains(neighbor)) {
                cycleFound = true;
                cycleCities.add(city);
                cycleCities.add(neighbor);
                break;
            }
        }

        stack.remove(city);

        if (cycleFound) {
            for (int cycleCity : cycleCities) {
                if (!dangerousCities.contains(cycleCity)) {
                    dangerousCities.add(cycleCity);
                }
            }

            dangerousCities.add(dangerousCities.get(0));
            dangerousCities.remove(dangerousCities.get(2));


        }

        return cycleFound;
    }

    public void printTraps(List<List<Integer>> trapPositions) {
        System.out.println("Danger exploration conclusions:");


        for (int i = 0; i < trapPositions.size(); i++) {
            List<Integer> trapCities = trapPositions.get(i);

            if (trapCities.isEmpty()) {
                System.out.println("Colony " + (i + 1) + ": Safe");
            } else {
                System.out.print("Colony " + (i + 1) + ": Dangerous. Cities on the dangerous path: [");
                for (int j = 0; j < trapCities.size(); j++) {
                    Collections.sort(trapCities);
                    System.out.print(trapCities.get(j) + 1);
                    if (j != trapCities.size() - 1) {
                        System.out.print(", ");
                    }
                }
                System.out.println("]");
            }
        }
    }
}
