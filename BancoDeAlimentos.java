import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;

public class BancoDeAlimentos {

    //creamos un ArrayList para almacenar objetos de tipo Donacion disponiibles (cantidad > 0)
    private static ArrayList<Donacion> donacionesDisponibles = new ArrayList<>();
    //creamos un ArrayList para almacenar los objetos de tipo Donación que ya se hayan distribuido (cantidad <= 0)
    private static ArrayList<DonacionNoDisponible> donacionesNoDisponibles = new ArrayList<>();

    //creamos un objeto scanner para leer por la entrada estándar
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;

        do {
            //Imprimimos por pantalla el menú principal de la aplicación
            System.out.println("\n--- Banco de Alimentos ---");
            System.out.println("1. Ingresar Donación");
            System.out.println("2. Distribuir Donación");
            System.out.println("3. Generar Reporte");
            System.out.println("4. Salir");
            System.out.print("Elige una opción: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                //según la opción escogida se ejecutará un método u otro
                switch (opcion) {
                    case 1:
                        ingresarDonacion();
                        break;
                    case 2:
                        distribuirDonacion();
                        break;
                    case 3:
                        generarReporte();
                        break;
                    case 4:
                        System.out.println("Saliendo del sistema...");
                        break;
                    default:
                        System.out.println("Opción no válida. Intenta de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Opción inválida. Debes ingresar un número.");
                opcion = 0; // Reset opcion to ensure the loop continues and re-prompts
            }
        } while (opcion != 4);
        //mantenemos el menú abierto hasta que se seleccione la opción 4: salir
    }

