package sandbox.payroll.external.config.querydsl

import com.querydsl.jpa.codegen.HibernateDomainExporter
import org.hibernate.cfg.Configuration
import sandbox.sandboxapp.external.config.main.SandboxApplication

class QueryClassGenerator {

    public static void main(String[] args) throws Exception {
        HibernateDomainExporter exporter = new HibernateDomainExporter(
                "Q",
                new File("target/generatedQueries"),
                SandboxApplication.addResources(new Configuration()));

        exporter.execute();
    }

}