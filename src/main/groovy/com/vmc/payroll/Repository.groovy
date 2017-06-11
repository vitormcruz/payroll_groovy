package com.vmc.payroll

interface Repository<E extends Entity> extends Collection<E>{

    E get(id)

    void update(E employee)
}