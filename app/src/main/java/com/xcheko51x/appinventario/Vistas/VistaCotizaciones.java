package com.xcheko51x.appinventario.Vistas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.xcheko51x.appinventario.Adaptadores.AdapterCotizaciones;
import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Producto;
import com.xcheko51x.appinventario.Modelos.ProductoCotizacion;
import com.xcheko51x.appinventario.R;

import java.util.ArrayList;
import java.util.List;

public class VistaCotizaciones extends AppCompatActivity {

    EditText etCodigo;
    ImageButton ibtnEscaner, ibtnBuscar;
    RecyclerView rvCotizaciones;
    TextView tvTotal;

    List<ProductoCotizacion> listaCotizaciones = new ArrayList<>();

    AdapterCotizaciones adapterCotizaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_cotizaciones);
        getSupportActionBar().setTitle("COTIZACIONES");

        etCodigo = findViewById(R.id.etCodigo);
        ibtnEscaner = findViewById(R.id.ibtnEscaner);
        ibtnBuscar = findViewById(R.id.ibtnBuscar);
        rvCotizaciones = findViewById(R.id.rvCotizaciones);
        rvCotizaciones.setLayoutManager(new GridLayoutManager(this, 1));
        tvTotal = findViewById(R.id.tvTotal);

        tvTotal.setText("$0.0");

        ibtnEscaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escanearCodigo();
            }
        });
        
        ibtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etCodigo.getText().toString().equals("")) {
                    Toast.makeText(VistaCotizaciones.this, "No hay nada que buscar.", Toast.LENGTH_SHORT).show();
                } else {
                    buscarProducto();
                    etCodigo.setText("");
                }
            }
        });
        
    }

    public void buscarProducto() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("select * from productos where idProducto=? or nomProducto like ?", new String[]{etCodigo.getText().toString(), etCodigo.getText().toString()+"%"});

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();

            if(listaCotizaciones.size() == 0) {
                listaCotizaciones.add(
                        new ProductoCotizacion(
                                1,
                                fila.getString(0),
                                fila.getString(2),
                                fila.getDouble(6)
                        )
                );
            } else {
                boolean bandera = false;

                for(int i = 0 ; i < listaCotizaciones.size() ; i++) {
                    if(fila.getString(0).equals(listaCotizaciones.get(i).getIdProducto())) {
                        int aux = listaCotizaciones.get(i).getCantidad();
                        aux++;
                        listaCotizaciones.get(i).setCantidad(aux);
                        bandera = true;
                    }
                }

                if(bandera == false) {
                    listaCotizaciones.add(
                            new ProductoCotizacion(
                                    1,
                                    fila.getString(0),
                                    fila.getString(2),
                                    fila.getDouble(6)
                            )
                    );
                }
            }

            adapterCotizaciones = new AdapterCotizaciones(VistaCotizaciones.this, listaCotizaciones, tvTotal);
            rvCotizaciones.setAdapter(adapterCotizaciones);
        } else {
            Toast.makeText(this, "No hay coincidencias con el: "+etCodigo.getText().toString().toUpperCase(), Toast.LENGTH_LONG).show();
        }
    }

    public void escanearCodigo() {
        IntentIntegrator intent = new IntentIntegrator(VistaCotizaciones.this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("ESCANEAR CÓDIGO");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelaste el escaneo", Toast.LENGTH_SHORT).show();
            } else {
                etCodigo.setText(result.getContents());
            }
        }
    }
}
