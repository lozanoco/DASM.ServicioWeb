package com.example.dasm.daniel.lozano;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultaRegistros extends Activity {

	private JSONArray registros;
	private int numeroRegistros;
	private int contadorRegistros;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consulta_registros);

		Bundle extras = getIntent().getExtras();
		String mensaje = "No se han encontrado registros";
		if(extras != null) {
			try {
				registros = new JSONArray(extras.getString("registros"));
				numeroRegistros = registros.getJSONObject(0).getInt("NUMREG");
				contadorRegistros=1;
				showJSONregister(contadorRegistros);				

			} catch (JSONException e) {
				Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

		} 
	}

	private void showJSONregister(int numeroRegistro){
		try {
			TextView textIdentificadorRegistro = (TextView)findViewById(R.id.textIdentificadorRegistro);
			textIdentificadorRegistro.setText("Registro "+contadorRegistros+" de "+numeroRegistros);

			JSONObject registroMostrable = registros.getJSONObject(numeroRegistro);

			TextView txtDni = (TextView)findViewById(R.id.editTextDni);
			TextView txtNombre = (TextView)findViewById(R.id.editTextNombre);
			TextView txtApellidos = (TextView)findViewById(R.id.editTextApellidos);
			TextView txtDireccion = (TextView)findViewById(R.id.editTextDireccion);
			TextView txtTelefono = (TextView)findViewById(R.id.editTextTelefono);
			TextView txtEquipo = (TextView)findViewById(R.id.editTextEquipo);

			txtDni.setText(registroMostrable.getString("DNI"));
			txtDni.setFocusable(false);
			txtNombre.setText(registroMostrable.getString("Nombre"));
			txtNombre.setFocusable(false);
			txtApellidos.setText(registroMostrable.getString("Apellidos"));
			txtApellidos.setFocusable(false);
			txtDireccion.setText(registroMostrable.getString("Direccion"));
			txtDireccion.setFocusable(false);
			txtTelefono.setText(registroMostrable.getString("Telefono"));
			txtTelefono.setFocusable(false);
			txtEquipo.setText(registroMostrable.getString("Equipo"));
			txtEquipo.setFocusable(false);				
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void nextRegister(View v){
		contadorRegistros+=1;
		showJSONregister(contadorRegistros);
	}
	public void lastRegister(View v){
		contadorRegistros=numeroRegistros;
		showJSONregister(contadorRegistros);
	}
	public void previousRegister(View v){
		contadorRegistros-=1;
		showJSONregister(contadorRegistros);
	}
	public void firstRegister(View v){
		contadorRegistros=1;
		showJSONregister(contadorRegistros);
	}


	@Override
	public void onBackPressed(){
		Intent i=new Intent();
		i.putExtra("respuesta","Pulsado para Atras!");
		setResult(RESULT_OK,i);
		super.onBackPressed();
	}
}
