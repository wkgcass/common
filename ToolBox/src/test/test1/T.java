package test.test1;

public class T implements IT{
	public int a=1;
	public T(){
		this.a=6;
	}
	public T(String a){
		
	}
	public T(long a){
		this.a=(int)a;
	}
	public int p(){
		return a;
	}
	public int p(int a,String b){
		return this.a+a+Integer.parseInt(b);
	}
}
