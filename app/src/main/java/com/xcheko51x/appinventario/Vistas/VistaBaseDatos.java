package com.xcheko51x.appinventario.Vistas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VistaBaseDatos extends AppCompatActivity {

    TextView tvFechaRespaldo;
    CheckBox cbBackupProv, cbBackupProd, cbBackupVent;
    CheckBox cbRestoreProv, cbRestoreProd, cbRestoreVent;
    CheckBox cbClearProv, cbClearProd, cbClearVent;
    Button btnBackup, btnRestore, btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_base_datos);
        getSupportActionBar().hide();

        tvFechaRespaldo = findViewById(R.id.tvFechaRespaldo);
        cbBackupProv = findViewById(R.id.cbBackupProv);
        cbBackupProd = findViewById(R.id.cbBackupProd);
        cbBackupVent = findViewById(R.id.cbBackupVent);
        cbRestoreProv = findViewById(R.id.cbRestoreProv);
        cbRestoreProd = findViewById(R.id.cbRestoreProd);
        cbRestoreVent = findViewById(R.id.cbRestoreVent);
        cbClearProv = findViewById(R.id.cbClearProv);
        cbClearProd = findViewById(R.id.cbClearProd);
        cbClearVent = findViewById(R.id.cbClearVent);
        btnBackup = findViewById(R.id.btnBackup);
        btnRestore = findViewById(R.id.btnRestore);
        btnClear = findViewById(R.id.btnClear);

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        tvFechaRespaldo.setText("Fecha último backup total: " + preferences.getString("fechaUltimoBackup", "No hay respaldo"));

        solicitarPermisos();

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbBackupProv.isChecked()) {
                    backupTablasCSV("proveedores");
                }

                if(cbBackupProd.isChecked()) {
                    backupTablasCSV("productos");
                }

                if(cbBackupVent.isChecked()) {
                    backupTablasCSV("ventas");
                }
                
                if(!cbBackupProv.isChecked() && !cbBackupProd.isChecked() && !cbBackupVent.isChecked()) {
                    Toast.makeText(VistaBaseDatos.this, "No hay tabla seleccionada a respaldar.", Toast.LENGTH_SHORT).show();
                }

                if(cbBackupProv.isChecked() && cbBackupProd.isChecked() && cbBackupVent.isChecked()) {
                    SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                    String fecha = date.format(new Date());

                    SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();

                    editor.putString("fechaUltimoBackup", fecha);
                    tvFechaRespaldo.setText("Fecha último backup total: "+fecha);

                    editor.commit();
                }
            }
        });

        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbRestoreProv.isChecked()) {
                    restoreTablasCSV("proveedores");
                }

                if(cbRestoreProd.isChecked()) {
                    restoreTablasCSV("productos");
                }

                if(cbRestoreVent.isChecked()) {
                    restoreTablasCSV("ventas");
                }

                if(!cbRestoreProv.isChecked() && !cbRestoreProd.isChecked() && !cbRestoreVent.isChecked()) {
                    Toast.makeText(VistaBaseDatos.this, "No hay tabla seleccionada a restaurar.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cbClearProv.isChecked()) {
                    clearTablas("proveedores");
                }

                if(cbClearProd.isChecked()) {
                    clearTablas("productos");
                }

                if(cbClearVent.isChecked()) {
                    clearTablas("ventas");
                }

                if(!cbClearProv.isChecked() && !cbClearProd.isChecked() && !cbClearVent.isChecked()) {
                    Toast.makeText(VistaBaseDatos.this, "No hay registros que eliminar.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void solicitarPermisos() {
        // PERMISOS PARA ANDROID 6 O SUPERIOR
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    0);
        }
    }

    public void backupTablasCSV(String tabla) {
        File carpeta = new File(Environment.getExternalStorageDirectory() + "/AppInventarioArchivos");
        String archivo = carpeta.toString() + "/" + "backup_"+tabla+".csv";

        if(!carpeta.exists()) {
            carpeta.mkdir();
        }

        try {
            FileWriter fileWriter = new FileWriter(archivo);

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaBaseDatos.this, "dbSistema", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("select * from "+tabla, null);

            if(tabla.equals("proveedores")) {
                if(fila != null && fila.getCount() != 0) {
                    fila.moveToFirst();
                    do {
                        fileWriter.append(fila.getString(0));
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(1));
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(2));
                        fileWriter.append("\n");
                    } while(fila.moveToNext());

                    Toast.makeText(VistaBaseDatos.this, "Se creo el respaldo de " + tabla.toUpperCase() + " exitosamente.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(VistaBaseDatos.this, "No hay " + tabla + " que respaldar.", Toast.LENGTH_LONG).show();
                }

            } else if(tabla.equals("productos")) {
                //Toast.makeText(this, "Registros: "+fila.getCount(), Toast.LENGTH_SHORT).show();
                if(fila != null && fila.getCount() != 0) {
                    fila.moveToFirst();
                    do {
                        byte[] bDatos = fila.getBlob(1);
                        String imagen = new String(bDatos);

                        fileWriter.append(fila.getString(0));
                        fileWriter.append(",");
                        //fileWriter.append(fila.getBlob(1).toString());
                        fileWriter.append(imagen);
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(2));
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(3));
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(4));
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(5));
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(6));
                        fileWriter.append(",");
                        fileWriter.append(fila.getString(7));
                        fileWriter.append("\n");

                    } while(fila.moveToNext());

                    Toast.makeText(VistaBaseDatos.this, "Se creo el respaldo de " + tabla.toUpperCase() + " exitosamente.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(VistaBaseDatos.this, "No hay " + tabla + " que respaldar.", Toast.LENGTH_LONG).show();
                }
            } else if(tabla.equals("ventas")) {
                if(fila != null && fila.getCount() != 0) {
                    fila.moveToFirst();
                    do {
                        fileWriter.append(fila.getString(0));
                        fileWriter.append("-");
                        fileWriter.append(fila.getString(1));
                        fileWriter.append("-");
                        fileWriter.append(fila.getString(2));
                        fileWriter.append("-");
                        fileWriter.append(fila.getString(3));
                        fileWriter.append("\n");

                    } while(fila.moveToNext());

                    Toast.makeText(VistaBaseDatos.this, "Se creo el respaldo de " + tabla.toUpperCase() + " exitosamente.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(VistaBaseDatos.this, "No hay " + tabla + " que respaldar.", Toast.LENGTH_LONG).show();
                }
            }
            db.close();
            fileWriter.close();

        } catch (Exception e) { }
    }

    public void restoreTablasCSV(String tabla) {
        clearTablas(tabla);

        File carpeta = new File(Environment.getExternalStorageDirectory() + "/AppInventarioArchivos");
        String archivo = carpeta.toString() + "/" + "backup_"+tabla+".csv";

        if(!carpeta.exists()) {
            Toast.makeText(VistaBaseDatos.this, "NO EXISTE EL ARCHIVO DE RESPALDO.", Toast.LENGTH_LONG).show();
        } else {
            String cadena;
            String[] arreglo;

            if(tabla.equals("proveedores")) {
                try {
                    FileReader fileReader = new FileReader(archivo);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    while((cadena = bufferedReader.readLine()) != null) {
                        arreglo = cadena.split(",");

                        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaBaseDatos.this, "dbSistema", null, 1);
                        SQLiteDatabase db = admin.getWritableDatabase();

                        ContentValues registro = new ContentValues();

                        registro.put("nomProveedor", arreglo[0]);
                        registro.put("telefono", arreglo[1]);
                        registro.put("email", arreglo[2]);

                        // los inserto en la base de datos
                        db.insert(tabla, null, registro);

                        db.close();
                    }

                    Toast.makeText(VistaBaseDatos.this, "Se importo exitosamente los "+tabla.toUpperCase(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) { }

            } else if(tabla.equals("productos")) {
                try {
                    FileReader fileReader = new FileReader(archivo);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    while((cadena = bufferedReader.readLine()) != null) {
                        //Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show();
                        arreglo = cadena.split(",");

                        //Toast.makeText(this, arreglo[0], Toast.LENGTH_SHORT).show();

                        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaBaseDatos.this, "dbSistema", null, 1);
                        SQLiteDatabase db = admin.getWritableDatabase();

                        ContentValues registro = new ContentValues();

                        registro.put("idProducto", arreglo[0]);
                        registro.put("imagenProducto", arreglo[1].trim());
                        registro.put("nomProducto", arreglo[2]);
                        registro.put("descripcion", arreglo[3]);
                        registro.put("nomProveedor", arreglo[4]);
                        registro.put("modelo", arreglo[5]);
                        registro.put("precio", arreglo[6]);
                        registro.put("almacen", arreglo[7]);

                        // los inserto en la base de datos
                        db.insert(tabla, null, registro);

                        db.close();
                    }
                    
                    Toast.makeText(VistaBaseDatos.this, "Se importo exitosamente los "+tabla, Toast.LENGTH_SHORT).show();

                } catch (Exception e) { }

            } else if(tabla.equals("ventas")) {
                try {
                    FileReader fileReader = new FileReader(archivo);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    while ((cadena = bufferedReader.readLine()) != null) {
                        //Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show();
                       arreglo = cadena.split("-");

                        String auxFecha = arreglo[2]+"-"+arreglo[3]+"-"+arreglo[4];
                        String auxTotalVenta = arreglo[5];

                        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaBaseDatos.this, "dbSistema", null, 1);
                        SQLiteDatabase db = admin.getWritableDatabase();

                        ContentValues registro = new ContentValues();

                        //registro.put("idVenta", arreglo[0]);
                        registro.put("idProductos", arreglo[1]);
                        registro.put("fechaVenta", auxFecha);
                        registro.put("total", auxTotalVenta);

                        db.insert(tabla, null, registro);

                        db.close();
                    }

                    Toast.makeText(VistaBaseDatos.this, "Se importo exitosamente los "+tabla, Toast.LENGTH_SHORT).show();

                } catch (Exception e) { }
            }
        }
    }

    public void clearTablas(String tabla) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(VistaBaseDatos.this, "dbSistema", null, 1);
        SQLiteDatabase db = admin.getWritableDatabase();

        admin.borrarRegistros(tabla, db);

        Toast.makeText(VistaBaseDatos.this, "Se limpio los registros de "+tabla.toUpperCase(), Toast.LENGTH_SHORT).show();
    }
}
