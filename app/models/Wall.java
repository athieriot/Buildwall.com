package models;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import play.data.validation.Min;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;
import java.net.URL;
import java.util.*;

@Entity
public class Wall extends Model {

    @Required public String name;
    public String wallKey;
    @Required @Min(0) public Integer buildsPerScreen;

    @OneToMany(orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    @OrderBy(value="date")
    public Set<Build> buildList;

    public Wall(String name, Integer buildsPerScreen) {
        super();
        this.name = name;
        this.buildsPerScreen = buildsPerScreen;
        resetWallKey();
        this.buildList = new TreeSet<Build>();
    }

    public void addBuild(Build build) {
        buildList.add(build);
    }

    public Build updateBuild(String title, String subtitle, String authors, BuildStatus status) {
        Build build = findBuild(title);
        if (build == null) {
            build = new Build(title, subtitle, authors, status);
            buildList.add(build);
        } else {
            build.updateWith(subtitle, authors, status);
        }

        return build;
    }

    public void update(String name, Integer buildsPerScreen) {
        this.name = name;
        this.buildsPerScreen = buildsPerScreen;
    }

    private Build findBuild(String title) {
        if(StringUtils.isEmpty(title)){
            return null;
        }
        for (Build build : buildList) {
            if (StringUtils.isNotEmpty(build.title) && build.title.trim().equalsIgnoreCase(title.trim())) {
                return build;
            }
        }
        return null;
    }

    public List<Build> buildsSince(Date date) {
        List<Build> result = new ArrayList();
        final long latency = 2000;
        for (Build build : buildList) {
            if (build.date.getTime() > date.getTime() - latency) {
                result.add(build);
            }
        }
        return result;
    }

    public void resetWallKey() {
        this.wallKey = RandomStringUtils.random(12, "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789");
    }

    public void reset() {
        buildList.clear();
    }

    public URL findImageURL(String author, Collection<TeamMember> team){
            return findImageURL(author,  team, new HashSet<Build>()); 
    }

    private URL findImageURL(String author, Collection<TeamMember> team, Collection<Build> excludeBuilds) {
        URL image = null;
        for (TeamMember member : team) {
            if (member.is(author)) {
                image = member.imageUrl();
            }
        }

         if(image==null){
            // find out if the author is a build
             image = findImageForBuild(author, team, excludeBuilds);
        }

        return image;
    }

    private URL findImageForBuild(String maybeABuild, Collection<TeamMember> team, Collection<Build> excludeBuilds) {
        Build build = findBuild(maybeABuild);
        if(build == null || excludeBuilds.contains(build)){
            return null;
        } else{
            excludeBuilds.add(build);
            for(String author: build.authors){
                URL image = findImageURL(author, team, excludeBuilds);
                if(image!=null){
                    return image;
                }
            }
        }

        return null;
    }

    public String toString() {
        return name;
    }
}
