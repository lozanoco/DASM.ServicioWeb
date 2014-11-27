package com.example.dasm.daniel.lozano;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import android.widget.Toast;

public class OperacionServicioWebActivity extends Activity {

	private EditText dni;
	private int operacion;
	private final int ACT_CONSULTA=1;
	private final int ACT_INSERCION=2;
	private final int ACT_BORRADO=3;
	private final int ACT_MODIFICACION=4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_operacion_servicio_web);
		operacion=0;

		dni = (EditText)findViewById(R.id.editTextDni);
	}

	public void lanzarConsultaRegistros(View v){
		operacion=ACT_CONSULTA;
		new ConsultaBD().execute(dni.getText().toString());
	}

	public void lanzarInsercionRegistro(View v){
		operacion=ACT_INSERCION;
		new ConsultaBD().execute(dni.getText().toString());
	}
	
	public void lanzarBorradoRegistro(View v){
		operacion=ACT_BORRADO;
		new ConsultaBD().execute(dni.getText().toString());
	}
	
	public void lanzarActualizacionRegistro(View v){
		operacion=ACT_MODIFICACION;
		new ConsultaBD().execute(dni.getText().toString());
	}

	@Override
	public void onActivityResult(int actividad,int resultado,Intent datos){
		if(resultado==RESULT_OK){
			switch(actividad){
			case ACT_CONSULTA: Toast.makeText(OperacionServicioWebActivity.this, "Consulta finalizada", Toast.LENGTH_SHORT).show();
			break;
			case ACT_INSERCION: Toast.makeText(OperacionServicioWebActivity.this, "Inserción realizada con exito", Toast.LENGTH_SHORT).show();
			break;
			case  ACT_BORRADO: Toast.makeText(OperacionServicioWebActivity.this, "Borrado realizado con exito", Toast.LENGTH_SHORT).show();
			break;	
			case ACT_MODIFICACION: Toast.makeText(OperacionServicioWebActivity.this, "Actualización realizada con exito", Toast.LENGTH_SHORT).show();
			break;
			default:Toast.makeText(OperacionServicioWebActivity.this, "No se ha completado la accion con exito", Toast.LENGTH_SHORT).show();
			}
		}else if(resultado==RESULT_CANCELED){
			switch(actividad){
			case ACT_INSERCION: Toast.makeText(OperacionServicioWebActivity.this, "Inserción cancelada", Toast.LENGTH_SHORT).show();
			break;
			case  ACT_BORRADO: Toast.makeText(OperacionServicioWebActivity.this, "Borrado cancelado", Toast.LENGTH_SHORT).show();
			break;	
			case ACT_MODIFICACION: Toast.makeText(OperacionServicioWebActivity.this, "Actualización cancelada", Toast.LENGTH_SHORT).show();
			break;
			default:Toast.makeText(OperacionServicioWebActivity.this, "No se ha completado la accion con exito", Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(OperacionServicioWebActivity.this, "No se ha completado la operación con exito", Toast.LENGTH_SHORT).show();
		}
		dni.setText("");
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
			Intent i;
			try {
				JSONArray arrayJSON = new JSONArray(respuesta);
				int numRegistros = arrayJSON.getJSONObject(0).getInt("NUMREG");
				pDialog.dismiss();
				switch(numRegistros) {
				case -1: 
					mensaje = "La consulta genera un error";
					Toast.makeText(OperacionServicioWebActivity.this, mensaje, Toast.LENGTH_LONG).show();
					break;
				case 0: 
					if(operacion==ACT_INSERCION){
						i = new Intent(OperacionServicioWebActivity.this,InsercionRegistro.class);
						i.putExtra("registros", respuesta);
						startActivityForResult(i,ACT_INSERCION);
					}else{						
						mensaje = "Registro no existente";
						Toast.makeText(OperacionServicioWebActivity.this, mensaje, Toast.LENGTH_LONG).show();
					}
					break;
				case 1: 
					switch(operacion){
					case ACT_CONSULTA:
						i = new Intent(OperacionServicioWebActivity.this,ConsultaRegistros.class);
						i.putExtra("registros", respuesta);
						startActivityForResult(i,ACT_CONSULTA);
						break;
					case ACT_BORRADO:
						i = new Intent(OperacionServicioWebActivity.this,BorradoRegistro.class);
						i.putExtra("registros", respuesta);
						startActivityForResult(i,ACT_BORRADO);
						break;
					case ACT_MODIFICACION:
						i = new Intent(OperacionServicioWebActivity.this,ModificacionRegistro.class);
						i.putExtra("registros", respuesta);
						startActivityForResult(i,ACT_MODIFICACION);
						break;
					case ACT_INSERCION:
						mensaje = "Registro existente";
						Toast.makeText(OperacionServicioWebActivity.this, mensaje, Toast.LENGTH_LONG).show();
						break;
					default:
						mensaje = "Error al encontrar 1 registro, imposible llegar aqui";
						Toast.makeText(OperacionServicioWebActivity.this, mensaje, Toast.LENGTH_LONG).show();
						break;
					}
					break;
				default:
					switch(operacion){
					case ACT_CONSULTA:
						i = new Intent(OperacionServicioWebActivity.this,ConsultaRegistros.class);
						i.putExtra("registros", respuesta);
						startActivityForResult(i,ACT_CONSULTA);
						break;
					default:
						Toast.makeText(OperacionServicioWebActivity.this, "Debe introducir un DNI", Toast.LENGTH_LONG).show();
						break;
					}

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}
}