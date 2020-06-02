package com.ses3a.u_argo;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;
import com.mapbox.services.android.navigation.v5.route.RouteFetcher;
import com.mapbox.services.android.navigation.v5.route.RouteListener;
import com.mapbox.services.android.navigation.v5.routeprogress.ProgressChangeListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;
import com.mapbox.vision.VisionManager;
import com.mapbox.vision.ar.VisionArManager;
import com.mapbox.vision.ar.core.models.ManeuverType;
import com.mapbox.vision.ar.core.models.Route;
import com.mapbox.vision.ar.core.models.RoutePoint;
import com.mapbox.vision.ar.view.gl.VisionArView;
import com.mapbox.vision.mobile.core.interfaces.VisionEventsListener;
import com.mapbox.vision.mobile.core.models.AuthorizationStatus;
import com.mapbox.vision.mobile.core.models.Camera;
import com.mapbox.vision.mobile.core.models.Country;
import com.mapbox.vision.mobile.core.models.FrameSegmentation;
import com.mapbox.vision.mobile.core.models.classification.FrameSignClassifications;
import com.mapbox.vision.mobile.core.models.detection.FrameDetections;
import com.mapbox.vision.mobile.core.models.position.GeoCoordinate;
import com.mapbox.vision.mobile.core.models.position.VehicleState;
import com.mapbox.vision.mobile.core.models.road.RoadDescription;
import com.mapbox.vision.mobile.core.models.world.WorldDescription;
import com.mapbox.vision.performance.ModelPerformance.On;
import com.mapbox.vision.performance.ModelPerformanceConfig.Merged;
import com.mapbox.vision.performance.ModelPerformanceMode;
import com.mapbox.vision.performance.ModelPerformanceRate;
import com.mapbox.vision.utils.VisionLogger;
import com.ses3a.u_argo.eneities.History;
import com.ses3a.u_argo.tools.LocationUtil;
import com.ses3a.u_argo.tools.SharedPreferenceUtil;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Example shows how Vision and VisionAR SDKs are used to draw AR lane over the video stream from camera.
 * Also, Mapbox navigation services are used to build route and  navigation session.
 */
public class ArActivity extends BaseActivity implements RouteListener, ProgressChangeListener, OffRouteListener {

    private static final String TAG = ArActivity.class.getSimpleName();

    // Handles navigation.
    private MapboxNavigation mapboxNavigation;
    // Fetches route from points.
    private RouteFetcher routeFetcher;
    private RouteProgress lastRouteProgress;
    private LocationEngine locationEngine;
    private LocationEngineCallback<LocationEngineResult> locationCallback;

    private boolean visionManagerWasInit = false;
    private boolean navigationWasStarted = false;

    boolean flag = true;

    // This dummy points will be used to build route. For real world test this needs to be changed to real values for
    // source and target locations.
    private final Point ROUTE_ORIGIN = Point.fromLngLat(151.040637, -33.910701);

    //Calculate calories
    final double[] distance = {0.0};
    final double[] duration = {0.0};

    private Point ROUTE_DESTINATION;
//    ori:   151.039349, -33.911610
//    des:   151.040637, -33.910701

    //history util
    SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Init the location picker when switching into the AR navigation.
        Intent intent = getIntent();
        Bundle point = intent.getBundleExtra("destination");
        double latitude = point.getDouble("latitude");
        double longitude = point.getDouble("longitude");

        ROUTE_DESTINATION = Point.fromLngLat(longitude, latitude);

        //add history
        LatLng op = new LatLng(ROUTE_ORIGIN.latitude(), ROUTE_ORIGIN.longitude());
        LatLng dp = new LatLng(ROUTE_DESTINATION.latitude(), ROUTE_DESTINATION.longitude());
        String startBuilding = LocationUtil.getBuildingName(op);
        String destinationBuilding = LocationUtil.getBuildingName(dp);
        Date startDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String startDateString = sdf.format(startDate);
        String duration = "";
        String cal = getCaloriesConsumed() + "kcal";
        History history = new History(startBuilding, destinationBuilding, startDateString, duration, cal);
        saveHistory(history);

        //Button to 2D navigation with destination
        final Button normalMap = findViewById(R.id.normalMap);
        normalMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent=new Intent(ArActivity.this,NormalMap.class);

                Bundle destination = new Bundle();
                destination.putDouble("latitude", ROUTE_DESTINATION.latitude());
                destination.putDouble("longitude", ROUTE_DESTINATION.longitude());
                intent.putExtra("destination",destination);
                flag = false;

