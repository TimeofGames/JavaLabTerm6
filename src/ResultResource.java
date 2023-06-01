import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ResultResource {
    private double result;
    private int changeCount;

    public ResultResource() {
        this.result = 0;
        this.changeCount = 0;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }

    public int getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(int changeCount) {
        this.changeCount = changeCount;
    }
}
