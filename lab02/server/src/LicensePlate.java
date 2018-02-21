public class LicensePlate {
    private String plate; // XX-XX-XX
    private String owner; // name

    public LicensePlate(String plate, String owner) throws Exception {
        if (!plate.matches("[A-Z0-9]{2}-[A-Z0-9]{2}-[A-Z0-9]{2}"))
            throw new Exception("Invalid License plate: " + plate);
        this.plate = plate;
        this.owner = owner;
    }

    public LicensePlate(String plate) throws Exception {
        this(plate, "");
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LicensePlate that = (LicensePlate) o;

        return getPlate().equals(that.getPlate());
    }

    @Override
    public int hashCode() {
        int result = getPlate().hashCode();
        result = 31 * result + (getOwner() != null ? getOwner().hashCode() : 0);
        return result;
    }
}
