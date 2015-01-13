package activity;

import java.io.IOException;
import java.io.InputStream;

import model.ResponseWs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LongRunningGetIO extends AsyncTask<Void, Void, String> {

	private ResponseWs responseWs;

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
			HttpResponse response = httpClient.execute(httpGet, localContext);
			HttpEntity entity = response.getEntity();
			text = getASCIIContentFromEntity(entity);
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}
		return text;
	}

	protected void onPostExecute(String results) {
		if (results != null) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
			responseWs = gson.fromJson(results, ResponseWs.class);
		}
	}

	public ResponseWs getResponseWs() {
		return responseWs;
	}

	public void setResponseWs(ResponseWs responseWs) {
		this.responseWs = responseWs;
	}

}
