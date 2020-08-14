package ar.com.ml.ibmcloud.projects.mutants.rest.v1;

import ar.com.ml.ibmcloud.projects.mutants.exceptions.MutantsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice(basePackageClasses = {IndexController.class})
public class RestErrorHandlerController {

    @ExceptionHandler(value = MutantsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<String> exception(MutantsException exception) {
        exception.printStackTrace();
        return new ResponseEntity<>("", HttpStatus.FORBIDDEN);
    }
}
