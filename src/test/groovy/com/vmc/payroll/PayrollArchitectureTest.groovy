package com.vmc.payroll

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchRule
import org.junit.Test

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import static com.tngtech.archunit.library.Architectures.layeredArchitecture

class PayrollArchitectureTest {

    private static JavaClasses importedProductionClasses = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
                                                                                  .importPackages("com.vmc.payroll")
    @Test
    void "Use correct package structure"() {
        ArchRule rule = classes().should()
                                 .resideInAnyPackage("com.vmc.payroll.domain..", "com.vmc.payroll.usecase..", "com.vmc.payroll.adapter..",
                                                     "com.vmc.payroll.config..")
        rule.check(importedProductionClasses)
    }

    @Test
    void "Clean Architecture Dependency Rule"() {
        ArchRule rule = layeredArchitecture()
                .layer("Domain").definedBy("com.vmc.payroll.domain..")
                .layer("UseCase").definedBy("com.vmc.payroll.usecase..")
                .layer("InterfaceAdapters").definedBy("com.vmc.payroll.adapter..")
                .layer("Framewors&Drivers").definedBy("com.vmc.payroll.config..")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("UseCase", "InterfaceAdapters", "Framewors&Drivers")
                .whereLayer("UseCase").mayOnlyBeAccessedByLayers("InterfaceAdapters", "Framewors&Drivers")
                .whereLayer("InterfaceAdapters").mayOnlyBeAccessedByLayers("Framewors&Drivers")
                .whereLayer("Framewors&Drivers").mayNotBeAccessedByAnyLayer()

        rule.check(importedProductionClasses)
    }

    @Test
    void "Domain an UseCase cannot depend on external dependencies"() {
        ArchRule rule = noClasses().that()
                .resideInAnyPackage("com.vmc.payroll.domain..", "com.vmc.payroll.usecase..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("javax..", "java.sql..", "groovy.sql..")

        rule.check(importedProductionClasses)
    }
}
