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

    public static RecIntegral fromString(String inputData) {
        StringBuffer sb =new StringBuffer(inputData);
        sb.delete(0,20);
        sb.delete(sb.length()-2,sb.length());
        List<String> localstring = List.of(sb.toString().split(", "));
        return new RecIntegral(localstring);
    }

    @Override
    public String toString() {
        return "RecIntegral{" +
                "data=" + data +
                '}';
    }
}
