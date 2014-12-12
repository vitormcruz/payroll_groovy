package sandbox.payroll

import sandbox.magritte.Description
import sandbox.magritte.DescriptionContainer
import sandbox.magritte.DescriptionMethod
import sandbox.magritte.StringDescription

class Employee {

    def String name
    def String address
    def String email

    //TODO how to use only Interfaces? Such as IDescriptionContainter.new().acessor...? Or maby a global variable (coud be configured by spring, but I want to avoid that)
    @DescriptionMethod
    public Description myDescription(){
        return new DescriptionContainer(
                    new StringDescription().acessor("name").label("employee.name"),
                    new StringDescription().acessor("address").label("employee.address"),
                    new StringDescription().acessor("email").label("employee.email"))
    }

    def Collection<String> validate(){
        //TODO change for validation api based on description
        if(name.length() > 100){
            return ["employee.validation.name.maxsize.error"]
        }
        return []
    }

}
