package mate.academy.service.impl;

import java.util.Optional;
import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> userFromDbOptional = userService.findByEmail(email);
        if (userFromDbOptional.isEmpty()) {
            throw new AuthenticationException("Can`t login. Incorrect email or password");
        }
        User user = userFromDbOptional.get();
        String hashedPassword = HashUtil.hashPassword(password, user.getSalt());
        if (user.getPassword().equals(hashedPassword)) {
            return user;
        }
        throw new AuthenticationException("Can`t login. Incorrect email or password");
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        if (userService.findByEmail(email).isPresent()) {
            throw new RegistrationException("Could`t register, email already registered.");
        }
        User user = new User();
        user.setEmail(email);
        user.setSalt(HashUtil.getSalt());
        user.setPassword(HashUtil.hashPassword(password, user.getSalt()));
        return userService.add(user);
    }
}
