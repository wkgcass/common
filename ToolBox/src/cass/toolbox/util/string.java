package cass.toolbox.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class string implements  java.io.Serializable, Comparable<string>, CharSequence {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1198543907439962261L;
	
	private String str;
	
	private static Map<String,string> exist=new HashMap<String,string>();
	protected string(String str){
		this.str=str;
	}
	
	public static string convert(String str){
		string s=exist.get(str);
		if(null==s){
			s=new string(str);
			exist.put(str, s);
		}
		return s;
	}

	@Override
	public int compareTo(string o) {
		return this.str.compareTo(o.str);
	}

	@Override
	public char charAt(int index) {
		return str.charAt(index);
	}

	@Override
	public int length() {
		return str.length();
	}

	@Override
	public CharSequence subSequence(int beginIndex, int endIndex) {
		return str.subSequence(beginIndex, endIndex);
	}
	
	public boolean isEmpty() {
		return str.isEmpty();
	}
	
	public int codePointAt(int index) {
        return str.codePointAt(index);
    }
	
	public int codePointBefore(int index) {
		return str.codePointBefore(index);
	}
	
	public int codePointCount(int beginIndex, int endIndex) {
		return str.codePointCount(beginIndex, endIndex);
	}
	
	public int offsetByCodePoints(int index, int codePointOffset) {
		return str.offsetByCodePoints(index, codePointOffset);
	}
	
	public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
		str.getChars(srcBegin, srcEnd, dst, dstBegin);
	}
	
	@Deprecated
	public void getBytes(int srcBegin, int srcEnd, byte dst[], int dstBegin) {
		str.getBytes(srcBegin, srcEnd, dst, dstBegin);
	}
	
	public byte[] getBytes(String charsetName)
	throws UnsupportedEncodingException
	{
		return str.getBytes(charsetName);
	}
	
	public byte[] getBytes(string charsetName)
	throws UnsupportedEncodingException
	{
		return str.getBytes(charsetName.str);
	}
	
	public byte[] getBytes(Charset charset) {
		return str.getBytes(charset);
	}
	
	public byte[] getBytes() {
		return str.getBytes();
	}
	
	public boolean equals(Object anObject) {
		if(anObject instanceof string){
			return this==anObject;
		}
		if(anObject instanceof String){
			return str.equals(anObject);
		}
		return false;
	}
	
	public boolean contentEquals(StringBuffer sb) {
		return str.contentEquals(sb);
	}
	
	public boolean contentEquals(CharSequence cs) {
		return str.contentEquals(cs);
	}
	
	public boolean equalsIgnoreCase(String anotherString) {
		return str.equalsIgnoreCase(anotherString);
	}
	
	public boolean equalsIgnoreCase(string anotherString) {
		return str.equalsIgnoreCase(anotherString.str);
	}
	
	public int compareTo(String anotherString) {
		return str.compareTo(anotherString);
	}
	
	//TODO

}
