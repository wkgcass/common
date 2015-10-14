package test;

import static net.cassite.pure.data.Functions.*;

import net.cassite.pure.data.Query;
import net.cassite.pure.data.jpa.JPQLDataAccess;

/**
 * Created by wkgcass on 15/10/10.
 */
public class Test {
    public static void main(String[] args) {
        Query query = new Query(new JPQLDataAccess());

        User user = new User();
        Role role = new Role();

        // test > <>
        query.from(user).where(user.age.$gt(15).and(role.name.$ne(user.name))).list();

        // test member
        query.from(role).where(user.age.$gt(18).and(role.id.member(user.roles))).list();

        // test exists and sub query
        query.from(user).where(exists(query.from(role).where(role.id.$ne(1)))).list();
    }
}
