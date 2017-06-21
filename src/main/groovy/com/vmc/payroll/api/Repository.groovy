package com.vmc.payroll.api

/**
 * This interface is a collection, threfore anyone can use it as it were a collection. The implements Collection<E> is'nt present because it causes groovy to generate wrong
 * code that causes problems to it subclasses.
 */
interface Repository<E extends Entity> {

    E get(id)

    void update(E employee)
}