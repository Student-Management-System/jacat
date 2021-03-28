package net.ssehub.jacat.platform.analysis;

import net.ssehub.jacat.api.addon.task.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Profile("local")
@Repository
public class EmbeddedAnalysisTaskRepository implements AnalysisTaskRepository {
    private List<AnalysisTask> analysisTasks = new ArrayList<>();

    @Override
    public List<AnalysisTask> findAllByStatus(Task.Status status) {
        return this.analysisTasks.stream()
            .filter(task -> status == task.getStatus())
            .collect(Collectors.toList());
    }

    @Override
    public <S extends AnalysisTask> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(
                UUID.randomUUID().toString().replaceAll("-", "").substring(0, 24)
            );
        }

        this.analysisTasks.remove(entity);
        this.analysisTasks.add(entity);
        return entity;
    }

    @Override
    public <S extends AnalysisTask> List<S> saveAll(Iterable<S> entities) {
        List<S> toSave = new ArrayList<>();
        entities.forEach(
            task -> {
                task.setId(UUID.randomUUID().toString());
                toSave.add(task);
            }
        );

        this.analysisTasks.forEach(this::save);
        return toSave;
    }

    @Override
    public Optional<AnalysisTask> findById(String s) {
        return this.analysisTasks.stream()
            .filter(task -> task.getId().equalsIgnoreCase(s))
            .findFirst();
    }

    @Override
    public boolean existsById(String s) {
        return findById(s).isPresent();
    }

    @Override
    public List<AnalysisTask> findAll() {
        return this.analysisTasks;
    }

    @Override
    public Iterable<AnalysisTask> findAllById(Iterable<String> strings) {
        List<String> ids = new ArrayList<>();
        strings.forEach(ids::add);

        return this.analysisTasks.stream()
            .filter(task -> ids.contains(task.getId()))
            .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return this.analysisTasks.size();
    }

    @Override
    public void deleteById(String s) {
        AnalysisTask task = new AnalysisTask();
        task.setId(s);
        this.analysisTasks.remove(task);
    }

    @Override
    public void delete(AnalysisTask entity) {
        this.analysisTasks.remove(entity);
    }

    @Override
    public void deleteAll(Iterable<? extends AnalysisTask> entities) {
        entities.forEach(task -> delete(task));
    }

    @Override
    public void deleteAll() {
        this.analysisTasks = new ArrayList<>();
    }

    @Override
    public List<AnalysisTask> findAll(Sort sort) {
        return this.findAll();
    }

    @Override
    public Page<AnalysisTask> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public <S extends AnalysisTask> S insert(S entity) {
        return this.save(entity);
    }

    @Override
    public <S extends AnalysisTask> List<S> insert(Iterable<S> entities) {
        return this.saveAll(entities);
    }

    @Override
    public <S extends AnalysisTask> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends AnalysisTask> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends AnalysisTask> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends AnalysisTask> Page<S> findAll(Example<S> example,
                                                    Pageable pageable) {
        return null;
    }

    @Override
    public <S extends AnalysisTask> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends AnalysisTask> boolean exists(Example<S> example) {
        return this.analysisTasks.contains(example.getProbe());
    }
}
