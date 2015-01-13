package app;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import model.ResponseWs;
import model.Restaurant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.osm.geo.R;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import activity.App2Activity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class myApp extends Activity {

	private MapView myOpenMapView;
	private IMapController myMapController;
	ArrayList<OverlayItem> overlayItemArray;
	private Context context;
	MyLocationNewOverlay myLocationOverlay = null;
	ResponseWs responseWs;
	LocationManager locationManager;
	private boolean firtLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		myOpenMapView = (MapView) findViewById(R.id.openmapview);
		myOpenMapView.setBuiltInZoomControls(true);
		myMapController = myOpenMapView.getController();
		myMapController.setZoom(18);
		context = getApplicationContext();
		overlayItemArray = new ArrayList<OverlayItem>();
		new LongRunningGetIO().execute();

		// Add Scale Bar
		ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
		myOpenMapView.getOverlays().add(myScaleBarOverlay);
		// Add MyLocationOverlay
		// myLocationOverlay = new MyLocationNewOverlay(this, myOpenMapView);
		// myLocationOverlay.enableMyLocation();
		// myOpenMapView.getOverlays().add(myLocationOverlay);
		// myOpenMapView.postInvalidate();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location lastLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		firtLocation = true;
		if (lastLocation != null) {
			updateLoc(lastLocation);
		}

	}

	private void updateLoc(Location loc) {
		GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(),
				loc.getLongitude());
		if (firtLocation) {
			myMapController.setCenter(locGeoPoint);
			firtLocation = false;
		}

		setOverlayLoc(loc);

		myOpenMapView.invalidate();
	}

	private void setOverlayLoc(Location overlayloc) {
		GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);
		// ---
		overlayItemArray.clear();

		OverlayItem newMyLocationItem = new OverlayItem("My Location",
				"My Location", overlocGeoPoint);
		newMyLocationItem.setMarker(this.getResources().getDrawable(
				R.drawable.ic_menu_mylocation));
		overlayItemArray.add(newMyLocationItem);
		new LongRunningGetIO().execute();
		// ---
	}

	private LocationListener myLocationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			updateLoc(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, myLocationListener);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, myLocationListener);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(myLocationListener);
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();
			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);
				if (n > 0)
					out.append(new String(b, 0, n));
			}
			return out.toString();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(
					"http://192.168.1.87:8080/OpenStreetMapGeoWs/services/OsmGeo/getAll");
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
				text = getASCIIContentFromEntity(entity);
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
			return text;
		}

		protected void onPostExecute(String results) {
			if (results != null) {
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd")
						.create();
				responseWs = gson.fromJson(results, ResponseWs.class);
				if (responseWs != null) {
					for (Restaurant restaurant : responseWs.getRestaurants()) {
						OverlayItem item = new OverlayItem(""
								+ restaurant.getId(), restaurant.getName(),
								restaurant.getAddress(), new GeoPoint(
										restaurant.getLatitude(),
										restaurant.getLongitude()));
						Drawable icon;
						switch (restaurant.getRate()) {
						case "V":
							icon = context.getResources().getDrawable(
									R.drawable.b_marker);
							break;
						case "R":
							icon = context.getResources().getDrawable(
									R.drawable.r_marker);
							break;
						default:
							continue;
						}
						item.setMarker(icon);
						overlayItemArray.add(item);
					}
					ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(
							context, overlayItemArray, myOnItemGestureListener);
					myOpenMapView.getOverlays().add(anotherItemizedIconOverlay);
					myOpenMapView.invalidate();
				}

			}
		}

	}

	OnItemGestureListener<OverlayItem> myOnItemGestureListener = new OnItemGestureListener<OverlayItem>() {

		@Override
		public boolean onItemLongPress(int arg0, OverlayItem arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onItemSingleTapUp(int index, OverlayItem item) {
			//
			Intent intent = new Intent(context, App2Activity.class);
			intent.putExtra("restaurant", responseWs.getRestaurants()
					.get(index));
			// intent.putExtra("stwing", "FAAAAAKE !!!");
			startActivity(intent);

			// Toast.makeText(
			// myApp.this,
			// item.getSnippet() + "\n" + item.getTitle() + "\n"
			// + item.getPoint().getLatitudeE6() + " : "
			// + item.getPoint().getLongitudeE6(),
			// Toast.LENGTH_LONG).show();
			return true;
		}

	};

}
