package controllers;

import models.User;
import play.cache.Cache;
import play.mvc.With;

import java.net.URL;

@With(Secure.class)
public class Team extends BuildWallAPI {

    public static void team() {
        render();
    }

    public static void save(Long memberId, String description, String aliases) {
        User user = Security.connectedUser();
        if (memberId != null) {
            if (user.updateTeamMember(memberId, description, aliases)) {
                user.save();
                renderText(memberId);
            } else {
                notFound();
            }
        } else {
            Long newMemberId = user.createTeamMember(description, aliases);
            user.save();
            renderText(newMemberId);
        }
    }

    public static void delete(Long memberId) {
        User user = Security.connectedUser();
        if (user.deleteTeamMember(memberId)) {
            user.save();
            renderText("OK");
        } else {
            notFound();
        }
    }

    public static void list() {
        User user = Security.connectedUser();
        //Cache.get(userCacheKey(wallId, wallKey));
        renderJSON(user.team);
    }
}