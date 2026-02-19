package edu.eci.arsw.blueprints.persistence.Postgres;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistence;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Profile("postgresBD")
@AllArgsConstructor
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    private final PostgresBlueprintPersistenceRepo blueprintRepository;


    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        if (bp.getId() != null && blueprintRepository.existsById(bp.getId())) {
            throw new BlueprintPersistenceException("Blueprint already exists: " + bp.getId());
        }
        blueprintRepository.save(bp);
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return  blueprintRepository.findByAuthorAndName(author,name).orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found: %s/%s".formatted(author, name)));
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        List<Blueprint> blueprints = blueprintRepository.findByAuthor(author);
        if (blueprints.isEmpty()) throw new BlueprintNotFoundException("No blueprints for author: " + author);
        return new HashSet<>(blueprints);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        List<Blueprint> blueprints = blueprintRepository.findAll();
        return new HashSet<>(blueprints);
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
            Blueprint blue =  blueprintRepository.findByAuthorAndName(author,name).orElseThrow(() -> new BlueprintNotFoundException("Blueprint not found: %s/%s".formatted(author, name)));
            blue.addPoint(new Point(x, y));
            blueprintRepository.save(blue);
    }
}