                startActivity(intent);
            }
        });
    }

    @Override
    protected void initViews() {
        setContentView(R.layout.activity_ar_navigation);
    }

    protected void setArRenderOptions(@NotNull final VisionArView visionArView) {
        visionArView.setFenceVisible(true);
    }

    @Override
    protected void onPermissionsGranted() {
        startVisionManager();
        startNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startVisionManager();
        startNavigation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopVisionManager();
        stopNavigation();
        //Log.d(TAG, "onZCQStop: " + getCaloriesConsumed());
    }

    private void startVisionManager() {
        if (allPermissionsGranted() && !visionManagerWasInit) {
            // Create and start VisionManager.
            VisionManager.create();
            VisionManager.setModelPerformanceConfig(new Merged(new On(ModelPerformanceMode.DYNAMIC, ModelPerformanceRate.HIGH)));
            VisionManager.start();
            VisionManager.setVisionEventsListener(new VisionEventsListener() {
                @Override
                public void onAuthorizationStatusUpdated(@NotNull AuthorizationStatus authorizationStatus) {
                }

                @Override
                public void onFrameSegmentationUpdated(@NotNull FrameSegmentation frameSegmentation) {
                }

                @Override
                public void onFrameDetectionsUpdated(@NotNull FrameDetections frameDetections) {
                }

                @Override
                public void onFrameSignClassificationsUpdated(@NotNull FrameSignClassifications frameSignClassifications) {
                }

                @Override
                public void onRoadDescriptionUpdated(@NotNull RoadDescription roadDescription) {
                }

                @Override
                public void onWorldDescriptionUpdated(@NotNull WorldDescription worldDescription) {
                }

                @Override
                public void onVehicleStateUpdated(@NotNull VehicleState vehicleState) {
                }

                @Override
                public void onCameraUpdated(@NotNull Camera camera) {
                }

                @Override
                public void onCountryUpdated(@NotNull Country country) {
                }

                @Override
                public void onUpdateCompleted() {
                }
            });

            VisionArView visionArView = findViewById(R.id.mapbox_ar_view);

            // Create VisionArManager.
            VisionArManager.create(VisionManager.INSTANCE);
            visionArView.setArManager(VisionArManager.INSTANCE);
            setArRenderOptions(visionArView);

            visionManagerWasInit = true;
        }
    }

    private void stopVisionManager() {
        if (visionManagerWasInit) {
            VisionArManager.destroy();
            VisionManager.stop();
            VisionManager.destroy();

            visionManagerWasInit = false;
        }
    }

    private void startNavigation() {
        if (allPermissionsGranted() && !navigationWasStarted) {
            // Initialize navigation with your Mapbox access token.
            mapboxNavigation = new MapboxNavigation(
                    this,
                    getString(R.string.mapbox_access_token),
                    MapboxNavigationOptions.builder().build()
            );

            // Initialize route fetcher with your Mapbox access token.
            routeFetcher = new RouteFetcher(this, getString(R.string.mapbox_access_token));
            routeFetcher.addRouteListener(this);

            locationEngine = LocationEngineProvider.getBestLocationEngine(this);

            LocationEngineRequest arLocationEngineRequest = new LocationEngineRequest.Builder(0)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .setFastestInterval(1000)
                    .build();

            locationCallback = new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    initDirectionsRoute(result.getLastLocation());
                }

                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            };



            try {
                locationEngine.requestLocationUpdates(arLocationEngineRequest, locationCallback, Looper.getMainLooper());
            } catch (SecurityException se) {
                VisionLogger.Companion.e(TAG, se.toString());
            }

