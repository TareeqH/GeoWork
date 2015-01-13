package activity;

import model.Restaurant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.osm.geo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.myApp;

public class App2Activity extends Activity {
	TextView nameTxt;
	TextView addressTxt;
	TextView phoneTxt;
	TextView specialtyTxt;
	EditText priceEditTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		Intent intent = getIntent();
		final Restaurant restaurant = (Restaurant) intent
				.getSerializableExtra("restaurant");

		nameTxt = (TextView) findViewById(R.id.textView1);
		addressTxt = (TextView) findViewById(R.id.textView2);
		phoneTxt = (TextView) findViewById(R.id.textView3);
		specialtyTxt = (TextView) findViewById(R.id.textView4);
		priceEditTxt = (EditText) findViewById(R.id.editText1);

		nameTxt.setText(restaurant.getName());
		addressTxt.setText(restaurant.getAddress());
		phoneTxt.setText(restaurant.getPhone());
		specialtyTxt.setText(restaurant.getSpecialty());
		priceEditTxt.setText("" + restaurant.getPrix());

		final Button valider = (Button) findViewById(R.id.button1);
		valider.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				double prix = Double.parseDouble(priceEditTxt.getText()
						.toString());
				new RunningIO(restaurant.getId(), prix).execute();
			}
		});

		final Button retour = (Button) findViewById(R.id.button2);
		retour.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), myApp.class);
				// intent.putExtra("stwing", "FAAAAAKE !!!");
				startActivity(intent);
			}
		});
		// nameTxt = (TextView) findViewById(R.id.textView1);
		// nameTxt.setText(getIntent().getStringExtra("stwing"));
	}

	private class RunningIO extends AsyncTask<Void, Void, String> {
		private int id;
		private double prix;

		public RunningIO(int id, double prix) {
			super();
			this.id = id;
			this.prix = prix;
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(
					"http://192.168.1.87:8080/OpenStreetMapGeoWs/services/OsmGeo/updateRestaurant/"
							+ id + "/" + prix);
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "ERROOOOOOOR !",
						Toast.LENGTH_LONG).show();
			}
			return null;

		}
	}
}
