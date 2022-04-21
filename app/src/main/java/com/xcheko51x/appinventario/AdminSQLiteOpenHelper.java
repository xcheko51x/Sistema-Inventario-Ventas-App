package com.xcheko51x.appinventario;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Aqui se crean las tablas
        db.execSQL("CREATE TABLE proveedores(" +
                "nomProveedor TEXT PRIMARY KEY," +
                "telefono TEXT, " +
                "email TEXT " +
                ")");

        db.execSQL("CREATE TABLE productos(" +
                "idProducto TEXT PRIMARY KEY," +
                "imagenProducto TEXT," +
                "nomProducto TEXT," +
                "descripcion TEXT," +
                "nomProveedor TEXT," +
                "modelo TEXT," +
                "precio REAL," +
                "almacen INTEGER" +
                ")");

        db.execSQL("CREATE TABLE ventas(" +
                "idVenta INTEGER PRIMARY KEY AUTOINCREMENT," +
                "idProductos TEXT," +
                "fechaVenta TEXT," +
                "total REAL" +
                ")");

        db.execSQL("CREATE TABLE clientes(" +
                "idCliente INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nomCliente TEXT," +
                "numTelefono TEXT," +
                "email TEXT," +
                "direccion TEXT," +
                "rfc TEXT," +
                "tipoPersona," +
                "observaciones" +
                ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void borrarRegistros(String tabla, SQLiteDatabase db) {
        db.execSQL("DELETE FROM "+tabla);
    }


}
