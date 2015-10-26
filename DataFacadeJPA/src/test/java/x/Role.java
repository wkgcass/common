package x;

import net.cassite.datafacade.Data;
import net.cassite.datafacade.DataComparable;
import net.cassite.datafacade.DataIterable;
import net.cassite.datafacade.DataUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkgcass on 15/10/10.
 */
@Entity
public class Role {
    public final DataComparable<Integer> id = new DataComparable<Integer>(this);
    public final Data<String> name = new Data<String>(this);
    public final DataIterable<User, List<User>> users = new DataIterable<User, List<User>>(new ArrayList<User>(), this);

    @Id
    @GeneratedValue()
    public Integer getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public void setId(Integer id) {
        DataUtils.set(this.id, id);
    }

    public void setName(String name) {
        DataUtils.set(this.name, name);
    }

    @ManyToMany
    public List<User> getUsers() {
        return users.get();
    }

    public void setUsers(List<User> users) {
        DataUtils.set(this.users, users);
    }
}
