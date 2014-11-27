package com.example.dasm.daniel.lozano;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
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

public class InsercionRegistro extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_insercion_registro);
		TextView textIdentificadorRegistro = (TextView)findViewById(R.id.textIdentificadorRegistro);
		textIdentificadorRegistro.setText("Nuevo registro");
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			try {
				JSONArray arrayJSON = new JSONArray(extras.getString("registros"));
				JSONObject registro = arrayJSON.getJSONObject(1);
				TextView txtDni = (TextView)findViewById(R.id.editTextDni);
				txtDni.setText(registro.getString("DNI"));
				txtDni.setFocusable(false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void insertarRegistro(View v){
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
			
			new InsercionBD().execute(json);
			
			Intent i=new Intent();
			i.putExtra("respuesta","Inserción realizada");
			setResult(RESULT_OK,i);
			super.onBackPressed();
			
		} catch (JSONException e) {
			onBackPressed();
			e.printStackTrace();
		}
	}

	private class InsercionBD extends AsyncTask <String, Void, Void> {

		private ProgressDialog pDialog;
		private final String URL = "http://demo.calamar.eui.upm.es/dasmapi/v1/miw22/fichas";


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(InsercionRegistro.this);
			pDialog.setMessage(getString(R.string.insert_progress_title));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			String json = params[0];
			String respuesta="";

			try {
				AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
				HttpPost httpPost = new HttpPost(URL);                
				StringEntity se = new StringEntity(json);
				httpPost.setEntity(se);        		
				HttpResponse response = httpclient.execute(httpPost);
				respuesta = EntityUtils.toString(response.getEntity());       
				httpclient.close();
			} catch (Exception e)  {
				Log.e(getString(R.string.app_name),e.toString());
				onBackPressed();
			}
			pDialog.dismiss();
			return null;
		}
	}
	
	@Override
	public void onBackPressed(){
		Intent i=new Intent();
		i.putExtra("respuesta","Inserción cancelada");
		setResult(RESULT_CANCELED,i);
		super.onBackPressed();
	}

}
