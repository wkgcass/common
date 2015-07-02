package cass.toolbox.xml;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XMLParser{
	
	private DocumentBuilder db;
	
	private Document doc;
	private Document originalDoc;
	private File file;
	
	public XMLParser(File file) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		this.db = dbf.newDocumentBuilder();
		this.doc = db.parse(file);
		this.originalDoc=db.parse(file);
		this.file=file;
	}
	
	public XMLParser(InputStream in) throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		this.db = dbf.newDocumentBuilder();
		this.doc = db.parse(in);
		//this.originalDoc=db.parse(in);
		this.file=null;
	}

	
	@SuppressWarnings("unchecked")
	public <T> T select(String str) {
			Node node=this.getSelectedNode(str);
			if(null==node) return null;
			
			String lastPos=str.substring(str.lastIndexOf('.')+1).trim();
			if(lastPos.startsWith("list")
					||
					lastPos.startsWith("set")
					||
					lastPos.startsWith("map")
			){
				String[] lastParam;
				try{
					lastParam=lastPos.substring(lastPos.indexOf( '(' )+1,lastPos.indexOf( ')' )).split(",");
				}catch(IndexOutOfBoundsException e){
					lastParam=new String[0];
				}
				String config[][]=new String[lastParam.length][];
				try{
					for(int i=0;i<lastParam.length;++i){
						config[i]=lastParam[i].split("=");
						config[i][0]=config[i][0].trim();
						config[i][1]=config[i][1].trim();
					}
				}catch(NullPointerException e){
					e.printStackTrace();
					return null;
				}
				Map<String,String> additionalConfig=new HashMap<String,String>();
				for(String[] pair: config){
					additionalConfig.put(pair[0], pair[1]);
				}
				if(lastPos.startsWith("list")){
					return (T) this.parseList(node, additionalConfig);
				}else if(lastPos.startsWith("set")){
					return (T) this.parseSet(node, additionalConfig);
				}else if(lastPos.startsWith("map")){
					return (T) this.parseMap(node, additionalConfig);
				}else{
					return null;
				}
			}else{
				return (T) this.getValues(node);
			}
	}
	
	public List<Map<String, String>> childElementNames(String key){
		Node n=this.getSelectedNode(key);
		if(null==n) return null;
		List<Map<String, String>> ret=new LinkedList<Map<String, String>>();
		for(int i=0; i<n.getChildNodes().getLength(); ++i){
			if(n.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
				Node tmp=n.getChildNodes().item(i);
				Map<String, String> map=new HashMap<String,String>();
				map.put("#name", tmp.getNodeName());
				for(int j=0; j<tmp.getAttributes().getLength(); ++j){
					map.put(tmp.getAttributes().item(j).getNodeName(), tmp.getAttributes().item(j).getNodeValue());
				}
				ret.add(map);
				
			}
		}
		return ret;
	}
	
	public boolean insert(String key) {
		try{
			String nodePos=key.substring(0,key.lastIndexOf('.'));
			String elemName=key.substring(key.lastIndexOf('.')+1);
			
			String[][] attrs=null;
			if(elemName.contains("(")){
				String[] attrPair=null;
				String attrsString=elemName.substring(elemName.indexOf('(')+1, elemName.indexOf(')'));
				attrPair=attrsString.split(",");
				attrs=new String[attrPair.length][2];
				for(int i=0; i<attrPair.length ; ++i){
					String[] kv=attrPair[i].split("=");
					attrs[i][0]=kv[0].trim();
					attrs[i][1]=kv[1].trim();
				}
				
				elemName=elemName.substring(0,elemName.indexOf('('));
			}else{
				attrs=new String[0][];
			}
			Node n=this.getSelectedNode(nodePos);
			Element e=doc.createElement(elemName);
			for(String[] pair : attrs){
				e.setAttribute(pair[0], pair[1]);
			}
			
			n.appendChild(e);
	
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean delete(String key) {
		Node n=this.getSelectedNode(key);
		if(null==n) return false;
		try{
			n.getParentNode().removeChild(n);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean commite(){
		try{
			Transformer tf=TransformerFactory.newInstance().newTransformer();
			StreamResult result = new StreamResult(file);
			DOMSource source = new DOMSource(doc);
			tf.transform(source, result);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean rollback(){
		this.doc=this.originalDoc;
		try{
			this.originalDoc=db.parse(file);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	
	
	private Node getSelectedNode(String str){
		//format path
		String[] positionRaw=str.split("\\.");
		String[] position;
		String[][][] param = new String[positionRaw.length][][];
		if(
				(positionRaw[positionRaw.length-1].startsWith("list(") && positionRaw[positionRaw.length-1].endsWith(")"))
				||
				(positionRaw[positionRaw.length-1].startsWith("map(") && positionRaw[positionRaw.length-1].endsWith(")"))
				||
				(positionRaw[positionRaw.length-1].startsWith("set(") && positionRaw[positionRaw.length-1].endsWith(")"))
				||
				positionRaw[positionRaw.length-1].equals("list")
				||
				positionRaw[positionRaw.length-1].equals("map")
				||
				positionRaw[positionRaw.length-1].equals("set")
				){
			position=new String[positionRaw.length-1];
			String last=positionRaw[positionRaw.length-1];
			String[][] parameterpair=new String[0][];
			if(last.contains("(")){
				last=last.substring(last.indexOf('(')+1,last.indexOf(')'));
				String[] parameterpairlink=last.split(",");
				parameterpair=new String[parameterpairlink.length][2];
				for(int i=0;i<parameterpair.length;++i){
					parameterpair[i]=parameterpairlink[i].split("=");
				}
			}
			param[param.length-1]=parameterpair;
		}else{
			position=new String[positionRaw.length];
		}
		for(int i=0; i< position.length; ++i){
			if(positionRaw[i].contains("(")){
				position[i]=positionRaw[i].substring(0, positionRaw[i].indexOf('('));
				String parameters;
				try{
					parameters=positionRaw[i].substring(positionRaw[i].indexOf('(')+1,positionRaw[i].indexOf(')'));
				}catch(IndexOutOfBoundsException e){
					e.printStackTrace();
					return null;
				}
				String[] parameterpair=parameters.split(",");
				param[i]=new String[parameterpair.length][2];
				for(int index=0;index<param[i].length;++index){
					String[] pair=parameterpair[i].split("=");
					try{
						param[i][index][0]=pair[0];
						param[i][index][1]=pair[1];
					}catch(IndexOutOfBoundsException e){
						e.printStackTrace();
						return null;
					}
				}
			}else{
				position[i]=positionRaw[i];
			}
		}
		
		//start searching
		Node ret=this.doc;
		for(int i=0;i<position.length;++i){
			Map<String,?> values=this.getValues(ret);
			try{
				for(String[] pair : param[i]){
					if(!values.get(pair[0]).toString().equals(pair[1])){
						ret=null;
						break;
					}
				}
			}catch(NullPointerException e){
				
			}
			if(null==ret) break;
			boolean found=false;
			for(int j=0;j<ret.getChildNodes().getLength();++j){
				if(ret.getChildNodes().item(j).getNodeName().equals(position[i])){
					ret=ret.getChildNodes().item(j);
					found=true;
					break;
				}
			}
			if(!found){
				return null;
			}
		}
		if(positionRaw.length!=position.length){
			//last position parameter is list/set/map
			if(positionRaw[positionRaw.length-1].startsWith("list")){
				for(int j=0;j<ret.getChildNodes().getLength();++j){
					if(ret.getChildNodes().item(j).getNodeName().equals("list")){
						Node jnode = ret.getChildNodes().item(j);
						boolean found0=true;
						try{
							for(int k=0;k<param[param.length-1].length;++k){
								boolean found1=false;
								for(int l=0;l<jnode.getAttributes().getLength();++l){
									if(jnode.getAttributes().item(l).getNodeName().equals(param[param.length-1][k][0])
											&&
											jnode.getAttributes().item(l).getNodeValue().equals(param[param.length-1][k][1])
											){
										found1=true;
										break;
									}
								}
								if(!found1){
									found0=false;
									break;
								}
							}
						}catch(NullPointerException e){}
						if(found0){
							ret=jnode;
							break;
						}
					}
				}
			}else if(positionRaw[positionRaw.length-1].startsWith("set")){
				for(int j=0;j<ret.getChildNodes().getLength();++j){
					if(ret.getChildNodes().item(j).getNodeName().equals("set")){
						Node jnode = ret.getChildNodes().item(j);
						boolean found0=true;
						try{
							for(int k=0;k<param[param.length-1].length;++k){
								boolean found1=false;
								for(int l=0;l<jnode.getAttributes().getLength();++l){
									if(jnode.getAttributes().item(l).getNodeName().equals(param[param.length-1][k][0])
											&&
											jnode.getAttributes().item(l).getNodeValue().equals(param[param.length-1][k][1])
											){
										found1=true;
										break;
									}
								}
								if(!found1){
									found0=false;
									break;
								}
							}
						}catch(NullPointerException e){}
						if(found0){
							ret=jnode;
							break;
						}
					}
				}
			}else if(positionRaw[positionRaw.length-1].startsWith("map")){
				for(int j=0;j<ret.getChildNodes().getLength();++j){
					if(ret.getChildNodes().item(j).getNodeName().equals("map")){
						Node jnode = ret.getChildNodes().item(j);
						boolean found0=true;
						try{
							for(int k=0;k<param[param.length-1].length;++k){
								boolean found1=false;
								for(int l=0;l<jnode.getAttributes().getLength();++l){
									if(jnode.getAttributes().item(l).getNodeName().equals(param[param.length-1][k][0])
											&&
											jnode.getAttributes().item(l).getNodeValue().equals(param[param.length-1][k][1])
											){
										found1=true;
										break;
									}
								}
								if(!found1){
									found0=false;
									break;
								}
							}
						}catch(NullPointerException e){}
						if(found0){
							ret=jnode;
							break;
						}
					}
				}
			}
		}
		return ret;
	}
	
	/**
	 * 
	 * @param node
	 * @return comment : empty map.
	 * <br/>comment : maps string "#comment" to comment text
	 * <br/>text/cdata : maps string "#text" to List<text> in the node
	 * <br/>map : maps string "#map" to Map
	 * <br/>list: maps string "#list" to List
	 * <br/>set: maps string "#set" to Set
	 * <br/>element : maps element-attrs to their values, and 
	 * <br/> if getValues( child node ) contains 2 entries (#name and another value)
	 * <br/> child node would also be considered as an attr with key=#name value=another value.
	 */
	@SuppressWarnings("unchecked")
	private Map<String,Object> getValues(Node node){
		Map<String,Object> map=new HashMap<String,Object>();
		
		//COMMENT
		if(node.getNodeType()==Node.COMMENT_NODE){
			if(node.getNodeValue().trim().equals("")){
				return map;
			}
			map.put("#name", "#comment");
			List<String> list=new LinkedList<String>();
			list.add(node.getNodeValue());
			map.put("#comment", list);
			return map;
		}
		
		//TEXT/CDATA
		if(node.getNodeType()==Node.TEXT_NODE
				||
				node.getNodeType()==Node.CDATA_SECTION_NODE
				){
			if(node.getNodeValue().trim().equals("")){
				return map;
			}
			map.put("#name", "#text");
			List<String> list=new LinkedList<String>();
			list.add(node.getNodeValue());
			map.put("#text", list);
			return map;
		}
		
		//<map> node
		if(node.getNodeName().equalsIgnoreCase("map")){
			map.put("#name", "map");
			map.put("#map", this.parseMap(node, null) );
			return map;
		}
		//<list> node
		if(node.getNodeName().equalsIgnoreCase("list")){
			map.put("#name", "list");
			map.put("#list", this.parseList(node, null));
			return map;
		}
		//<set> node
		if(node.getNodeName().equalsIgnoreCase("set")){
			map.put("#name", "set");
			map.put("#set", this.parseSet(node, null));
			return map;
		}
		//get (<node key="value">)
		map.put("#name", node.getNodeName());
		try{
			for(int i=0;i<node.getAttributes().getLength();++i){
				map.put(node.getAttributes().item(i).getNodeName(),
						this.stringToObject(node.getAttributes().item(i).getNodeValue())
						);
			}
		}catch(NullPointerException e){
		
		}
		//get
		//<node>
		//			<key>
		//				value
		//			</key>
		for(int i=0;i<node.getChildNodes().getLength();++i){
			Node n=node.getChildNodes().item(i);
			
				
				Map<String,Object> childmap=this.getValues(n);
				Set<String> keySet = childmap.keySet();
				int count=0;
				for(@SuppressWarnings("unused") String s:keySet){
					++count;
				}
				if(2==count){
					if(
							((String)childmap.get("#name")).contains("#comment")
					){
						//comment
						if(map.get("#comment")==null){
							map.put("#comment", new LinkedList<String>());
						}
						((List<String>)map.get("#comment")).addAll((List<String>)childmap.get("#comment"));
					}else	 if(
							((String)childmap.get("#name")).equals("#text")
					){
						//text
						if(map.get("#text")==null){
							map.put("#text", new LinkedList<String>());
						}
						((List<String>)map.get("#text")).addAll((List<String>)childmap.get("#text"));
					}else if(
							null!=childmap.get("#map")
					){
						//map
						int index=-1;
						for(int j=0;j<n.getAttributes().getLength();++j){
							if(n.getAttributes().item(j).getNodeName().equalsIgnoreCase("name")){
								index=j;
								break;
							}
						}
						if(-1==index){
							map.put("#map", childmap.get("#map"));
						}else{
							map.put(n.getAttributes().item(index).getNodeValue(), childmap.get("#map"));
						}
					}else if(
							null!=childmap.get("#set")
					){
						//set
						int index=-1;
						for(int j=0;j<n.getAttributes().getLength();++j){
							if(n.getAttributes().item(j).getNodeName().equalsIgnoreCase("name")){
								index=j;
								break;
							}
						}
						if(-1==index){
							map.put("#set", childmap.get("#set"));
						}else{
							map.put(n.getAttributes().item(index).getNodeValue(), childmap.get("#set"));
						}
					}else if(
							null!=childmap.get("#list")
					){
						//list
						int index=-1;
						for(int j=0;j<n.getAttributes().getLength();++j){
							if(n.getAttributes().item(j).getNodeName().equalsIgnoreCase("name")){
								index=j;
								break;
							}
						}
						if(-1==index){
							map.put("#list", childmap.get("#list"));
						}else{
							map.put(n.getAttributes().item(index).getNodeValue(), childmap.get("#list"));
						}
					}else{
						//other
						//find another attr
						String attr=null;
						for(String s : keySet){
							if(!s.equals("#name")){
								attr=s;
							}
						}
						map.put((String)childmap.get("#name"), childmap.get(attr));
					}
				}
				
			
		}
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private Map<?,?> parseMap(Node node,Map<String,String> additionalConfig){
		if(!node.getNodeName().equalsIgnoreCase("map")){
			return null;
		}
		if(null==additionalConfig){
			additionalConfig=new HashMap<String,String>();
		}
		
		
		if(null==additionalConfig.get("value")){
			for(int i=0;i<node.getAttributes().getLength();++i){
				if(node.getAttributes().item(i).getNodeName().equals("value")){
					additionalConfig.put("value", node.getAttributes().item(i).getNodeValue());
				}
			}
		}
		if(null==additionalConfig.get("key")){
			
			for(int i=0;i<node.getAttributes().getLength();++i){
				if(node.getAttributes().item(i).getNodeName().equals("key")){
					additionalConfig.put("key", node.getAttributes().item(i).getNodeValue());
				}
			}
			if(null!=additionalConfig.get("key")){
				return this.parseMapWithKey(node, additionalConfig);
			}
			
			//infer key
			//get first element node
			Node firstElement=null;
			for(int i=0;i<node.getChildNodes().getLength();++i){
				if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
					firstElement=node.getChildNodes().item(i);
					break;
				}
			}
			if(null==firstElement){
				return new HashMap<Object,Object>();
			}
			//get values
			Map<String,Object> valuesOfFirstElement = this.getValues(firstElement);
			//check the following attr names
			// id name (#name)value key
			String attr=null;
			if(valuesOfFirstElement.containsKey("id")){
				attr="id";
				if(this.allChildNodesHaveAttrName(node, attr)){
					additionalConfig.put("key", attr);
				}
			}else if(valuesOfFirstElement.containsKey("name")){
				attr="name";
				if(this.allChildNodesHaveAttrName(node, attr)){
					additionalConfig.put("key", attr);
				}
			}else if(valuesOfFirstElement.containsKey(valuesOfFirstElement.get("#name"))){
				attr=(String)valuesOfFirstElement.get("#name");
				if(this.allChildNodesHaveAttrName(node, attr)){
					additionalConfig.put("key", attr);
				}
			}else if(valuesOfFirstElement.containsKey("key")){
				attr="key";
				if(this.allChildNodesHaveAttrName(node, attr)){
					additionalConfig.put("key", attr);
				}
			}else if(valuesOfFirstElement.get("#name").equals("key")
					&&
					valuesOfFirstElement.containsKey("value")
					){
				attr="value";
				if(this.allChildNodesHaveAttrName(node, attr)
						&&
						this.allChilNodesHaveNodeName(node, "key")
						){
					
					Map<Object,Object> map=new HashMap<Object,Object>();
					for(int i=0;i<node.getChildNodes().getLength();++i){
						if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
							Map<String,?>values=this.getValues(node.getChildNodes().item(i));
							map.put(((List<String>)values.get("#text")).get(0), values.get("value"));
						}
					}
					return map;
					
				}
			}else{
				Set<String> set=valuesOfFirstElement.keySet();
				for(String s:set){
					if(!s.startsWith("#")){
						additionalConfig.put("key", s);
						break;
					}
				}
			}
			
			return this.parseMapWithKey(node, additionalConfig);
		}else{
			return this.parseMapWithKey(node, additionalConfig);
		}
	}
	
	private Map<?,?> parseMapWithKey(Node node,Map<String,String> additionalConfig){
		if(null==additionalConfig.get("key")){
			return new HashMap<Object,Object>();
		}
		
		Map<Object,Object> map=new HashMap<Object,Object>();
		
		if(null==additionalConfig.get("value")
				||
				additionalConfig.get("value").equals("#node")
				
		){
			for(int i=0;i<node.getChildNodes().getLength();++i){
				if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
					Map<String,?>values=this.getValues(node.getChildNodes().item(i));
					map.put(
							this.stringToObject(
									values.get(additionalConfig.get("key")))
							, this.stringToObject(
									values));
				}
			}
		}else{
			for(int i=0;i<node.getChildNodes().getLength();++i){
				if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
					Map<String,?>values=this.getValues(node.getChildNodes().item(i));
					map.put(values.get(
							this.stringToObject(
									additionalConfig.get("key")))
							,this.stringToObject(
									values.get(additionalConfig.get("value"))));
				}
			}
			
		}
		
		return map;
	}
	
	private boolean allChildNodesHaveAttrName(Node node,String attr){
		for(int i=0;i<node.getChildNodes().getLength();++i){
			if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
				if(!this.getValues(node).containsKey(attr))
					return false;
			}else{
				continue;
			}
		}
		return true;
	}
	private boolean allChilNodesHaveNodeName(Node node,String name){
		for(int i=0;i<node.getChildNodes().getLength();++i){
			if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
				if(!node.getChildNodes().item(i).getNodeName().equals(name))
					return false;
			}else{
				continue;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private List<?> parseList(Node node,Map<String,String> additionalConfig){
		if(!node.getNodeName().equalsIgnoreCase("list")
				&&
				!node.getNodeName().equalsIgnoreCase("set")
				){
			return null;
		}
		if(null==additionalConfig){
			additionalConfig=new HashMap<String,String>();
		}
		if(null==additionalConfig.get("value")){
			for(int i=0;i<node.getAttributes().getLength();++i){
				if(node.getAttributes().item(i).getNodeName().equals("value")){
					additionalConfig.put("value", node.getAttributes().item(i).getNodeValue());
				}
			}
			if(null!=additionalConfig.get("value")){
				return this.parseListWithInferredValue(node, additionalConfig);
			}
			//infer value
			//get first element node
			Node firstElement=null;
			for(int i=0;i<node.getChildNodes().getLength();++i){
				if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
					firstElement=node.getChildNodes().item(i);
					break;
				}
			}
			if(null==firstElement){
				return new LinkedList<Object>();
			}
			//get values
			Map<String,Object> valuesOfFirstElement = this.getValues(firstElement);
			
			//check if it's the only attr
			Set<String> keySet=valuesOfFirstElement.keySet();
			int count=0;
			String found=null;
			for(String s:keySet){
				if(!s.startsWith("#")){
					++count;
					if(null==found){
						found=s;
					}else{
						found=null;
						break;
					}
				}
			}
			if(null!=found){
				if(this.allChildNodesHaveAndOnlyHaveAttr(node, found)){
					additionalConfig.put("value", found);
				}
			}else{
				if(0==count){
					if(this.allChilNodesHaveNodeName(node, (String)valuesOfFirstElement.get("#name"))){
						List<Object> list=new LinkedList<Object>();
						for(int i=0;i<node.getChildNodes().getLength();++i){
							if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
								Map<String,Object> values=this.getValues(node.getChildNodes().item(i));
								List<String> tmp=(List<String>)values.get("#text");
								try{
									for(String s:tmp){
										list.add(s);
									}
								}catch(NullPointerException e){
									if(1==this.childContainingElementCount(node.getChildNodes().item(i))){
										Node element=null;
										for(int j=0;j<node.getChildNodes().item(i).getChildNodes().getLength();++j){
											if(node.getChildNodes().item(i).getChildNodes().item(j).getNodeType()==Node.ELEMENT_NODE){
												element=node.getChildNodes().item(i).getChildNodes().item(j);
											}
										}
										list.add(this.getValues(element));
									}
								}
							}
						}
						return list;
					}
				}
			}
			
			
			return this.parseListWithInferredValue(node, additionalConfig);
		}else{
			return this.parseListWithInferredValue(node, additionalConfig);
		}
		
	}
	
	
	private int childContainingElementCount(Node node){
		int count=0;
		for(int i=0;i<node.getChildNodes().getLength();++i){
			if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
				++count;
			}
		}
		return count;
	}
	
	private List<?> parseListWithInferredValue(Node node,Map<String,String> additionalConfig){
		List<Object> list=new LinkedList<Object>();
		if(null==additionalConfig.get("value")
				||
				additionalConfig.get("value").equals("#node")
				){
			for(int i=0;i<node.getChildNodes().getLength();++i){
				if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
					Map<String,?>values=this.getValues(node.getChildNodes().item(i));
					list.add(values);
				}
			}
		}else{
			for(int i=0;i<node.getChildNodes().getLength();++i){
				if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
					Map<String,?>values=this.getValues(node.getChildNodes().item(i));
					list.add(
							this.stringToObject(values.get(additionalConfig.get("value")))
							);
				}
			}
		}
		
		return list;
	}
	private boolean allChildNodesHaveAndOnlyHaveAttr(Node node,String attr){
		for(int i=0;i<node.getChildNodes().getLength();++i){
			if(node.getChildNodes().item(i).getNodeType()==Node.ELEMENT_NODE){
				Map<String,Object> map=this.getValues(node.getChildNodes().item(i));
				if(!map.containsKey(attr))
					return false;
				else{
					Set<String> keySet=map.keySet();
					int count=0;
					for(String s:keySet){
						if(!s.startsWith("#")){
							if(0==count){
								count=1;
							}else{
								return false;
							}
						}
					}
				}
			}else{
				continue;
			}
		}
		return true;
	}
	
	
	private Set<?> parseSet(Node node,Map<String,String> additionalConfig){
		if(!node.getNodeName().equalsIgnoreCase("set")){
			return null;
		}
		List<?> list=this.parseList(node, additionalConfig);
		
		Set<Object> set=new HashSet<Object>();
		for(Object o : list){
			set.add(o);
		}
		
		return set;
	}
	
	private boolean isNumeric(String str){  
		int dotCount=0;
		for (int i = str.length()-1;i>=0;--i){    
			if(str.charAt(i)<'0' || str.charAt(i)>'9'){
				if(str.charAt(i)=='.'){
					if(1==dotCount) return false;
					else dotCount=1;
				}else{
					return false;
				}
			}
		}  
		return true;  
	} 
	
	private Object stringToObject(Object o){
		if(null==o) return null;
		String eValue=null;
		try{
			eValue=(String)o;
		}catch(ClassCastException e){
			return o;
		}
		eValue=eValue.trim();
		if(eValue.equalsIgnoreCase("null")
				||
				eValue.equalsIgnoreCase("nullptr")
				||
				eValue.equals("")
				){
			return null;
		}else if(eValue.equalsIgnoreCase("true")){
			return true;
		}else if(eValue.equalsIgnoreCase("false")){
			return false;
		}else if(this.isNumeric(eValue)){
			if(eValue.contains(".")){
				return Double.parseDouble(eValue);
			}else{
				return Integer.parseInt(eValue);
			}
		}else{
			return eValue;
		}
	}

}
