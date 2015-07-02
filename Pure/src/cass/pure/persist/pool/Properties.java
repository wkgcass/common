package cass.pure.persist.pool;

/**
 * connection properties<br/>
 * url, user, pwd
 * 
 * @author wkgcass
 *
 */
public class Properties {
	public String url;
	public String user;
	public String pwd;

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o instanceof Properties) {
			Properties p = (Properties) o;
			if (url.equals(p.url) && user.equals(p.user) && pwd.equals(p.pwd)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return url.hashCode() * 100 + user.hashCode() * 10 + pwd.hashCode();
	}

	public String toString() {
		return "{url=" + url + ", user=" + user + ", pwd=" + pwd + "}";
	}
}
