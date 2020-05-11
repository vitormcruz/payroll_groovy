Payroll Groovy
==============

[![Build Status](https://cloud.drone.io/api/badges/vitormcruz/payroll_groovy/status.svg)](https://cloud.drone.io/vitormcruz/payroll_groovy)
                
Implementation of the Payroll application as described (more or less) by Agile Software Development - Principles, Patterns and Practices. Used it to make various experiments.

#### Clean and Screaming Architecture
  
![Clean Architecture Image](https://8thlight.com/blog/assets/posts/2012-08-13-the-clean-architecture/CleanArchitecture.jpg)

The specific payroll implementation is on the payroll package, where two other packages reside: domain and external. 

The domain contains the Entity layer, it had the UseCase too but I decided to remove it since I think this layer is unecessary. I did all I could so that the domain package 
"screams": *Payroll*

The external package contains everything that is external to the business rules, such as presentation, persistence etc. Each of one those externalities are represented by one 
package, that contains, itself, another package representing a significant technology or framework used. Examples:

external.persistence.inMemory -> external domain persistence representation in memory
 
external.presentation.vaadin -> external domain presentation representation using Vaadin Framework

external.presentation.webservice.spark -> external domain presentation representation of webservices technology using Spark Framework 

All those correspond to the Interface Adapter Layer.

Lastly, beside the Interface Adapter packages, there is a package named config that corresponds to the Framework and Drivers layer. Since there is little code in there, I found 
unecessary to broke further packages, but it could be done if desired/needed. 

#### Rich model, or actual OO

I found it very hard using all OO resources with Hibernate, so I gave up since I wanted to explore OO design as much as possible. I relized that to use Hibernate is to accept 
limitations to your OO design (which shouldn't).
  
#### Validation using an uniform notification pattern

This implementation resides in the validationNotification package. For details, see see my post at
[Collecting Validation Results using Notification Pattern](http://techbeatscorner.blogspot.com.br/2017/04/collecting-validation-result-using.html).

#### Embedded Jetty

Really liked the approach of embedding the web server, still don't know how to do HotSwap. See PayrollJettyConfiguration for implementation details.

#### ByteBuddy

Really good API to do ByteCode manipulation, easier to use than CGLIB. I used it to create generic NullObjects of various classes. See GenericNullObjectBuilder for implementation 
details. 

#### Groovy Parse Class

Does everything Byte Buddy does but easier :), You create a GroovyClassLoader and the parseClass method passing the 
class string definition, done! See ObjectProxyFactory for details. 

#### Vaadin

(Will try the new version)

#### React


     
#### Spark

Liked, simpler than SpringMVC, also don't use annotations, which I generally dislike. 

#### No DI



#### Unit of work and domain synchronization with persistence

#### Generic Object Mother

Nice way to generate datasets of objects for integration and performance that adheres to domain rules and that can be switched easily to any persistence provider. Gets really 
powerful if used with [Faker](https://dius.github.io/java-faker/) or [Fixture Factory](https://github.com/six2six/fixture-factory). Can be used to provide generic Builders for
classes with complex construction. See ObjectMother class for implementation details. 

#### Method Extensions

When applying real OO, i.e. no Anemic Model, you get more in the situation where some method actually belongs to some object you don't have access. For example, all StringUtils 
methods belong to the String class in the OO point of view. Without method extensions support, the StringUtils class is just a consequence of language limitation. While 
I liked using methods extensions, it is rather messy and IDE support is somewhat crude.    