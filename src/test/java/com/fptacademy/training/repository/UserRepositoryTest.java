package com.fptacademy.training.repository;

import static com.fptacademy.training.service.util.RandomUtil.randomBoolean;
import static com.fptacademy.training.service.util.RandomUtil.randomDate;
import static com.fptacademy.training.service.util.RandomUtil.randomInt;
import static com.fptacademy.training.service.util.RandomUtil.randomString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fptacademy.training.domain.enumeration.RoleName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fptacademy.training.domain.Level;
import com.fptacademy.training.domain.Role;
import com.fptacademy.training.domain.User;
import com.fptacademy.training.domain.enumeration.UserStatus;
import com.fptacademy.training.security.Permissions;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LevelRepository levelRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Direction direction = Direction.DESC;
    private String sortBy = "role";
    private final Pageable pageable = PageRequest.of(0, 10, direction, sortBy);

    private class UserIdComparator implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getId().compareTo(u2.getId());
        }
    }

    class UserCustomComparator implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            switch (sortBy) {
                case "id":
                    return u1.getId().compareTo(u2.getId());
                case "code":
                    return u1.getCode().compareTo(u2.getCode());
                case "fullName":
                    return u1.getFullName().compareTo(u2.getFullName());
                case "email":
                    return u1.getEmail().compareTo(u2.getEmail());
                case "birthday":
                    return u1.getBirthday().compareTo(u2.getBirthday());
                case "level":
                    return u1.getLevel().getName().compareTo(u2.getLevel().getName());
                case "role":
                    return u1.getRole().getName().compareTo(u2.getRole().getName());
                case "activated":
                    return u1.getActivated().compareTo(u2.getActivated());
                case "status":
                    return u1.getStatus().compareTo(u2.getStatus());
                default:
                    return u1.getId().compareTo(u2.getId());
            }
        }
    }

    @BeforeAll
    void createFakeData() {
        List<String> levelNames = List.of("BEGIN", "INTERMEDIATE", "ADVANCED");
        List<Level> levels = new ArrayList<>();
        levelNames.forEach(levelName -> {
            Level level = new Level();
            level.setName(levelName);
            levelRepository.save(level);
            levels.add(level);
        });

        List<String> roleNames = List.of("Admin", "User");
        List<Role> roles = new ArrayList<>();
        roleNames.forEach(roleName -> {
            Role role = new Role();
            role.setName(roleName);
            role.setPermissions(List.of(Permissions.USER_FULL_ACCESS));
            roleRepository.save(role);
            roles.add(role);
        });

        List<UserStatus> userStatuses = List.of(UserStatus.ACTIVE, UserStatus.INACTIVE,
                UserStatus.IN_CLASS, UserStatus.OFF_CLASS, UserStatus.ON_BOARDING);

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setCode(randomString(6, false));
            user.setFullName(randomString(randomInt(10, 20), true));
            user.setEmail(randomString(randomInt(5, 15), true) + "@gmail.com");
            user.setBirthday(randomDate());
            user.setLevel(levels.get(randomInt(0, 2)));
            user.setRole(roles.get(randomInt(0, 1)));
            user.setActivated(randomBoolean());
            user.setStatus(userStatuses.get(randomInt(0, 4)));
            user.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(user);
        }
    }

    @AfterAll
    void deleteFakeData() {
        userRepository.deleteAll();
        levelRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private Boolean isUserMatchFilters(User user, String email, String fullName, String code, String levelName,
            String roleName, Boolean activated, LocalDate birthdayFrom, LocalDate birthdayTo, String status) {
        if (email != null && !user.getEmail().toLowerCase().contains(email.toLowerCase()))
            return false;
        if (fullName != null && !user.getFullName().toLowerCase().contains(fullName.toLowerCase()))
            return false;
        if (code != null && !user.getCode().toLowerCase().contains(code.toLowerCase()))
            return false;
        if (levelName != null && !user.getLevel().getName().toLowerCase().contains(levelName.toLowerCase()))
            return false;
        if (roleName != null && !user.getRole().getName().toLowerCase().contains(roleName.toLowerCase()))
            return false;
        if (activated != null && !user.getActivated().equals(activated))
            return false;
        if (birthdayFrom == null)
            birthdayFrom = LocalDate.of(0, 1, 1);
        if (birthdayTo == null)
            birthdayTo = LocalDate.of(9999, 12, 31);
        if (user.getBirthday().isBefore(birthdayFrom) || user.getBirthday().isAfter(birthdayTo))
            return false;
        if (status != null && !user.getStatus().name().toLowerCase().contains(status.toLowerCase()))
            return false;
        return true;
    }

    List<User> getFilteredUser(List<User> allUsers, String email, String fullName, String code, String levelName,
            String roleName,
            Boolean activated, LocalDate birthdayFrom, LocalDate birthdayTo, String status) {
        List<User> actualResult = allUsers.stream()
                .filter(user -> isUserMatchFilters(user, email, fullName, code, levelName, roleName, activated,
                        birthdayFrom, birthdayTo, status))
                .toList();
        if (direction.isAscending())
            actualResult = actualResult.stream().sorted(new UserIdComparator()).toList();
        else
            actualResult = actualResult.stream().sorted(new UserIdComparator().reversed()).toList();
        actualResult = actualResult.stream().skip(pageable.getOffset()).limit(pageable.getPageSize()).toList();
        return actualResult;
    }

    @Test
    void performanceTestingBetweenQuerySQLAndJavaForLoop() {
        final String email = randomBoolean() ? randomString(1, false) : null,
                fullName = randomBoolean() ? randomString(1, false) : null,
                code = randomBoolean() ? randomString(1, false) : null,
                levelName = randomBoolean() ? randomString(1, false) : null,
                roleName = randomBoolean() ? randomString(1, false) : null,
                status = randomBoolean() ? randomString(1, false) : null;
        final Boolean activated = randomBoolean() ? randomBoolean() : null;

        LocalDate dayBefore = randomDate(), dayAfter = randomDate();
        if (dayBefore.compareTo(dayAfter) > 0) {
            LocalDate temp = dayBefore;
            dayBefore = dayAfter;
            dayAfter = temp;
        }
        final LocalDate birthdayFrom = randomBoolean() ? dayBefore : null,
                birthdayTo = randomBoolean() ? dayAfter : null;

        Long start, finish, timeJava, timeSQL;

        List<User> allUsers = userRepository.findAll();

        start = System.nanoTime();
        List<User> actualResult = getFilteredUser(allUsers, email, fullName, code, levelName, roleName, activated,
                birthdayFrom, birthdayTo, status);
        finish = System.nanoTime();
        timeJava = finish - start;

        start = System.nanoTime();
        Page<User> usersFullFilters = userRepository.findByFilters(email, fullName, code, levelName, roleName,
                activated, birthdayFrom, birthdayTo, status, pageable);
        finish = System.nanoTime();
        timeSQL = finish - start;

        assertTrue(timeJava < timeSQL);
    }

    @Test
    void shouldReturnAllUsers_whenFindByNoFilters() {
        Page<User> allUsers = userRepository.findAll(pageable);
        Page<User> usersNoFilters = userRepository.findByFilters(null, null, null, null, null, null,
                LocalDate.of(0, 1, 1), LocalDate.of(9999, 12, 31), null, pageable);

        assertThat(usersNoFilters.getSize()).isEqualTo(pageable.getPageSize());
        assertThat(usersNoFilters.getContent())
                .usingElementComparator(new UserIdComparator())
                .containsExactlyInAnyOrderElementsOf(allUsers.getContent());
    }

    @Test
    void shouldReturnCorrectUsers_whenFindByAllFilters() {
        final String email = randomString(1, false),
                fullName = randomString(1, false),
                code = randomString(1, false),
                levelName = randomString(1, false),
                roleName = randomString(1, false),
                status = randomString(1, false);
        final Boolean activated = true;
        final LocalDate birthdayFrom = LocalDate.of(1990, 1, 1);
        final LocalDate birthdayTo = LocalDate.of(2023, 12, 31);

        List<User> allUsers = userRepository.findAll();
        List<User> actualResult = getFilteredUser(allUsers, email, fullName, code, levelName, roleName, activated,
                birthdayFrom, birthdayTo, status);

        Page<User> usersFullFilters = userRepository.findByFilters(email, fullName, code, levelName, roleName,
                activated, birthdayFrom, birthdayTo, status, pageable);
        assertThat(usersFullFilters.getContent())
                .usingElementComparator(new UserIdComparator())
                .containsExactlyInAnyOrderElementsOf(actualResult);
    }

    @Test
    void shouldReturnCorrectUsers_whenFindByPartialFilters() {
        final String email = randomString(1, false), fullName = null, code = null, levelName = null,
                roleName = randomString(1, false), status = null;
        final Boolean activated = true;
        final LocalDate birthdayFrom = null, birthdayTo = null;

        List<User> allUsers = userRepository.findAll();
        List<User> actualResult = getFilteredUser(allUsers, email, fullName, code, levelName, roleName, activated,
                birthdayFrom, birthdayTo, status);

        Page<User> usersFullFilters = userRepository.findByFilters(email, fullName, code, levelName, roleName,
                activated, birthdayFrom, birthdayTo, status, pageable);
        assertThat(usersFullFilters.getContent())
                .usingElementComparator(new UserIdComparator())
                .containsExactlyInAnyOrderElementsOf(actualResult);
    }

    @Test
    void findMemberOfClassByRoleShouldWork(){
        List<User> user = userRepository.findMemberOfClassByRole(1L, RoleName.CLASS_ADMIN.toString());
        assertNotNull(user);
    }
}
