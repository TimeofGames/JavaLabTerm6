import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ResultResource {
    int selectedRow;
    private DefaultTableModel model;
    private List<RecIntegral> data = new ArrayList<>();

    public ResultResource(DefaultTableModel model, List<RecIntegral> data , int selectedRow) {
        this.model = model;
        this.data = data;
        this.selectedRow = selectedRow;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public DefaultTableModel getModel() {
        return model;
    }

    public void setModel(DefaultTableModel model) {
        this.model = model;
    }

    public List<RecIntegral> getData() {
        return data;
    }

    public void setData(List<RecIntegral> data) {
        this.data = data;
    }
}
