package models;

import org.apache.commons.lang.StringUtils;
import play.db.jpa.Model;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Build extends Model implements Serializable, Comparable<Build> {

    public String title;
    public String subtitle;

    public String[] authors;
    public BuildStatus status;
    public Date date;

    public Build(String title, String subtitle, String authors, BuildStatus buildStatus) {
        super();
        this.title = title;
        this.subtitle = subtitle;
        this.authors = listAuthors(authors);
        this.status = buildStatus;
        this.date = new Date();
    }

    public void updateWith(String subtitle, String authors, BuildStatus buildStatus) {
        this.subtitle = subtitle;
        this.authors = listAuthors(authors);
        this.status = buildStatus;
        this.date = new Date();
    }

    protected String[] listAuthors(String authors) {
        if (StringUtils.isEmpty(authors)) {
            return new String[0];
        }
        String[] duplicateAuthors = StringUtils.split(authors, ";,");
        for (int i = 0; i < duplicateAuthors.length; i++) {
            duplicateAuthors[i] = duplicateAuthors[i].trim();
        }
        Set authorSet = new HashSet(Arrays.asList(duplicateAuthors));
        String[] duplicateLess = (String[]) (authorSet.toArray(new String[authorSet.size()]));

        return duplicateLess;
    }

    public String toString() {
        return title;
    }

    public int compareTo(Build o) {
        if (o == null) {
            return 1;
        } else {
            if (this.date.getTime() > o.date.getTime()) {
                return 1;
            } else if (this.date.getTime() == o.date.getTime()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
