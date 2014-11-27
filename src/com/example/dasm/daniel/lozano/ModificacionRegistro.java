package com.example.dasm.daniel.lozano;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ModificacionRegistro extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_modificacion_registro);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			try {
				JSONArray arrayJSON = new JSONArray(extras.getString("registros"));
				JSONObject registroMostrable = arrayJSON.getJSONObject(1);
							
				TextView txtDni = (TextView)findViewById(R.id.editTextDni);
				TextView txtNombre = (TextView)findViewById(R.id.editTextNombre);
				TextView txtApellidos = (TextView)findViewById(R.id.editTextApellidos);
				TextView txtDireccion = (TextView)findViewById(R.id.editTextDireccion);
				TextView txtTelefono = (TextView)findViewById(R.id.editTextTelefono);
				TextView txtEquipo = (TextView)findViewById(R.id.editTextEquipo);
	
				txtDni.setText(registroMostrable.getString("DNI"));
				txtDni.setFocusable(false);
				txtNombre.setText(registroMostrable.getString("Nombre"));
				txtApellidos.setText(registroMostrable.getString("Apellidos"));
				txtDireccion.setText(registroMostrable.getString("Direccion"));
				txtTelefono.setText(registroMostrable.getString("Telefono"));
				txtEquipo.setText(registroMostrable.getString("Equipo"));
				
			} catch (JSONException e) {
				onBackPressed();
				e.printStackTrace();
			}
		}
	}
	public void actualizarRegistro(View v){
		String json=""; 
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("DNI", ((EditText)findViewById(R.id.editTextDni)).getText().toString());
			jsonObject.put("Nombre", ((EditText)findViewById(R.id.editTextNombre)).getText().toString());
			jsonObject.put("Apellidos", ((EditText)findViewById(R.id.editTextApellidos)).getText().toString());
			jsonObject.put("Direccion", ((EditText)findViewById(R.id.editTextDireccion)).getText().toString());
			jsonObject.put("Telefono", ((EditText)findViewById(R.id.editTextTelefono)).getText().toString());
			jsonObject.put("Equipo", ((EditText)findViewById(R.id.editTextEquipo)).getText().toString());
			json = jsonObject.toString();
			
			new ActualizacionBD().execute(json);
			
			Intent i=new Intent();
			i.putExtra("respuesta","Actualización realizada");
			setResult(RESULT_OK,i);
			super.onBackPressed();
			
		} catch (JSONException e) {
			onBackPressed();
			e.printStackTrace();
		}
	}

	private class ActualizacionBD extends AsyncTask <String, Void, String> {

		private ProgressDialog pDialog;
		private final String URL = "http://demo.calamar.eui.upm.es/dasmapi/v1/miw22/fichas";


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ModificacionRegistro.this);
			pDialog.setMessage(getString(R.string.update_progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String json = params[0];
			String respuesta="";
			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpPut httpUpdate = new HttpPut(URL);      		
				StringEntity se = new StringEntity(json);
				httpUpdate.setEntity(se);        		
				HttpResponse response = httpclient.execute(httpUpdate);
				respuesta = EntityUtils.toString(response.getEntity());       
				httpclient.close();
			} catch (Exception e)  {
				Log.e(getString(R.string.app_name),e.toString());
				onBackPressed();
			}
			return respuesta;
		}


		@Override
		protected void onPostExecute(String respuesta) {
			Intent i=new Intent();
			pDialog.dismiss();
			try {
				JSONArray arrayJSON = new JSONArray(respuesta);
				int numRegistros = arrayJSON.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros) {
				case -1: 
					setResult(RESULT_CANCELED,i);
					break;
				case 1: 
					setResult(RESULT_OK,i);
					break;
				default:
					setResult(RESULT_CANCELED,i);
					break;
				}
				onBackPressed();
			} catch (JSONException e) {
				onBackPressed();
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onBackPressed(){
		Intent i=new Intent();
		i.putExtra("respuesta","Borrado cancelado");
		setResult(RESULT_CANCELED,i);
		super.onBackPressed();
	}
}