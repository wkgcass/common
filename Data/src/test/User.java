package test;

import net.cassite.pure.data.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wkgcass on 15/10/10.
 */
@Entity
public class User {
    public final DataComparable<Integer> id = new DataComparable<Integer>(this);
    public final Data<String> name = new Data<String>(this);
    public final DataComparable<Integer> age = new DataComparable<Integer>(this);
    public final DataIterable<Role, List<Role>> roles = new DataIterable<Role, List<Role>>(new ArrayList<Role>(), this);

    @Id
    @GeneratedValue
    public Integer getId() {
        return id.get();
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Integer getAge() {
        return age.get();
    }

    public void setAge(Integer age) {
        this.age.set(age);
    }

    public void setRoles(List<Role> roles) {
        this.roles.set(roles);
    }

    @ManyToMany
    public List<Role> getRoles() {
        return this.roles.get();
    }
}
