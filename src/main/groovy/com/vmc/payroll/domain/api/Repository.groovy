package com.vmc.payroll.domain.api

interface Repository<E extends Entity> extends Collection<E>{

    E get(id)
}