import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BancoDeAlimentosTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    // Helper to provide input to System.in
    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @BeforeEach
    public void setUpStreamsAndReset() {
        System.setOut(new PrintStream(outContent));
        BancoDeAlimentos.resetDonationsForTesting(); // Reset data before each test
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    // --- Test Ingresar Donacion ---

    @Test
    void testIngresarDonacion_success() {
        LocalDate futureDate = LocalDate.now().plusMonths(1);
        String futureDateString = futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String input = "Arroz\n5.0\n" + futureDateString + "\nGRANOS\n";
        provideInput(input);

        BancoDeAlimentos.main(new String[]{"1", "4"}); // Simulate choosing option 1 then 4 to exit

        String output = outContent.toString();
        assertTrue(output.contains("Donación registrada con éxito."), "Output: " + output);

        // Directly check the list (assuming we can access it or have a getter for tests)
        // For this example, we'll infer success from the message and lack of error.
        // A more robust test would involve checking donacionesDisponibles.
    }

    @Test
    void testIngresarDonacion_invalidQuantity_zero() {
        LocalDate futureDate = LocalDate.now().plusMonths(1);
        String futureDateString = futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String input = "Frijoles\n0\n" + futureDateString + "\nGRANOS\n";
        provideInput(input);

        BancoDeAlimentos.main(new String[]{"1", "4"});
        String output = outContent.toString();
        assertTrue(output.contains("Error al registrar la donación. Verifica los datos ingresados.La cantidad de la nueva donación debe ser positiva."), "Output: " + output);
    }
    
    @Test
    void testIngresarDonacion_invalidQuantity_negative() {
        LocalDate futureDate = LocalDate.now().plusMonths(1);
        String futureDateString = futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String input = "Frijoles\n-2.0\n" + futureDateString + "\nGRANOS\n";
        provideInput(input);

        BancoDeAlimentos.main(new String[]{"1", "4"});
        String output = outContent.toString();
        assertTrue(output.contains("Error al registrar la donación. Verifica los datos ingresados.La cantidad de la nueva donación debe ser positiva."), "Output: " + output);
    }

    @Test
    void testIngresarDonacion_invalidDateFormat() {
        String input = "Pasta\n10.0\n2023/12/31\nOTROS\n"; // Invalid date format
        provideInput(input);
        BancoDeAlimentos.main(new String[]{"1", "4"});
        String output = outContent.toString();
        assertTrue(output.contains("Error al registrar la donación. Verifica los datos ingresados."), "Output: " + output);
    }

    @Test
    void testIngresarDonacion_invalidCategory() {
        LocalDate futureDate = LocalDate.now().plusMonths(1);
        String futureDateString = futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String input = "Galletas\n2.0\n" + futureDateString + "\nDULCES\n"; // Invalid category
        provideInput(input);
        BancoDeAlimentos.main(new String[]{"1", "4"});
        String output = outContent.toString();
        assertTrue(output.contains("Error al registrar la donación. Verifica los datos ingresados."), "Output: " + output);
    }

    @Test
    void testIngresarDonacion_nonNumericQuantity() {
        LocalDate futureDate = LocalDate.now().plusMonths(1);
        String futureDateString = futureDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String input = "Lentejas\nMUCHAS\n" + futureDateString + "\nGRANOS\n"; // Non-numeric quantity
        provideInput(input);
        BancoDeAlimentos.main(new String[]{"1", "4"});
        String output = outContent.toString();
        assertTrue(output.contains("Error al registrar la donación. Verifica los datos ingresados."), "Output: " + output);
    }

    // --- Test Distribuir Donacion ---

    private Donacion addDirectDonationForTesting(String nombre, double cantidad, LocalDate fecha, CategoriaAlimento categoria) {
        Donacion d = new Donacion(nombre, cantidad, fecha, categoria);
        // This is a simplified way to add; ideally, BancoDeAlimentos would have a direct add method for testing
        // or we use reflection to access donacionesDisponibles.
        // For now, we'll assume ingresarDonacion works and use it, or manually add if possible.
        // Let's assume we can call a method like this:
        // BancoDeAlimentos.addDonacionDirectly(d);
        // If not, we'll have to use the menu system which makes tests more complex.

        // Simulating direct addition for test setup by calling the internal logic or a test helper
        // If donacionesDisponibles is not directly accessible, this part needs adjustment.
        // For now, let's assume a hypothetical direct addition for setup clarity:
        // For the sake of this example, we'll use the menu to add, then test distribution.
        // This makes tests longer but avoids needing direct list access if not available.
        String futureDateString = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String anadirInput = nombre + "\n" + cantidad + "\n" + futureDateString + "\n" + categoria.name() + "\n";
        provideInput(anadirInput);
        BancoDeAlimentos.main(new String[]{"1"}); // Just run option 1
        outContent.reset(); // Clear output from the addition
        return d; // this isn't the actual object in the list, but represents it
    }


    @Test
    void testDistribuirDonacion_partialQuantity() {
        LocalDate expiry = LocalDate.now().plusDays(10);
        addDirectDonationForTesting("Leche", 10.0, expiry, CategoriaAlimento.LACTEOS);

        String inputDistribuir = "Leche\n3.0\nComedor Infantil\n";
        provideInput(inputDistribuir);
        BancoDeAlimentos.main(new String[]{"2", "4"}); // Distribuir then exit

        String output = outContent.toString();
        assertTrue(output.contains("DISTRIBUCIÓN REALIZADA CON ÉXITO."), "Output: " + output);
        // Further checks:
        // 1. donacionesDisponibles should have Leche with 7.0 quantity
        // 2. donacionesNoDisponibles should have Leche with 3.0 quantity and "Comedor Infantil"
        // This requires access to the lists or specific report outputs.
    }

    @Test
    void testDistribuirDonacion_fullQuantity() {
        LocalDate expiry = LocalDate.now().plusDays(10);
        addDirectDonationForTesting("Pan", 2.0, expiry, CategoriaAlimento.OTROS);

        String inputDistribuir = "Pan\n2.0\nRefugio Municipal\n";
        provideInput(inputDistribuir);
        BancoDeAlimentos.main(new String[]{"2", "4"});

        String output = outContent.toString();
        assertTrue(output.contains("DISTRIBUCIÓN REALIZADA CON ÉXITO."), "Output: " + output);
        // Further checks:
        // 1. "Pan" should be removed from donacionesDisponibles
        // 2. donacionesNoDisponibles should have Pan with 2.0 quantity and "Refugio Municipal"
    }

    @Test
    void testDistribuirDonacion_moreThanAvailable() {
        LocalDate expiry = LocalDate.now().plusDays(10);
        addDirectDonationForTesting("Zanahorias", 5.0, expiry, CategoriaAlimento.VERDURAS);

        String inputDistribuir = "Zanahorias\n10.0\nAsociación Vecinal\n";
        provideInput(inputDistribuir);
        BancoDeAlimentos.main(new String[]{"2", "4"});

        String output = outContent.toString();
        assertTrue(output.toUpperCase().contains("LA CANTIDAD DISPONIBLE DEL ALIMENTO SOLICITADO ES INSUFICIENTE."), "Output: " + output);
    }

    @Test
    void testDistribuirDonacion_nonExistentFood() {
        LocalDate expiry = LocalDate.now().plusDays(10);
        addDirectDonationForTesting("Manzanas", 5.0, expiry, CategoriaAlimento.FRUTAS);

        String inputDistribuir = "Peras\n1.0\nFundación Ayuda\n";
        provideInput(inputDistribuir);
        BancoDeAlimentos.main(new String[]{"2", "4"});

        String output = outContent.toString();
        assertTrue(output.toUpperCase().contains("ALIMENTO NO ENCONTRADO."), "Output: " + output);
    }
    
    @Test
    void testDistribuirDonacion_emptyInventory() {
        // No donations added
        String inputDistribuir = "Leche\n1.0\nHogar de Ancianos\n";
        provideInput(inputDistribuir);
        BancoDeAlimentos.main(new String[]{"2", "4"});

        String output = outContent.toString();
        // The method mostrarDonacionesDisponibles is called first, which prints this
        assertTrue(output.toUpperCase().contains("NO HAY DONACIONES DISPONIBLES AÚN."), "Output: " + output);
    }

    @Test
    void testDistribuirDonacion_nonNumericQuantity() {
        LocalDate expiry = LocalDate.now().plusDays(10);
        addDirectDonationForTesting("Atun", 10.0, expiry, CategoriaAlimento.OTROS);

        String inputDistribuir = "Atun\nMUCHO\nCruz Roja\n";
        provideInput(inputDistribuir);
        BancoDeAlimentos.main(new String[]{"2", "4"});

        String output = outContent.toString();
        assertTrue(output.toUpperCase().contains("ERROR EN LA DISTRIBUCIÓN DEL ALIMENTO: FOR INPUT STRING: \"MUCHO\""), "Output: " + output);
    }


    // --- Test Report Methods ---

    @Test
    void testListarPorCategoria() {
        LocalDate date1 = LocalDate.now().plusDays(5);
        LocalDate date2 = LocalDate.now().plusDays(10);
        addDirectDonationForTesting("Arroz", 2.0, date1, CategoriaAlimento.GRANOS);
        addDirectDonationForTesting("Leche", 3.0, date2, CategoriaAlimento.LACTEOS);
        addDirectDonationForTesting("Lentejas", 1.5, date1, CategoriaAlimento.GRANOS);

        provideInput(""); // No further input needed for this report
        // Simulate choosing option 3 (Generate Report), then 1 (ListarPorCategoria), then 4 (Exit main menu)
        BancoDeAlimentos.main(new String[]{"3", "1", "4"}); 

        String output = outContent.toString().toUpperCase();
        assertTrue(output.contains("CATEGORÍA: GRANOS"), "Output: " + output);
        assertTrue(output.contains("ARROZ"), "Output: " + output);
        assertTrue(output.contains("LENTEJAS"), "Output: " + output);
        assertTrue(output.contains("CATEGORÍA: LACTEOS"), "Output: " + output);
        assertTrue(output.contains("LECHE"), "Output: " + output);
        assertTrue(output.contains("NO HAY DONACIONES EN ESTA CATEGORÍA.") || !output.contains("CATEGORÍA: FRUTAS"), "Should not list empty categories or explicitly state no donations for them. Output: " + output);
    }

    @Test
    void testListarProximasACaducar_someExpiring() {
        LocalDate soonDate = LocalDate.now().plusDays(3);
        LocalDate laterDate = LocalDate.now().plusDays(10);
        LocalDate verySoonDate = LocalDate.now().plusDays(1);

        addDirectDonationForTesting("Yogurt", 1.0, soonDate, CategoriaAlimento.LACTEOS);
        addDirectDonationForTesting("Queso", 0.5, laterDate, CategoriaAlimento.LACTEOS);
        addDirectDonationForTesting("Jamon", 0.8, verySoonDate, CategoriaAlimento.OTROS);


        provideInput("");
        BancoDeAlimentos.main(new String[]{"3", "2", "4"}); // Report -> ProximasACaducar -> Exit

        String output = outContent.toString().toUpperCase();
        //System.err.println(output); // For debugging the test output
        assertTrue(output.contains("DONACIONES PRÓXIMAS A CADUCAR (EN LOS PRÓXIMOS 7 DÍAS)"), "Output: " + output);
        assertTrue(output.contains("YOGURT"), "Output: " + output);
        assertTrue(output.contains("JAMON"), "Output: " + output);
        assertFalse(output.contains("QUESO"), "Queso should not be listed as it expires in 10 days. Output: " + output);
        
        // Check order: verySoonDate (Jamon) should appear before soonDate (Yogurt)
        int jamonIndex = output.indexOf("JAMON");
        int yogurtIndex = output.indexOf("YOGURT");
        assertTrue(jamonIndex != -1 && yogurtIndex != -1 && jamonIndex < yogurtIndex, "Jamon should be listed before Yogurt. Output: " + output);
    }
    
    @Test
    void testListarProximasACaducar_noneExpiringSoon() {
        LocalDate laterDate1 = LocalDate.now().plusDays(10);
        LocalDate laterDate2 = LocalDate.now().plusDays(15);
        addDirectDonationForTesting("Conservas", 10.0, laterDate1, CategoriaAlimento.OTROS);
        addDirectDonationForTesting("Pasta Seca", 5.0, laterDate2, CategoriaAlimento.GRANOS);

        provideInput("");
        BancoDeAlimentos.main(new String[]{"3", "2", "4"}); // Report -> ProximasACaducar -> Exit

        String output = outContent.toString().toUpperCase();
        assertTrue(output.contains("NO HAY DONACIONES QUE CADUQUEN EN LOS PRÓXIMOS 7 DÍAS."), "Output: " + output);
    }

    @Test
    void testMostrarStock() {
        LocalDate date = LocalDate.now().plusMonths(1);
        addDirectDonationForTesting("Arroz", 5.0, date, CategoriaAlimento.GRANOS);
        addDirectDonationForTesting("Frijoles", 3.0, date, CategoriaAlimento.GRANOS);
        addDirectDonationForTesting("Leche", 2.0, date, CategoriaAlimento.LACTEOS);

        provideInput("");
        BancoDeAlimentos.main(new String[]{"3", "3", "4"}); // Report -> MostrarStock -> Exit

        String output = outContent.toString().toUpperCase();
        //System.err.println(output);
        assertTrue(output.contains("STOCK ACTUAL POR TIPO DE ALIMENTO"), "Output: " + output);
        assertTrue(output.contains("GRANOS: 8.0 KG O LITROS"), "Output: " + output); // 5.0 + 3.0
        assertTrue(output.contains("LACTEOS: 2.0 KG O LITROS"), "Output: " + output);
        assertTrue(output.contains("FRUTAS: 0.0 KG O LITROS"), "Output: " + output); // Should show 0 for others
    }

    @Test
    void testMostrarDonacionesNoDisponibles_someDistributed() {
        LocalDate date = LocalDate.now().plusDays(10);
        // Add and fully distribute one item
        addDirectDonationForTesting("Pan", 2.0, date, CategoriaAlimento.OTROS);
        String inputDistribuir1 = "Pan\n2.0\nRefugio Municipal\n";
        provideInput(inputDistribuir1);
        BancoDeAlimentos.main(new String[]{"2"}); // Distribute
        outContent.reset(); // Clear output from distribution

        // Add and partially distribute another item
        addDirectDonationForTesting("Leche", 5.0, date.plusDays(1), CategoriaAlimento.LACTEOS);
        String inputDistribuir2 = "Leche\n3.0\nComedor Infantil\n";
        provideInput(inputDistribuir2);
        BancoDeAlimentos.main(new String[]{"2"}); // Distribute
        outContent.reset(); // Clear output from distribution


        provideInput("");
        BancoDeAlimentos.main(new String[]{"3", "4", "4"}); // Report -> Historial -> Exit

        String output = outContent.toString().toUpperCase();
        //System.err.println(output);
        assertTrue(output.contains("HISTORIAL DE DONACIONES DISTRIBUIDAS"), "Output: " + output);
        assertTrue(output.contains("PAN") && output.contains("REFUGIO MUNICIPAL"), "Output: " + output);
        assertTrue(output.contains("LECHE") && output.contains("COMEDOR INFANTIL"), "Output: " + output);
        
        // Check order - Pan was distributed first (date), but Leche was distributed with a later expiry (date.plusDays(1))
        // The sorting in mostrarDonacionesNoDisponibles is by FECHA DE CADUCIDAD, not distribution date.
        // So Pan (date) should be before Leche (date.plusDays(1)) if their expiry dates are different.
        // If expiry dates are the same, original insertion order might be preserved or become unpredictable.
        // Let's ensure the test setup reflects this. The current setup has Leche with a later expiry.
        int panIndex = output.indexOf("PAN");
        int lecheIndex = output.indexOf("LECHE");

        // If both used 'date', the order is not strictly guaranteed by date unless stable sort.
        // The code uses Collections.sort which is stable for non-primitive types.
        // Let's assume 'date' for Pan and 'date.plusDays(1)' for Leche as their original expiry.
        // The sort is on fechaCaducidad.
        assertTrue(panIndex != -1 && lecheIndex != -1 && panIndex < lecheIndex, "Pan (expiry " + date + ") should be listed before Leche (expiry " + date.plusDays(1) + "). Output: " + output);
    }
    
    @Test
    void testMostrarDonacionesNoDisponibles_noneDistributed() {
        addDirectDonationForTesting("Agua", 10.0, LocalDate.now().plusDays(30), CategoriaAlimento.OTROS);
        
        provideInput("");
        BancoDeAlimentos.main(new String[]{"3", "4", "4"}); // Report -> Historial -> Exit

        String output = outContent.toString().toUpperCase();
        assertTrue(output.contains("NO SE HAN DISTRIBUÏDO DONACIONES AÚN."), "Output: " + output);
    }

    // --- Test Main Menu and GenerarReporte Menu Input Validation ---

    @Test
    void testMainMenu_invalidInput_nonNumeric() {
        provideInput("ABC\n4\n"); // Invalid input, then 4 to exit
        BancoDeAlimentos.main(new String[]{}); // No args, will use System.in

        String output = outContent.toString();
        assertTrue(output.contains("Opción no válida. Intenta de nuevo."), "Output: " + output);
    }
    
    @Test
    void testMainMenu_invalidInput_outOfBounds() {
        provideInput("9\n4\n"); // Invalid input (9), then 4 to exit
        BancoDeAlimentos.main(new String[]{}); 

        String output = outContent.toString();
        assertTrue(output.contains("Opción no válida. Intenta de nuevo."), "Output: " + output);
    }

    @Test
    void testGenerarReporteMenu_invalidInput_nonNumeric() {
        // Need at least one donation to avoid "no donations" messages masking the menu
        addDirectDonationForTesting("Arroz", 1.0, LocalDate.now().plusDays(1), CategoriaAlimento.GRANOS);
        
        provideInput("XYZ\n4\n"); // Invalid input for report submenu, then 4 to go back to main menu (simulated by how main loop works)
        BancoDeAlimentos.main(new String[]{"3", "4"}); // Choose 3 (Reportes), then input XYZ, then 4 to exit main

        String output = outContent.toString().toUpperCase();
        //System.err.println(output);
        assertTrue(output.contains("OPCIÓN INVÁLIDA."), "Output: " + output);
    }
    
    @Test
    void testGenerarReporteMenu_invalidInput_outOfBounds() {
        addDirectDonationForTesting("Fideos", 1.0, LocalDate.now().plusDays(1), CategoriaAlimento.GRANOS);

        provideInput("7\n4\n"); 
        BancoDeAlimentos.main(new String[]{"3", "4"}); 

        String output = outContent.toString().toUpperCase();
        assertTrue(output.contains("OPCIÓN INVÁLIDA."), "Output: " + output);
    }
}
