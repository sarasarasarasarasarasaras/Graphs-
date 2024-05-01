import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class TravelMap {


    public Map<Integer, Location> locationMap = new HashMap<>();


    public List<Location> locations = new ArrayList<>();


    public List<Trail> trails = new ArrayList<>();



    public void initializeMap(String filename) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(filename));
            NodeList locationList = document.getElementsByTagName("Location");
            NodeList trailList = document.getElementsByTagName("Trail");


            Map<Integer, Location> locationMap = new HashMap<>();
            for (int i = 0; i < locationList.getLength(); i++) {
                Element locationElement = (Element) locationList.item(i);
                int id = Integer.parseInt(locationElement.getElementsByTagName("Id").item(0).getTextContent());
                String name = locationElement.getElementsByTagName("Name").item(0).getTextContent();
                Location location = new Location(name, id);
                locationMap.put(id, location);
                locations.add(location);
            }


            for (int i = 0; i < trailList.getLength(); i++) {
                Element trailElement = (Element) trailList.item(i);
                int sourceId = Integer.parseInt(trailElement.getElementsByTagName("Source").item(0).getTextContent());
                int destinationId = Integer.parseInt(trailElement.getElementsByTagName("Destination").item(0).getTextContent());
                int danger = Integer.parseInt(trailElement.getElementsByTagName("Danger").item(0).getTextContent());
                Location source = locationMap.get(sourceId);
                Location destination = locationMap.get(destinationId);
                Trail trail = new Trail(source, destination, danger);
                trails.add(trail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public List<Trail> getSafestTrails() {
        List<Trail> safestTrails = new ArrayList<>();

        Collections.sort(trails, Comparator.comparingInt(t -> t.danger));

        Map<Integer, Integer> parent = new HashMap<>();
        for (Location location : locations) {
            parent.put(location.id, location.id);
        }

        for (Trail trail : trails) {
            int sourceParent = findParent(parent, trail.source.id);
            int destParent = findParent(parent, trail.destination.id);
            if (sourceParent != destParent) {
                safestTrails.add(trail);
                parent.put(sourceParent, destParent);
            }
        }
        return safestTrails;
    }

    private int findParent(Map<Integer, Integer> parent, int id) {
        if (parent.get(id) == id) {
            return id;
        }
        int root = findParent(parent, parent.get(id));
        parent.put(id, root);
        return root;
    }

    public void printSafestTrails(List<Trail> safestTrails) {
        System.out.println("Safest trails are:");
        int totalDanger = 0;
        Trail highestWeightTrail = null;
        safestTrails.sort(new Comparator<Trail>() {
            @Override
            public int compare(Trail t1, Trail t2) {
                int cmp = t1.source.name.compareTo(t2.source.name);
                if (cmp == 0) {
                    cmp = t1.destination.name.compareTo(t2.destination.name);
                }
                return cmp;
            }
        });
        for (Trail trail : safestTrails) {
            totalDanger += trail.danger;
            System.out.printf("The trail from %s to %s with danger %d%n", trail.source.name, trail.destination.name, trail.danger);
            if (highestWeightTrail == null || trail.danger > highestWeightTrail.danger) {
                highestWeightTrail = trail;
            }
        }
        System.out.printf("Total danger: %d%n", totalDanger);

    }
}