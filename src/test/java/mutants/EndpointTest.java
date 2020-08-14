package mutants;

import ar.com.ml.ibmcloud.projects.mutants.SBApplication;
import ar.com.ml.ibmcloud.projects.mutants.models.Person;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SBApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EndpointTest {

    public static final String HTTP_LOCALHOST = "http://localhost:";
    public static final String V1_IS_MUTANT = "/v1/mutant";
    public static final String V1_STATS = "/v1/stats";

    @Autowired
    private TestRestTemplate server;

    private final String[] DNAMutants = new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAATG", "CCCCTA", "TCACTT"};

    private final String[] DNAHumans = new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGTATG", "CCCATA", "CCGCTA"};

    private final String[] DNAInvalidMutants = new String[]{"ATGCGA", "CAGTGC", "TTATGT", "AGAATG", "CCCCTA", "TCACTR"};

    @LocalServerPort
    private int PORT;

    @Test
    public void ATestEndpoint() {
        String endpoint = HTTP_LOCALHOST + PORT;
        ResponseEntity<String> response = server.getForEntity(endpoint, String.class);
        HttpStatus status = response.getStatusCode();
        assertEquals("Index is OK: " + response, HttpStatus.OK, status);
    }

    @Execution(ExecutionMode.CONCURRENT)
    @TestFactory
    Collection<DynamicTest> BDynamicTestsFromCollectionIsMutantOK() {
        Collection<DynamicTest> dynamicTest = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            int finalI = i;
            dynamicTest.add(dynamicTest(i + "st dynamic testCIsHumanOK", () -> {
                String endpoint = HTTP_LOCALHOST + PORT + V1_IS_MUTANT;
                Person person = new Person();
                person.setDna(DNAMutants);
                HttpEntity<Person> httpEntity = new HttpEntity<>(person, getHeaders());
                ResponseEntity<String> response = server.exchange(endpoint, HttpMethod.POST, httpEntity, String.class);
                HttpStatus status = response.getStatusCode();
                System.out.println(Thread.currentThread().getName() + " => " + finalI + "st dynamic testIsMutantOK");
                assertEquals("Is Mutants is OK: " + response, HttpStatus.OK, status);
            }));
        }
        return dynamicTest;
    }

    @Execution(ExecutionMode.CONCURRENT)
    @TestFactory
    Collection<DynamicTest> CDynamicTestsFromCollectionIsHumanOK() {
        Collection<DynamicTest> dynamicTest = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            int finalI = i;
            dynamicTest.add(dynamicTest(i + "st dynamic testCIsHumanOK", () -> {
                String endpoint = HTTP_LOCALHOST + PORT + V1_IS_MUTANT;
                Person person = new Person();
                person.setDna(DNAHumans);
                HttpEntity<Person> httpEntity = new HttpEntity<>(person, getHeaders());
                ResponseEntity<String> response = server.exchange(endpoint, HttpMethod.POST, httpEntity, String.class);
                HttpStatus status = response.getStatusCode();
                System.out.println(Thread.currentThread().getName() + " => " + finalI + "st dynamic testCIsHumanOK");
                assertEquals("Invalid response from server : " + response, HttpStatus.FORBIDDEN, status);
            }));
        }
        return dynamicTest;
    }

    @Test
    public void DTestIsMutantsInvalidDNA() {
        String endpoint = HTTP_LOCALHOST + PORT + V1_IS_MUTANT;
        Person person = new Person();
        person.setDna(DNAInvalidMutants);
        HttpEntity<Person> httpEntity = new HttpEntity<>(person, getHeaders());
        ResponseEntity<String> response = server.exchange(endpoint, HttpMethod.POST, httpEntity, String.class);
        HttpStatus status = response.getStatusCode();
        assertEquals("Invalid response from server : " + response, HttpStatus.FORBIDDEN, status);
    }

    @Test
    public void ETestStatsOK() {
        String endpoint = HTTP_LOCALHOST + PORT + V1_STATS;
        HttpEntity<Person> httpEntity = new HttpEntity<>(getHeaders());
        ResponseEntity<String> response = server.exchange(endpoint, HttpMethod.GET, httpEntity, String.class);
        HttpStatus status = response.getStatusCode();
        assertEquals("Stats Code OK: " + response, HttpStatus.OK, status);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        headers.add("Accept", "application/json");
        return headers;
    }
}
