package ml.socshared.service.auth.service;

import ml.socshared.service.auth.domain.request.ScopeRequest;
import ml.socshared.service.auth.entity.Scope;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ScopeService {

    Scope save(ScopeRequest scopeRequest);
    void deleteById(UUID id);
    Scope findById(UUID id);
    Scope findByName(String name);
    Page<Scope> findAll(Integer page, Integer size);

}
