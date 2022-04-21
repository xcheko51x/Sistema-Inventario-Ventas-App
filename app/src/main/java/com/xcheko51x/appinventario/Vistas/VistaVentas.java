package com.xcheko51x.appinventario.Vistas;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.xcheko51x.appinventario.Adaptadores.AdapterVentas;
import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Producto;
import com.xcheko51x.appinventario.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VistaVentas extends AppCompatActivity {

    RecyclerView rvVentaProductos;
    TextView tvTotal;
    EditText etCodBarr, etPago;
    ImageButton ibtnBuscar;
    Button btnRegistrarVenta;
    ArrayList<Producto> listaProductos = new ArrayList<>();

    List<String> arrayVentas = new ArrayList<>();

    String codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_ventas);
        getSupportActionBar().setTitle("VENTA");

        rvVentaProductos = findViewById(R.id.rvVentaProductos);
        rvVentaProductos.setLayoutManager(new GridLayoutManager(this, 1));
        tvTotal = findViewById(R.id.tvTotal);
        etCodBarr = findViewById(R.id.etCodBarr);
        etPago = findViewById(R.id.etPago);
        ibtnBuscar = findViewById(R.id.ibtnBuscar);
        btnRegistrarVenta = findViewById(R.id.btnRegistrarVenta);

        ibtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codigo = etCodBarr.getText().toString();
                obtenerProducto();
            }
        });

        btnRegistrarVenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listaProductos.size() == 0) {
                    Toast.makeText(VistaVentas.this, "No hay productos a vender.", Toast.LENGTH_SHORT).show();
                } else {
                    if(etPago.getText().toString().equals("")) {
                        Toast.makeText(VistaVentas.this, "Ingresa el pago.", Toast.LENGTH_SHORT).show();
                    } else {

                        for(int i = 0 ; i < listaProductos.size() ; i++) {
                            //Toast.makeText(VistaVentas.this, listaProductos.get(i).getCantidad()+","+listaProductos.get(i).getIdProducto()+","+listaProductos.get(i).getNomProducto()+","+listaProductos.get(i).getPrecio(), Toast.LENGTH_SHORT).show();
                            arrayVentas.add(
                                    listaProductos.get(i).getCantidad()+","+listaProductos.get(i).getIdProducto()+","+listaProductos.get(i).getNomProducto()+","+listaProductos.get(i).getPrecio()
                            );
                        }

                        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                        String fecha = date.format(new Date());

                        if(Double.parseDouble(etPago.getText().toString()) < Double.parseDouble(tvTotal.getText().toString())) {
                            Toast.makeText(VistaVentas.this, "El pago debe ser mayor o igual al total.", Toast.LENGTH_SHORT).show();
                        } else {
                            registrarVenta(arrayVentas, fecha, tvTotal.getText().toString());

                            AlertDialog.Builder builder = new AlertDialog.Builder(VistaVentas.this);
                            LayoutInflater inflater = VistaVentas.this.getLayoutInflater();
                            builder.setTitle("CAMBIO");
                            View dialogVista = inflater.inflate(R.layout.alert_dialog_venta, null);
                            builder.setView(dialogVista);

                            TextView tvCambio = dialogVista.findViewById(R.id.tvCambio);

                            double cambio = Double.parseDouble(etPago.getText().toString()) - Double.parseDouble(tvTotal.getText().toString());
                            tvCambio.setText("" + cambio);

                            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                            builder.show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar_ventas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_producto:
                //Toast.makeText(this, "ADD PRODUCTO", Toast.LENGTH_SHORT).show();
                leerCodigo();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    // Metodo para registrar la venta
    public void registrarVenta(List<String> arrayVentas, String fecha, String total) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaVentas.this, "dbSistema", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        ContentValues registro = new ContentValues();

        String infoProductos = "";

        for(int i = 0 ; i < arrayVentas.size() ; i++) {
            infoProductos = infoProductos + arrayVentas.get(i)+",";
            String[] arrayCantidad = arrayVentas.get(i).split(",");
            actualizarAlmacen(arrayCantidad[0], arrayCantidad[1]);
        }

        //infoProductos = infoProductos + "'";

        registro.put("idProductos", infoProductos);
        registro.put("fechaVenta", fecha);
        registro.put("total", Double.parseDouble(total));

        // los inserto en la base de datos
        db.insert("ventas", null, registro);

        db.close();

        Toast.makeText(VistaVentas.this, "Datos agregados", Toast.LENGTH_LONG).show();
    }

    // Metodo para actualizar la cantidad en el almacen
    public void actualizarAlmacen(String cantidad, String idProducto) {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select almacen from productos where idProducto=?", new String[]{idProducto});

        if(fila != null && fila.getCount() != 0) {
                fila.moveToFirst();
                do {

                    int almacen = fila.getInt(0);
                    almacen = almacen - Integer.parseInt(cantidad);

                    ContentValues registro = new ContentValues();

                    registro.put("almacen", almacen);

                    db.update("productos", registro, "idProducto=?", new String[]{idProducto});

                } while(fila.moveToNext());
            } else {
                Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
            }

            db.close();
    }

    // Metodo para leer el codigo de barras
    public void leerCodigo() {
        IntentIntegrator intent = new IntentIntegrator(VistaVentas.this);
        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intent.setPrompt("ESCANEAR CÓDIGO");
        intent.setCameraId(0);
        intent.setBeepEnabled(false);
        intent.setBarcodeImageEnabled(false);
        intent.initiateScan();
    }

    // Lectura de respuesta del activity para el escaneo de codigo de barras
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelaste el escaneo", Toast.LENGTH_SHORT).show();
            } else {
                codigo = result.getContents();

                obtenerProducto();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Metodo para obtener los productos
    public void obtenerProducto() {
        //listaProductos.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from productos where idProducto=?", new String[]{codigo});

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do {

                if(fila.getInt(7) == 0) {
                    Toast.makeText(VistaVentas.this, "No hay en el almacen.", Toast.LENGTH_SHORT).show();
                } else {

                    if(listaProductos.size() == 0) {

                        listaProductos.add(
                          new Producto(
                                  1,
                                  fila.getString(0),
                                  fila.getString(2),
                                  fila.getDouble(6)
                          )
                        );
                    } else {
                        boolean bandera = false;

                        for(int i = 0 ; i < listaProductos.size() ; i++) {
                            if(fila.getString(0).equals(listaProductos.get(i).getIdProducto())) {
                                int aux = listaProductos.get(i).getCantidad();
                                aux++;

                                if(aux <= fila.getInt(7)) {
                                    listaProductos.get(i).setCantidad(aux);
                                } else {
                                    Toast.makeText(this, "Ya no hay en Almacen.", Toast.LENGTH_SHORT).show();
                                }

                                bandera = true;
                            }
                        }

                        if(bandera == false) {
                            listaProductos.add(
                                    new Producto(
                                            1,
                                            fila.getString(0),
                                            fila.getString(2),
                                            fila.getDouble(6)
                                    )
                            );
                        }
                    }
                }

            } while(fila.moveToNext());
        } else {
            Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();

        //Toast.makeText(VistaProductos.this, ""+listaProductos.size(), Toast.LENGTH_SHORT).show();
        AdapterVentas adaptador = new AdapterVentas(VistaVentas.this, listaProductos, tvTotal);
        rvVentaProductos.setAdapter(adaptador);

    }

}
