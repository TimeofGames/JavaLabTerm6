import javax.swing.*;
import java.awt.event.*;

public class WrongInputDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    public WrongInputDialog() {
        setTitle("Wrong Input Data");
        setContentPane(contentPane);
        setVisible(true);
        getRootPane().setDefaultButton(buttonOK);
        setSize(300, 200);

        buttonOK.addActionListener(e -> onOK());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });


        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
