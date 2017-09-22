package com.vmc.payroll.external.persistence.orientDB.repository

import com.orientechnologies.orient.core.command.script.OCommandScript
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.domain.api.Repository

class OrientDBRepository<E extends Entity> extends AbstractCollection<E> implements Repository<E>{

    private Class<E> entityType
    private OObjectDatabaseTx database

    OrientDBRepository(Class<E> entityType, OObjectDatabaseTx database) {
        this.entityType = entityType
        this.database = database
    }

    @Override
    E get(Object id) {
        return null
    }

    @Override
    boolean add(E e) {
        if(e == null) throw new NullPointerException("Repositoryies cannot store null")
        return database.save(e)
    }

    @Override
    void update(E employee) {

    }

    @Override
    void clear() {
        executeCommand("delete from ${entityType.simpleName}")
    }

    @Override
    boolean contains(Object o) {
        if(!entityType.isInstance(o)) return false
        return !executeSQL("select from ${entityType.simpleName} where id = '${o.getId()}'").isEmpty()
    }

    @Override
    Iterator<E> iterator() {
        return database.browseClass(entityType) //Todo change to something more memory efficient.
    }

    @Override
    int size() {
        return executeCommand("select count(*) from ${entityType.simpleName}").get(0).field("count")
    }

    List<?> executeSQL(String sql) {
        database.query(new OSQLSynchQuery(sql))
    }

    def executeCommand(String sqlCommand) {
        return database.command(new OCommandScript(sqlCommand)).execute()
    }
}
