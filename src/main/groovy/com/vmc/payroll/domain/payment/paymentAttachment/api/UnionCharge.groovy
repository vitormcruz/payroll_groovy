package com.vmc.payroll.domain.payment.paymentAttachment.api

/**
 * I am an interface for various ways of union charges. Since I can interfere in the final pay value, I am also a
 * payment attachment.
 */
interface UnionCharge extends PaymentAttachment{

}