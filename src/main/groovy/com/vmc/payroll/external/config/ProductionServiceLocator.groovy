package com.vmc.payroll.external.config

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.orientechnologies.orient.client.remote.OServerAdmin
import com.orientechnologies.orient.core.serialization.serializer.object.OObjectSerializer
import com.orientechnologies.orient.object.db.OObjectDatabaseTx
import com.orientechnologies.orient.object.serialization.OObjectSerializerContext
import com.orientechnologies.orient.object.serialization.OObjectSerializerHelper
import com.orientechnologies.orient.server.OServerMain
import com.orientechnologies.orient.server.config.OServerConfiguration
import com.orientechnologies.orient.server.config.OServerNetworkConfiguration
import com.orientechnologies.orient.server.config.OServerUserConfiguration
import com.vmc.concurrency.api.AtomicBlock
import com.vmc.concurrency.api.ModelSnapshot
import com.vmc.concurrency.singleVM.SingleVMAtomicBlock
import com.vmc.concurrency.singleVM.SingleVMModelSnapshot
import com.vmc.payroll.domain.Employee
import com.vmc.payroll.domain.api.Repository
import com.vmc.payroll.domain.payment.attachment.SalesReceipt
import com.vmc.payroll.domain.payment.attachment.ServiceCharge
import com.vmc.payroll.domain.payment.attachment.TimeCard
import com.vmc.payroll.domain.payment.attachment.api.PaymentAttachment
import com.vmc.payroll.domain.payment.attachment.api.UnionCharge
import com.vmc.payroll.domain.payment.attachment.api.WorkDoneProof
import com.vmc.payroll.domain.payment.delivery.AccountTransfer
import com.vmc.payroll.domain.payment.delivery.Mail
import com.vmc.payroll.domain.payment.delivery.Paymaster
import com.vmc.payroll.domain.payment.delivery.api.PaymentDelivery
import com.vmc.payroll.domain.payment.type.Commission
import com.vmc.payroll.domain.payment.type.Hourly
import com.vmc.payroll.domain.payment.type.Monthly
import com.vmc.payroll.domain.payment.type.api.PaymentType
import com.vmc.payroll.domain.unionAssociation.BasicUnionAssociation
import com.vmc.payroll.domain.unionAssociation.NoUnionAssociation
import com.vmc.payroll.domain.unionAssociation.api.UnionAssociation
import com.vmc.payroll.external.persistence.inMemory.repository.CommonInMemoryRepository
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.joda.time.DateTime

import javax.sql.DataSource
import java.util.concurrent.Executor

class ProductionServiceLocator extends ServiceLocator{

    private static myself = new ProductionServiceLocator()

    static ServiceLocator getInstance(){
        return myself
    }

    @Override
    Properties loadSystemProperties() {
        return System.getProperties()
    }

    @Override
    ObjectMapper loadMapper() {
        return new ObjectMapper().configure(MapperFeature.AUTO_DETECT_FIELDS, false)
    }

    @Override
    AtomicBlock loadAtomicBlock() {
        return new SingleVMAtomicBlock()
    }

    @Override
    ModelSnapshot loadModelSnapshot() {
        return new SingleVMModelSnapshot(atomicBlock).with {add(employeeRepository); it}
    }

    @Override
    Repository<Employee> loadEmployeeRepository() {
        return new CommonInMemoryRepository<Employee>()
    }

    @Override
    OObjectDatabaseTx loadOrientDBDatabase() {
        def orientDbServer = OServerMain.create()
        System.setProperty("ORIENTDB_HOME", new File("").getAbsolutePath());
        orientDbServer.startup(new OServerConfiguration().with { cfg ->
            location = "memory"
            network = new OServerNetworkConfiguration(this)
            users = [new OServerUserConfiguration(name: "root", password: "root", resources: "*")] as OServerUserConfiguration[]
            cfg
        })
        orientDbServer.activate()

        addShutdownHook {
            orientDbServer.shutdown()
        }

        new OServerAdmin("localhost").connect("root", "root").createDatabase("test", "document", "memory").close()
        OObjectDatabaseTx database = new OObjectDatabaseTx("memory:localhost/test").open("admin", "admin")

        OObjectSerializerContext serializerContext = new OObjectSerializerContext();
        serializerContext.bind(new OObjectSerializer<DateTime, Long>() {
            @Override
            Object serializeFieldValue(Class<?> iClass, DateTime iFieldValue) {
                return iFieldValue.getMillis()
            }

            @Override
            Object unserializeFieldValue(Class<?> iClass, Long iFieldValue) {
                return new DateTime(iFieldValue)
            }
        }, database)

        OObjectSerializerHelper.bindSerializerContext(null, serializerContext)

        [Employee, UnionAssociation, BasicUnionAssociation, NoUnionAssociation, PaymentAttachment, UnionCharge,
         WorkDoneProof, SalesReceipt, ServiceCharge, TimeCard, PaymentDelivery, AccountTransfer, Mail, Paymaster,
         PaymentType, Commission, Hourly, Monthly]
                .each { aClass -> database.getEntityManager().registerEntityClass(aClass) }

        return database
    }

    @Override
    DataSource loadDataSource() {
        def hikariConfig = new HikariConfig()
        hikariConfig.setDriverClassName("com.orientechnologies.orient.jdbc.OrientJdbcDriver")
        hikariConfig.setUsername("root")
        hikariConfig.setPassword("root")
        hikariConfig.setJdbcUrl("jdbc:orient:remote:localhost/memory:test")
        return new HikariDataSource(hikariConfig)
    }

    @Override
    Executor loadExecutor() {
        return {Thread.start(it)} as Executor
    }
}
