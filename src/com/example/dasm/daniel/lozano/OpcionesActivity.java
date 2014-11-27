package com.example.dasm.daniel.lozano;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;

public class OpcionesActivity extends PreferenceActivity {

	private boolean CONEXION_ESTABLECIDA=false;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencias);
	}

	public void consultarPreferencias() {
		SharedPreferences pref =
				PreferenceManager.getDefaultSharedPreferences(this);
		String url= pref.getString("menu_servidor","")+"/"+pref.getString("menu_usuario","")+"/connect/"+pref.getString("menu_clave", "");
		new ConexionBD().execute(url);
		
		Intent i=new Intent();
		setResult(RESULT_OK,i);
		super.onBackPressed();

	}

	@Override
	public void onBackPressed(){
		if(!CONEXION_ESTABLECIDA)consultarPreferencias();
		else{
			Toast.makeText(OpcionesActivity.this, "Conexión establecida", Toast.LENGTH_LONG).show();
			super.onBackPressed();
		}
	}

	private class ConexionBD extends AsyncTask <String, Void, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(OpcionesActivity.this);
			pDialog.setMessage(getString(R.string.progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... url) {
			String respuesta = getString(R.string.sin_respuesta);
			String url_final = ""+url[0];
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpGet httpPost = new HttpGet(url_final);                
				HttpResponse response = httpclient.execute(httpPost);
				respuesta = EntityUtils.toString(response.getEntity());  
				Log.d("",""+url_final);
				httpclient.close();
			} catch (Exception e)  {
				Log.e(getString(R.string.app_name),e.toString());
			}
			return respuesta;
		}


		@Override
		protected void onPostExecute(String respuesta) {
			String mensaje = getString(R.string.sin_datos); 
			pDialog.dismiss();

			try {
				JSONArray arrayJSON = new JSONArray(respuesta);
				int numRegistros = arrayJSON.getJSONObject(0).getInt("NUMREG");
				Log.d("NUMERO DE REGISTROS", "N: " + numRegistros);
				switch(numRegistros) {

				case 0: 
					CONEXION_ESTABLECIDA=true;
					onBackPressed();
					break;
				default: 
					mensaje = "No se ha posiso establecer la conexión con el servicio. Configure la conexión";
					Toast.makeText(OpcionesActivity.this, mensaje, Toast.LENGTH_LONG).show();
					break;
				}
			} catch (JSONException e) {
				mensaje = "No se ha posiso establecer la conexión con el servicio. Configure la conexión";
				Toast.makeText(OpcionesActivity.this, mensaje, Toast.LENGTH_LONG).show();
				e.printStackTrace();

			}

		}


	}
}
