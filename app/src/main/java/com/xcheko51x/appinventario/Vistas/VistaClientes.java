package com.xcheko51x.appinventario.Vistas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.xcheko51x.appinventario.Adaptadores.AdapterClientes;
import com.xcheko51x.appinventario.Adaptadores.AdapterProductos;
import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Cliente;
import com.xcheko51x.appinventario.Modelos.Producto;
import com.xcheko51x.appinventario.R;

import java.util.ArrayList;

public class VistaClientes extends AppCompatActivity {

    EditText etBusqueda, etNombre, etNumTelefono, etEmail, etDireccion, etRfc, etTipoPersona, etObservaciones;
    ImageButton ibtnBuscar, ibtnAdd;
    RecyclerView rvClientes;

    ArrayList<Cliente> listaClientes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_clientes);
        getSupportActionBar().setTitle("CLIENTES");

        etBusqueda = findViewById(R.id.etBusqueda);
        ibtnBuscar = findViewById(R.id.ibtnBuscar);
        ibtnAdd = findViewById(R.id.ibtnAdd);

        rvClientes = findViewById(R.id.rvClientes);
        rvClientes.setLayoutManager(new GridLayoutManager(this, 1));

        // Obtiene los clientes
        obtenerClientes();

        ibtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Abre un AlertDialog para poner la informacion del producto a añadir
                AlertDialog.Builder builder = new AlertDialog.Builder(VistaClientes.this);
                LayoutInflater inflater = VistaClientes.this.getLayoutInflater();
                builder.setTitle("Agregar Cliente");
                View dialogVista = inflater.inflate(R.layout.alert_dialog_clientes, null);
                builder.setView(dialogVista);

                etNombre = dialogVista.findViewById(R.id.etNombre);
                etNumTelefono = dialogVista.findViewById(R.id.etNumTelefono);
                etEmail = dialogVista.findViewById(R.id.etEmail);
                etDireccion = dialogVista.findViewById(R.id.etDireccion);
                etRfc = dialogVista.findViewById(R.id.etRfc);
                etTipoPersona = dialogVista.findViewById(R.id.etTipoPersona);
                etObservaciones = dialogVista.findViewById(R.id.etObservaciones);


                // Accion al presionar aceptar en el AlertDialog
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Si falta algun campo muestra un mensaje informando
                        if(etNombre.getText().toString().equals("") ||
                                etNumTelefono.getText().toString().equals("") ||
                                etEmail.getText().toString().equals("") ||
                                etDireccion.getText().toString().equals("") ||
                                etRfc.getText().toString().equals("") ||
                                etTipoPersona.getText().toString().equals("") ||
                                etObservaciones.getText().toString().equals("")) {

                            Toast.makeText(VistaClientes.this, "Debes llenar todos los campos.", Toast.LENGTH_SHORT).show();

                        } else {

                            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaClientes.this, "dbSistema", null, 1);
                            SQLiteDatabase db = admin.getWritableDatabase();

                            ContentValues registro = new ContentValues();

                            registro.put("nomCliente", etNombre.getText().toString());
                            registro.put("numTelefono", etNumTelefono.getText().toString());
                            registro.put("email", etEmail.getText().toString());
                            registro.put("direccion", etDireccion.getText().toString());
                            registro.put("rfc", etRfc.getText().toString());
                            registro.put("tipoPersona", etTipoPersona.getText().toString());
                            registro.put("observaciones", etObservaciones.getText().toString());

                            // los inserto en la base de datos
                            db.insert("clientes", null, registro);

                            db.close();

                            //Toast.makeText(VistaProductos.this, spiProveedor.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(VistaClientes.this, "Datos agregados", Toast.LENGTH_LONG).show();

                            // Obtenemos los clientes
                            obtenerClientes();
                        }

                    }
                });
                builder.show();
            }
        });

    }

    // Metodo para obtener los productos
    public void obtenerClientes() {

        listaClientes.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaClientes.this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from clientes", null);

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do {
                listaClientes.add(
                        new Cliente(
                                fila.getString(0),
                                fila.getString(1),
                                fila.getString(2),
                                fila.getString(3),
                                fila.getString(4),
                                fila.getString(5),
                                fila.getString(6),
                                fila.getString(7)
                        )
                );
            } while(fila.moveToNext());
        } else {
            //Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();

        //Toast.makeText(VistaClientes.this, ""+listaClientes.size(), Toast.LENGTH_SHORT).show();
        AdapterClientes adaptador = new AdapterClientes(VistaClientes.this, listaClientes);
        rvClientes.setAdapter(adaptador);

    }
}
