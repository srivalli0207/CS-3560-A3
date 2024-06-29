package a3;

import javax.swing.tree.DefaultMutableTreeNode;

public class UserGroupTreeNode extends DefaultMutableTreeNode {

    private String groupId;

    public UserGroupTreeNode(String groupId) {
        super(groupId);
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}
