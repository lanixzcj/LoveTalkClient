package com.example.lovetalk.service;

import android.widget.ImageView;

import com.avos.avoscloud.*;
import com.example.lovetalk.DemoApplication;
import com.example.lovetalk.util.Logger;
import com.example.lovetalk.util.PhotoUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by lzw on 14-9-15.
 */
public class UserService {
    public static ImageLoader imageLoader = ImageLoader.getInstance();

    public static AVUser findUser(String id) throws AVException {
        AVQuery<AVUser> q = AVUser.getQuery();
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        return q.get(id);
    }

    public static List<AVUser> findFriends(boolean useCache) throws Exception {
        final List<AVUser> friends = new ArrayList<AVUser>();
        final AVException[] es = new AVException[1];
        final CountDownLatch latch = new CountDownLatch(1);
        findFriendsWithCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK, new FindCallback<AVUser>() {
            @Override
            public void done(List<AVUser> avUsers, AVException e) {
                if (e != null) {
                    es[0] = e;
                } else {
                    friends.addAll(avUsers);
                }
                latch.countDown();
            }
        });
        latch.await();
        if (es[0] != null) {
            throw es[0];
        } else {
            List<String> userIds = new ArrayList<String>();
            for (AVUser user : friends) {
                userIds.add(user.getObjectId());
            }
            CacheService.cacheUsers(userIds);
            List<AVUser> newFriends = new ArrayList<>();
            for (AVUser user : friends) {
                newFriends.add(CacheService.lookupUser(user.getObjectId()));
            }
            DemoApplication.registerBatchUserCache(newFriends);
            return newFriends;
        }
    }

    public static List<AVUser> findFriends() throws Exception {
        return findFriends(false);
    }

    public static void findFriendsWithCachePolicy(AVQuery.CachePolicy cachePolicy, FindCallback<AVUser>
            findCallback) {
        AVQuery<AVUser> q = getFriendQuery();
        q.setCachePolicy(cachePolicy);
        q.setMaxCacheAge(TimeUnit.MINUTES.toMillis(1));
        q.findInBackground(findCallback);
    }

    public static AVQuery<AVUser> getFriendQuery() {
        AVUser curUser = AVUser.getCurrentUser();
        AVQuery<AVUser> q = null;
        try {
            q = curUser.followeeQuery(AVUser.class);
        } catch (Exception e) {
            //在 currentUser.objectId 为 null 的时候抛出的，不做处理
            Logger.e(e.getMessage());
        }
        return q;
    }

    public static void displayAvatar(String imageUrl, ImageView avatarView) {
        imageLoader.displayImage(imageUrl, avatarView, PhotoUtil.avatarImageOptions);
    }

    public static void cacheUser(List<String> uncachedIds) throws AVException {
        if (uncachedIds.size() == 0) {
            return;
        }
        findUsers(uncachedIds);
    }

    public static List<AVUser> findUsers(List<String> userIds) throws AVException {
        if (userIds.size() <= 0) {
            return new ArrayList<AVUser>();
        }
        AVQuery<AVUser> q = AVUser.getQuery();
        q.whereContainedIn("objectId", userIds);
        q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        List<AVUser> users = q.find();
        DemoApplication.registerBatchUserCache(users);
        return users;
    }

    public static void searchUser(String searchName, int skip, FindCallback<AVUser> findCallback) {
//    AVQuery<AVUser> q = AVUser.getQuery();
//    q.whereContains("username", searchName);
//    q.limit(10);
//    q.skip(skip);
//    User user = User.curUser();
//    List<String> friendIds = getFriendIds();
//    friendIds.add(user.getObjectId());
//    q.whereNotContainedIn(C.OBJECT_ID, friendIds);
//    q.orderByDescending(C.UPDATED_AT);
//    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
//    q.findInBackground(findCallback);
    }

    //  private static List<String> getFriendIds() {
////    List<User> friends = App.getInstance().getFriends();
////    List<String> ids = new ArrayList<String>();
////    for (User friend : friends) {
////      ids.add(friend.getObjectId());
////    }
////    return ids;
//  }
//
    public static List<AVObject> findMeetPeople() throws AVException {
        AVUser user = AVUser.getCurrentUser();
        AVRelation<AVObject> relation = user.getRelation("MeetingInfo");
        AVQuery<AVObject> query = relation.getQuery();
        query.whereExists("YourUser");
        query.include("YourUser");
        query.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
//		query.findInBackground(new FindCallback<AVObject>() {
//			@Override
//			public void done(List<AVObject> arg0, AVException arg1) {
//				// TODO Auto-generated method stub
//				if(arg1 == null){
//					List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
//					for(AVObject meeting:arg0){
//						MeetingInfo.addMeeting(meeting);
//					}
//					
//					for(HashMap<String, Object> meeting:MeetingInfo.getList()){
//						AVUser user = (AVUser)meeting.get("user");
//						Log.d("lan", user.getUsername());
//						Log.d("lan", meeting.get("times").toString());
//					}
//					Adapter = new MeetAdapter(context, MeetingInfo.getList());
//					listView.setAdapter(Adapter);
//				}
//			}
//		});
        return query.find();

    }

    //
    public static void saveSex(int gender, SaveCallback saveCallback) {
        AVUser user = AVUser.getCurrentUser();
        user.put("gender", gender);
        user.saveInBackground(saveCallback);
    }

    //
//  public static List<String> transformIds(List<? extends AVObject> objects) {
//    List<String> ids = new ArrayList<String>();
//    for (AVObject o : objects) {
//      ids.add(o.getObjectId());
//    }
//    return ids;
//  }
//
//  public static User signUp(String name, String password) throws AVException {
//    User user = new User();
//    user.setUsername(name);
//    user.setPassword(password);
//    user.signUp();
//    return user;
//  }
//
    public static void saveAvatar(String path) throws IOException, AVException {
        AVUser user = AVUser.getCurrentUser();
        if (user.getAVGeoPoint("location") == null) {
            PreferenceMap preferenceMap = new PreferenceMap(DemoApplication.context, user.getObjectId());
            user.put("location", preferenceMap.getLocation());
        }
        final AVFile file = AVFile.withAbsoluteLocalPath(user.getUsername(), path);
        file.save();
        user.put("avatar", file);
        ;

        user.save();
        user.fetch();
    }

    public static void addFriend(String friendId, final SaveCallback saveCallback) {
        AVUser user = AVUser.getCurrentUser();
        user.followInBackground(friendId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                saveCallback.done(e);
            }
        });
    }

    public static void removeFriend(String friendId, final SaveCallback saveCallback) {
        AVUser user = AVUser.getCurrentUser();
        user.unfollowInBackground(friendId, new FollowCallback() {
            @Override
            public void done(AVObject object, AVException e) {
                saveCallback.done(e);
            }
        });
    }

    //
    public static void cacheUserIfNone(String userId) throws AVException {
        if (DemoApplication.lookupUser(userId) == null) {
            DemoApplication.registerUserCache(findUser(userId));
        }
    }
}
