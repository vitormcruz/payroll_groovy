package sandbox.magritte.methodGenerator.imp

import sandbox.magritte.methodGenerator.GeneratedMethod


class SimpleGeneratedMethod implements GeneratedMethod{
    protected String methodName
    protected Closure closure

    SimpleGeneratedMethod() {
    }

    SimpleGeneratedMethod(String methodName, Closure closure) {
        this.methodName = methodName
        this.closure = closure
    }

    @Override
    String getMethodName() {
        return methodName
    }

    @Override
    Closure getClosure() {
        return closure
    }

    @Override
    void teachMyselfTo(Class grasshopper) {
        grasshopper.metaClass."${getMethodName()}" << getClosure()
    }
}
