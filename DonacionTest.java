import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class DonacionTest {

    @Test
    void testConstructorAndGetters() {
        LocalDate date = LocalDate.of(2024, 12, 31);
        Donacion d = new Donacion("Arroz", 5.0, date, CategoriaAlimento.GRANOS);

        assertEquals("Arroz", d.getNombreAlimento());
        assertEquals(5.0, d.getCantidad());
        assertEquals(date, d.getFechaCaducidad());
        assertEquals(CategoriaAlimento.GRANOS, d.getCategoria());
    }

    @Test
    void testSetCantidad_valid() {
        LocalDate date = LocalDate.now();
        Donacion d = new Donacion("Leche", 1.0, date, CategoriaAlimento.LACTEOS);
        d.setCantidad(2.5);
        assertEquals(2.5, d.getCantidad());
    }

    @Test
    void testSetCantidad_zero() {
        LocalDate date = LocalDate.now();
        Donacion d = new Donacion("Pan", 1.0, date, CategoriaAlimento.OTROS);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            d.setCantidad(0);
        });
        assertEquals("La cantidad de la nueva donación debe ser positiva.", exception.getMessage());
    }

    @Test
    void testSetCantidad_negative() {
        LocalDate date = LocalDate.now();
        Donacion d = new Donacion("Zanahorias", 0.5, date, CategoriaAlimento.VERDURAS);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            d.setCantidad(-1.0);
        });
        assertEquals("La cantidad de la nueva donación debe ser positiva.", exception.getMessage());
    }

    @Test
    void testToString() {
        LocalDate date = LocalDate.of(2025, 1, 15);
        Donacion d = new Donacion("Manzanas", 10.0, date, CategoriaAlimento.FRUTAS);
        String expectedString = "Donación{nombreAlimento='Manzanas', cantidad=10.0, fechaCaducidad=2025-01-15, categoría=FRUTAS}";
        assertEquals(expectedString, d.toString());

        // Also check if the output contains key information substrings
        String output = d.toString();
        assertTrue(output.contains("Manzanas"));
        assertTrue(output.contains("10.0"));
        assertTrue(output.contains("2025-01-15"));
        assertTrue(output.contains(CategoriaAlimento.FRUTAS.toString()));
    }

    @Test
    void testSetters() {
        LocalDate date1 = LocalDate.of(2023, 10, 20);
        Donacion d = new Donacion("Frijoles", 2.0, date1, CategoriaAlimento.GRANOS);

        // Test setNombreAlimento
        d.setNombreAlimento("Lentejas");
        assertEquals("Lentejas", d.getNombreAlimento());

        // Test setFechaCaducidad
        LocalDate date2 = LocalDate.of(2024, 5, 10);
        d.setFechaCaducidad(date2);
        assertEquals(date2, d.getFechaCaducidad());

        // Test setCategoria
        d.setCategoria(CategoriaAlimento.OTROS);
        assertEquals(CategoriaAlimento.OTROS, d.getCategoria());
    }
}
