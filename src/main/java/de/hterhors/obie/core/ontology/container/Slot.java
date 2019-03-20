package de.hterhors.obie.core.ontology.container;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import de.hterhors.obie.core.ontology.ReflectionUtils;
import de.hterhors.obie.core.ontology.annotations.RelationTypeCollection;
import de.hterhors.obie.core.ontology.interfaces.IOBIEThing;

public class Slot {

//	final private Field f;
	final public boolean isMultiValueSlot;
	final public String slotName;
//	final private IOBIEThing parentEntity;
	final private List<IOBIEThing> multiValueSlotValues;
	final private IOBIEThing singleValueSlotValue;

	public Slot(IOBIEThing parentEntity, Field f) {
//		this.f = f;
		this.slotName = f.getName();
//		this.parentEntity = parentEntity;
		this.isMultiValueSlot = ReflectionUtils.isAnnotationPresent(f, RelationTypeCollection.class);

		try {
			if (this.isMultiValueSlot) {
				this.multiValueSlotValues = (List<IOBIEThing>) f.get(parentEntity);
				this.singleValueSlotValue = null;
			} else {
				this.singleValueSlotValue = (IOBIEThing) f.get(parentEntity);
				this.multiValueSlotValues = Collections.emptyList();
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public IOBIEThing getSingleValue() {
		if (isMultiValueSlot)
			throw new IllegalStateException("Slot is not of single value type: " + slotName);
		return singleValueSlotValue;
	}

	public List<IOBIEThing> getMultiValues() {
		if (!isMultiValueSlot)
			throw new IllegalStateException("Slot is not of multi value type: " + slotName);
		return multiValueSlotValues;
	}

}
