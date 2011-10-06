package controllers;

import models.Build;
import models.BuildStatus;
import models.User;
import models.Wall;
import org.apache.commons.lang.StringUtils;
//import play.cache.Cache;
import play.mvc.Controller;

import java.net.URL;
import java.util.List;

public class BuildWallAPI extends Controller {

    public static void delete(Long wallId, String wallKey) {
        Wall wall = findWall(wallId, wallKey);
        checkWall(wall);
        wall.delete();
//        Cache.delete(wallCacheKey(wallId, wallKey));
    }

    public static void wall(Long wallId, String wallKey) {
        checkParams(wallId, wallKey);
        Wall wall = findWall(wallId, wallKey);
        checkWall(wall);
        renderJSON(wall);
    }

    public static void fullScreen(Long wallId, String wallKey) {
        Long wId = wallId;
        String wKey = wallKey;
        checkParams(wallId, wallKey);
        Wall wall = findWall(wallId, wallKey);
        checkWall(wall);
        render("@BuildWall.fullScreen", wId, wKey);
    }

    public static void newWallKey(Long wallId, String wallKey) {
        checkParams(wallId, wallKey);
        Wall wall = findWall(wallId, wallKey);
        checkWall(wall);
        wall.resetWallKey();
        wall.save();
//        Cache.set(wallCacheKey(wallId, wall.wallKey), wall, "30mn");
        renderJSON(wall);
    }

    public static void reset(Long wallId, String wallKey) {
        checkParams(wallId, wallKey);
        Wall wall = findWall(wallId, wallKey);
        checkWall(wall);
        wall.reset();
        wall.save();
//        Cache.set(wallCacheKey(wallId, wallKey), wall, "30mn");
        renderJSON(wall);
    }


    public static void update(Long wallId, String wallKey, String name, Integer buildsPerScreen) {
        checkParams(wallId, wallKey);
        Wall wall = findWall(wallId, wallKey);
        checkWall(wall);
        wall.update(name, buildsPerScreen);
        validWall(wall);
        wall.save();
//        Cache.set(wallCacheKey(wallId, wallKey), wall, "30mn");
        renderJSON(wall);
    }

    public static void image(String[] authors, String wallKey, Long wallId) {
        checkParams(wallId, wallKey);
        Wall wall = findWallInCache(wallId, wallKey);
        checkWall(wall);
        User user = findUserInCache(wall);
        if (authors != null) {
            for (String author : authors) {
                URL image = wall.findImageURL(author, user.team);
                if (null != image) {
                    renderText(image.toString());
                }
            }
        }

        notFound();
    }

    protected static void validWall(Wall wall) {
        if (!validation.valid(wall).ok) {
            badRequest();
        }
    }

    public static void publish(Long wallId, String wallKey, String project, String category, String authors, String status) {
        notifyBuild(wallId, wallKey, project, category, authors, status);
    }

    static void notifyBuild(Long wallId, String wallKey, String title, String subtitle, String authors, String status) {
        checkParams(wallId, wallKey, title, status);
        Wall wall = findWall(wallId, wallKey);
        checkWall(wall);
        wall.updateBuild(title, subtitle, authors, BuildStatus.valueOf(status.toUpperCase())).save();
        wall.save();
//        Cache.set(wallCacheKey(wallId, wallKey), wall, "30mn");
        renderText("OK");

    }

    public static void newBuilds(Long wallId, String wallKey) {
        Wall wall = findWallInCache(wallId, wallKey);
        checkWall(wall);
        final List<Build> buildList = wall.buildsSince(request.date);
        if (buildList.isEmpty()) {
            suspend("1s");
        }
        renderJSON(buildList);
    }

    protected static Wall findWallInCache(Long wallId, String wallKey) {
//        String wallCacheKey = wallCacheKey(wallId, wallKey);
//        Wall wall = Cache.get(wallCacheKey, Wall.class);
//        if (wall == null) {
        Wall wall = findWall(wallId, wallKey);
//            Cache.set(wallCacheKey, wall, "30mn");
//        }
        return wall;
    }

    private static User findUserInCache(Wall wall) {
//        String userCacheKey = userCacheKey(wall.id, wall.wallKey);
//        User user = Cache.get(userCacheKey, User.class);
//        if (user == null) {
        User user = findUser(wall.id, wall.wallKey);
//            Cache.set(userCacheKey, user, "15s");
//        }

        return user;
    }

    private static User findUser(Long id, String wallKey) {
        return User.find("select u from User u, Wall w where w.id = ? and w.wallKey = ? and w MEMBER OF u.accessibleWalls", id, wallKey).first();
    }

    protected static Wall findWall(Long wallId, String wallKey) {
        return Wall.find("id = ? and wallKey = ?", wallId, wallKey).first();
    }

    protected static String wallCacheKey(String wallId, String wallKey) {
        String wallCacheKey = "wall_" + wallId + "_" + wallKey;
        return wallCacheKey;
    }

    protected static String userCacheKey(Long wallId, String wallKey) {
        String wallCacheKey = "user_" + wallId + "_" + wallKey;
        return wallCacheKey;
    }

    protected static void checkParams(Object... params) {
        for (Object param : params) {
            if (null == param) {
                badRequest();
            }
        }
    }

    protected static void checkWall(Wall wall) {
        if (wall == null) {
            notFound();
        }
    }
}