package com.xcheko51x.appinventario.Vistas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.xcheko51x.appinventario.Adaptadores.AdapterProductos;
import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Producto;
import com.xcheko51x.appinventario.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class VistaProductos extends AppCompatActivity {

    EditText etBusqueda;
    ImageView ivFoto;
    ImageButton ibtnBuscar, ibtnAdd, ibtnTomarFoto;
    RecyclerView rvProductos;

    EditText etCodigo, etNomProducto, etDescripcion, etModelo, etPrecio, etAlmacen;
    Spinner spiProveedor;

    ArrayList<String> listaProveedores = new ArrayList<>();
    ArrayList<Producto> listaProductos = new ArrayList<>();
    ArrayList<Producto> lista = new ArrayList<>();

    final int COD_FOTO = 20;
    //final String CARPETA_RAIZ = "MisFotosApp";
    final String CARPETA_RAIZ = "AppInventarioArchivos/";
    final String CARPETA_IMAGENES = "productos";
    final String RUTA_IMAGEN = CARPETA_RAIZ + CARPETA_IMAGENES;
    String path;

    String nombreImagen = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_productos);
        getSupportActionBar().setTitle("PRODUCTOS");

        etBusqueda = findViewById(R.id.etBusqueda);
        ibtnBuscar = findViewById(R.id.ibtnBuscar);
        ibtnAdd = findViewById(R.id.ibtnAdd);
        rvProductos = findViewById(R.id.rvProductos);
        rvProductos.setLayoutManager(new GridLayoutManager(this, 1));

        // PERMISOS PARA ANDROID 6 O SUPERIOR
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        // Llamada al metodo para obtener los productos
        obtenerProductos();

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

                // Si el EditText esta vacio muestra todos los productos
                if(etBusqueda.getText().toString().trim().equals("")) {
                    AdapterProductos adaptador = new AdapterProductos(VistaProductos.this, listaProductos);
                    rvProductos.setAdapter(adaptador);
                } else {

                    // Si el EditText no esta vacio recorre la lista para solo mostrar los productos que concuerden con el nombre o id del producto
                    for (int i = 0; i < listaProductos.size(); i++) {
                        if (etBusqueda.getText().toString().trim().toLowerCase().equals(listaProductos.get(i).getNomProducto().trim().toLowerCase()) ||
                                etBusqueda.getText().toString().trim().toLowerCase().equals(listaProductos.get(i).getIdProducto())) {
                            lista.add(listaProductos.get(i));
                        }
                    }

                    AdapterProductos adaptador = new AdapterProductos(VistaProductos.this, lista);
                    rvProductos.setAdapter(adaptador);
                }
            }
        });

        // Accion para el ImageButton de buscar
        ibtnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Abre un AlertDialog para escanear el codigo de barras a buscar
                AlertDialog.Builder builder = new AlertDialog.Builder(VistaProductos.this);
                LayoutInflater inflater = VistaProductos.this.getLayoutInflater();
                builder.setTitle("Buscar Producto");
                View dialogVista = inflater.inflate(R.layout.alert_dialog_buscar_productos, null);
                builder.setView(dialogVista);

                etCodigo = dialogVista.findViewById(R.id.etCodigo);

                IntentIntegrator intent = new IntentIntegrator(VistaProductos.this);
                intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intent.setPrompt("ESCANEAR CÓDIGO");
                intent.setCameraId(0);
                intent.setBeepEnabled(false);
                intent.setBarcodeImageEnabled(false);
                intent.initiateScan();

                builder.setPositiveButton("BUSCAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(etCodigo.getText().toString().trim().equals("")) {
                            AdapterProductos adaptador = new AdapterProductos(VistaProductos.this, listaProductos);
                            rvProductos.setAdapter(adaptador);
                        } else {

                            etBusqueda.setText(etCodigo.getText().toString());

                        }
                    }
                });

                builder.show();
            }
        });

        // Accion para añadir un producto
        ibtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Abre un AlertDialog para poner la informacion del producto a añadir
                AlertDialog.Builder builder = new AlertDialog.Builder(VistaProductos.this);
                LayoutInflater inflater = VistaProductos.this.getLayoutInflater();
                builder.setTitle("Agregar Producto");
                View dialogVista = inflater.inflate(R.layout.alert_dialog_productos, null);
                builder.setView(dialogVista);

                etCodigo = dialogVista.findViewById(R.id.etCodigo);
                final ImageButton ibtnEscaner = dialogVista.findViewById(R.id.ibtnEscaner);
                ivFoto = dialogVista.findViewById(R.id.ivFoto);
                ibtnTomarFoto = dialogVista.findViewById(R.id.ibtnTomarFoto);
                etNomProducto = dialogVista.findViewById(R.id.etNomProducto);
                etDescripcion = dialogVista.findViewById(R.id.etDescripcion);
                spiProveedor = dialogVista.findViewById(R.id.spiProveedor);
                etModelo = dialogVista.findViewById(R.id.etModelo);
                etPrecio = dialogVista.findViewById(R.id.etPrecio);
                etAlmacen = dialogVista.findViewById(R.id.etAlmacen);

                // Obtiene los proveedores
                obtenerProveedores();

                // Muestra los proveedores en el Spinner
                spiProveedor.setAdapter(new ArrayAdapter<String>(VistaProductos.this, android.R.layout.simple_spinner_item, listaProveedores));

                // Accion al presionar el ImageButton para obtener el codigo de barras del producto
                ibtnEscaner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentIntegrator intent = new IntentIntegrator(VistaProductos.this);
                        intent.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                        intent.setPrompt("ESCANEAR CÓDIGO");
                        intent.setCameraId(0);
                        intent.setBeepEnabled(false);
                        intent.setBarcodeImageEnabled(false);
                        intent.initiateScan();
                    }
                });

                // Accion al presionar el ImageButton para tomar la foto del producto
                ibtnTomarFoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tomarFoto(); // Metodo para abrir la camara y tomar la foto
                    }
                });

                // Accion al presionar aceptar en el AlertDialog
                builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Si falta algun campo muestra un mensaje informando
                        if(etCodigo.getText().toString().equals("") ||
                                etNomProducto.getText().toString().equals("") ||
                                etDescripcion.getText().toString().equals("") ||
                                etModelo.getText().toString().equals("") ||
                                etPrecio.getText().toString().equals("") ||
                                etAlmacen.getText().toString().equals("")) {

                            Toast.makeText(VistaProductos.this, "Debes llenar todos los campos.", Toast.LENGTH_SHORT).show();

                        } else {
                            // Si cumple con el llenado de los campos hacemos el registro del producto en la base de datos

                            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaProductos.this, "dbSistema", null, 1);
                            SQLiteDatabase db = admin.getWritableDatabase();

                            ContentValues registro = new ContentValues();

                            registro.put("idProducto", etCodigo.getText().toString());


                            // Convierte la foto en un arreglo de byte[] para poderlo insertar en el Blob de la base de datos;
                            ivFoto.buildDrawingCache();
                            byte[] imagen = getBitmapAsByteArray(ivFoto.getDrawingCache());
                            //String base64 = Base64.encodeToString(imagen, Base64.DEFAULT);*/

                            //registro.put("imagenProducto", nombreImagen);
                            registro.put("imagenProducto", nombreImagen+".png");
                            registro.put("nomProducto", etNomProducto.getText().toString());
                            registro.put("descripcion", etDescripcion.getText().toString());
                            registro.put("nomProveedor", spiProveedor.getSelectedItem().toString());
                            registro.put("modelo", etModelo.getText().toString());
                            registro.put("precio", etPrecio.getText().toString());
                            registro.put("almacen", etAlmacen.getText().toString());

                            // los inserto en la base de datos
                            db.insert("productos", null, registro);

                            db.close();

                            //Toast.makeText(VistaProductos.this, spiProveedor.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(VistaProductos.this, "Datos agregados", Toast.LENGTH_LONG).show();

                            // Obtenemos los productos
                            obtenerProductos();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    // Convierte la imagen (Foto) en un Arreglo de byte[]
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    // Metodo para tomar la foto
    public void tomarFoto() {
        File fileImagen = new File(Environment.getExternalStorageDirectory(), RUTA_IMAGEN);
        boolean isCreada = fileImagen.exists();

        if(isCreada == false) {
            isCreada = fileImagen.mkdirs();
        }

        if(isCreada == true) {
            nombreImagen = (System.currentTimeMillis() / 1000) + "";
        }

        path = Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+nombreImagen+".png";
        File imagen = new File(path);

        Intent intent = null;
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Permisos
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authorities = this.getPackageName()+".provider";
            Uri imageUri = FileProvider.getUriForFile(this, authorities, imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent, COD_FOTO);
    }

    // Metodo para obtener los productos
    public void obtenerProductos() {

        listaProductos.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from productos", null);

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do {
                listaProductos.add(
                        new Producto(
                                fila.getString(0),
                                fila.getString(1),
                                fila.getString(2),
                                fila.getString(3),
                                fila.getString(4),
                                fila.getString(5),
                                fila.getDouble(6),
                                fila.getInt(7)
                        )
                );
            } while(fila.moveToNext());
        } else {
            //Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();

        //Toast.makeText(VistaProductos.this, ""+listaProductos.size(), Toast.LENGTH_SHORT).show();
        AdapterProductos adaptador = new AdapterProductos(VistaProductos.this, listaProductos);
        rvProductos.setAdapter(adaptador);

    }

    // Metodo para obtener los proveedores
    public void obtenerProveedores() {
        listaProveedores.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select nomProveedor from proveedores", null);

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do {
                listaProveedores.add(
                        fila.getString(0)
                );
            } while(fila.moveToNext());
        } else {
            //Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();
    }

    // Leemos la respuesta del Activity de el escaneo y toma de fotos para poder tomar la desicion
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelaste el escaneo", Toast.LENGTH_SHORT).show();
            } else {
                etCodigo.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

            if(resultCode == RESULT_OK) {
                switch (requestCode) {
                    case COD_FOTO:
                        MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });

                        // Muestra la foto en el ImageView
                        Bitmap bitmap = BitmapFactory.decodeFile(path);
                        ivFoto.setImageBitmap(bitmap);

                        break;
                }
            }

        }
    }

}
