package ml.socshared.service.auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import ml.socshared.service.auth.domain.request.RoleRequest;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.exception.impl.HttpNotFoundException;
import ml.socshared.service.auth.repository.RoleRepository;
import ml.socshared.service.auth.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repository;

    public RoleServiceImpl(RoleRepository repository) {
        this.repository = repository;
    }

    @Override
    public Role save(RoleRequest request) {
        log.info("saving -> {}", request);

        Role role = new Role();
        role.setName(request.getName());

        return repository.save(role);
    }

    @Override
    public void deleteById(UUID id) {
        log.info("deleting by id -> {}", id);
        repository.deleteById(id);
    }

    @Override
    public Role findById(UUID id) {
        log.info("find by id -> {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new HttpNotFoundException("Not found user role by id: " + id));
    }

    @Override
    public Role findByName(String name) {
        log.info("find by name -> {}", name);
        return repository.findByName(name)
                .orElseThrow(() -> new HttpNotFoundException("Not found user role by name: " + name));
    }

    @Override
    public Page<Role> findAll(Integer page, Integer size) {
        log.info("find all");
        Pageable pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }
}
