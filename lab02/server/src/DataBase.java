import java.util.ArrayList;

public class DataBase {
    private ArrayList<LicensePlate> cars;

    public DataBase() {
        this.cars = new ArrayList<>();
    }

    public Integer register(LicensePlate newPlate){
        for (LicensePlate plate: this.cars)
            if (plate.equals(newPlate))
                return -1;

        cars.add(newPlate);
        return cars.size();
    }

    public String lookup(String plateNumber) throws Exception {
        LicensePlate temp = new LicensePlate(plateNumber);
        for (LicensePlate plate: this.cars)
            if (plate.equals(temp))
                return plate.getOwner();
        return "NOT_FOUND";
    }
}
