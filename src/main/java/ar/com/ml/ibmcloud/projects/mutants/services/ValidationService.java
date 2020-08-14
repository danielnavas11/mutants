package ar.com.ml.ibmcloud.projects.mutants.services;

import ar.com.ml.ibmcloud.projects.mutants.exceptions.MutantsException;
import ar.com.ml.ibmcloud.projects.mutants.models.Person;
import ar.com.ml.ibmcloud.projects.mutants.models.Stats;

/**
 * @author Daniel Navas
 */

public interface ValidationService {
    void validateIfIsMutant(final Person person) throws MutantsException;
    Stats getStat();
}
