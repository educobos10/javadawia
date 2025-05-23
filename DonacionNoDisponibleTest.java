import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class DonacionNoDisponibleTest {

    @Test
    void testConstructorAndGetters() {
        LocalDate date = LocalDate.of(2024, 8, 15);
        DonacionNoDisponible dnd = new DonacionNoDisponible(
                "Arroz", 
                10.0, 
                date, 
                CategoriaAlimento.GRANOS, 
                "Comedor Social XYZ"
        );

        // Test getters from Donacion superclass
        assertEquals("Arroz", dnd.getNombreAlimento());
        assertEquals(10.0, dnd.getCantidad());
        assertEquals(date, dnd.getFechaCaducidad());
        assertEquals(CategoriaAlimento.GRANOS, dnd.getCategoria());

        // Test getter from DonacionNoDisponible class
        assertEquals("Comedor Social XYZ", dnd.getOrganizacionBeneficiaria());
    }

    @Test
    void testSetOrganizacionBeneficiaria() {
        LocalDate date = LocalDate.now();
        DonacionNoDisponible dnd = new DonacionNoDisponible(
                "Frijoles", 
                5.5, 
                date, 
                CategoriaAlimento.GRANOS, 
                "Organización ABC"
        );
        
        dnd.setOrganizacionBeneficiaria("Nueva Organización 123");
        assertEquals("Nueva Organización 123", dnd.getOrganizacionBeneficiaria());
    }

    @Test
    void testToString() {
        LocalDate date = LocalDate.of(2025, 3, 20);
        DonacionNoDisponible dnd = new DonacionNoDisponible(
                "Pasta", 
                20.0, 
                date, 
                CategoriaAlimento.OTROS, 
                "Banco de Alimentos Local"
        );

        String expectedSuperToString = "Donación{" +
                "nombreAlimento='Pasta'" +
                ", cantidad=20.0" +
                ", fechaCaducidad=2025-03-20" +
                ", categoría=OTROS" +
                '}';
        
        String expectedToString = expectedSuperToString + " | Organización Beneficiaria: Banco de Alimentos Local";
        assertEquals(expectedToString, dnd.toString());

        // Also check if the output contains key information substrings
        String output = dnd.toString();
        assertTrue(output.contains("Pasta"));
        assertTrue(output.contains("20.0"));
        assertTrue(output.contains("2025-03-20"));
        assertTrue(output.contains(CategoriaAlimento.OTROS.toString()));
        assertTrue(output.contains("Banco de Alimentos Local"));
    }
}
