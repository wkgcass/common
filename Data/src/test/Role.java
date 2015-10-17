package test;

import net.cassite.pure.data.Data;
import net.cassite.pure.data.DataComparable;
import net.cassite.pure.data.DataIterable;

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
        this.id.set(id);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    @ManyToMany
    public List<User> getUsers() {
        return users.get();
    }

    public void setUsers(List<User> users) {
        this.users.set(users);
    }
}
