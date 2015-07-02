package test2;

//import cass.toolbox.sql.SQL;

import javax.swing.JFrame;
import javax.swing.JLabel;

import cass.toolbox.ui.SelfAdaptionJPanel;
import cass.toolbox.util.Utility_;
import cass.toolbox.xml.xquery.$;

public class TestGramma {
	public static void main(String[] args) throws InterruptedException, Throwable {
		/*
		 * System.out.println(SQL.insert.into("table")._("a", "b", "c")
		 * .values(2, 4.4, 5) + "\n" + SQL.select("a", "b",
		 * "c").from("table").where("a").equal(2)
		 * .and("b").equal(5).orderBy("c", SQL.DESC) + "\n" +
		 * SQL.update("table").set("a", 2).and("b", 5).where("c").lt(6) + "\n" +
		 * SQL.delete.from("table").where("c").equal(6));
		 */
		// new $("").xml("");
		JFrame jf = new JFrame();
		jf.setSize(1000, 700);
		jf.setLayout(null);
		SelfAdaptionJPanel sap = new SelfAdaptionJPanel(0, 0,
				SelfAdaptionJPanel.MODE_NONE);
		jf.add(sap);

		JLabel jl = new JLabel("���Թ�����");
		jl.setSize(500, 200);
		sap.add(jl);

		System.out.println(Utility_.toJSON(jl, true));
	}
}
