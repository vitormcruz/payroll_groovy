package com.vmc.payroll.domain.unionAssociation

import com.vmc.payroll.domain.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.domain.payment.attachment.api.UnionCharge
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation

/**
 * I am used when there is no union membership. I am, therefore, a singleton Null Object for UnionAssociation interface, and my instance should
 * be obtained through getInstance static method.
 */
class NoUnionAssociation implements UnionAssociation{

    protected static validateInstantiation = {
        /* The first instantiation from my static context is valid. After that I will aways throw and UnuportedOperationException */
        validateInstantiation = {throw new UnsupportedOperationException("I am a singleton, please get my instance thought the getInstance method.")}
    }
    protected static myself = new NoUnionAssociation()

    static getInstance(){
        return myself
    }

    /**
     * Use getInstance instead, I am a singleton.
     */
    NoUnionAssociation(){
        validateInstantiation()
    }

    @Override
    Integer getRate() {
        return null
    }

    @Override
    void setRate(Integer newRate) {
        throw new UnsupportedOperationException("This employee don't have a union association, you cannot set a rate to it")
    }

    @Override
    def getEmployee() {
        return null
    }

    @Override
    Boolean isUnionMember() {
        return false
    }

    @Override
    void postPaymentAttachment(PaymentAttachment paymentAttachment) {
        throw new UnsupportedOperationException("This employee don't have a union association, you cannot post a work event to it")
    }

    @Override
    Collection<UnionCharge> getCharges() {
        return Collections.emptyList()
    }
}
