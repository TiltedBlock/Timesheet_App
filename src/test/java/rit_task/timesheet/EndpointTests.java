package rit_task.timesheet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import rit_task.timesheet.repositories.TimeEntryRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.main.web-application-type=reactive")
@AutoConfigureWebTestClient
public class EndpointTests {

    @Autowired
    TimeEntryRepository repository;

    @Autowired
    private WebTestClient webClient;

    @Test
    void testCreateTimeEntry() {

        TimeEntry newEntry = new TimeEntry(
                "01.01.21",
                "15:00",
                "14:00",
                " ",
                "Testcase 1"
        );

        newEntry.presetId("test01");

        webClient.post().uri("/create")
                .header(HttpHeaders.ACCEPT, "application/json")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .body(BodyInserters.fromValue(newEntry))
                .exchange()
                .expectStatus().isCreated();

    }

    @Test
    void testUpdateMethod() {

        TimeEntry updatedEntry = new TimeEntry(
                "01.01.21",
                "12:00",
                "13:00",
                "Development",
                "Testcase 2"
        );

        updatedEntry.presetId("test02");

        webClient.put().uri("/update/{id}", "test01")
                .header(HttpHeaders.ACCEPT, "application/json")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .body(BodyInserters.fromValue(updatedEntry))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.description").isEqualTo("Testcase 2");

    }

    @Test
    void testCategory() {
        webClient.get().uri("/category/{category}", "Development")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.category").isEqualTo("Development");

        // This test fails and I could not figure out why exactly, it works when testing manually.
        // I think the problem might be that this request returns a Flux instead of a Mono (like the others that pass) and probably needs different treatment
    }

    @Test
    void testDate() {
        webClient.get().uri("/date/{date}", "01.01.21")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.date").isEqualTo("01.01.21");

        // This test fails and I could not figure out why exactly, it works when testing manually.
        // I think the problem might be that this request returns a Flux instead of a Mono (like the others that pass) and probably needs different treatment
    }

    @Test
    void testDelete() {

        webClient.delete().uri("/delete/{id}", "test01")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectStatus().isOk();

        webClient.get().uri("/{id}", "test01")
                .headers(headers -> headers.setBasicAuth("user", "password"))
                .exchange()
                .expectBody()
                .isEmpty();

    }

}
