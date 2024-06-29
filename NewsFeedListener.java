package a3;

import java.util.List;

public interface NewsFeedListener {
    void onNewsFeedUpdated(String userId, List<String> updatedNewsFeed);
}
