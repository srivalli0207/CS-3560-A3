package a3;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class GroupTreeCellRenderer extends DefaultTreeCellRenderer {

    private Icon groupIcon = UIManager.getIcon("FileView.directoryIcon");
    private Icon userIcon = UIManager.getIcon("FileView.fileIcon");

    public GroupTreeCellRenderer() {
        setOpenIcon(groupIcon);
        setClosedIcon(groupIcon);
        setLeafIcon(userIcon);  // Set user icon for leaf nodes
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        if (value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof String) {
                String nodeValue = (String) userObject;
                if (nodeValue.startsWith("Group")) {
                    setIcon(groupIcon);
                } else {
                    setIcon(userIcon);  // Set user icon for non-group nodes
                }
            }
        }
        return this;
    }
}
