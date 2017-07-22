package com.vmc.payroll.unionAssociation

import com.vmc.payroll.payment.paymentAttachment.api.UnionCharge
import com.vmc.payroll.payment.paymentAttachment.api.PaymentAttachment
import com.vmc.payroll.unionAssociation.api.UnionAssociation
/**
 * I am used when there is no union membership. I am, therefore, a singleton Null Object for UnionAssociation interface, and my instance should
 * be obtained throught the getInstance static method.
 */
class NoUnionAssociation implements UnionAssociation{

    private static validateInstantiation = {/* The first instantiation from my static context is valid. */}
    private static myself = new NoUnionAssociation()

    static getInstance(){
        return myself
    }

    /**
     * Use getInstance instead, I am a singleton
     */
    NoUnionAssociation(){
        validateInstantiation()
        validateInstantiation = {throw new UnsupportedOperationException("I am a singleton, please get my instance thought the getInstance method.")}
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