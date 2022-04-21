package com.xcheko51x.appinventario.Modelos;

public class Venta {

    public int idVenta;
    public String idProductos;
    public String fechaVenta;
    public String total;

    public Venta(int idVenta, String idProductos, String fechaVenta, String total) {
        this.idVenta = idVenta;
        this.idProductos = idProductos;
        this.fechaVenta = fechaVenta;
        this.total = total;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getIdProductos() {
        return idProductos;
    }

    public void setIdProductos(String idProductos) {
        this.idProductos = idProductos;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
