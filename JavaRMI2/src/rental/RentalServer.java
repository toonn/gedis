package rental;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class RentalServer {

    public static void main(String[] args) throws ReservationException,
            NumberFormatException, IOException {
        List<Car> hertzCars = loadData("../assignment-RMI-23_src/hertz.csv");
        List<Car> dockxCars = loadData("../assignment-RMI-23_src/dockx.csv");
        String host = "localhost";
        if (args.length > 0)
            host = args[0];
        new RentalCompany("Hertz", hertzCars, host);
        new RentalCompany("Dockx", dockxCars, host);
    }

    public static List<Car> loadData(String datafile)
            throws ReservationException, NumberFormatException, IOException {
        System.out.println("Loading data for " + datafile);

        List<Car> cars = new LinkedList<Car>();

        int nextuid = 0;

        // open file
        BufferedReader in = new BufferedReader(new FileReader(datafile));
        // while next line exists
        while (in.ready()) {
            // read line
            String line = in.readLine();
            // if comment: skip
            if (line.startsWith("#"))
                continue;
            // tokenize on ,
            StringTokenizer csvReader = new StringTokenizer(line, ",");
            // create new car type from first 5 fields
            CarType type = new CarType(csvReader.nextToken(), Integer
                    .parseInt(csvReader.nextToken()), Float
                    .parseFloat(csvReader.nextToken()), Double
                    .parseDouble(csvReader.nextToken()), Boolean
                    .parseBoolean(csvReader.nextToken()));
            System.out.println(type);
            // create N new cars with given type, where N is the 5th field
            for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                cars.add(new Car(nextuid++, type));
            }
        }

        return cars;
    }
}