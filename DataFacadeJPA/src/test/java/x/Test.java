package x;

import static net.cassite.datafacade.Functions.*;

import net.cassite.datafacade.Query;
import net.cassite.datafacade.jpa.JPQLDataAccess;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

/**
 * Created by wkgcass on 15/10/10.
 */
public class Test {
    public static void main(String[] args) {
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
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("mysqlJPA");
        EntityManager manager = factory.createEntityManager();
        Query query = new Query(new JPQLDataAccess(manager));

        User user = new User();
        Role role = new Role();
        user.getRoles().add(role);
        role.getUsers().add(user);

        // test > <> and
        List<User> list = query.from(user).where(user.age.$gt(15).and(role.name.$ne(user.name))).list();
        for (User u : list) {
            System.out.println(u.getId());
            System.out.println(u.getName());
            System.out.println(u.getAge());

            for (Role r : u.getRoles()) {
                System.out.println(r.getId());
                System.out.println(r.getName());
            }
        }

        List<Map<String, Object>> listMap = query.from(user).where(user.age.$gt(15)).projection();
        System.out.println(listMap);

        // test > <> or
        query.from(user).where(user.age.$gt(15).or(role.name.$ne(user.name))).list();

        // test member
        query.from(role).where(user.age.$gt(18).and(role.id.member(user.roles))).list();

        // test exists and sub query
        query.from(user).where(exists(query.from(role).where(role.id.$ne(1).and(user.id.$ne(5)))).and(user.id.$gt(1))).list();

        query.from(user).where(sum(user.age).$gt(1l).and(user.id.$gt(2))).list();

        System.out.println(query.from(user).where(user.age.$gt(15)).count());

        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        query.from(user).where(user.age.$gt(18)).set(user.age.as(user.age.add(1)), user.name.as("wkg"));
        tx.commit();

    }
}
