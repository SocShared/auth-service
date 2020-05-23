package ml.socshared.auth.service;

import ml.socshared.auth.domain.request.RoleRequest;
import ml.socshared.auth.entity.Role;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface RoleService {

    Role save(RoleRequest roleRequest);
    void deleteById(UUID id);
    Role findById(UUID id);
    Role findByName(String name);
    Page<Role> findAll(Integer page, Integer size);

}
