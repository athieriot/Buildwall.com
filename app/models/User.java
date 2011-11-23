package models;

import org.apache.commons.lang.StringUtils;
import play.data.validation.*;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jean-Baptiste Lemée
 */
@Entity
public class User extends Model {

    @OneToMany(fetch = FetchType.EAGER) public Set<Wall> accessibleWalls = new HashSet<Wall>();
    @OneToMany(fetch = FetchType.EAGER) public Set<TeamMember> team = new HashSet<TeamMember>();
    @Required @MinSize(6) public String username;
    @Required public String firstname;
    @Required public String lastname;
    @Required @MinSize(6) public String password;
    @Required @Equals("password") public String passwordConfirm;
    @Required @Email public String email;
    @Required @Equals("email") public String emailConfirm;
    @Required @IsTrue public boolean termsOfUse;
    @Required public Long creationDate = System.currentTimeMillis();
    @Required public boolean activated = false;
    public String company;

    public void activate() {
        this.activated = true;
    }

    public boolean isActivated() {
        return activated;
    }

    public void addWall(Wall wall) {
        accessibleWalls.add(wall);
    }

    public void deleteAllWall() {
        // TODO: Be carefull of other User who can access to those walls..
        accessibleWalls.clear();
    }

    public boolean isAccessibleWall(Wall wall) {
        if (wall == null) {
            return false;
        } else {
            return accessibleWalls.contains(wall);
        }
    }

    public boolean deleteTeamMember(Long memberId) {
        for (TeamMember member : team) {
            if (member.id.equals(memberId)) {
                team.remove(member);
                return true;
            }
        }
        return false;
    }

    public boolean updateTeamMember(Long memberId, String description, String aliases) {
        for (TeamMember member : team) {
            if (member.id.equals(memberId)) {
                member.description = description;
                member.aliases = aliases;
                member.save();
                return true;
            }
        }
        return false;
    }

    public Long createTeamMember(String description, String aliases) {
        TeamMember member = new TeamMember();
        member.description = description;
        member.aliases = aliases;
        team.add(member);
        member.save();
        return member.id;
    }

    protected String[] listAliases(String aliases) {
        return StringUtils.split(aliases, ";,");
    }

    public String toString() {
        return username;
    }
}
