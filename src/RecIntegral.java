import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RecIntegral implements Serializable {

    private List<String> data;

    public RecIntegral(List<String> data) {
        this.data = new ArrayList<>(data);
        if(this.data.size()==3){this.data.add("0");}
    }

    public void set(int index, String data) {
        this.data.set(index,data);
    }

    public List<String> getData() {
        return data;
    }

    public static RecIntegral fromString(String inputData) {
        StringBuffer sb =new StringBuffer(inputData);
        sb.delete(0,18);
        sb.delete(sb.length()-2,sb.length());
        List<String> localstring = List.of(sb.toString().split(", "));
        return new RecIntegral(localstring);
    }

    public void setDataByIndex(int index, String record) {
        this.data.set(index, record);
    }
    @Override
    public String toString() {
        return "RecIntegral{" +
                "data=" + data +
                '}';
    }
}
