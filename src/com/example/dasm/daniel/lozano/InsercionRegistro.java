package com.example.dasm.daniel.lozano;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InsercionRegistro extends Activity {

	
	private JSONArray registros;
	private int numeroRegistros;
	private int contadorRegistros;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consulta_registros);
	}
	public void lanzarInsercionRegistro(View v){
		new InsercionBD().execute();
	}

	@Override
	public void onActivityResult(int actividad,int resultado,Intent datos){
		if(actividad==1){
			Toast.makeText(InsercionRegistro.this, "Fin de la consulta", Toast.LENGTH_SHORT).show();
		}else{
			Toast.makeText(InsercionRegistro.this, "Resultado erroneo", Toast.LENGTH_SHORT).show();
		}

	}

	private class InsercionBD extends AsyncTask <Void, Void, String> {
        
        private ProgressDialog pDialog;
        private final String URL = "http://demo.calamar.eui.upm.es/dasmapi/v1/miw22/fichas";
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InsercionRegistro.this);
            pDialog.setMessage(getString(R.string.progress_title));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        @Override
        protected String doInBackground(Void... params) {
        	String respuesta = getString(R.string.sin_respuesta);
        	String url_final = URL;
        	
        	try {
        		AndroidHttpClient httpclient = AndroidHttpClient.newInstance("AndroidHttpClient");
        		HttpPost httpPost = new HttpPost(url_final);
        		String json=""; 
        		
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("DNI", ((EditText)findViewById(R.id.editTextDni)).getText().toString());
                jsonObject.put("Nombre", ((EditText)findViewById(R.id.editTextDni)).getText().toString());
                jsonObject.put("Apellidos", ((EditText)findViewById(R.id.editTextApellidos)).getText().toString());
                jsonObject.put("Direccion", ((EditText)findViewById(R.id.editTextDireccion)).getText().toString());
                jsonObject.put("Telefono", ((EditText)findViewById(R.id.editTextTelefono)).getText().toString());
                jsonObject.put("Equipo", ((EditText)findViewById(R.id.editTextEquipo)).getText().toString());
                
                json = jsonObject.toString();
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
        		
        		HttpResponse response = httpclient.execute(httpPost);
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

        	
			try {
				JSONArray arrayJSON = new JSONArray(respuesta);
				int numRegistros = arrayJSON.getJSONObject(0).getInt("NUMREG");
				switch(numRegistros){
				case -1: mensaje="Error en la insercion";					
					break;
				
				}
				Toast.makeText(InsercionRegistro.this,mensaje, Toast.LENGTH_SHORT).show();	
			} catch (JSONException e) {
				e.printStackTrace();
			}
			pDialog.dismiss();
        }




	

    }

}
