package a3;

import java.util.*;

public class TwitterService {

    private static TwitterService instance;
    private Map<String, User> users;
    private Map<String, UserGroup> groups;
    private List<TwitterUpdateListener> listeners;
    private int totalTweetsCount; // Total tweets count across all users

    private TwitterService() {
        users = new HashMap<>();
        groups = new HashMap<>();
        listeners = new ArrayList<>();
        totalTweetsCount = 0; // Initialize total tweets count to zero
        // Initialize root group
        groups.put("Root", new UserGroup("Root"));
    }

    public static TwitterService getInstance() {
        if (instance == null) {
            instance = new TwitterService();
        }
        return instance;
    }

    public void addUser(String userId) {
        if (!users.containsKey(userId)) {
            users.put(userId, new User(userId));
            // Add user to Root group by default
            groups.get("Root").addMember(users.get(userId));
        }
    }

    public void addUserToGroup(String groupId) {
        if (!groups.containsKey(groupId)) {
            groups.put(groupId, new UserGroup(groupId));
            // Add the new group to the Root group by default
            groups.get("Root").addMember(groups.get(groupId));
        }
    }

    public void addUserToGroup(String groupId, String userId) {
        User user = users.get(userId);
        if (user != null && groups.containsKey(groupId)) {
            UserGroup group = groups.get(groupId);
            group.addMember(user);
        }
    }

    public int getTotalUsers() {
        return users.size();
    }

    public int getTotalGroups() {
        int count = 0;
        for (UserGroup group : groups.values()) {
            if (!(group.getGroupId().equals("Root"))) { // Exclude the root group
                count++;
            }
        }
        return count;
    }

    public User getUser(String userId) {
        return users.get(userId);
    }

    public UserGroup getGroup(String groupId) {
        return groups.get(groupId);
    }

    public void postTweet(String userId, String message) {
        if (users.containsKey(userId)) 
        {
            User user = users.get(userId);
            user.postTweet(message, this);
            totalTweetsCount++; // Increment total tweets count
        }
    }

    void updateFollowersNewsFeed(String userId, String message) {
        User user = users.get(userId);
        if (user != null) {
            List<String> followers = new ArrayList<>(user.getFollowers());
            followers.add(userId); // Include the user itself
            for (String followerId : followers) {
                User follower = users.get(followerId);
                if (follower != null) {
                    follower.addToNewsFeed(message);
                    follower.setLastUpdatedTime(System.currentTimeMillis());
                }
            }
        }
    }

    public List<String> getNewsFeed(String userId) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            return user.getNewsFeed();
        }
        return Collections.emptyList(); // Return an empty list if user not found or has no tweets
    }

    public void followUser(String userId, String targetUserId) {
        if (users.containsKey(userId) && users.containsKey(targetUserId)) {
            User user = users.get(userId);
            User targetUser = users.get(targetUserId);
            user.addFollowing(targetUserId);
            targetUser.addFollower(userId);
        }
    }

    public void addListener(TwitterUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TwitterUpdateListener listener) {
        listeners.remove(listener);
    }

    public int getTotalTweetsCount() {
        return totalTweetsCount;
    }
    
    public int getTotalTweets() {
        int total = 0;
        for (User user : users.values()) {
            total += user.getDistinctTweetCount(); // Count distinct tweets per user
        }
        return total;
    }

    public double getPositiveTweetPercentage() {
        int totalTweets = getTotalTweets();
        if (totalTweets == 0) {
            return 0.0;
        }

        int positiveCount = 0;
        for (User user : users.values()) {
            for (String tweet : user.getNewsFeed()) {
                if (isTweetPositive(tweet)) {
                    positiveCount++; // Only count if tweet is positive
                }
            }
        }

        double positivePercentage = ((double) positiveCount / totalTweets) * 100.0;
        return positivePercentage;
    }

    private boolean isTweetPositive(String tweet) {
        // Simplified positive word check, ensure each word only counts once
        String[] positiveWords = {"good", "great", "excellent"};
        for (String word : positiveWords) {
            if (tweet.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, UserGroup> getGroups() {
        return groups;
    }

    public long getUserCreationTime(String userId) {
        if (users.containsKey(userId)) {
            User user = users.get(userId);
            return user.getCreationTime();
        }
        return 0L; // Return default if user not found (handle as per your application logic)
    }

    public String findLastUpdatedUser() {
        long maxTime = Long.MIN_VALUE;
        String lastUpdatedUserId = null;

        for (User user : users.values()) {
            if (user.getLastUpdatedTime() > maxTime) {
                maxTime = user.getLastUpdatedTime();
                lastUpdatedUserId = user.getUserId();
            }
        }

        return lastUpdatedUserId;
    }

    public long getUserLastUpdateTime(String userId) {
        User user = users.get(userId);
        if (user != null) {
            return user.getLastUpdatedTime();
        }
        return -1; // Return -1 or handle as appropriate if user is not found
    }
}
