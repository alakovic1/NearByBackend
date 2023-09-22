package com.example.nearbymarketplace.seeder;

import com.example.nearbymarketplace.model.Category;
import com.example.nearbymarketplace.model.auth.Role;
import com.example.nearbymarketplace.model.auth.User;
import com.example.nearbymarketplace.repository.CategoryRepository;
import com.example.nearbymarketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @EventListener
    public void seed(ContextRefreshedEvent event) {
        seedDatabase();
    }

    private void seedDatabase(){

        //adding user
        createUser("admin", "admin", "admin", passwordEncoder.encode("admin"), Role.ROLE_ADMIN);
        createUser("user", "user", "user", passwordEncoder.encode("user"), Role.ROLE_USER);

        //adding categories
        createCategory("Nekretnine");
        createCategory("Vozila");
        createCategory("Laptopi");
        createCategory("Other");
    }

    private void createUser(String email,
                            String firstName,
                            String lastName,
                            String password,
                            Role role) {

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);

    }

    private void createCategory(String name){
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
    }

}
