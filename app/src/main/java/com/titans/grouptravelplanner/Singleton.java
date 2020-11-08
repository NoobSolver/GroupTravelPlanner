package com.titans.grouptravelplanner;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * This class contains global variables
 */

public class Singleton {
    private static Singleton singleton;
    private Context context;
    private String TAG = "Singleton";
    private FirebaseUser firebaseUser;
    private boolean checkUserLogin = true;
    private Place sourcePlace;
    private Place destinationPlace;
    private int filterRange;
    private Location location;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private RequestQueue requestQueue;
    private CameraPosition googleMapCurrentCameraPosition;
    private UserModel loggedInUserModel;

    // assuming that new update is available, once fetched from remote config, will set to false
    private boolean updateAvailable = true;

    private Singleton(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        getLastLocation();
        requestQueue = getRequestQueue();
    }

    public static synchronized Singleton getInstance(Context context) {
        if (singleton == null) {
            singleton = new Singleton(context);
        }
        return singleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public Location getCurrentLocation() {
        if (getLocation() == null) {
            // fallback to last known location
            Log.d(TAG, "Fallback to getLastKnownLocation using Criteria and provider");
            if (locationManager == null) {
                locationManager =
                        (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            }
            Location location = null;
            try {
                Criteria criteria = new Criteria();
                location = locationManager.getLastKnownLocation(
                        locationManager.getBestProvider(criteria, false)
                );
            } catch (SecurityException e) {
                e.printStackTrace(); // permission denied
            } catch (Exception e) {
                e.printStackTrace();
            }
            getLastLocation(); // setting fused location again
            return location;
        } else {
            Log.d(TAG, "Using FusedLocationProviderClient to get last location");
            return getLocation();
        }
    }

    public FirebaseUser getFirebaseUser() {
        if (firebaseUser == null && checkUserLogin) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            checkUserLogin = false; // only one time
        }
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }

    public Place getSourcePlace() {
        return sourcePlace;
    }

    public void setSourcePlace(Place sourcePlace) {
        this.sourcePlace = sourcePlace;
    }

    public Place getDestinationPlace() {
        return destinationPlace;
    }

    public void setDestinationPlace(Place destinationPlace) {
        this.destinationPlace = destinationPlace;
    }

    public int getFilterRange() {
        if (filterRange != 0) {
            return filterRange;
        } else {
            return AppConstants.DEFAULT_RANGE;
        }
    }

    public void setFilterRange(int filterRange) {
        this.filterRange = filterRange;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    private void getLastLocation() {
        try {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> setLocation(location));
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }

    public CameraPosition getGoogleMapCurrentCameraPosition() {
        return googleMapCurrentCameraPosition;
    }

    public void setGoogleMapCurrentCameraPosition(CameraPosition googleMapCurrentCameraPosition) {
        this.googleMapCurrentCameraPosition = googleMapCurrentCameraPosition;
    }

    public UserModel getLoggedInUserModel() {
        return loggedInUserModel;
    }

    public void setLoggedInUserModel(UserModel loggedInUserModel) {
        this.loggedInUserModel = loggedInUserModel;
    }
}
