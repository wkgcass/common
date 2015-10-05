package test;

import net.cassite.sql.SQL;
import net.cassite.sql.dialect.MySQLDialect;

public class SQLTest {

        public static void main(String[] args) {
                SQL $ = new SQL();
                System.out.println($.select().distinct("S.Id", "S.Name", "R.Id").and("R.Code").as("C").from("Student").as("S").fullJoin("Room")
                                .as("R").on("R.Id").eq("S.Room").where("S.Age").$gt(20).orderBy("S.Name").desc().pretty());

                System.out.println($.select().distinct("t.Id", "t.Name").from($.select("Id", "Name").from("tbl")).as("t").pretty());

                System.out.println($.select("t.Id", "t.Name").from("Tbl").as("t").whereExists($.select("t2.Id", "t2.Name").from("Tbl2").as("t2"))
                                .$(MySQLDialect.class).limit(100).$());

                System.out.println($.select("t.Id", "t.Name").from("Tbl").as("t")
                                .whereExists($.select("t2.Id", "t2.Name").from("Tbl2").as("t2").where("t").between(1).and(2)).$(MySQLDialect.class)
                                .limit(100).$());

                System.out.println($.insert().into("tbl")._("a", "b", "c").values(1, 2, 3)._(4, 5, 6));

                System.out.println($.insert().into("tbl")._("a", "b", "c")._($.select().distinct("a", "b", "c").from("tbl")));

                System.out.println($.update("tbl").set("a", 2).and("b", 3).and("c", 4).where("a").$eq(1));

                System.out.println($.delete().from("tbl").where("a").$eq(2));
        }

}
