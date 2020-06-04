package com.ses3a.u_argo;
import com.ses3a.u_argo.tools.CustomToast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.ses3a.u_argo.eneities.History;
import com.ses3a.u_argo.tools.LocationUtil;
import com.ses3a.u_argo.tools.SharedPreferenceUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Use the query feature to select a building, get its geometry, and draw a polygon highlighting it.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        MapboxMap.OnMapClickListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private LatLng startPoint;

    ArrayList<History> historyList;
    SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil();

    private static final LatLng BOUND_CORNER_NW = new LatLng(-33.879520, 151.194502);
    private static final LatLng BOUND_CORNER_SE = new LatLng(-33.889531, 151.206090);
    private static final LatLngBounds RESTRICTED_BOUNDS_AREA = new LatLngBounds.Builder().include(BOUND_CORNER_NW).include(BOUND_CORNER_SE).build();

    private CustomToast ToastCustom;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ToastCustom.getInstance(this).show("Let’s get into AR mode, please choose one building you want to go", 8000);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main);

        Button historyBtn = findViewById(R.id.btn_history);
        historyBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

//        final Button arCamera = findViewById(R.id.arCamera);
//        arCamera.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent=new Intent(MainActivity.this,ArActivity.class);
//
//                Bundle destination = new Bundle();
//                destination.putDouble("latitude", startPoint.getLatitude());
//                destination.putDouble("longitude", startPoint.getLongitude());
//                intent.putExtra("destination",destination);
//
//                startActivity(intent);
//            }
//        });

//        final Button normalMap = findViewById(R.id.normalMap);
//        normalMap.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent=new Intent(MainActivity.this,NormalMap.class);
//
//                Bundle destination = new Bundle();
//                destination.putDouble("latitude", startPoint.getLatitude());
//                destination.putDouble("longitude", startPoint.getLongitude());
//                intent.putExtra("destination",destination);
//
//                startActivity(intent);
//            }
//        });

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        MainActivity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapboxMap.setLatLngBoundsForCameraTarget(RESTRICTED_BOUNDS_AREA);
                style.addSource(new GeoJsonSource("source-id"));

                style.addLayer(new FillLayer("layer-id", "source-id").withProperties(
                        PropertyFactory.fillColor(Color.parseColor("#8A8ACB"))
                ));

                mapboxMap.addOnMapClickListener(MainActivity.this);

                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_red);
                mapboxMap.getStyle().addImage("my-marker",bm);

                SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

                symbolManager.setIconAllowOverlap(true);
                symbolManager.setTextAllowOverlap(true);
                symbolManager.setIconIgnorePlacement(true);

                symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(-33.88358927248891, 151.20111371633055))
                        .withIconImage("my-marker")
                        .withIconSize(0.5f)
                        .withTextField("Building 1")
                        .withIconOffset(new Float[] {0f,-1.5f})
                        .withTextHaloColor("rgba(255, 255, 255, 100)")
                        .withTextHaloWidth(5.0f)
                        .withTextAnchor("top")
                        .withTextOffset(new Float[] {0f, 1.5f})
                );
                symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(-33.88094231843857, 151.2013071351103))
                        .withIconImage("my-marker")
                        .withIconSize(0.5f)
                        .withTextField("Building 8")
                        .withIconOffset(new Float[] {0f,-1.5f})
                        .withTextHaloColor("rgba(255, 255, 255, 100)")
                        .withTextHaloWidth(5.0f)
                        .withTextAnchor("top")
                        .withTextOffset(new Float[] {0f, 1.5f})
                );
                symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(-33.88350025713034, 151.1989419327386))
                        .withIconImage("my-marker")
                        .withIconSize(0.5f)
                        .withTextField("Building 10")
                        .withIconOffset(new Float[] {0f,-1.5f})
                        .withTextHaloColor("rgba(255, 255, 255, 100)")
                        .withTextHaloWidth(5.0f)
                        .withTextAnchor("top")
                        .withTextOffset(new Float[] {0f, 1.5f})
                );
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull final LatLng point) {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                final PointF finalPoint = mapboxMap.getProjection().toScreenLocation(point);
                List<Feature> features = mapboxMap.queryRenderedFeatures(finalPoint, "building");
                if (features.size() > 0) {
                    GeoJsonSource selectedBuildingSource = style.getSourceAs("source-id");
                    if (selectedBuildingSource != null) {
                        selectedBuildingSource.setGeoJson(FeatureCollection.fromFeatures(features));
                        startPoint = point;

                        //Navigate to the AR Navigation page
                        Intent intent = new Intent(MainActivity.this, ArActivity.class);

                        Bundle destination = new Bundle();
                        destination.putDouble("latitude", startPoint.getLatitude());
                        destination.putDouble("longitude", startPoint.getLongitude());
                        intent.putExtra("destination", destination);

                        startActivity(intent);
                    }
                }
            }
        });

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String historyInstance = (String) (sharedPreferenceUtil.get("historyInstance", "historyInstance", MainActivity.this));
        History history;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (historyInstance != null) {
            history = (History) SharedPreferenceUtil.String2Object(historyInstance);
            Date endTime = new Date();
            try {
                Date startTime = df.parse(history.getStartTime());
                long l = endTime.getTime() - startTime.getTime();
                long hour = l / (1000 * 60 * 60);
                long min = ((l / (60 * 1000)) - hour * 60);
                String result;
                if (hour >= 1) {
                    result = hour + " h" + min + "min";
                } else {
                    result = min + "min";
                }
                history.setDuration(result);
                saveHistory(history);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private void saveHistory(History history) {
        String historyString = (String) (sharedPreferenceUtil.get("history", "history", MainActivity.this));
        historyList = (ArrayList<History>) SharedPreferenceUtil.String2Object(historyString);
        if (historyList == null) {
            historyList = new ArrayList<>();
        }
        historyList.add(history);
        sharedPreferenceUtil.save("history", "history", SharedPreferenceUtil.Object2String(historyList), MainActivity.this);
        sharedPreferenceUtil.remove("historyInstance", "historyInstance", MainActivity.this);
    }
}

