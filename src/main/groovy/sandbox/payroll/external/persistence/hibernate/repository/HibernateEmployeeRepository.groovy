package sandbox.payroll.external.persistence.hibernate.repository

import com.querydsl.jpa.hibernate.HibernateQueryFactory
import org.hibernate.SessionFactory
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import sandbox.concurrency.AtomicBlock
import sandbox.payroll.business.entity.Employee
import sandbox.payroll.business.entity.repository.EmployeeRepository
import sandbox.payroll.business.entity.repository.entityQuery.QEmployee
import sandbox.concurrency.dbBased.hibernate.HibernateAtomicBlock

class HibernateEmployeeRepository implements EmployeeRepository{

    private SessionFactory sessionFactory
    private pending = []
    private AtomicBlock atomicBlock
    private TransactionTemplate transactionTemplate

    HibernateEmployeeRepository(SessionFactory sessionFactory, PlatformTransactionManager transactionManager) {
        this.sessionFactory = sessionFactory
        atomicBlock = new HibernateAtomicBlock(transactionManager)
        transactionTemplate = new TransactionTemplate(transactionManager)
    }

    @Override
    public void update(Employee employee) {
        pending.add({
            sessionFactory.getCurrentSession().merge(employee)
        })
    }

    public void executeAllPending(){
        pending.each {it()}
        pending.clear()
    }

    @Override
    int size() {
        return 0
    }

    @Override
    boolean isEmpty() {
        return false
    }

    @Override
    boolean contains(Object o) {
        return false
    }

    @Override
    Iterator<Employee> iterator() {
        //TODO use pagination
        Collection<Employee> employees
        transactionTemplate.execute{
            employees = new HibernateQueryFactory(this.sessionFactory.getCurrentSession()).selectFrom(QEmployee.employee).fetch()
        }

        return employees.iterator()
    }

    @Override
    Object[] toArray() {
        return new Object[0]
    }

    @Override
    def <T> T[] toArray(T[] a) {
        return null
    }

    @Override
    boolean add(Employee employee) {
        pending.add({
            sessionFactory.getCurrentSession().persist(employee)
        })
        return false
    }

    @Override
    boolean remove(Object o) {
        return false
    }

    @Override
    boolean containsAll(Collection<?> c) {
        return false
    }

    @Override
    boolean addAll(Collection<? extends Employee> c) {
        return false
    }

    @Override
    boolean removeAll(Collection<?> c) {
        return false
    }

    @Override
    boolean retainAll(Collection<?> c) {
        return false
    }

    @Override
    void clear() {

    }
}
