package test;

import static net.cassite.pure.data.Functions.*;

import net.cassite.pure.data.Query;
import net.cassite.pure.data.jpa.JPQLDataAccess;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by wkgcass on 15/10/10.
 */
public class Test {
    public static void main(String[] args) {
        // hibernate initiate
        EntityManager manager = Persistence.createEntityManagerFactory("mysqlJPA").createEntityManager();
        /*
        EntityTransaction tx=manager.getTransaction();
        tx.begin();

        // init data
        User userToSave = new User();
        userToSave.setAge(21);
        userToSave.setName("kg");

        Role roleToSave = new Role();
        roleToSave.setName("usr");

        roleToSave.getUsers().add(userToSave);
        userToSave.getRoles().add(roleToSave);

        manager.persist(userToSave);
        manager.persist(roleToSave);

        tx.commit();
        */

        Query query = new Query(new JPQLDataAccess(manager));

        User user = new User();
        Role role = new Role();
        user.getRoles().add(role);
        role.getUsers().add(user);

        // test > <> and
        List<User> list = query.from(user).where(user.age.$gt(15).and(role.name.$ne(user.name))).list();
        System.out.println("!!!");
        for (User u : list) {
            System.out.println(u.getId());
            System.out.println(u.getName());
            System.out.println(u.getAge());

            for (Role r : u.getRoles()) {
                System.out.println(r.getId());
                System.out.println(r.getName());
            }
        }

        // test > <> or
        query.from(user).where(user.age.$gt(15).or(role.name.$ne(user.name))).list();

        // test member
        query.from(role).where(user.age.$gt(18).and(role.id.member(user.roles))).list();

        // test exists and sub query
        query.from(user).where(exists(query.from(role).where(role.id.$ne(1).and(user.id.$ne(5)))).and(user.id.$gt(1))).list();
    }
}
