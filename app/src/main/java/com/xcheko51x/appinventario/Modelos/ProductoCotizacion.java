package com.xcheko51x.appinventario.Modelos;

public class ProductoCotizacion {

    int cantidad;
    String idProducto;
    String nomProducto;
    Double precio;

    public ProductoCotizacion(int cantidad, String idProducto, String nomProducto, Double precio) {
        this.cantidad = cantidad;
        this.idProducto = idProducto;
        this.nomProducto = nomProducto;
        this.precio = precio;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }

    public String getNomProducto() {
        return nomProducto;
    }

    public void setNomProducto(String nomProducto) {
        this.nomProducto = nomProducto;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
