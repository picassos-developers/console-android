package app.mynta.console.android.models;

public class Packages {
    private String package_name;
    private String package_summary;
    private String package_description;
    private double package_price;
    private int package_id;

    public Packages(String package_name, String package_summary, String package_description, double package_price, int package_id) {
        this.package_name = package_name;
        this.package_summary = package_summary;
        this.package_description = package_description;
        this.package_price = package_price;
        this.package_id = package_id;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getPackage_summary() {
        return package_summary;
    }

    public void setPackage_summary(String package_summary) {
        this.package_summary = package_summary;
    }

    public String getPackage_description() {
        return package_description;
    }

    public void setPackage_description(String package_description) {
        this.package_description = package_description;
    }

    public double getPackage_price() {
        return package_price;
    }

    public void setPackage_price(double package_price) {
        this.package_price = package_price;
    }

    public int getPackage_id() {
        return package_id;
    }

    public void setPackage_id(int package_id) {
        this.package_id = package_id;
    }
}
