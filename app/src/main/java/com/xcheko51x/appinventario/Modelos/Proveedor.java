package com.xcheko51x.appinventario.Modelos;

public class Proveedor {

    public String nomProveedor;
    public String telefono;
    public String email;

    public Proveedor(String nomProveedor, String telefono, String email) {
        this.nomProveedor = nomProveedor;
        this.telefono = telefono;
        this.email = email;
    }

    public String getNomProveedor() {
        return nomProveedor;
    }

    public void setNomProveedor(String nomProveedor) {
        this.nomProveedor = nomProveedor;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
