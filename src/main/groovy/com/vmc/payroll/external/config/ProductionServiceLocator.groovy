package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.orientechnologies.orient.core.sql.OCommandSQL
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery
import com.orientechnologies.orient.server.OServerMain
import com.vmc.concurrency.api.AtomicBlock
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.concurrency.singleVM.SingleVMAtomicBlock
import com.vmc.concurrency.singleVM.SingleVMModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.external.persistence.inMemory.InMemoryEmployeeRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory

import javax.sql.DataSource

class ProductionServiceLocator extends ServiceLocator{

    static final logger = LoggerFactory.getLogger(ProductionServiceLocator.class);

    @Lazy
    static final ProductionServiceLocator myself = {new ProductionServiceLocator()}()

    @Lazy
    protected Properties systemProperties = {System.getProperties()}()

    @Lazy
    protected ObjectMapper mapper = {new ObjectMapper().configure(MapperFeature.AUTO_DETECT_FIELDS, false)}()

    @Lazy
    protected AtomicBlock atomicBlock = {new SingleVMAtomicBlock()}()

    @Lazy
    protected ModelSnapshot modelSnapshot = {new SingleVMModelSnapshot(atomicBlock).with {
                                                    add(employeeRepository)
                                                    it
                                                  }}()

    @Lazy
    protected Repository<Employee> employeeRepository = {new InMemoryEmployeeRepository()}()

    @Lazy
    protected DataSource dataSource = {
        def hikariConfig = new HikariConfig()
        hikariConfig.setDriverClassName("com.orientechnologies.orient.jdbc.OrientJdbcDriver")
        hikariConfig.setUsername("root")
        hikariConfig.setPassword("root")
        hikariConfig.setJdbcUrl("jdbc:orient:remote:localhost/memory:test")
        return new HikariDataSource(hikariConfig)
    }()

    ProductionServiceLocator() {
        def orientDbServer = OServerMain.create()
        System.setProperty("ORIENTDB_HOME", new File("").getAbsolutePath());
        orientDbServer.startup(
         "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
         + "<orient-server>"
         + "<network>"
         + "<protocols>"
         + "<protocol name=\"binary\" implementation=\"com.orientechnologies.orient.server.network.protocol.binary.ONetworkProtocolBinary\"/>"
         + "</protocols>"
         + "<listeners>"
         + "<listener ip-address=\"0.0.0.0\" port-range=\"2424-2430\" protocol=\"binary\"/>"
         + "</listeners>"
         + "</network>"
         + "<users>"
         + "<user name=\"root\" password=\"root\" resources=\"*\"/>"
         + "</users>"
         + "<properties>"
         + "<entry name=\"log.console.level\" value=\"info\"/>"
         + "<entry name=\"log.file.level\" value=\"fine\"/>"
         //The following is required to eliminate an error or warning "Error on resolving property: ORIENTDB_HOME"
         + "<entry name=\"plugin.dynamic\" value=\"false\"/>"
         + "</properties>" + "</orient-server>");

//        orientDbServer.startup(new OServerConfiguration(new OServerConfigurationLoaderXml(OServerConfiguration, new File(""))).with { cfg ->
//                    network.listeners.clear()
//                    network.listeners.add(new OServerNetworkListenerConfiguration().with { listener ->
//                        portRange = "2424-2424"
//                        listener
//                    })
//
//                    users = [new OServerUserConfiguration(name: "root", password: "system")] as OServerUserConfiguration[]
//                    cfg.@properties = [new OServerEntryConfiguration(name:  "log.console.level", value: "fine"),
//                                       new OServerEntryConfiguration(name:  "log.file.level", value: "fine"),
//                                       new OServerEntryConfiguration(name:  "plugin.dynamic", value: "false")
//                                      ] as OServerEntryConfiguration[]
//
//                    cfg
//                })



        orientDbServer.activate()
        def process = new ProcessBuilder("console.bat", "create database remote:localhost/test root root memory;").start()
        process.waitFor()
        logger.info(process.inputStream.getText())
        process.destroy()
        def database = orientDbServer.openDatabase("memory:test", "root", "root")
        database.command(new OCommandSQL("create class Employee extends v")).execute()
        println(database.query(new OSQLSynchQuery("select * from Employee")))
        addShutdownHook {
            orientDbServer.shutdown()
        }

    }

}
