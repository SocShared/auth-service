package ml.socshared.service.auth.api.v1.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ml.socshared.service.auth.domain.model.UserModel;
import ml.socshared.service.auth.domain.request.NewUserRequest;
import ml.socshared.service.auth.domain.response.UserResponse;
import ml.socshared.service.auth.entity.Role;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;

import java.util.HashMap;
import java.util.UUID;

@Api(value = "Registration Controller")
public interface UserApi {

    @ApiOperation(value = "Регистрация нового пользователя", notes = "Регистрация нового пользователя")
    UserResponse add(NewUserRequest request);

    @ApiOperation(value = "Активация пользователя", notes = "Активация пользователя")
    UserResponse activation(UUID userId);

    @ApiOperation(value = "Деактивация пользователя", notes = "Деактивация пользователя")
    UserResponse deactivation(UUID userId);

    @ApiOperation(value = "Установка роли", notes = "Изменение роли пользователя")
    UserResponse addRole(UUID userId, UUID roleId);

    @ApiOperation(value = "Удаление роли", notes = "Изменение роли пользователя")
    UserResponse removeRole(UUID userId, UUID roleId);

    @ApiOperation(value = "Получение списка пользователей", notes = "Получение списка пользователей")
    Page<UserModel> findAll(Integer page, Integer size);

    @ApiOperation(value = "Удаление пользователя", notes = "Удаление пользователя")
    UserResponse delete(UUID userId);

}
