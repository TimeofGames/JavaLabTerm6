import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Application extends JFrame {
    private static final List<String> tableHeader = List.of(new String[]{"step", "min", "max", "result"});
    private static final List<List<String>> startData = List.of(List.of(new String[]{"0.01", "1", "5"}),
            List.of(new String[]{"0.001", "-2", "5"}));
    private static final int NON_EDITABLE_COLUMN = 3;
    private JTextField stepTextField;
    private JTextField minTextField;
    private JTextField maxTextField;
    private JButton addButton;
    private JButton deleteButton;
    private JButton calculateButton;
    private JTable table;
    private JPanel rootPanel;
    private JButton deleteTableButton;
    private JButton uploadButton;
    private JButton saveBinaryButton;
    private JButton saveTextButton;
    private JButton loadingBinaryButton;
    private JButton loadingTextButton;
    private DefaultTableModel defaultTableModel;
    private List<RecIntegral> data;

    public Application() {
        super("Lab_1");
        setContentPane(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(800, 600);
        addButton.addActionListener(new AddButtonActionListener());
        deleteButton.addActionListener(new DeleteButtonActionListener());
        calculateButton.addActionListener(new CalculateButtonActionListener());
        defaultTableModel.addTableModelListener(new ChangeTableListener());
        deleteTableButton.addActionListener(new DeleteTableListener());
        uploadButton.addActionListener(new UploadTableListener());

        saveBinaryButton.addActionListener(new SaveBinaryButtonActionListener());
        loadingBinaryButton.addActionListener(new LoadingBinaryButtonActionListener());
        saveTextButton.addActionListener(new SaveTextActionListener());
        loadingTextButton.addActionListener(new LoadingTextButtonActionListener());
    }

    public static void main(String[] args) {
        new Application();
    }

    private void createUIComponents() {
        data = new ArrayList<>();
        table = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != NON_EDITABLE_COLUMN;
            }
        };
        defaultTableModel = (DefaultTableModel) table.getModel();
        tableHeader.forEach(defaultTableModel::addColumn);
        startData.forEach(this::addRow);

    }

    private void addRow(List<String> data) {
        defaultTableModel.addRow(data.toArray());
        this.data.add(new RecIntegral(data));
    }

    private boolean inRange(double arg) {
        return arg > 0.000001 && arg < 1000000;
    }

    private class SaveBinaryButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ObjectOutputStream out = null;
            int temp = table.getSelectedRow();
            if (temp == -1) {
                return;
            }
            try {
                out = new ObjectOutputStream(new BufferedOutputStream(
                        new FileOutputStream("BinaryStringNumber" + temp + ".txt")));
                out.writeObject(data.get(temp));
                out.close();
            } catch (IOException ignored) {
            }
        }
    }

    private class LoadingBinaryButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView()
                    .getHomeDirectory());
            fileChooser.showOpenDialog(null);
            ObjectInputStream in = null;
            RecIntegral restObj = null;
            int temp = table.getRowCount();
            if (temp != -1) {
                for (int i = 0; i < temp; i++) {
                    defaultTableModel.removeRow(0);
                }
            }
            try {
                in = new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(fileChooser.getSelectedFile()
                                .getAbsolutePath())));
                restObj = (RecIntegral) in.readObject();
                data.add(restObj);
                defaultTableModel.addRow((Vector<?>) restObj.getData());
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class SaveTextActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            ObjectOutputStream out = null;
            int temp = table.getSelectedRow();
            if (temp == -1) {
                return;
            }
            try {
                out = new ObjectOutputStream(new BufferedOutputStream(
                        new FileOutputStream("TextStringNumber" + temp + ".txt")));
                out.writeObject(data.get(temp)
                        .toString());
                out.close();
            } catch (IOException ignored) {
            }
        }
    }

    private class LoadingTextButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView()
                    .getDefaultDirectory());
            fileChooser.showOpenDialog(null);
            ObjectInputStream in = null;
            RecIntegral restObj = null;
            int temp = table.getRowCount();
            if (temp != -1) {
                for (int i = 0; i < temp; i++) {
                    defaultTableModel.removeRow(0);
                }
            }
            try {
                in = new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(fileChooser.getSelectedFile()
                                .getAbsolutePath())));
                restObj = RecIntegral.fromString((String) in.readObject());
                data.add(restObj);
                defaultTableModel.addRow((Vector<?>) restObj.getData());

            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class AddButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<String> row = new ArrayList<>();
            try {
                if ((!stepTextField.getText()
                        .isEmpty() && inRange(Double.parseDouble(stepTextField.getText()))) &&
                        (!minTextField.getText()
                                .isEmpty() && inRange(Double.parseDouble(minTextField.getText()))) &&
                        (!maxTextField.getText()
                                .isEmpty() && inRange(Double.parseDouble(maxTextField.getText())))) {
                    row.add(stepTextField.getText());
                    stepTextField.setText("");
                    row.add(minTextField.getText());
                    minTextField.setText("");
                    row.add(maxTextField.getText());
                    maxTextField.setText("");
                    addRow(row);
                } else {
                    stepTextField.setText("");
                    minTextField.setText("");
                    maxTextField.setText("");
                    throw new WrongInputException();
                }
            } catch (WrongInputException exc) {
                new WrongInputDialog();
            }
        }
    }

    private class DeleteButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                defaultTableModel.removeRow(selectedRow);
                data.remove(selectedRow);
            }
        }
    }

    private class CalculateButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                return;
            }
            Vector args = defaultTableModel.getDataVector()
                    .get(selectedRow);
            double step = Double.parseDouble((String) args.get(0));
            double min = Double.parseDouble((String) args.get(1));
            double max = Double.parseDouble((String) args.get(2));
            double inResult = 0;
            double i;
            for (i = min; i <= max - step * 2; i += step) {
                inResult += (Math.sin(i) + Math.sin(i + step)) * step / 2;
            }
            inResult += (Math.sin(max) + Math.sin(i)) * (max - i) / 2;
            defaultTableModel.setValueAt(inResult, selectedRow, NON_EDITABLE_COLUMN);
            data.get(selectedRow)
                    .set(NON_EDITABLE_COLUMN, String.valueOf(inResult));
        }
    }

    private class ChangeTableListener implements TableModelListener {
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE) {
                data.get(table.getSelectedRow())
                        .set(table.getSelectedColumn(),
                                (String) defaultTableModel.getDataVector()
                                        .get(table.getSelectedRow())
                                        .get(table.getSelectedColumn()));
            }
        }
    }

    private class DeleteTableListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int items = defaultTableModel.getRowCount();
            for (int i = 0; i < items; i++) {
                defaultTableModel.removeRow(0);
            }
        }
    }

    private class UploadTableListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int items = defaultTableModel.getRowCount();
            for (int i = 0; i < items; i++) {
                defaultTableModel.removeRow(0);
            }
            data.forEach(i -> defaultTableModel.addRow(i.getData()
                    .toArray()));
        }
    }
}
