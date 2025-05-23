import java.time.LocalDate;

public class Donacion {
    //atributos privados para controlar el acceso
    private String nombreAlimento;
    private double cantidad;
    private LocalDate fechaCaducidad;
    private CategoriaAlimento categoria;

    //Constructor de Donacion
    public Donacion(String nombreAlimento, double cantidad, LocalDate fechaCaducidad, CategoriaAlimento categoria) {
        this.setNombreAlimento(nombreAlimento);
        this.setCantidad(cantidad);
        this.setFechaCaducidad(fechaCaducidad);
        this.setCategoria(categoria);
    }

    //getter de NombreAlimento
    public String getNombreAlimento() {
        return this.nombreAlimento;
    }
    //setter de NombreAlimento
    public void setNombreAlimento(String nombreAlimento) {
        this.nombreAlimento = nombreAlimento;
    }
    //getter de Cantidad
    public double getCantidad() {
        return cantidad;
    }
    //setter de Cantidad
    public void setCantidad(double cantidad) {
        //cantidad siempre debe ser positiva. Sino, lanzamos excepción
        if(cantidad <= 0){
            throw new IllegalArgumentException("La cantidad de la nueva donación debe ser positiva.");
        }else{
            this.cantidad = cantidad;
        }
    }
    //getter de FechaCaducidad
    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }
    //setter de FechaCaducidad
    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }
    //getter de Categoria
    public CategoriaAlimento getCategoria() {
        return this.categoria;
    }
    //setter de Categoria
    public void setCategoria(CategoriaAlimento categoria) {
        this.categoria = categoria;
    }

    //sobreescribimos toString para los reportes
    @Override
    public String toString() {
        return "Donación{" +
                "nombreAlimento='" + this.nombreAlimento + '\'' +
                ", cantidad=" + this.cantidad +
                ", fechaCaducidad=" + this.fechaCaducidad +
                ", categoría=" + this.categoria +
                '}';
    }
}
