import java.time.LocalDate;

//subclase de Donacion
public class DonacionNoDisponible extends Donacion {
    //este atributo guarda el nombre de la organización Beneficiaria de la donación
    private String organizacionBeneficiaria;

    public DonacionNoDisponible(String nombreAlimento, double cantidad, LocalDate fechaCaducidad, 
                                CategoriaAlimento categoria, String organizacionBeneficiaria) {
        //llamamos al constructor de la superclase
        super(nombreAlimento, cantidad, fechaCaducidad, categoria);
        this.setOrganizacionBeneficiaria(organizacionBeneficiaria);
    }

    //getter de organizacionBeneficiaria
    public String getOrganizacionBeneficiaria() {
        return organizacionBeneficiaria;
    }

    //setter de organizacionBeneficiaria
    public void setOrganizacionBeneficiaria(String organizacionBeneficiaria){
        this.organizacionBeneficiaria = organizacionBeneficiaria;
    }

    //sobreescribimos toString para los reportes
    @Override
    public String toString() {
        return super.toString() + " | Organización Beneficiaria: " + this.getOrganizacionBeneficiaria();
    }
}
