package com.ses3a.u_argo.tools;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

public class LocationUtil {

    private final static LatLng building_1 = new LatLng(-33.88358927248891, 151.20111371633055);
    private final static LatLng building_8 = new LatLng(-33.88094231843857, 151.2013071351103);
    private final static LatLng building_10 = new LatLng(-33.88350025713034, 151.1989419327386);
    private final static ArrayList<LatLng> buildings = new ArrayList<>();

    public static String getBuildingName(LatLng latLng) {
        buildings.add(building_1);
        buildings.add(building_8);
        buildings.add(building_10);
        double distance = latLng.distanceTo(building_1);
        LatLng building = building_1;
        for (LatLng l : buildings) {
            if (l.distanceTo(latLng) < distance) {
                building = l;
                distance = l.distanceTo(latLng);
            }
        }
        if (building.equals(building_1)) {
            return "Building 1";
        } else if (building.equals(building_8)) {
            return "Building 8";
        } else if (building.equals(building_10)) {
            return "Building 10";
        } else {
            return null;
        }
    }
}
