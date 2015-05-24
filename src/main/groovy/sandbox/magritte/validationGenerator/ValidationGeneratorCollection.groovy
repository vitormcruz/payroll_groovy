package sandbox.magritte.validationGenerator

import sandbox.magritte.methodGenerator.GeneratedMethod
import sandbox.magritte.methodGenerator.description.MethodGenerator
import sandbox.magritte.methodGenerator.imp.SimpleGeneratedMethod

class ValidationGeneratorCollection implements MethodGenerator {

    Collection<SimpleGeneratedMethod> validatons = []
    private Object descriptedObject

    @Override
    Collection<GeneratedMethod> getGeneratedMethods() {
        return validatons
    }

    ValidationGeneratorCollection(descriptions) {
        descriptions.each {
           validatons.addAll(it.asMethodGenerator().getGeneratedMethods())
        }
    }

}