package sandbox.payroll.payment

import sandbox.validationNotification.ApplicationValidationNotifier

abstract class GenericPaymentType implements PaymentType{

    protected static ApplicationValidationNotifier notifier = new ApplicationValidationNotifier()

    protected Long id
    protected Set<? extends PaymentAttachment> paymentAttachments = new HashSet<PaymentAttachment>()


    public Set<PaymentAttachment> getPaymentAttachments(){
        return new HashSet<PaymentAttachment>(paymentAttachments)
    }

}
