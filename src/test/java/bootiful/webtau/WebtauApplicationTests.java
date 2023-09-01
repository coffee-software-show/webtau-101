package bootiful.webtau;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testingisdocumenting.webtau.db.Database;
import org.testingisdocumenting.webtau.junit5.WebTau;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

import static org.testingisdocumenting.webtau.WebTauCore.*;
import static org.testingisdocumenting.webtau.cfg.WebTauConfig.getCfg;
import static org.testingisdocumenting.webtau.db.Database.db;
import static org.testingisdocumenting.webtau.http.Http.http;

@WebTau
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebtauApplicationTests {

    private final DataSource dataSource;

    private Database mainDb;

    @Container
    @ServiceConnection
    static GenericContainer postgreSqlDb = new PostgreSQLContainer(
            DockerImageName.parse("postgres:latest"))
            .waitingFor(new HostPortWaitStrategy());

    @LocalServerPort
    private int port;

    WebtauApplicationTests(@Autowired DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @BeforeEach
    void before() {
        getCfg().setUrl("http://localhost:" + this.port);
        this.mainDb = db
                .labeled("main db")
                .fromDataSource(this.dataSource);
    }

    @Test
    void httpCustomersEndpointReturnsAllCustomers() throws Exception {

        var customerTable = this.mainDb.table("customer");
        customerTable.clear();
        http.get("/customers", (header, body) -> {
            body.should(equal(list()));
        });

        customerTable.insert(List.of(Map.of("id", 1, "name", "Sanjeev")));

        http.get("/customers", (header, body) -> {
            body.should(
                    contain(
                            map("id", 1, "name", "Sanjeev")
                    )
            );

        });
    }

}
