package de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.FIELD, })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RelationTypeCollection {

}
