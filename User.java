package a3;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String userId;
    private List<String> followers;
    private List<String> following;
    private List<String> newsFeed;
    private long lastUpdatedTime; 
    private int tweetCount;
    private long creationTime;

    public User(String userId) {
        this.userId = userId;
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.newsFeed = new ArrayList<>();
        this.lastUpdatedTime = System.currentTimeMillis();
        tweetCount = 0;
        this.creationTime = System.currentTimeMillis();
    }

    // Getters and setters for attributes
    
    public long getCreationTime() {
        return creationTime;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public List<String> getNewsFeed() {
        return newsFeed;
    }

    public long getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(long lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void postTweet(String message, TwitterService twitterService) {
        newsFeed.add(message);
        setLastUpdatedTime(System.currentTimeMillis()); // Update lastUpdatedTime
        twitterService.updateFollowersNewsFeed(userId, message);
        tweetCount++; // Increment tweet count
        for (String followerId : followers) {
            User follower = twitterService.getUser(followerId);
            if (follower != null) {
                follower.addToNewsFeed(message);
                follower.setLastUpdatedTime(System.currentTimeMillis());
            }
        }
        // Update followers' news feeds
        twitterService.updateFollowersNewsFeed(userId, message);
    }
    
    public int getTweetCount() 
    {
        return tweetCount;
    }

    public void addFollower(String followerId) {
        followers.add(followerId);
    }

    public void addFollowing(String followingId) {
        following.add(followingId);
    }

    public void addToNewsFeed(String message) {
        newsFeed.add(message);
    }
    
    public int getDistinctTweetCount() {
        return newsFeed.size(); // Assuming newsFeed contains distinct tweets
    }
}
