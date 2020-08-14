package ar.com.ml.ibmcloud.projects.mutants.rest.v1;

import ar.com.ml.ibmcloud.projects.mutants.exceptions.MutantsException;
import ar.com.ml.ibmcloud.projects.mutants.models.Person;
import ar.com.ml.ibmcloud.projects.mutants.models.Stats;
import ar.com.ml.ibmcloud.projects.mutants.services.ValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Daniel Navas
 */
@RestController
@RequestMapping("/v1")
public class IndexController {

    private final ValidationService validationService;

    public IndexController(ValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping(value = "/mutant", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<String> validateIfIsMutant(@RequestBody Person person) throws MutantsException {
        validationService.validateIfIsMutant(person);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @GetMapping(value = "/stats", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<Stats> getStats() {
        return new ResponseEntity<>(validationService.getStat(), HttpStatus.OK);
    }

}
