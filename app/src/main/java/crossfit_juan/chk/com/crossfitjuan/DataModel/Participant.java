package crossfit_juan.chk.com.crossfitjuan.DataModel;

/**
 * Created by erslab-gh on 2017-08-18.
 */

public class Participant {
    private String access_key;
    private String name;

    public Participant() {}

    @Override
    public String toString() {
        return "Participant{" +
                ", access_key='" + access_key + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Participant(String access_key, String name) {
        this.access_key = access_key;
        this.name = name;
    }

    public String getAccess_key() {
        return access_key;
    }

    public void setAccess_key(String access_key) {
        this.access_key = access_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
