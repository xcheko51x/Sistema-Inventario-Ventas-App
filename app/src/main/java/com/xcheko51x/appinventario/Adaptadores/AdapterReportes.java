package com.xcheko51x.appinventario.Adaptadores;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xcheko51x.appinventario.AdminSQLiteOpenHelper;
import com.xcheko51x.appinventario.Modelos.Venta;
import com.xcheko51x.appinventario.R;

import java.util.ArrayList;

public class AdapterReportes extends RecyclerView.Adapter<AdapterReportes.reportesViewHolder> {

    Context context;
    ArrayList<Venta> listaVentas;

    public AdapterReportes(Context context, ArrayList<Venta> listaVentas) {
        this.context = context;
        this.listaVentas = listaVentas;
    }

    @NonNull
    @Override
    public reportesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_reporte, null, false);
        return new AdapterReportes.reportesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reportesViewHolder holder, int position) {

        holder.tvFecha.setText(listaVentas.get(position).getFechaVenta());
        obtenerDatosProducto(
                listaVentas.get(position).getIdProductos(),
                holder.tvCodProductos,
                holder.tvNomProducto,
                holder.tvCantidad,
                holder.tvPrecio
        );
        holder.tvTotal.setText(""+listaVentas.get(position).getTotal());

    }

    public void obtenerDatosProducto(String codigos, TextView tvCodigos, TextView tvNomProducto, TextView tvCantidad, TextView tvPrecio) {

        String[] arrayVenta = codigos.split(",");

        String auxCodigo = "";
        String auxNomProducto = "";
        String auxCantidad = "";
        String auxPrecio = "";

        for(int i = 0 ; i < arrayVenta.length ; i = i + 4) {

            auxCantidad = auxCantidad + arrayVenta[i] + "\n";
            auxCodigo = auxCodigo + arrayVenta[i+1] + "\n";
            auxNomProducto = auxNomProducto + arrayVenta[i+2] + "\n";
            auxPrecio = auxPrecio + arrayVenta[i+3] + "\n";

        }

        tvCodigos.setText(auxCodigo);
        tvNomProducto.setText(auxNomProducto);
        tvCantidad.setText(auxCantidad);
        tvPrecio.setText(auxPrecio);

    }


    @Override
    public int getItemCount() {
        return listaVentas.size();
    }

    public class reportesViewHolder extends RecyclerView.ViewHolder {

        TextView tvFecha, tvCodProductos, tvNomProducto, tvCantidad, tvPrecio, tvTotal;

        public reportesViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFecha = itemView.findViewById(R.id.tvFechaVenta);
            tvCodProductos = itemView.findViewById(R.id.tvCodProducto);
            tvNomProducto = itemView.findViewById(R.id.tvNomProducto);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            tvTotal = itemView.findViewById(R.id.tvTotal);

        }
    }
}
