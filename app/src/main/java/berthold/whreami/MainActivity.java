package berthold.whreami;
/**
 * An app telling you the street name and address of your
 * current location.
 */

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Local lib's
import berthold.trendAnalyzer.DataToAnalyze;

/**
 * Let's get started...
 */
public class MainActivity extends AppCompatActivity implements EnvironmentInterf {

    // Debug
    String tag;

    // Geocoder
    private Geocoder geocoder;
    private List<Address> addresses;

    // GPS
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String provider;

    // Calc
    private Location lastLoc;
    private float distanceTraveledIn_m, distanceTraveledSinceLastUpdateIn_m, speedIn_ms, getSpeedIn_kmH;

    // Timing
    private long nowIn_ms, lastUpdateIn_ms, lastUpdateIn_SEC;

    // UI
    private ProgressBar waitingForGpsView;
    private TextView gpsStatusView, gpsCoordinatesView, locationNameAndAdressView, speedView,avrSpeedView, heighView, distanceView,timeZoneView,sunriseView,sunsetView;


    //
    Handler handler;

    //
    Environment env;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI
        waitingForGpsView = findViewById(R.id.waiting_for_gps_progress);
        gpsStatusView = findViewById(R.id.gps_coordinates);
        gpsCoordinatesView = findViewById(R.id.gps_status_messages);
        timeZoneView=findViewById(R.id.time_zone_name);
        sunriseView=findViewById(R.id.time_of_sunrise);
        sunsetView=findViewById(R.id.time_of_sunset);

        handler = new Handler();

        // New
        locationNameAndAdressView = findViewById(R.id.name_and_address_of_current_location);
        speedView = findViewById(R.id.speed);
        avrSpeedView=findViewById(R.id.avarage_speed);
        heighView = findViewById(R.id.high);
        distanceView = findViewById(R.id.distance_traveled);

        // Debug
        tag = MainActivity.class.getSimpleName();

        // GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        if (env == null) {
            env = new Environment(this, locationManager, geocoder);
            env.start();
        }
    }

    /**
     * Get location data and update display.
     *
     * @param heightIn_m
     * @param getSpeedIn_kmH
     * @param distanceTraveledIn_m
     * @param lon
     * @param lat
     */
    @Override
    public void getEnviromentalData(final EnvironmentAddress environmentAddress, final float heightIn_m, final float getSpeedIn_kmH, final float avrSpeedIn_kmH, final float distanceTraveledIn_m, final Double lon, final Double lat) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                waitingForGpsView.setVisibility(View.GONE);

                String fullAddress=environmentAddress.getAddress();
                String city = environmentAddress.getCity();
                String state = environmentAddress.getState();
                String postalCode = environmentAddress.getPostalCode();
                String address = fullAddress+"\n\n"+postalCode + "\n" + city + "\n" + state;

                locationNameAndAdressView.setText(address);

                heighView.setText(heightIn_m + " m");

                speedView.setText(getSpeedIn_kmH + " km/h");

                avrSpeedView.setText("Avr:"+avrSpeedIn_kmH);

                distanceView.setText(distanceTraveledIn_m + " m");

                String statusText = "Long:" + lon + " Lat:" + lat;
                gpsCoordinatesView.setText(statusText);

                Calendar cal=Calendar.getInstance();

                int dayOfYear=EnvironmentSunriseSunsetTimeDateCalc.getDayOfCurrentYear();
                int offsetToGMT=EnvironmentSunriseSunsetTimeDateCalc.getTimeZoneOffsetIn_h();

                String nameOfTimeZone= EnvironmentSunriseSunsetTimeDateCalc.getCurrentTimezoneName()+" GMT+"+offsetToGMT;
                timeZoneView.setText(nameOfTimeZone);

                double sunriseTimeIn_h=EnvironmentSunsetSunriseCalc.getSunriseTimeAtObserversLocationIn_h(lon,lat,dayOfYear,offsetToGMT);
                sunriseView.setText("Sonnenaufgang:"+EnvironmentSunriseSunsetTimeDateCalc.getTimeInTwentyFourHourFormat(sunriseTimeIn_h)+" Uhr, Ortszeit");

                double sunsetTimeIn_h=EnvironmentSunsetSunriseCalc.getSunsetTimeAtObserversLocationIn_h(lon,lat,dayOfYear,offsetToGMT);
                sunsetView.setText("Sonnenuntergang:"+EnvironmentSunriseSunsetTimeDateCalc.getTimeInTwentyFourHourFormat(sunsetTimeIn_h)+" Uhr, Ortszeit.");
            }
        });
    }

    /**
     * Get status info like update time and/ or error messages...
     *
     * @param timestamp
     * @param lastUpdateIn_s
     * @param statusMessage
     */
    @Override
    public void getStatusData(Long timestamp, float lastUpdateIn_s, String statusMessage) {
        String status = "Last update:" + lastUpdateIn_s + "s // Status:" + statusMessage;
        gpsStatusView.setText(status);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
