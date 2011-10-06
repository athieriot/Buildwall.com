package controllers;

import models.User;
import models.Wall;
import play.data.validation.Valid;
import play.mvc.With;

@With(Secure.class)
public class BuildWall extends BuildWallAPI {

    public static void wall() {
        render();
    }

    public static void fullScreen() {
        render();
    }

    public static void create(String name, Integer buildsPerScreen) {
        Wall wall = new Wall(name, buildsPerScreen);
        validWall(wall);
        User user = Security.connectedUser();
        wall.save();
        user.addWall(wall);
        user.save();
        renderJSON(wall);
    }

    public static void deleteAll() {
        User user = Security.connectedUser();
        user.deleteAllWall();
        user.save();      
        renderJSON(user.accessibleWalls);
    }

     public static void list() {
       User user = Security.connectedUser();
         if(user.accessibleWalls.isEmpty()){
             notFound();
         }else {
            renderJSON(user.accessibleWalls.iterator().next());
         }
    }
}