import java.util.ArrayList;
import java.util.List;

public class RecIntegral {

    private List<String> data;

    public RecIntegral(List<String> data) {
        this.data = new ArrayList<>(data);
    }

    public void set(int index, String data) {
        this.data.set(index,data);
    }

    public List<String> getData() {
        return data;
    }
}
