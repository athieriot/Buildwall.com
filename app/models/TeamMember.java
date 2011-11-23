package models;

import org.apache.commons.lang.StringUtils;
import play.data.validation.*;
import play.db.jpa.Model;
import utils.CypherUtil;

import javax.persistence.Entity;
import java.net.*;
import java.net.URL;

/**
 * @author Jean-Baptiste Lemée
 */
@Entity
public class TeamMember extends Model {

    private static final String URL_GRAVATAR = "http://www.gravatar.com/avatar/";
    private static final String MAX_GRAVATAR_SIZE = "?s=512";

    // Gravatar (Email) or Description.
    @Required public String description;
    @Required public String aliases;

    public boolean is(String alias) {
        if(StringUtils.isNotEmpty(description)&& description.trim().equalsIgnoreCase(alias.trim())){
            return true;
        } else {
            if(StringUtils.isNotEmpty(aliases)) {
                String[] aliasArray = StringUtils.split(aliases, ",;");
                for(String aliasIt : aliasArray){
                    if(aliasIt.trim().equalsIgnoreCase(alias.trim())){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public URL imageUrl() {
       if (description.indexOf("@") == -1) {
           return null; //new URL(baseURL.toString() + "/" + endURl);
       } else {
           try {
               return new URL(URL_GRAVATAR + CypherUtil.md5Hex(description) + MAX_GRAVATAR_SIZE);
           } catch (MalformedURLException e) {
               return null;
           }
       }
    }

    public String toString() {
        return description;
    }
}