    //método para registrar una donación en el ArrayList donacionesDisponibles
    private static void ingresarDonacion() {
        try {
            //Pedimos el nombre del alimento por teclado
            System.out.print("Introduce el nombre del alimento: ");
            String nombre = scanner.nextLine().toLowerCase();

            //Pedimos la cantidad del alimento en kg o litros

            System.out.print("Introduce la cantidad del alimento en kg o litros: ");
            double cantidad = Double.parseDouble(scanner.nextLine());

            //Pedimos la fecha de caducidad del alimento
            System.out.print("Introduce la fecha de caducidad (YYYY-MM-DD): ");
            LocalDate fecha = LocalDate.parse(scanner.nextLine());

            //Pedimos la categoria del alimento por teclado
            System.out.println("Introduce la categoría del alimento (GRANOS, LACTEOS, FRUTAS, VERDURAS, OTROS): ");
            CategoriaAlimento categoria = CategoriaAlimento.valueOf(scanner.nextLine().toUpperCase());

            //Creamos un nuevo objeto Donación con los datos recogidos
            Donacion donacion = new Donacion(nombre, cantidad, fecha, categoria);

            //Añadimos la donación al ArrayList
            donacionesDisponibles.add(donacion);

            //Informamos de que hemos registrado la donación con éxito
            System.out.println("Donación registrada con éxito.");
        } catch (NumberFormatException e) {
            System.out.println("Error: La cantidad debe ser un número válido.");
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println("Error: Formato de fecha inválido. Usa YYYY-MM-DD.");
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().startsWith("No enum constant CategoriaAlimento")) {
                System.out.println("Error: Categoría de alimento no válida.");
            } else if (e.getMessage() != null && e.getMessage().equals("La cantidad de la nueva donación debe ser positiva.")) {
                System.out.println(e.getMessage()); // Print the specific message from Donacion class
            } else {
                System.out.println("Error en los datos ingresados: " + (e.getMessage() != null ? e.getMessage() : ""));
            }
        } catch (Exception e) {
            //tratamos las excepciones con try-catch
            System.out.println("Error inesperado al registrar la donación: " + e.getMessage());
        }
    }

    //método para distribuir alguna donación disponible en donacionesDisponibles
    private static void distribuirDonacion() {


        if(!donacionesDisponibles.isEmpty()){
            try {
                //Primero muestra por pantalla las donaciones disponibles ordenadas por fecha de caducidad
                mostrarDonacionesDisponibles();
                //pedimos los datos sobre el alimento que se quiera distribuir
                System.out.print("Ingrese el nombre del alimento a distribuir: ");
                String nombre = scanner.nextLine().toLowerCase();

                System.out.print("Ingrese la cantidad a distribuir en kg o litros: ");
                double cantidadDistribuir;
                try {
                    cantidadDistribuir = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Error: La cantidad a distribuir debe ser un número válido.");
                    return; // Exit method if quantity is invalid
                }

                System.out.print("Ingrese el nombre de la organización beneficiaria: ");
                String organizacion = scanner.nextLine();


                boolean encontrada = false;

                //creamos un objeto iterador de donaciones e iteramos por el ArrayList
                Iterator<Donacion> it = donacionesDisponibles.iterator();
                while (it.hasNext()) {
                    Donacion d = it.next();
                    if (d.getNombreAlimento().equalsIgnoreCase(nombre)) {
                        //hemos encontrado el alimento
                        encontrada = true;
                        //comprobamos que tenemos suficiente cantidad de alimento
                        if (d.getCantidad() > cantidadDistribuir) {
                            d.setCantidad(d.getCantidad() - cantidadDistribuir);
                            //guardamos la donación en el histórico donacionesNoDisponibles
                            DonacionNoDisponible dnd = new DonacionNoDisponible(
                                        d.getNombreAlimento(), cantidadDistribuir,
                                        d.getFechaCaducidad(), d.getCategoria(), organizacion);
                            donacionesNoDisponibles.add(dnd);

                            System.out.println("Distribución realizada con éxito.".toUpperCase());

                        } else if(d.getCantidad() == cantidadDistribuir){
                            //si el alimento se ha quedado con cantidad 0, debemos pasarlo al listado de donacionesNoDisponibles
                            DonacionNoDisponible dnd = new DonacionNoDisponible(
                                        d.getNombreAlimento(), cantidadDistribuir,
                                        d.getFechaCaducidad(), d.getCategoria(), organizacion);
                            //añadimos la donación a donacionesNoDisponibles
                            donacionesNoDisponibles.add(dnd);
                            //eliminamos la donación que ya no está disponible
                            it.remove();
                            System.out.println("Distribución realizada con éxito.".toUpperCase());
                        }else{
                            //si no hay suficiente cantidad de alimento informamos i cancelamos la operación
                            System.out.println("La cantidad disponible del alimento solicitado es insuficiente.".toUpperCase());
                            return; // Important: returns from the method
                        }
                        break; // Found and processed, exit loop
                    }
                }

                if (!encontrada) {
                    System.out.println("Alimento no encontrado.".toUpperCase());
                }
            } catch (IllegalArgumentException e) { // Catching potential exceptions from DonacionNoDisponible constructor if quantity becomes <=0
                 System.out.println("Error en los datos para la distribución: ".toUpperCase() + (e.getMessage() != null ? e.getMessage().toUpperCase() : ""));
            } catch (Exception e) { // General exception for other unexpected issues
                System.out.println("Error inesperado en la distribución del alimento: ".toUpperCase() + (e.getMessage() != null ? e.getMessage().toUpperCase() : ""));
            }
        }else{
            mostrarDonacionesDisponibles(); // This will print "No hay donaciones disponibles aún."
        }

    }

    public static void ordenarDonacionesDisponibles(){

        //llamamos al método sort de Collections, pasandole como parámetro un nuevo objeto que compare donaciones
        Collections.sort(donacionesDisponibles, new Comparator<Donacion>() {
            public int compare(Donacion d1, Donacion d2) {
                return d1.getFechaCaducidad().compareTo(d2.getFechaCaducidad());
            }
        });

    }

    public static void ordenarDonacionesNoDisponibles(){
        Collections.sort(donacionesNoDisponibles, new Comparator<DonacionNoDisponible>() {
            public int compare(DonacionNoDisponible d1, DonacionNoDisponible d2) {
                return d1.getFechaCaducidad().compareTo(d2.getFechaCaducidad());
            }
        });
    }

    public static void mostrarDonacionesDisponibles(){

        if (donacionesDisponibles.isEmpty()) {
            System.out.println("No hay donaciones disponibles aún.".toUpperCase());
            return;
        }

        //antes de mostrarlas queremos que esten ordenadas
        ordenarDonacionesDisponibles();
        //después mostramos las donaciones
        System.out.println("\n--- Donaciones Disponibles ---".toUpperCase());
        for (Donacion d : donacionesDisponibles) {
            System.out.println(d.toString());
        }
    }

    private static void generarReporte() {
        System.out.println("\n--- Seleccione el reporte que le interese: ---".toUpperCase());
        System.out.println("1. Donaciones disponibles separadas por Categoría");
        System.out.println("2. Alimentos més próximos a caducar");
        System.out.println("3. Cantidad total por Categoría");
        System.out.println("4. Historial de Donaciones");
        System.out.print("Seleccione una opción: ");
        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1:
                    listarPorCategoria();
                    break;
                case 2:
                    listarProximasACaducar();
                    break;
                case 3:
                    mostrarStock();
                    break;
                case 4:
                    mostrarDonacionesNoDisponibles();
                    break;
                default: 
                    System.out.println("Opción inválida.".toUpperCase());
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Opción de reporte inválida. Debes ingresar un número.");
        }
    }

    // The empty listarPorCategoria() method that was here has been removed.

    public static void listarPorCategoria() {
        //por cada categoria buscamos en el ArrayList las donaciones que coincidan y las imprimimos por pantalla
        System.out.println("\n--- Donaciones Disponibles por Categoría ---".toUpperCase());
        for (CategoriaAlimento categoria : CategoriaAlimento.values()) {
            System.out.println("\nCategoría: " + categoria);
            boolean hayDonaciones = false;
            for (Donacion d : donacionesDisponibles) {
                if (d.getCategoria() == categoria) {
                    System.out.println(d.toString());
                    hayDonaciones = true;
                }
            }
            //si para una categoria no encontramos donaciones, informamos por pantalla
            if (!hayDonaciones) {
                System.out.println("  No hay donaciones en esta categoría.");
            }
        }
    }

    //método para mostrar las donaciones más próximas a caducar
    public static void listarProximasACaducar() {
        //definimos una fecha limite de caducidad para mostrar
        LocalDate fechaLimite = LocalDate.now().plusDays(7);
        boolean encontrado = false;

        System.out.println("\n--- Donaciones Próximas a Caducar (en los próximos 7 días) ---".toUpperCase());
        // Create a temporary list to hold soon-to-expire items to sort them
        ArrayList<Donacion> proximasACaducar = new ArrayList<>();
        for (Donacion d : donacionesDisponibles) {
            if (!d.getFechaCaducidad().isAfter(fechaLimite)) {
                proximasACaducar.add(d);
                encontrado = true;
            }
        }
        
        if (!encontrado) {
            System.out.println("No hay donaciones que caduquen en los próximos 7 días.");
        } else {
            // Sort the soon-to-expire items by expiration date
            Collections.sort(proximasACaducar, new Comparator<Donacion>() {
                public int compare(Donacion d1, Donacion d2) {
                    return d1.getFechaCaducidad().compareTo(d2.getFechaCaducidad());
                }
            });
            for (Donacion d : proximasACaducar) {
                System.out.println(d.toString());
            }
        }
    }

    public static void mostrarStock() {
        System.out.println("\n--- Stock Actual por Tipo de Alimento ---".toUpperCase());
        for (CategoriaAlimento categoria : CategoriaAlimento.values()) {
            double total = 0.0;
            for (Donacion d : donacionesDisponibles) {
                if (d.getCategoria() == categoria) {
                    total += d.getCantidad();
                }
            }
            System.out.println(categoria + ": " + total + " kg o litros");
        }
    }

    public static void mostrarDonacionesNoDisponibles(){
        if (donacionesNoDisponibles.isEmpty()) {
            System.out.println("No se han distribuïdo donaciones aún.".toUpperCase());
            return;
        }
        //antes de mostrarlas queremos que esten ordenadas
        ordenarDonacionesNoDisponibles();
        //después mostramos las donaciones
        System.out.println("\n--- Historial de Donaciones Distribuidas ---".toUpperCase());
        for (DonacionNoDisponible d : donacionesNoDisponibles) {
            System.out.println(d.toString());
        }
    }

    // Helper method for testing purposes
    public static void resetDonationsForTesting() {
        donacionesDisponibles.clear();
        donacionesNoDisponibles.clear();
    }
}
