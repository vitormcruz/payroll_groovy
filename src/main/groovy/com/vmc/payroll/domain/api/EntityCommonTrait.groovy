package com.vmc.payroll.domain.api

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
/**
 * Defines commons traits for entities:
 *
 * <pre>
 * 1- They have an ID;
 * 2- Equals and hash code are defined at lest by it's id;
 * 3- Subclasses of entities may be equals to it's parents. See @com.vmc.payroll.domain.api.EntityCommonTrait#getEntityClass()
 * </pre>
 */
trait EntityCommonTrait implements Entity{

    boolean equals(Object that) {
        if(that == null) return false
        if(that.is(this)) return true
        if(!(that instanceof EntityCommonTrait)) return false
        if(!getEntityClass().isInstance(that)) return false
        if(!that.canEqual(this)) return false
        return new EqualsBuilder().append(this.getId(), that.getId()).isEquals()
    }

    /**
     * Used to simulate the common static equals check: other instanceof CurrentClass, where CurrentClass is the implementer of the EntityCommonTrait trait. Entities must
     * implement this method returning it's static class if they don't want to be considered equals to other Entities if they happend to have the same id. So, for example:
     *
     * <pre>
     * Supose two instances, employee$id='x' and process$id='x', if this method is not implemented accodingly in at least one of Employee or Process classes, then
     * employee$id='x'.equals(process$id='x') will be true
     *
     * Now suppose other two instances, process$id='x' and processSubclass1$id='x', again, if this method is not implemented accodingly in at least one of Process or
     * ProcessSubclass1 classes, then processSubclass1$id='x'.equals(process$id='x') will be true. This is usually desired only if ProcessSubclass1 don't have addictional
     * state to be used in equals method and if the change in behavior don't affect the equality. See Pitfall #4 in http://www.artima.com/lejava/articles/equality.html
     * for details of this equals scheme.
     * </pre>
     */
    def getEntityClass(){
        return EntityCommonTrait
    }

    //See Pitfall #4 in http://www.artima.com/lejava/articles/equality.html
    boolean canEqual(EntityCommonTrait other) {
        return getEntityClass().isInstance(other)
    }

    int hashCode(){
        return new HashCodeBuilder(127, 3).append(getId()).toHashCode()
    }

}