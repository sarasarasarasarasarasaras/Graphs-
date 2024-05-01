import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Kingdom {

    private int[][] adjacencyMatrix;
    private List<Colony> colonies;
    private HashMap<Integer, List<Integer>> directed = new HashMap<>();

    public void initializeKingdom(String filename) {
        try {
            Scanner scanner = new Scanner(new File(filename));
            List<int[]> matrixRows = new ArrayList<>();
            List<List<Integer>> adjacency = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] values = line.split(" ");
                int[] row = new int[values.length];
                List<Integer> rowa = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    row[i] = Integer.parseInt(values[i]);
                    rowa.add(Integer.parseInt(values[i]));
                }

                matrixRows.add(row);
                adjacency.add(rowa);
            }
            scanner.close();

            int n = matrixRows.size();
            adjacencyMatrix = new int[n][n];


            for (int i = 0; i < n; i++) {

                int[] row = matrixRows.get(i);
                List<Integer> rowa = adjacency.get(i);
                List<Integer> friend = new ArrayList<>();
                for (int j = 0; j < n; j++) {
                    if (row[j] == 1) {
                        adjacencyMatrix[i][j] = 1;
                        adjacencyMatrix[j][i] = 1;


                    }
                    int edge = adjacency.get(i).get(j);
                    if (edge == 1){
                        friend.add(j);

                    }
                }
                directed.put(i, friend);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private void dfs(int node, Colony colony, boolean[] visited) {
        visited[node] = true;
        colony.cities.add(node);
        for (int i = 0; i < adjacencyMatrix[node].length; i++) {
            if (adjacencyMatrix[node][i] == 1 && !visited[i]) {
                dfs(i, colony, visited);

            }
        }
    }

    public List<Colony> getColonies() {
        if (adjacencyMatrix == null) {
            throw new IllegalStateException("Kingdom not initialized. Call initializeKingdom first.");
        }
        colonies = new ArrayList<>();
        boolean[] visited = new boolean[adjacencyMatrix.length];

        for (int i = 0; i < adjacencyMatrix.length; i++) {
            if (!visited[i]) {
                Colony colony = new Colony();
                dfs(i, colony, visited);
                colonies.add(colony);

                for (int a : colony.cities){
                    colony.roadNetwork.put(a, directed.get(a));

                }
            }
        }

        return colonies;
    }

    public void printColonies(List<Colony> discoveredColonies) {
        if (adjacencyMatrix == null) {
            throw new IllegalStateException("Kingdom not initialized. Call initializeKingdom first.");
        }

        System.out.println("Discovered colonies are:");
        for (int i = 0; i < discoveredColonies.size(); i++) {
            Colony colony = discoveredColonies.get(i);
            System.out.print("Colony " + (i + 1) + ": [");
            for (int j = 0; j < colony.cities.size(); j++) {
                Collections.sort(colony.cities);
                System.out.print(colony.cities.get(j) + 1);
                if (j != colony.cities.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }
}
