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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.xcheko51x.appinventario.Adaptadores.AdapterProveedores;
import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Proveedor;
import com.xcheko51x.appinventario.R;

import java.util.ArrayList;

public class VistaProveedores extends AppCompatActivity {

    EditText etBusqueda;
    ImageButton ibtnAdd;
    RecyclerView rvProveedores;

    ArrayList<Proveedor> listaProveedores = new ArrayList<>();
    ArrayList<Proveedor> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_proveedores);
        getSupportActionBar().setTitle("PROVEEDORES");

        etBusqueda = findViewById(R.id.etBusqueda);
        ibtnAdd = findViewById(R.id.ibtnAdd);
        rvProveedores = findViewById(R.id.rvProveedores);
        rvProveedores.setLayoutManager(new GridLayoutManager(this, 1));

        // Metodo para obtener los proveedores
        obtenerProveedores();

        // Lectura de eventos del EditText para la busqueda
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                lista.clear();

                if(etBusqueda.getText().toString().trim().equals("")) {
                    AdapterProveedores adaptador = new AdapterProveedores(VistaProveedores.this, listaProveedores);
                    rvProveedores.setAdapter(adaptador);
                } else {

                    for (int i = 0; i < listaProveedores.size(); i++) {
                        if (etBusqueda.getText().toString().trim().toLowerCase().equals(listaProveedores.get(i).getNomProveedor().trim().toLowerCase())) {
                            lista.add(listaProveedores.get(i));
                        }
                    }

                    AdapterProveedores adaptador = new AdapterProveedores(VistaProveedores.this, lista);
                    rvProveedores.setAdapter(adaptador);
                }
            }
        });

        // Accion para el ImageButton
        ibtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(VistaProveedores.this);
                LayoutInflater inflater = VistaProveedores.this.getLayoutInflater();
                builder.setTitle("Agregar Proveedor");
                View dialogVista = inflater.inflate(R.layout.alert_dialog_proveedores, null);
                builder.setView(dialogVista);

                final EditText etNomProveedor = dialogVista.findViewById(R.id.etNomProveedor);
                final EditText etTelefono = dialogVista.findViewById(R.id.etTelefono);
                final EditText etEmail = dialogVista.findViewById(R.id.etEmail);

                // Accion para cuando se pulsa aceptar en el AlertDialog
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Si falta algun campo se manda un aviso informando
                        if(etNomProveedor.getText().toString().equals("") || etTelefono.getText().toString().equals("") || etEmail.getText().toString().equals("")) {

                            Toast.makeText(VistaProveedores.this, "Debes llenar todos los campos.", Toast.LENGTH_SHORT).show();

                        } else { // Si se cumplen los campos registramos en la base de datos
                            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaProveedores.this, "dbSistema", null, 1);

                            SQLiteDatabase db = admin.getWritableDatabase();

                            ContentValues registro = new ContentValues();

                            registro.put("nomProveedor", etNomProveedor.getText().toString());
                            registro.put("telefono", etTelefono.getText().toString());
                            registro.put("email", etEmail.getText().toString());

                            // los inserto en la base de datos
                            db.insert("proveedores", null, registro);

                            db.close();

                            Toast.makeText(VistaProveedores.this, "Datos agregados", Toast.LENGTH_LONG).show();

                            obtenerProveedores();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    // Metodo para obtener los proveedores
    public void obtenerProveedores() {
        listaProveedores.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from proveedores", null);

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do {
                listaProveedores.add(
                        new Proveedor(
                                fila.getString(0),
                                fila.getString(1),
                                fila.getString(2)
                        )
                );
            } while(fila.moveToNext());
        } else {
            //Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();

        //Toast.makeText(VistaProveedores.this, ""+listaProveedores.size(), Toast.LENGTH_SHORT).show();
        AdapterProveedores adaptador = new AdapterProveedores(VistaProveedores.this, listaProveedores);
        rvProveedores.setAdapter(adaptador);
    }
}
