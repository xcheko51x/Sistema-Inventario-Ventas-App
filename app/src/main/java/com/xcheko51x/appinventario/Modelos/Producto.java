package com.xcheko51x.appinventario.Modelos;

public class Producto {

    public int cantidad;

    public String idProducto;
    public String imagenProducto;
    public String nomProducto;
    public String descripcion;
    public String nomProveedor;
    public String modelo;
    public double precio;
    public int almacen;

    public Producto(String idProducto, String imagenProducto, String nomProducto, String descripcion, String nomProveedor, String modelo, double precio, int almacen) {
        this.idProducto = idProducto;
        this.imagenProducto = imagenProducto;
        this.nomProducto = nomProducto;
        this.descripcion = descripcion;
        this.nomProveedor = nomProveedor;
        this.modelo = modelo;
        this.precio = precio;
        this.almacen = almacen;
    }

    // PARA VENTAS
    public Producto(int cantidad, String idProducto, String nomProducto, Double precio) {
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

    public String getImagenProducto() {
        return imagenProducto;
    }

    public void setImagenProducto(String imagenProducto) {
        this.imagenProducto = imagenProducto;
    }

    public String getNomProducto() {
        return nomProducto;
    }

    public void setNomProducto(String nomProducto) {
        this.nomProducto = nomProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNomProveedor() {
        return nomProveedor;
    }

    public void setNomProveedor(String nomProveedor) {
        this.nomProveedor = nomProveedor;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getAlmacen() {
        return almacen;
    }

    public void setAlmacen(int almacen) {
        this.almacen = almacen;
    }

}
