package ml.socshared.service.auth.controller.v1;

import lombok.RequiredArgsConstructor;
import ml.socshared.service.auth.domain.request.RoleRequest;
import ml.socshared.service.auth.entity.Role;
import ml.socshared.service.auth.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final RoleService roleService;

    @GetMapping(value = "/public/roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Role> findAllRoles(@Valid @NotNull @RequestParam(name = "page", required = false) Integer page,
                                   @Valid @NotNull @RequestParam(name = "size", required = false) Integer size) {
        return roleService.findAll(page, size);
    }

    @PostMapping(value = "/public/roles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Role save(@Valid @RequestBody RoleRequest request) {
        return roleService.save(request);
    }

    @DeleteMapping(value = "/public/roles/{roleId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable UUID roleId) {
        roleService.deleteById(roleId);
    }

}
