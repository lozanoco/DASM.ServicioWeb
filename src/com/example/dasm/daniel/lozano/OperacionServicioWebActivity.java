package com.example.dasm.daniel.lozano;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class OperacionServicioWebActivity extends Activity {

	private EditText dni;
	private final int actividad=2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_operacion_servicio_web);

		dni = (EditText)findViewById(R.id.editTextDni);
	}

	public void lanzarConsultaRegistros(View v){
		new ConsultaBD().execute(dni.getText().toString());
	}

	@Override
	public void onActivityResult(int actividad,int resultado,Intent datos){
		if(actividad==this.actividad){
			String respuesta=datos.getStringExtra("respuesta");
			Toast.makeText(OperacionServicioWebActivity.this, "Vuelvo de la actividad 2, respuesta:"+respuesta, Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(OperacionServicioWebActivity.this, "Resultado erroneo", Toast.LENGTH_SHORT).show();
		}

	}

	private class ConsultaBD extends AsyncTask <String, Void, String> {
        
        private ProgressDialog pDialog;
        private final String URL = "http://demo.calamar.eui.upm.es/dasmapi/v1/miw22/fichas";
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OperacionServicioWebActivity.this);
            pDialog.setMessage(getString(R.string.progress_title));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        @Override
        protected String doInBackground(String... dnis) {
        	String respuesta = getString(R.string.sin_respuesta);
        	String url_final = URL;
        	if(!dnis[0].equals("")){
        		url_final += "/"+dnis[0];
        	}
        	try {
        		AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
        		HttpGet httpget = new HttpGet(url_final);
        		HttpResponse response = httpclient.execute(httpget);
        		respuesta = EntityUtils.toString(response.getEntity());     
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
				if(numRegistros>0){
					Intent i = new Intent(OperacionServicioWebActivity.this,ConsultaRegistros.class);
					i.putExtra("registros", respuesta);
					startActivityForResult(i,actividad);
					mensaje = respuesta;
				}
	        	Toast.makeText(getBaseContext(), mensaje, Toast.LENGTH_LONG).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }

    }
}