package a3;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Map;

public class AdminControlPanel extends JFrame {

    private TwitterService twitterService;
    private DefaultTreeModel treeModel;
    private JTree userGroupTree;

    public AdminControlPanel() {
        super("Admin Control Panel");
        twitterService = TwitterService.getInstance();

        // Initialize UI components
        initComponents();

        // Set JFrame properties
        setSize(1000, 500);
        setLocationRelativeTo(null); // Center the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create tree view
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        userGroupTree = new JTree(rootNode);
        treeModel = (DefaultTreeModel) userGroupTree.getModel();
        
        userGroupTree.setCellRenderer(new GroupTreeCellRenderer());

        JScrollPane treeScrollPane = new JScrollPane(userGroupTree);
        treeScrollPane.setPreferredSize(new Dimension(250, 400));

        mainPanel.add(treeScrollPane, BorderLayout.CENTER);

        // Control panel for buttons and input fields
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // Panel for user ID input
        JPanel userIdPanel = new JPanel();
        userIdPanel.add(new JLabel("User ID:"));
        JTextField userIdField = new JTextField(10);
        userIdPanel.add(userIdField);

        // Panel for group ID input
        JPanel groupIdPanel = new JPanel();
        groupIdPanel.add(new JLabel("Group ID:"));
        JTextField groupIdField = new JTextField(10);
        groupIdPanel.add(groupIdField);

        // Panel for buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Button to add user
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> {
            String userId = userIdField.getText().trim();
            if (!userId.isEmpty()) {
                twitterService.addUser(userId);
                updateTreeView();
                userIdField.setText("");
            }
        });
        buttonsPanel.add(addUserButton);

        // Button to add group
        JButton addGroupButton = new JButton("Add Group");
        addGroupButton.addActionListener(e -> {
            String groupId = groupIdField.getText().trim();
            if (!groupId.isEmpty()) {
                twitterService.addUserToGroup(groupId);
                updateTreeView();
                groupIdField.setText("");
            }
        });
        buttonsPanel.add(addGroupButton);

        // Button to open user view
        JButton openUserViewButton = new JButton("Open User View");
        openUserViewButton.addActionListener(e -> {
            TreePath path = userGroupTree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object selectedObject = selectedNode.getUserObject();
                if (selectedObject instanceof String) {
                    String userId = (String) selectedObject;
                    openUserView(userId);
                }
            }
        });
        buttonsPanel.add(openUserViewButton);

        // Button to validate IDs
        JButton validateIdsButton = new JButton("Validate IDs");
        validateIdsButton.addActionListener(e -> {
            boolean valid = validateUserGroupIds();
            if (valid) {
                JOptionPane.showMessageDialog(this, "All IDs are valid.");
            } else {
                JOptionPane.showMessageDialog(this, "There are duplicate IDs or IDs containing spaces.");
            }
        });
        buttonsPanel.add(validateIdsButton);

        // Button to find last updated user
        JButton lastUpdatedUserButton = new JButton("Last Updated User");
        lastUpdatedUserButton.addActionListener(e -> {
            String lastUpdatedUserId = twitterService.findLastUpdatedUser();
            JOptionPane.showMessageDialog(this, "Last updated user: " + lastUpdatedUserId);
        });
        buttonsPanel.add(lastUpdatedUserButton);

        controlPanel.add(userIdPanel);
        controlPanel.add(groupIdPanel);
        controlPanel.add(buttonsPanel);

        mainPanel.add(controlPanel, BorderLayout.WEST);

        // Add main panel to frame
        add(mainPanel);
        
        // Buttons for statistics
        JPanel statPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton totalUsersButton = new JButton("Total Users");
        totalUsersButton.addActionListener(e -> {
            int totalUsers = twitterService.getTotalUsers();
            JOptionPane.showMessageDialog(null, "Total Users: " + totalUsers);
        });
        statPanel.add(totalUsersButton);

        JButton totalGroupsButton = new JButton("Total Groups");
        totalGroupsButton.addActionListener(e -> {
            int totalGroups = twitterService.getTotalGroups();
            JOptionPane.showMessageDialog(null, "Total Groups: " + totalGroups);
        });
        statPanel.add(totalGroupsButton);

        JButton totalTweetsButton = new JButton("Total Tweets");
        totalTweetsButton.addActionListener(e -> {
            int totalTweets = twitterService.getTotalTweets();
            JOptionPane.showMessageDialog(null, "Total Tweets: " + totalTweets);
        });
        statPanel.add(totalTweetsButton);

        JButton positivePercentageButton = new JButton("Positive Tweets %");
        positivePercentageButton.addActionListener(e -> {
            double positivePercentage = twitterService.getPositiveTweetPercentage();
            JOptionPane.showMessageDialog(null, "Positive Tweets Percentage: " + positivePercentage + "%");
        });
        statPanel.add(positivePercentageButton);

        mainPanel.add(statPanel, BorderLayout.SOUTH);

        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(mainPanel);

        pack();

        // Make the frame visible
        setVisible(true);
    }

    private void openUserView(String userId) {
        UserView userView = new UserView(userId, twitterService);
        userView.setVisible(true);
        userView.addNewsFeedListener((userId1, updatedNewsFeed) -> {
            System.out.println("News feed updated for user " + userId1 + ": " + updatedNewsFeed);
        });
    }

    private boolean validateUserGroupIds() {
        Map<String, User> users = twitterService.getUsers();
        Map<String, UserGroup> groups = twitterService.getGroups();

        // Check users' IDs
        for (String userId : users.keySet()) {
            if (!isValidId(userId)) {
                return false;
            }
        }

        // Check groups' IDs
        for (String groupId : groups.keySet()) {
            if (!isValidId(groupId)) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidId(String id) {
        // Check for uniqueness and absence of spaces
        if (id.contains(" ") || isDuplicateId(id)) {
            return false;
        }
        return true;
    }

    private boolean isDuplicateId(String id) {
        Map<String, User> users = twitterService.getUsers();
        Map<String, UserGroup> groups = twitterService.getGroups();

        // Check among users
        if (users.containsKey(id)) {
            return true;
        }

        // Check among groups
        if (groups.containsKey(id)) {
            return true;
        }

        return false;
    }

    private void updateTreeView() 
    {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Root");
        populateTree(rootNode, "Root");
        treeModel.setRoot(rootNode);
        treeModel.reload(); // Refresh the tree model to reflect changes
        
    }
    
    private void populateTree(DefaultMutableTreeNode node, String groupId) 
    {
        UserGroup group = twitterService.getGroup(groupId);
        if (group != null) 
        {
            for (Object member : group.getMembers())
            {
                if (member instanceof User) 
                {
                    User user = (User) member;
                    DefaultMutableTreeNode userNode = new DefaultMutableTreeNode(user.getUserId());
                    node.add(userNode);
                } 
                else if (member instanceof UserGroup) 
                {
                    UserGroup subGroup = (UserGroup) member;
                    DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(subGroup.getGroupId());
                    groupNode.setAllowsChildren(true); // Enable to display as folder icon
                    node.add(groupNode);
                    populateTree(groupNode, subGroup.getGroupId());
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminControlPanel::new);
    }
}
