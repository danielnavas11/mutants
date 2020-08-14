package ar.com.ml.ibmcloud.projects.mutants;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
public class Info {

    @EventListener(ApplicationReadyEvent.class)
    public void contextRefreshedEvent() {
        System.out.println("The following endpoints are available by default : ");
        System.out.println("  Health    : http://localhost:8080/health");
        System.out.println("  Mutants   : http://localhost:8080/v1/mutant");
        System.out.println("  Stats     : http://localhost:8080/v1/stats");
    }

}
