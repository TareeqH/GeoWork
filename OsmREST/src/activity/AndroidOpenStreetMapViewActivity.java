package activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AndroidOpenStreetMapViewActivity extends Activity {

	private MapView myOpenMapView;
	private IMapController myMapController;

	LocationManager locationManager;

	ArrayList<OverlayItem> overlayItemArray;
	ArrayList<OverlayItem> anotherOverlayItemArray;
	private static Context context;
	ResponseWs responseWs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		myOpenMapView = (MapView) findViewById(R.id.openmapview);
		myOpenMapView.setBuiltInZoomControls(true);
		myMapController = myOpenMapView.getController();
		myMapController.setZoom(20);
		AndroidOpenStreetMapViewActivity.context = getApplicationContext();
		// --- Create Overlay
		overlayItemArray = new ArrayList<OverlayItem>();

		DefaultResourceProxyImpl defaultResourceProxyImpl = new DefaultResourceProxyImpl(
				this);
		MyItemizedIconOverlay myItemizedIconOverlay = new MyItemizedIconOverlay(
				overlayItemArray, null, defaultResourceProxyImpl);
		myOpenMapView.getOverlays().add(myItemizedIconOverlay);
		// ---

		//

		// --- Create Another Overlay for multi marker
		anotherOverlayItemArray = new ArrayList<OverlayItem>();
		new LongRunningGetIO().execute();

		// buildPoints();
		// ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = new
		// ItemizedIconOverlay<OverlayItem>(
		// this, anotherOverlayItemArray, myOnItemGestureListener);
		// myOpenMapView.getOverlays().add(anotherItemizedIconOverlay);

		//
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// for demo, getLastKnownLocation from GPS only, not from NETWORK
		Location lastLocation = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (lastLocation != null) {
			updateLoc(lastLocation);
		}

		// Add Scale Bar
		ScaleBarOverlay myScaleBarOverlay = new ScaleBarOverlay(this);
		myOpenMapView.getOverlays().add(myScaleBarOverlay);

	}

	OnItemGestureListener<OverlayItem> myOnItemGestureListener = new OnItemGestureListener<OverlayItem>() {

		@Override
		public boolean onItemLongPress(int arg0, OverlayItem arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onItemSingleTapUp(int index, OverlayItem item) {

			Intent intent = new Intent(context, App2Activity.class);
			// intent.putExtra("restaurant", responseWs.getRestaurants()
			// .get(index));
			intent.putExtra("stwing", "FAAAAAKE !!!");
			startActivity(intent);

			// Toast.makeText(
			// AndroidOpenStreetMapViewActivity.this,
			// item.getSnippet() + "\n" + item.getTitle() + "\n"
			// + item.getPoint().getLatitudeE6() + " : "
			// + item.getPoint().getLongitudeE6(),
			// Toast.LENGTH_LONG).show();
			return true;
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

	private void updateLoc(Location loc) {
		GeoPoint locGeoPoint = new GeoPoint(loc.getLatitude(),
				loc.getLongitude());
		myMapController.setCenter(locGeoPoint);

		setOverlayLoc(loc);

		myOpenMapView.invalidate();
	}

	private void setOverlayLoc(Location overlayloc) {
		GeoPoint overlocGeoPoint = new GeoPoint(overlayloc);
		// ---
		overlayItemArray.clear();

		OverlayItem newMyLocationItem = new OverlayItem("My Location",
				"My Location", overlocGeoPoint);
		overlayItemArray.add(newMyLocationItem);
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

	private class MyItemizedIconOverlay extends
			ItemizedIconOverlay<OverlayItem> {

		public MyItemizedIconOverlay(
				List<OverlayItem> pList,
				org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener<OverlayItem> pOnItemGestureListener,
				ResourceProxy pResourceProxy) {
			super(pList, pOnItemGestureListener, pResourceProxy);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void draw(Canvas canvas, MapView mapview, boolean arg2) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapview, arg2);

			if (!overlayItemArray.isEmpty()) {

				// overlayItemArray have only ONE element only, so I hard code
				// to get(0)
				GeoPoint in = overlayItemArray.get(0).getPoint();

				Point out = new Point();
				mapview.getProjection().toPixels(in, out);

				Bitmap bm = BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_menu_mylocation);
				canvas.drawBitmap(bm, out.x - bm.getWidth() / 2, // shift the
																	// bitmap
																	// center
						out.y - bm.getHeight() / 2, // shift the bitmap center
						null);
			}
		}

		@Override
		public boolean onSingleTapUp(MotionEvent event, MapView mapView) {
			// TODO Auto-generated method stub
			// return super.onSingleTapUp(event, mapView);
			return true;
		}
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
						anotherOverlayItemArray.add(item);
					}
					ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(
							context, anotherOverlayItemArray,
							myOnItemGestureListener);
					myOpenMapView.getOverlays().add(anotherItemizedIconOverlay);
					myOpenMapView.invalidate();
				}

			}
		}

	}

}