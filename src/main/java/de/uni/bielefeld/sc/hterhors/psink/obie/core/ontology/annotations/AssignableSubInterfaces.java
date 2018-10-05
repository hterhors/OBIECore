package de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.uni.bielefeld.sc.hterhors.psink.obie.core.ontology.interfaces.IOBIEThing;

@Target(value = { ElementType.TYPE, })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AssignableSubInterfaces {
	public Class<? extends IOBIEThing>[] get();
}
