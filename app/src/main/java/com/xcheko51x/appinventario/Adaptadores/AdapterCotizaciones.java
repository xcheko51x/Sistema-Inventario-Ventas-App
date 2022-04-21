package com.xcheko51x.appinventario.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xcheko51x.appinventario.Modelos.ProductoCotizacion;
import com.xcheko51x.appinventario.R;
import com.xcheko51x.appinventario.Vistas.VistaVentas;

import java.util.List;

public class AdapterCotizaciones extends RecyclerView.Adapter<AdapterCotizaciones.CotizacionViewHolder> {

    Context context;
    List<ProductoCotizacion> listaCotizaciones;
    TextView tvTotalCotizacion;

    double  suma = 0.0;

    public AdapterCotizaciones(Context context, List<ProductoCotizacion> listaCotizaciones, TextView tvTotal) {
        this.context = context;
        this.listaCotizaciones = listaCotizaciones;
        this.tvTotalCotizacion = tvTotal;
    }

    @NonNull
    @Override
    public CotizacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_cotizacion, null, false);
        return new AdapterCotizaciones.CotizacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CotizacionViewHolder holder, final int position) {

        holder.tvCantidad.setText(""+listaCotizaciones.get(position).getCantidad());
        holder.tvCodPro.setText(listaCotizaciones.get(position).getIdProducto());
        holder.tvNomProd.setText(listaCotizaciones.get(position).getNomProducto());
        holder.tvPrec.setText("$"+listaCotizaciones.get(position).getPrecio());
        double aux = listaCotizaciones.get(position).getCantidad() * listaCotizaciones.get(position).getPrecio();
        holder.tvTotal.setText("$"+aux);

        holder.ibtnRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int aux = listaCotizaciones.get(position).getCantidad();
                aux--;
                if(aux == 0) {
                    listaCotizaciones.remove(position);
                    notifyDataSetChanged();
                    suma = 0.0;
                } else {
                    listaCotizaciones.get(position).setCantidad(aux);
                    notifyDataSetChanged();
                    suma = 0.0;
                }
            }
        });

        holder.ibtnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listaCotizaciones.remove(position);
                notifyDataSetChanged();
                suma = 0.0;
            }
        });

        suma += aux;
        tvTotalCotizacion.setText("$"+suma);

    }

    @Override
    public int getItemCount() {
        return listaCotizaciones.size();
    }

    public class CotizacionViewHolder extends RecyclerView.ViewHolder {

        ImageButton ibtnEliminar, ibtnRestar;
        TextView tvCantidad, tvCodPro, tvNomProd, tvPrec, tvTotal;

        public CotizacionViewHolder(@NonNull View itemView) {
            super(itemView);

            ibtnEliminar = itemView.findViewById(R.id.ibtnEliminar);
            ibtnRestar = itemView.findViewById(R.id.ibtnRestar);
            tvCantidad = itemView.findViewById(R.id.tvCantidad);
            tvCodPro = itemView.findViewById(R.id.tvCodPro);
            tvNomProd = itemView.findViewById(R.id.tvNomProd);
            tvPrec = itemView.findViewById(R.id.tvPrec);
            tvTotal = itemView.findViewById(R.id.tvTotal);
        }
    }
}
