package ml.socshared.service.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.request.ScopeRequest;
import ml.socshared.service.auth.entity.Scope;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.repository.ScopeRepository;
import ml.socshared.service.auth.service.ScopeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ScopeServiceImpl implements ScopeService {

    private final ScopeRepository repository;

    public ScopeServiceImpl(ScopeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Scope save(ScopeRequest request) {
        log.info("saving -> {}", request);

        Scope scope = new Scope();
        scope.setName(request.getName());

        return scope;
    }

    @Override
    public void deleteById(UUID id) {
        log.info("deleting by id -> {}", id);
        repository.deleteById(id);
    }

    @Override
    public Scope findById(UUID id) {
        log.info("find by id -> {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found scope by id: " + id));
    }

    @Override
    public Scope findByName(String name) {
        log.info("find by name -> {}", name);
        return repository.findByName(name)
                .orElseThrow(() -> new HttpNotFoundException("Not found scope by name: " + name));
    }

    @Override
    public Page<Scope> findAll(Integer page, Integer size) {
        log.info("find all");
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }
}
