package com.xcheko51x.appinventario.Modelos;

public class Cliente {

    String idCliente;
    String nomCliente;
    String numTelefono;
    String email;
    String direccion;
    String rfc;
    String tipoPersona;
    String observaciones;

    public Cliente(String idCliente, String nomCliente, String numTelefono, String email, String direccion, String rfc, String tipoPersona, String observaciones) {
        this.idCliente = idCliente;
        this.nomCliente = nomCliente;
        this.numTelefono = numTelefono;
        this.email = email;
        this.direccion = direccion;
        this.rfc = rfc;
        this.tipoPersona = tipoPersona;
        this.observaciones = observaciones;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getNomCliente() {
        return nomCliente;
    }

    public void setNomCliente(String nomCliente) {
        this.nomCliente = nomCliente;
    }

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}