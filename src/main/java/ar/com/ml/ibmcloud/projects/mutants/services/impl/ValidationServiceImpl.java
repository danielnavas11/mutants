package ar.com.ml.ibmcloud.projects.mutants.services.impl;

import ar.com.ml.ibmcloud.projects.mutants.exceptions.MutantsException;
import ar.com.ml.ibmcloud.projects.mutants.models.Person;
import ar.com.ml.ibmcloud.projects.mutants.models.Stats;
import ar.com.ml.ibmcloud.projects.mutants.repositories.StatsRepository;
import ar.com.ml.ibmcloud.projects.mutants.services.ValidationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Daniel Navas
 */
@Service
public class ValidationServiceImpl implements ValidationService {

    @Value("${CHARS_DNA}")
    private String CHARS_DNA;

    private final StatsRepository statsRepository;

    static final Pattern PATTERN_MUTANT = Pattern.compile("([a-zA-Z])\\1{3,}", Pattern.MULTILINE);

    public ValidationServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @Override
    public void validateIfIsMutant(final Person person) throws MutantsException {
        final Map<String, Integer> dnaPatterns = new HashMap<>();
        synchronized (dnaPatterns){
            String[][] matrix = new String[person.getDna().length][person.getDna()[0].length()];
            loadMatrix(matrix, person, dnaPatterns);
            printMatrix(matrix);
            System.out.println("dnaPatterns: " + dnaPatterns.toString());
            isMutant(dnaPatterns);
        }
    }

    @Override
    public Stats getStat() {
        List<Stats> all = statsRepository.findAll();
        final Stats stats = all.isEmpty() ? new Stats(null, 0L, 0L, 0.0) : all.get(0);
        if (stats.getCount_human_dna() == 0L)
            stats.setRatio(0.0);
        else
            stats.setRatio(Double.valueOf(stats.getCount_mutant_dna() / stats.getCount_human_dna() + "." + (stats.getCount_mutant_dna() % stats.getCount_human_dna())));
        System.out.println(stats);
        return stats;
    }

    /*
     * Funcion que carga la matriz y se aprovecha la primera carga para obtener las cadenas de los Ejes X-Y
     * Como las diagonal principal y la inversa
     * */
    private void loadMatrix(final String[][] matrix, final Person person,final Map<String, Integer> dnaPatterns) throws MutantsException {
        StringBuilder diagonalPrincipal = new StringBuilder();
        StringBuilder diagonalSecondary = new StringBuilder();
        int maxX = person.getDna().length;
        //Eje X
        for (int i = 0; i < person.getDna().length; i++) {
            StringBuilder word = new StringBuilder();
            for (int j = 0; j < person.getDna()[i].length(); j++) {
                String oneChar = String.valueOf(person.getDna()[i].charAt(j)).toUpperCase();
                if (!CHARS_DNA.contains(oneChar)) {
                    throw new MutantsException("Character no valid for DNA");
                }
                matrix[i][j] = oneChar;
                word.append(oneChar);
                if (i == j) {
                    diagonalPrincipal.append(matrix[i][j]);
                }
                if ((i + j) == (maxX - 1)) {
                    diagonalSecondary.append(matrix[i][j]);
                }
            }
            putAndValidateDnaPatterns(dnaPatterns, word);
        }
        putAndValidateDnaPatterns(dnaPatterns, diagonalPrincipal);
        putAndValidateDnaPatterns(dnaPatterns, diagonalSecondary);
        //Eje Y
        for (int j = 0; j < person.getDna()[0].length(); j++) {
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < person.getDna().length; i++) {
                word.append(matrix[i][j]);
            }
            putAndValidateDnaPatterns(dnaPatterns, word);
        }
    }

    private void putAndValidateDnaPatterns(final Map<String, Integer> dnaPatterns, StringBuilder dnaParam) {
        dnaPatterns.put(dnaParam.toString(), dnaPatterns.keySet().stream().anyMatch(key -> dnaParam.toString().equals(key)) ? dnaPatterns.get(dnaParam.toString()) + 1 : 1);
    }

    private void isMutant(final Map<String, Integer> dnaPatterns) throws MutantsException {
        int count = 0;
        count = dnaPatterns.keySet().stream().filter((key) -> (PATTERN_MUTANT.matcher(key).lookingAt()))
                .map(dnaPatterns::get).reduce(count, Integer::sum);
        System.out.println("Mutant DNA Count: " + count);
        saveStats(count <= 1);
    }

    private void saveStats(final boolean isHuman) throws MutantsException {
        synchronized (this) {
            if (statsRepository.findAll().isEmpty()) {
                Stats stats = new Stats();
                stats.setCount_human_dna(0L);
                stats.setCount_mutant_dna(0L);
                stats.setRatio(Double.valueOf("0"));
                statsRepository.saveAndFlush(stats);
            }
            for (Stats stats : statsRepository.findAll()) {
                if (isHuman) {
                    stats.setCount_human_dna(stats.getCount_human_dna() + 1);
                } else {
                    stats.setCount_mutant_dna(stats.getCount_mutant_dna() + 1);
                }
                statsRepository.saveAndFlush(stats);
            }
            if (isHuman)
                throw new MutantsException("Magneto Is Human :( ");
        }
    }

    private void printMatrix(String[][] matrix) {
        for (String[] row : matrix) {
            System.out.print("[ ");
            for (String value : row) {
                System.out.print(value + "  ");
            }
            System.out.println("]");
        }
    }
}
