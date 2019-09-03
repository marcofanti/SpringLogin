package com.gpch.login.service;

import com.gpch.login.model.User;
import com.gpch.login.repository.RoleRepository;
import com.gpch.login.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.UnsupportedEncodingException;

public class UserServiceTest {
    public User user;
    public UserService userServiceUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        user = User.builder()
                .id(1)
                .name("Gustavo")
                .lastName("Ponce")
                .email("test@test.com")
                .secret("HVG2XNTVMZ4H4NUZ")
                .build();
	}
    
    @Test
    public void testFindUserByEmail() {
    	String QRUrl = null;
    	try {
    		QRUrl = userServiceUnderTest.generateQRUrl(user);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("User " + user);
        System.out.println("QRUrl " + QRUrl);
    }

/*
    @Mock
    public UserRepository mockUserRepository;
    @Mock
    public RoleRepository mockRoleRepository;
    @Mock
    public BCryptPasswordEncoder mockBCryptPasswordEncoder;

    public UserService userServiceUnderTest;
    public User user;

    @Before
    public void setUp() {
        initMocks(this);
        userServiceUnderTest = new UserService(mockUserRepository,
                                               mockRoleRepository,
                                               mockBCryptPasswordEncoder);
        user = User.builder()
                .id(1)
                .name("Gustavo")
                .lastName("Ponce")
                .email("test@test.com")
                .build();

        Mockito.when(mockUserRepository.save(any()))
                .thenReturn(user);
        Mockito.when(mockUserRepository.findByEmail(anyString()))
                .thenReturn(user);
    }

    @Test
    public void testFindUserByEmail() {
        // Setup
        final String email = "test@test.com";

        // Run the test
        final User result = userServiceUnderTest.findUserByEmail(email);

        // Verify the results
        assertEquals(email, result.getEmail());
    }

    @Test
    public void testSaveUser() {
        // Setup
        final String email = "test@test.com";

        // Run the test
        User result = userServiceUnderTest.saveUser(User.builder().build());

        // Verify the results
        assertEquals(email, result.getEmail());
    } */
}
