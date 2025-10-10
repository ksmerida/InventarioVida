package Producto.model;

public class Producto {
    private int id;
    private String nombre;
    private String sku;
    private String descripcion;
    private double precio;
    private int cantidad;
    private Integer idSede; // nullable

    public Producto() {}

    public Producto(int id, String nombre, String sku, String descripcion, double precio, int cantidad, Integer idSede) {
        this.id = id;
        this.nombre = nombre;
        this.sku = sku;
        this.descripcion = descripcion;
        this.precio = precio;
        this.cantidad = cantidad;
        this.idSede = idSede;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public Integer getIdSede() { return idSede; }
    public void setIdSede(Integer idSede) { this.idSede = idSede; }
}
