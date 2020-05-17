package ml.socshared.service.auth.service;

import ml.socshared.service.auth.domain.request.RoleRequest;
import ml.socshared.service.auth.entity.Role;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface RoleService {

    Role save(RoleRequest roleRequest);
    void deleteById(UUID id);
    Role findById(UUID id);
    Role findByName(String name);
    Page<Role> findAll(Integer page, Integer size);

}
