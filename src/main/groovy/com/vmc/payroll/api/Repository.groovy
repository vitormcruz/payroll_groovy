package com.vmc.payroll.api

/**
 *
  */
interface Repository<E extends Entity> extends Collection<E>{

    E get(id)

    void update(E employee)
}