package a3;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class UserView extends JFrame {

    private String userId;
    private TwitterService twitterService;
    private JTextArea followUserIdField = new JTextArea(1, 15);
    private DefaultListModel<String> followingListModel = new DefaultListModel<>();
    private JList<String> followingList = new JList<>(followingListModel);
    private JTextArea tweetMessageField = new JTextArea(2, 30);
    private DefaultListModel<String> newsFeedListModel = new DefaultListModel<>();
    private JList<String> newsFeedList = new JList<>(newsFeedListModel);
    private NewsFeedListener newsFeedListener;

    public UserView(String userId, TwitterService twitterService) {
        this.userId = userId;
        this.twitterService = twitterService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("User View - " + userId);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel for followings
        JPanel followingPanel = new JPanel(new BorderLayout());
        followingPanel.setBorder(BorderFactory.createTitledBorder("Followings"));
        followingPanel.add(new JScrollPane(followingList), BorderLayout.CENTER);

        JPanel followPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel followLabel = new JLabel("Follow User ID:");
        JButton followButton = new JButton("Follow");
        followButton.addActionListener(e -> {
            String followUserId = followUserIdField.getText().trim();
            if (!followUserId.isEmpty() && !followingListModel.contains(followUserId) && !followUserId.equals(userId)) {
                followingListModel.addElement(followUserId);
                twitterService.followUser(userId, followUserId);
                followUserIdField.setText("");
            }
        });
        followPanel.add(followLabel);
        followPanel.add(followUserIdField);
        followPanel.add(followButton);
        followingPanel.add(followPanel, BorderLayout.SOUTH);

        mainPanel.add(followingPanel, BorderLayout.WEST);

        // Panel for news feed
        JPanel newsFeedPanel = new JPanel(new BorderLayout());
        newsFeedPanel.setBorder(BorderFactory.createTitledBorder("News Feed"));

        newsFeedList.setModel(newsFeedListModel); // Set the model to JList

        // Add news feed list with custom renderer
        newsFeedList.setCellRenderer(new NewsFeedCellRenderer());

        newsFeedPanel.add(new JScrollPane(newsFeedList), BorderLayout.CENTER);

        List<String> initialNewsFeed = twitterService.getNewsFeed(userId);
        if (initialNewsFeed != null) {
            for (String tweet : initialNewsFeed) {
                newsFeedListModel.addElement(formatTweet(userId, tweet));
            }
        }

        mainPanel.add(newsFeedPanel, BorderLayout.CENTER);
        
        // Display creation time
        long creationTime = twitterService.getUserCreationTime(userId);
        JLabel creationTimeLabel = new JLabel("User Creation Time: " + new Date(creationTime).toString());
        mainPanel.add(creationTimeLabel, BorderLayout.NORTH);
        
        // Display last update time label
        long lastUpdateTime = twitterService.getUserLastUpdateTime(userId);
        JLabel lastUpdateTimeLabel = new JLabel("Last Update Time: " + new Date(lastUpdateTime).toString());
        mainPanel.add(lastUpdateTimeLabel, BorderLayout.SOUTH);

        // Panel for posting tweets
        JPanel postTweetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel tweetLabel = new JLabel("Tweet Message:");
        JButton postButton = new JButton("Post");
        postButton.addActionListener(e -> {
            String tweetMessage = tweetMessageField.getText().trim();
            if (!tweetMessage.isEmpty()) {
                twitterService.postTweet(userId, tweetMessage);
                // Update news feed list model
                String formattedTweet = formatTweet(userId, tweetMessage);
                newsFeedListModel.addElement(formattedTweet); // Add formatted tweet
                // Clear tweet text area
                tweetMessageField.setText("");
            }
        });
        postTweetPanel.add(tweetLabel);
        postTweetPanel.add(tweetMessageField);
        postTweetPanel.add(postButton);

        mainPanel.add(postTweetPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public void addNewsFeedListener(NewsFeedListener listener) {
        this.newsFeedListener = listener;
    }

    private void notifyNewsFeedListener(List<String> updatedNewsFeed) {
        if (newsFeedListener != null) {
            newsFeedListener.onNewsFeedUpdated(userId, updatedNewsFeed);
        }
    }

    private String formatTweet(String userId, String tweet) {
        return userId + ": " + tweet; // Format userId next to tweet message
    }

    // Custom cell renderer for news feed list
    private class NewsFeedCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String tweetText = (String) value;
            label.setText(tweetText); // Set the text with formatted tweet
            return label;
        }
    }
}
