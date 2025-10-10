package Sede.model;

public class Sede {
    private int id;
    private String nombre;
    private String direccion;
    private int estado;

    public Sede() {}
    public Sede(int id, String nombre, String direccion, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.estado = estado;
    }

    // getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public int getEstado() { return estado; }
    public void serEstado(int estado) { this.estado = estado; }
}
