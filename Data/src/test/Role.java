package test;

import net.cassite.pure.data.Data;
import net.cassite.pure.data.DataComparable;

/**
 * Created by wkgcass on 15/10/10.
 */
public class Role {
    public final DataComparable<Integer> id = new DataComparable<Integer>(this);
    public final Data<String> name = new Data<String>(this);

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
}
