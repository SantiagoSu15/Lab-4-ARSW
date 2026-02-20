package edu.eci.arsw.blueprints.filters;

import edu.eci.arsw.blueprints.model.Blueprint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Default filter: returns the blueprint unchanged.
 * This matches the baseline behavior of the reference lab before students implement custom filters.
 */
@Component
@Profile("default")
public class IdentityFilter implements BlueprintsFilter {
    private static final Logger log = LoggerFactory.getLogger(IdentityFilter.class);
    public IdentityFilter() {
        log.info("IdentityFilter bean creado");
    }
    @Override
    public Blueprint apply(Blueprint bp) {

        return bp; }
}
