package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

import android.util.Log;

import com.indoorway.android.common.sdk.IndoorwaySdk;
import com.indoorway.android.common.sdk.listeners.generic.Action1;
import com.indoorway.android.common.sdk.model.Coordinates;
import com.indoorway.android.common.sdk.model.IndoorwayMap;
import com.indoorway.android.common.sdk.model.IndoorwayNode;
import com.indoorway.android.common.sdk.model.IndoorwayObjectParameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class IndoorSDKUtils {
    public static HashMap<String, String> translationMap = new HashMap<String, String>(){{
        put("room", "sala");
        put("floor", "piętro");
        put("stairs", "schody");
        put("II", "2");
        put("I", "1");
        put("elevator", "winda");
        put("corridor", "korytarz");
    }};

    static public Long getNearestToCoordinates(HashMap<Long, MapNode> adjacencyMap, final Coordinates coordinates) {
        double min = Double.POSITIVE_INFINITY;
        Long minId = 0L;
        for (Long k1: adjacencyMap.keySet()) {
            MapNode n1 = adjacencyMap.get(k1);
            double dist = n1.distanceFrom(new MapNode(){{
                Lat = coordinates.getLatitude();
                Lon = coordinates.getLongitude();
                Neighbours = null;
            }});
            if(dist < min)
            {
                minId = k1;
                min = dist;
            }
        }
        return minId;
    }


    static public HashMap<Long,MapNode> getMapPaths(List<IndoorwayNode> paths) {
        final HashMap<Long, MapNode> neighbourMap = new HashMap<>();
        for (final IndoorwayNode node: paths) {
            Collection<Long> nodeCol = node.getNeighbours();
            final ArrayList<Long> list = new ArrayList<>();
            for(Long l:nodeCol) {
                if(!l.equals(node.getId())) list.add(l);
            }
            neighbourMap.put(node.getId(), new MapNode(){{
                Lat = node.getCoordinates().getLatitude();
                Lon = node.getCoordinates().getLongitude();
                Neighbours = list;
            }});
        }
        return neighbourMap;
    }

    static public Coordinates getPositionFromObjectName(String name, IndoorwayMap indoorwayMap) throws Exception{
        List<IndoorwayObjectParameters> mapObjects = indoorwayMap.getObjects();
        Log.d("indoorway", Integer.toString(mapObjects.size()));
        while (mapObjects.size() == 0){
            mapObjects = indoorwayMap.getObjects();
            Thread.sleep(1000);
        }
        for (IndoorwayObjectParameters iop: mapObjects) {
            Log.d("indoorway", iop.getName());

            String mapped = "";
            for (String splitted: iop.getName().toLowerCase().split(" ")) {
                if (translationMap.containsKey(splitted))
                    mapped = mapped + " " + translationMap.get(splitted);
                else
                    mapped = mapped + " " + splitted;
            }
            String translated = mapped.trim();
            if (name.equals(translated)){
                return iop.getCenterPoint();
            }
        }
        throw new Exception("Location not found");
    }
}
