package com.xcheko51x.appinventario.Adaptadores;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.xcheko51x.appinventario.Modelos.Cliente;
import com.xcheko51x.appinventario.R;

import java.util.ArrayList;

public class AdapterClientes extends RecyclerView.Adapter<AdapterClientes.clienteViewHolder> {

    Context context;
    ArrayList<Cliente> listaClientes;

    public AdapterClientes(Context context, ArrayList<Cliente> listaClientes) {
        this.context = context;
        this.listaClientes = listaClientes;
    }

    @Override
    public clienteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_cliente, null, false);
        return new AdapterClientes.clienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(clienteViewHolder clienteViewHolder, final int i) {
        clienteViewHolder.tvNomCliente.setText(listaClientes.get(i).getNomCliente());
        clienteViewHolder.tvRfc.setText(listaClientes.get(i).getRfc());
        clienteViewHolder.tvTipoPersona.setText(listaClientes.get(i).getTipoPersona());
        clienteViewHolder.tvObservaciones.setText(listaClientes.get(i).getObservaciones());

        clienteViewHolder.ibtnTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + listaClientes.get(i).getNumTelefono()));
                context.startActivity(intent);
            }
        });

        clienteViewHolder.ibtnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {listaClientes.get(i).getEmail()}; //aquí pon tu correo
                String[] CC = {""};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_CC, CC);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Asunto");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje");

                try {
                    context.startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clienteViewHolder.ibtnDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("DIRECCIÓN");
                builder.setMessage(listaClientes.get(i).getDireccion());
                // Create the AlertDialog object and return it
                builder.create();
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public class clienteViewHolder extends RecyclerView.ViewHolder {

        TextView tvNomCliente, tvRfc, tvTipoPersona, tvObservaciones;
        ImageButton ibtnTelefono, ibtnEmail, ibtnDireccion;


        public clienteViewHolder(View itemView) {
            super(itemView);

            tvNomCliente = itemView.findViewById(R.id.tvNomCliente);
            tvRfc = itemView.findViewById(R.id.tvRfc);
            tvTipoPersona = itemView.findViewById(R.id.tvTipoPersona);
            tvObservaciones = itemView.findViewById(R.id.tvObservaciones);
            ibtnTelefono = itemView.findViewById(R.id.ibtnTelefono);
            ibtnEmail = itemView.findViewById(R.id.ibtnEmail);
            ibtnDireccion = itemView.findViewById(R.id.ibtnDireccion);

        }
    }
}
