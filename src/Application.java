import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class Application extends JFrame {
    private static final List<String> tableHeader = List.of(new String[]{"step", "min", "max", "result"});
    private static final List<List<String>> startData = List.of(List.of(new String[]{"0.01", "1", "5"}));
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

    private UDPThread server;

    public Application() throws SocketException {
        super("Lab_1");
        setContentPane(rootPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setSize(800, 600);
        server = new UDPThread();
        server.start();
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

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    server.sendEnd();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public static void main(String[] args) throws SocketException {
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
            try {
                out = new ObjectOutputStream(new BufferedOutputStream(
                        new FileOutputStream("BinaryStringNumber" + ".txt")));
                out.writeObject(data);
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
                ArrayList<RecIntegral> local = (ArrayList<RecIntegral>) in.readObject();
                local.forEach(i -> addRow(i.getData()));
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class SaveTextActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            BufferedOutputStream out = null;
            try {
                out = new BufferedOutputStream(
                        new FileOutputStream("TextStringNumber" + ".txt"));
                for (RecIntegral ri : data) {
                    out.write((ri.toString() + "\n")
                            .getBytes());
                }
                out.flush();
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
            Scanner in = null;
            RecIntegral restObj = null;
            int temp = table.getRowCount();
            if (temp != -1) {
                for (int i = 0; i < temp; i++) {
                    defaultTableModel.removeRow(0);
                }
            }
            data = new ArrayList<>();
            try {
                in = new Scanner(new InputStreamReader(new FileInputStream(fileChooser.getSelectedFile()
                        .getAbsolutePath())));
                while (in.hasNextLine()) {
                    restObj = RecIntegral.fromString(in.nextLine());
                    addRow(restObj.getData());
                }

            } catch (IOException ex) {
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

            if(server.getCountSockets() >= 3){
                try {
                    server.sendMessages(min,max,step);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                double result;
                try {
                    result= server.getResults();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                defaultTableModel.setValueAt(result,selectedRow, 3);
                data.get(selectedRow).setDataByIndex(3, String.valueOf(result));
            }
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