//            initDirectionsRoute();

            // Route need to be reestablished if off route happens.
            mapboxNavigation.addOffRouteListener(this);
            mapboxNavigation.addProgressChangeListener(this);

            navigationWasStarted = true;
        }
    }

    private void stopNavigation() {
        if (navigationWasStarted) {
            locationEngine.removeLocationUpdates(locationCallback);

            mapboxNavigation.removeProgressChangeListener(this);
            mapboxNavigation.removeOffRouteListener(this);
            mapboxNavigation.stopNavigation();

            navigationWasStarted = false;
        }
    }

    private void initDirectionsRoute(Location location) {
        // Get route from predefined points.
        System.out.println("Location::::::::::::::::::::::::::::::::::::");
        System.out.println(location);

        double bearing = Float.valueOf(location.getBearing()).doubleValue();
        double tolerance = 45d;

        NavigationRoute.builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(ROUTE_ORIGIN, bearing, tolerance)
                .destination(ROUTE_DESTINATION)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .voiceUnits(DirectionsCriteria.METRIC)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null || response.body().routes().isEmpty()) {
                            return;
                        }

                        // Start navigation session with retrieved route.
                        DirectionsRoute route = response.body().routes().get(0);

                        //Get distance and duration during the navigation
                        distance[0] = response.body().routes().get(0).distance();
                        duration[0] = response.body().routes().get(0).duration();

                        mapboxNavigation.startNavigation(route);

                        // Set route progress.
                        VisionArManager.setRoute(new Route(
                                getRoutePoints(route),
                                route.duration().floatValue(),
                                "",
                                ""
                        ));
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                    }
                });
    }

    @Override
    public void onErrorReceived(Throwable throwable) {
        if (throwable != null) {
            throwable.printStackTrace();
        }

        mapboxNavigation.stopNavigation();
        Toast.makeText(this, "Can not calculate the route requested", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponseReceived(@NotNull DirectionsResponse response, RouteProgress routeProgress) {
        mapboxNavigation.stopNavigation();
        if (response.routes().isEmpty()) {
            Toast.makeText(this, "Can not calculate the route requested", Toast.LENGTH_SHORT).show();
        } else {
            DirectionsRoute route = response.routes().get(0);

            mapboxNavigation.startNavigation(route);

            // Set route progress.
            VisionArManager.setRoute(new Route(
                    getRoutePoints(route),
                    (float) routeProgress.durationRemaining(),
                    "",
                    ""
            ));
        }
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        Log.d(TAG, "onZCQProgressChange: " + routeProgress);
        lastRouteProgress = routeProgress;
    }

    @Override
    public void userOffRoute(Location location) {
        routeFetcher.findRouteFromRouteProgress(location, lastRouteProgress);
    }

    private RoutePoint[] getRoutePoints(@NotNull DirectionsRoute route) {
        ArrayList<RoutePoint> routePoints = new ArrayList<>();

        List<RouteLeg> legs = route.legs();
        if (legs != null) {
            for (RouteLeg leg : legs) {

                List<LegStep> steps = leg.steps();
                if (steps != null) {
                    for (LegStep step : steps) {
                        RoutePoint point = new RoutePoint((new GeoCoordinate(
                                step.maneuver().location().latitude(),
                                step.maneuver().location().longitude()
                        )), mapToManeuverType(step.maneuver().type()));

                        routePoints.add(point);

                        List<Point> geometryPoints = buildStepPointsFromGeometry(step.geometry());
                        for (Point geometryPoint : geometryPoints) {
                            point = new RoutePoint((new GeoCoordinate(
                                    geometryPoint.latitude(),
                                    geometryPoint.longitude()
                            )), ManeuverType.None);

                            routePoints.add(point);
                        }
                    }
                }
            }
        }

        return routePoints.toArray(new RoutePoint[0]);
    }

    private List<Point> buildStepPointsFromGeometry(String geometry) {
        return PolylineUtils.decode(geometry, Constants.PRECISION_6);
    }

    private ManeuverType mapToManeuverType(@Nullable String maneuver) {
        if (maneuver == null) {
            return ManeuverType.None;
        }
        switch (maneuver) {
            case "turn":
                return ManeuverType.Turn;
            case "depart":
                return ManeuverType.Depart;
            case "arrive":
                return ManeuverType.Arrive;
            case "merge":
                return ManeuverType.Merge;
            case "on ramp":
                return ManeuverType.OnRamp;
            case "off ramp":
                return ManeuverType.OffRamp;
            case "fork":
                return ManeuverType.Fork;
            case "roundabout":
                return ManeuverType.Roundabout;
            case "exit roundabout":
                return ManeuverType.RoundaboutExit;
            case "end of road":
                return ManeuverType.EndOfRoad;
            case "new name":
                return ManeuverType.NewName;
            case "continue":
                return ManeuverType.Continue;
            case "rotary":
                return ManeuverType.Rotary;
            case "roundabout turn":
                return ManeuverType.RoundaboutTurn;
            case "notification":
                return ManeuverType.Notification;
            case "exit rotary":
                return ManeuverType.RotaryExit;
            default:
                return ManeuverType.None;
        }
    }

    /**
     * Calculate calories through weight, distance and duration.
     * @return Calories in KCal
     */
    private double getCaloriesConsumed(){
        double k;
        double rate = 3.6;  // 1m/s = 3.6km/h
        double weight = 60; //60kg
        if( (distance[0] / duration[0]) * rate <= 8){ // <= 8km/h
            k = 0.1355;
        }else if ( (distance[0] / duration[0]) * rate <=12){ // <= 12km/h
            k = 0.1797;
        }else { // > 12km/h
            k = 0.1875;
        }
        double kCal = weight * (duration[0] / 60) * k;
        Log.d(TAG, "onZCQCaloriesConsumed: distance: " + distance[0]);
        Log.d(TAG, "onZCQCaloriesConsumed: duration: " + duration[0]);
        Log.d(TAG, "onZCQCaloriesConsumed: kcal: " + kCal);
        return Double.parseDouble(new DecimalFormat("#.00").format(kCal)); // Return the calories in KCal.
    }

    private void saveHistory(History history) {
        sharedPreferenceUtil.save("historyInstance", "historyInstance", SharedPreferenceUtil.Object2String(history), ArActivity.this);
    }
}
