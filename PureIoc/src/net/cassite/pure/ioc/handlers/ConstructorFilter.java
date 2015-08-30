package net.cassite.pure.ioc.handlers;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import net.cassite.pure.ioc.AnnotationHandlingException;
import net.cassite.style.reflect.ConstructorSup;

public interface ConstructorFilter {
        boolean canHandle(Set<Annotation> annotations);

        ConstructorSup<Object> handle(List<ConstructorSup<Object>> cons, ConstructorFilterChain chain) throws AnnotationHandlingException;
}
