package net.cassite.pure.ioc;
/**
 * Thrown when handling an annotation.
 * @author wkgcass
 *
 */
public class AnnotationHandlingException extends IOCException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8266215180131541540L;
	
	public AnnotationHandlingException(){
		super();
	}
	public AnnotationHandlingException(String msg){
		super(msg);
	}
	public AnnotationHandlingException(Throwable t){
		super(t);
	}
	public AnnotationHandlingException(String msg, Throwable t){
		super(msg,t);
	}

}
