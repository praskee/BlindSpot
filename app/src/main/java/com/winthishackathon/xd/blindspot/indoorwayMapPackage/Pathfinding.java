package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hub on 2018-01-13.
 */

public class Pathfinding {

    public static HashMap<FromToContainer, Double> Dijkstra(HashMap<Long, MapNode> graph, Long from)
    {
        HashMap<FromToContainer, Double> res = new HashMap<>();
        List<Long> unresolvedNodes = new ArrayList<Long>();
        for(Long Id: graph.keySet()) {
            unresolvedNodes.add(Id);
            res.put(new FromToContainer(from,Id),Double.POSITIVE_INFINITY);
        }
        res.put(new FromToContainer(from,from), 0.0);

        while(unresolvedNodes.size() > 0) {
            double minDist = Double.POSITIVE_INFINITY;
            Long minID = -1L;
            int minIdx = -1;
            for(int i = 0;i<unresolvedNodes.size();i++) {
                FromToContainer tup = new FromToContainer(from,unresolvedNodes.get(i));
                double distance = res.get(tup);
                if(distance < minDist) {
                    minDist = distance;
                    minID = unresolvedNodes.get(i);
                    minIdx = i;
                }
            }
            unresolvedNodes.remove(minIdx);
            MapNode currentNode = graph.get(minID);
            for(Long id:currentNode.Neighbours) {
                MapNode neighbour = graph.get(id);
                double distFromCurrent = currentNode.distanceFrom(neighbour);
                FromToContainer tup = new FromToContainer(from,id);
                double dis = res.get(tup);
                if(minDist
                        +
                        distFromCurrent <
                        dis)
                {
                    res.put(new FromToContainer(from,id),
                            minDist+
                                    distFromCurrent);
                }
            }
        }

        return res;
    }
    public static List<MapNode> getPathFromTo(HashMap<Long,MapNode> adjacencyMap, HashMap<FromToContainer,Double> shortestDists, Long from, Long to){
        List<MapNode> res = new ArrayList<>();
        Long currentPos = to;

        while(shortestDists.get(new FromToContainer(from,currentPos)) != 0.0) {
            MapNode currentNode = adjacencyMap.get(currentPos);
            res.add(currentNode);
            for(Long neighbourID: currentNode.Neighbours) {
                MapNode neighbour = adjacencyMap.get(neighbourID);
                double dist = Math.abs(neighbour.distanceFrom(currentNode) + shortestDists.get(new FromToContainer(from,neighbourID))
                        - shortestDists.get(new FromToContainer(from,currentPos)));
                if(dist < 0.001) {
                    currentPos = neighbourID;
                    break;
                }
            }
        }
        return res;
    }

}
