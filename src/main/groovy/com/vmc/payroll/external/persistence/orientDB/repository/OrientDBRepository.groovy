package com.vmc.payroll.external.persistence.orientDB.repository

import com.orientechnologies.orient.core.command.script.OCommandScript
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.vmc.payroll.domain.api.Entity
import com.vmc.payroll.domain.api.Repository
import org.apache.commons.lang.StringUtils
//TODO Optimize super implementation
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
    boolean removeAll(Collection<?> c) {
        def itensToRemove = new ArrayList(c)
        Objects.requireNonNull(itensToRemove)
        itensToRemove.removeAll {it == null}
        if(itensToRemove.isEmpty()) return false
        validateTypes(itensToRemove)
        return executeCommand("delete from ${entityType.simpleName} where id in [${toListOfIds(itensToRemove)}]") > 0
    }

    void validateTypes(Collection<?> c) {
        c.each { validateType(it) }
    }

    void validateType(Object entity) {
        if (!entityType.isInstance(entity)) throw new ClassCastException("${entity.getClass()} is not of type ${entityType}")
    }

    String toListOfIds(Collection<?> listOfEntities) {
        return StringUtils.join(listOfEntities.collect { entity -> "'${entity.getId()}'" }, ", ")
    }

    @Override
    boolean retainAll(Collection<?> c) {
        def itensToKeep = new ArrayList(c)
        Objects.requireNonNull(itensToKeep)
        itensToKeep.removeAll {it == null}
        if(itensToKeep.isEmpty()){
            if(isEmpty()){
                return false
            }else {
                clear()
                return true
            }

        }
        validateTypes(itensToKeep)
        return executeCommand("delete from ${entityType.simpleName} where id not in [${toListOfIds(itensToKeep)}]") > 0
    }

    @Override
    boolean remove(Object o) {
        Objects.requireNonNull(o)
        validateType(o)
        return executeCommand("delete from ${entityType.simpleName} where id = '${o.getId()}'") > 0
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


    @Override
    public String toString() {
        return "OrientDBRepository{" +
                "entityType=" + entityType +
                ", database=" + database +
                '}';
    }
}
