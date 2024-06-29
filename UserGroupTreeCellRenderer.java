package a3;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class UserGroupTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if (node instanceof UserGroupTreeNode) {
            setIcon(UIManager.getIcon("Tree.openIcon")); // Customize the icon as needed
            setFont(getFont().deriveFont(Font.BOLD)); // Customize the font for groups
        } else {
            setIcon(UIManager.getIcon("Tree.leafIcon")); // Customize the icon as needed
            setFont(getFont().deriveFont(Font.PLAIN)); // Use plain font for users
        }

        return this;
    }
}
