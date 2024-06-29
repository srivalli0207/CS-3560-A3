package a3;

import javax.swing.*;

public class Driver {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminControlPanel adminPanel = new AdminControlPanel();
            adminPanel.setVisible(true);
        });
    }
}
