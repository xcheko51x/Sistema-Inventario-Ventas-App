package com.xcheko51x.appinventario.Vistas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.xcheko51x.appinventario.Adaptadores.AdapterReportes;
import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Venta;
import com.xcheko51x.appinventario.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class VistaReportes extends AppCompatActivity {

    EditText etFechaInicio, etFechaFin;
    Button btnMostarReporte, btnGenerarPDF;
    RecyclerView rvVentas;

    ArrayList<Venta> listaVentas = new ArrayList<>();

    Calendar calendario = Calendar.getInstance();
    int dia = calendario.get(Calendar.DAY_OF_MONTH);
    int mes = calendario.get(Calendar.MONTH);
    int anio = calendario.get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_reportes);
        getSupportActionBar().setTitle("REPORTE DE VENTAS");

        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        btnMostarReporte = findViewById(R.id.btnMostrarReporte);
        btnGenerarPDF = findViewById(R.id.btnGenerarPDF);

        rvVentas = findViewById(R.id.rvVentas);
        rvVentas.setLayoutManager(new GridLayoutManager(this, 1));

        // Permisos
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }

        // Metodo para obtener la fecha inicial del reporte
        etFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha(etFechaInicio);
            }
        });

        // Metodo para obtener la fecha final del reporte
        etFechaFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerFecha(etFechaFin);
            }
        });

        // Accion para el Boton de Mostrar reporte
        btnMostarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Si falta alguna fecha mostramos un aviso
                if((etFechaInicio.getText().toString().equals("") && etFechaFin.getText().toString().equals("")) ||
                        etFechaInicio.getText().toString().equals("")) {

                    Toast.makeText(VistaReportes.this, "Selecciona una fecha.", Toast.LENGTH_SHORT).show();
                    btnGenerarPDF.setEnabled(false); // Desabilitamos el boton

                } else { // Si se cumplen las fechas del reporte
                    // Si hay fecha de inicio de no fecha final
                    if((etFechaInicio.getText().toString() != "") && (etFechaFin.getText().toString().equals(""))) {

                        // Metodo para obtener las ventas de solo la una fecha
                        obtenerVentas(etFechaInicio.getText().toString());
                        if(listaVentas.size() == 0) {
                            btnGenerarPDF.setEnabled(false);
                        } else {
                            btnGenerarPDF.setEnabled(true);
                        }

                    } else { // Si hay fecha de inicio y fecha final

                        // Metodo para obtener las ventas de fecha inicial y fecha final
                        obtenerVentas(etFechaInicio.getText().toString(), etFechaFin.getText().toString());
                        if(listaVentas.size() == 0) {
                            btnGenerarPDF.setEnabled(false);
                        } else {
                            btnGenerarPDF.setEnabled(true);
                        }
                    }
                }
            }
        });

        // Accion para el boton que genera el PDF con el reporte de ventas
        btnGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String NOMBRE_DIRECTORIO = "AppInventarioArchivos";
                String NOMBRE_DOCUMENTO = "Reporte_"+etFechaInicio.getText().toString()+"_"+etFechaFin.getText().toString()+".pdf";

                crearReportePDF(NOMBRE_DIRECTORIO, NOMBRE_DOCUMENTO);


                /*String NOMBRE_DIRECTORIO = "MisReportes";
                String NOMBRE_REPORTE = "Reporte_"+etFechaInicio.getText().toString()+"_"+etFechaFin.getText().toString()+".pdf";

                // Llamada al metodo para crear el reporte
                crearReportePDF(NOMBRE_DIRECTORIO, NOMBRE_REPORTE);*/

            }
        });
    }

    public void crearReportePDF(String carpeta, String archivo) {
        Document documento = new Document();

        try {
            File file = crearFichero(carpeta, archivo);
            FileOutputStream ficheroPDF = new FileOutputStream(file.getAbsolutePath());

            PdfWriter pdfWriter = PdfWriter.getInstance(documento, ficheroPDF);

            documento.open();

            documento.add(new Paragraph(
                    "Reporte de ventas entre las fechas: "
                            + etFechaInicio.getText().toString() +" y "
                            + etFechaFin.getText().toString() + "\n\n",
                    FontFactory.getFont("arial", 16, Font.BOLD)
            ));

            double auxTotal = 0;
            for(int k = 0 ; k < listaVentas.size() ; k++) {
                auxTotal = auxTotal + Double.parseDouble(listaVentas.get(k).getTotal());
            }
            documento.add(new Paragraph("En este periodo se vendio un total de $"+auxTotal+"\n\n"));

            String fecha = listaVentas.get(0).getFechaVenta();

            for(int i = 0 ; i < listaVentas.size() ; i++) {
                //Toast.makeText(this, "SIZE: "+listaVentas.size(), Toast.LENGTH_SHORT).show();

                documento.add(new Paragraph(listaVentas.get(i).getFechaVenta()));

                String[] arrayVentas = listaVentas.get(i).getIdProductos().split(",");

                for(int j = 0 ; j < arrayVentas.length ; j = j + 4) {

                    documento.add(
                      new Paragraph(
                              arrayVentas[j] + "     " + arrayVentas[j+1] + "     " + arrayVentas[j+2] + "      $" + arrayVentas[j+3] + "      $" + (Double.parseDouble(arrayVentas[j]) * Double.parseDouble(arrayVentas[j+3]))
                      )
                    );
                }

                documento.add(new Paragraph("Total de la venta: $"+listaVentas.get(i).getTotal()+"\n\n"));
                documento.add(new Paragraph("\n"));

            }

            documento.close();

            Toast.makeText(this, "Se creo el reporte.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) { }

    }

    // Metodo para crear el archivo
    public File crearFichero(String NOMBRE_DIRECTORIO, String nombreFichero) {
        File ruta = getRuta(NOMBRE_DIRECTORIO);

        File fichero = null;
        if(ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    }

    // Metodo para obtener la ruta
    public File getRuta(String NOMBRE_DIRECTORIO) {
        File ruta = null;

        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), NOMBRE_DIRECTORIO);
            ruta = new File(Environment.getExternalStorageDirectory(), "/"+NOMBRE_DIRECTORIO);

            if(ruta != null) {
                if(!ruta.mkdirs()) {
                    if(!ruta.exists()) {
                        return null;
                    }
                }
            }

        }
        return ruta;
    }


    // Metodo para obtener las ventas con solo fecha de inicio
    public void obtenerVentas(String fechaInicio) {
        listaVentas.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from ventas where fechaVenta=?", new String[]{fechaInicio});

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do {

                //Toast.makeText(VistaReportes.this, fila.getString(1), Toast.LENGTH_SHORT).show();
                listaVentas.add(
                        new Venta(
                                fila.getInt(0),
                                fila.getString(1),
                                fila.getString(2),
                                fila.getString(3)
                        )
                );
            } while(fila.moveToNext());
        } else {
            Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();

        //Toast.makeText(VistaReportes.this, ""+listaVentas.size(), Toast.LENGTH_SHORT).show();
        AdapterReportes adaptador = new AdapterReportes(VistaReportes.this, listaVentas);
        rvVentas.setAdapter(adaptador);
    }

    // Metodo para obtener las ventas con solo fecha de inicio y fecha final
    public void obtenerVentas(String fechaInicio, String fechaFin) {
        listaVentas.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, "dbSistema", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select * from ventas where fechaVenta between ? and ? order by idVenta asc", new String[]{fechaInicio, fechaFin});

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do {
                listaVentas.add(
                        new Venta(
                                fila.getInt(0),
                                fila.getString(1),
                                fila.getString(2),
                                fila.getString(3)
                        )
                );
            } while(fila.moveToNext());
        } else {
            //Toast.makeText(this, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();

        AdapterReportes adaptador = new AdapterReportes(VistaReportes.this, listaVentas);
        rvVentas.setAdapter(adaptador);
    }

    // Metodo para obtener las fechas
    public  void obtenerFecha(final EditText etFecha) {
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                final int mesActual = month + 1;
                String diaFormateado = (dayOfMonth < 10)? 0 + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                String mesFormateado = (mesActual < 10)? 0 + String.valueOf(mesActual):String.valueOf(mesActual);
                etFecha.setText(diaFormateado + "-" + mesFormateado + "-" + year);
            }

        },anio, mes, dia);
        recogerFecha.show();
    }
}
