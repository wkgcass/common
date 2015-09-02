package net.cassite.pure.ioc;

/**
 * Fill implements of this interface into Extend annotation.<br>
 * This interface's implements are used to simplify extending object factories'
 * retrieving process.<br>
 * 
 * @author wkgcass
 * 
 * @see net.cassite.pure.ioc.annotations.Extend
 *
 */
public interface ExtendingHandler {
        /**
         * Retrieve objects with given arguments.
         * 
         * @param args
         *                arguments from Extend annotation
         * @return retrieved object
         */
        Object get(String[] args);
}
