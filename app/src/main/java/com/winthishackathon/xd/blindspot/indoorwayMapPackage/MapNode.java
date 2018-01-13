package com.winthishackathon.xd.blindspot.indoorwayMapPackage;

import java.util.Collection;

/**
 * Created by hub on 2018-01-13.
 */

public class MapNode {
    public double Lat;
    public double Lon;
    public Collection<Long> Neighbours;

    public boolean hasNeighbour(Long potentialNeighbour){
        return Neighbours.contains(potentialNeighbour);
    }

    public double distanceFrom(MapNode other){
        return distance(Lat, Lon, other.Lat, other.Lon);
    }
    private static double distance(double lat1, double lon1, double lat2, double lon2){
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344; // KILOMETERES

        return (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
}
