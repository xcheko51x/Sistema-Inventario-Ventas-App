package com.xcheko51x.appinventario.Adaptadores;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Producto;
import com.xcheko51x.appinventario.R;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdapterProductos extends RecyclerView.Adapter<AdapterProductos.productosViewHolder> {

    Context context;
    ArrayList<Producto> listaProductos;
    ArrayList<String> listaProveedores = new ArrayList<>();

    final String CARPETA_RAIZ = "AppInventarioArchivos/";
    final String CARPETA_IMAGENES = "productos";
    final String RUTA_IMAGEN = CARPETA_RAIZ + CARPETA_IMAGENES;

    public AdapterProductos(Context context, ArrayList<Producto> listaProductos) {
        this.context = context;
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public AdapterProductos.productosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_producto, null, false);
        return new AdapterProductos.productosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterProductos.productosViewHolder holder, final int position) {

        holder.tvIdProducto.setText(listaProductos.get(position).getIdProducto());
        holder.tvNomProducto.setText(listaProductos.get(position).getNomProducto());
        holder.tvNomImagen.setText(listaProductos.get(position).getImagenProducto());
        holder.tvDescripcion.setText(listaProductos.get(position).getDescripcion());
        holder.tvNomProveedor.setText(listaProductos.get(position).getNomProveedor());
        holder.tvModelo.setText(listaProductos.get(position).getModelo());
        holder.tvPrecio.setText(""+listaProductos.get(position).getPrecio());
        holder.tvAlmacen.setText(""+listaProductos.get(position).getAlmacen());

        /*byte[] decodeString = Base64.decode(listaProductos.get(position).getImagenProducto(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        holder.ivProducto.setImageBitmap(bitmap);*/

        String path = Environment.getExternalStorageDirectory()+File.separator+RUTA_IMAGEN+File.separator+listaProductos.get(position).getImagenProducto();
        File imagen = new File(path);

        Picasso.get().load(imagen).into(holder.ivProducto);


        holder.ibtnAcciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.ibtnAcciones);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(context, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        if(item.getTitle().equals("Editar")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                            builder.setTitle("Actualizar Producto");
                            View dialogVista = inflater.inflate(R.layout.alert_dialog_productos, null);
                            builder.setView(dialogVista);

                            final EditText etCodigo = dialogVista.findViewById(R.id.etCodigo);
                            final ImageButton ibtnEscaner = dialogVista.findViewById(R.id.ibtnEscaner);
                            final EditText etNomProducto = dialogVista.findViewById(R.id.etNomProducto);
                            final EditText etDescripcion = dialogVista.findViewById(R.id.etDescripcion);
                            final Spinner spiProveedor = dialogVista.findViewById(R.id.spiProveedor);
                            final EditText etModelo = dialogVista.findViewById(R.id.etModelo);
                            final EditText etPrecio = dialogVista.findViewById(R.id.etPrecio);
                            final EditText etAlmacen = dialogVista.findViewById(R.id.etAlmacen);

                            etCodigo.setEnabled(false);
                            ibtnEscaner.setVisibility(View.INVISIBLE);

                            etCodigo.setText(listaProductos.get(position).getIdProducto());
                            etNomProducto.setText(listaProductos.get(position).getNomProducto());
                            etDescripcion.setText(listaProductos.get(position).getDescripcion());

                            obtenerProveedores();
                            spiProveedor.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listaProveedores));
                            for(int j = 0; j < listaProveedores.size() ; j++) {
                                if(listaProveedores.get(j).equals(listaProductos.get(position).getNomProveedor())) {
                                    spiProveedor.setSelection(j);
                                }
                            }

                            etModelo.setText(listaProductos.get(position).getModelo());
                            etPrecio.setText(""+listaProductos.get(position).getPrecio());
                            etAlmacen.setText(""+listaProductos.get(position).getAlmacen());

                            builder.setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(etNomProducto.getText().toString().equals("") ||
                                            etDescripcion.getText().toString().equals("") ||
                                            etModelo.getText().toString().equals("") ||
                                            etPrecio.getText().toString().equals("") ||
                                            etAlmacen.getText().toString().equals("")
                                    ){
                                        Toast.makeText(context, "Debes llenar todos los campos.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "dbSistema", null, 1);

                                        SQLiteDatabase db = admin.getWritableDatabase();

                                        ContentValues registro = new ContentValues();

                                        registro.put("nomProducto", etNomProducto.getText().toString());
                                        registro.put("descripcion", etDescripcion.getText().toString());
                                        registro.put("nomProveedor", spiProveedor.getSelectedItem().toString());
                                        registro.put("modelo", etModelo.getText().toString());
                                        registro.put("precio", etPrecio.getText().toString());
                                        registro.put("almacen", etAlmacen.getText().toString());

                                        int cant = db.update("productos", registro, "idProducto=?", new String[]{listaProductos.get(position).getIdProducto()});

                                        db.close();

                                        if(cant == 1) {
                                            Toast.makeText(context, "Datos modificados con éxito", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(context, "No existe el registro", Toast.LENGTH_LONG).show();
                                        }
                                        ((Activity)context).finish();
                                        context.startActivity(((Activity)context).getIntent());
                                    }
                                }
                            });
                            builder.show();

                        } else if(item.getTitle().equals("Eliminar")) {

                            AlertDialog.Builder dialogo = new AlertDialog.Builder(context);

                            dialogo.setTitle("ELIMINAR");
                            dialogo.setMessage("¿Estas seguro que deseas eliminar el elemento?");
                            dialogo.setCancelable(false);

                            dialogo.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    //Toast.makeText(context,"ELEMENTO: "+listaProductos.get(i).getIdProducto(), Toast.LENGTH_SHORT).show();

                                    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "dbSistema", null, 1);

                                    SQLiteDatabase db = admin.getWritableDatabase();

                                    int cant = db.delete("productos", "idProducto=?", new String[]{listaProductos.get(position).getIdProducto()});

                                    db.close();

                                    if (cant == 1) {
                                        Toast.makeText(context, "Producto eliminado", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "No existe ese producto", Toast.LENGTH_LONG).show();
                                    }

                                    listaProductos.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                            dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {

                                }
                            });
                            dialogo.show();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    public void obtenerProveedores() {
        listaProveedores.clear();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, "dbSistema", null, 1);

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
            //Toast.makeText(context, "No hay registros", Toast.LENGTH_LONG).show();
        }

        db.close();
    }


    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public class productosViewHolder extends RecyclerView.ViewHolder {

        TextView tvIdProducto, tvNomProducto, tvNomImagen, tvDescripcion, tvNomProveedor, tvModelo, tvPrecio, tvAlmacen;
        ImageView ivProducto;
        ImageButton ibtnAcciones;

        public productosViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIdProducto = itemView.findViewById(R.id.tvIdProducto);
            ivProducto = itemView.findViewById(R.id.ivProducto);
            tvNomProducto = itemView.findViewById(R.id.tvNomProducto);
            tvNomImagen = itemView.findViewById(R.id.tvNomImagen);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvNomProveedor = itemView.findViewById(R.id.tvNomProveedor);
            tvModelo = itemView.findViewById(R.id.tvModelo);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvAlmacen = itemView.findViewById(R.id.tvAlmacen);
            ibtnAcciones = itemView.findViewById(R.id.ibtnAcciones);

        }
    }
}
