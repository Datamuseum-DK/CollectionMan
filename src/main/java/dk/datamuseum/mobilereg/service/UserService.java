package dk.datamuseum.mobilereg.service;

import dk.datamuseum.mobilereg.model.UserDto;
import dk.datamuseum.mobilereg.entities.User;

import java.util.List;

/**
 * Unused.
 */
public interface UserService {

    void saveUser(UserDto userDto);

    List<UserDto> findAllUsers();

    User findUserByEmail(String email);

    UserDto findUserById(Integer userId);

    boolean doesUserExist(Integer userId);

    void editUser(UserDto updatedUserDto, Integer userId);

    void deleteUserById(Integer userId);

}
