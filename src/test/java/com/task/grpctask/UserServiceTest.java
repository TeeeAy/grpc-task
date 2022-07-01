package com.task.grpctask;

import com.task.grpctask.configuration.UserServiceIntegrationTestConfiguration;
import com.task.grpctask.entity.User;
import com.task.grpctask.repository.UserRepository;
import com.task.grpctask.service.UserNotFoundException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.task.grpctask.service.UserService.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test",
        "grpc.server.port=-1",
        "grpc.client.inProcess.address=in-process:test"
})
@SpringJUnitConfig(classes = {UserServiceIntegrationTestConfiguration.class})
@DirtiesContext
public class UserServiceTest {

    @GrpcClient("inProcess")
    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    public void shouldGetUser() {
        UserRequest request = UserRequest.newBuilder()
                .setId("id")
                .build();
        User user = User.builder()
                .id("id")
                .name("name")
                .build();
        given(userRepository.findById(request.getId())).willReturn(java.util.Optional.ofNullable(user));
        UserResponse response = userServiceBlockingStub.getUser(request);
        then(userRepository).should(only()).findById(request.getId());
        assertNotNull(response);
        assertEquals(request.getId(), response.getId());
    }

    @Test
    @DirtiesContext
    public void shouldGetAllUsers() {
        User user1 = User.builder()
                .id("id1")
                .name("name")
                .build();
        User user2 = User.builder()
                .id("id2")
                .name("name")
                .build();
        given(userRepository.findAll()).willReturn(List.of(user1, user2));
        MultipleUsersResponse response = userServiceBlockingStub.getAllUsers(EmptyBody.getDefaultInstance());
        then(userRepository).should(only()).findAll();
        assertNotNull(response);
        assertEquals(2, response.getResponsesCount());
        assertEquals(user1.getId(), response.getResponses(0).getId());
        assertEquals(user2.getId(), response.getResponses(1).getId());
    }


    @Test
    @DirtiesContext
    public void shouldDeleteUser() {
        UserRequest request = UserRequest.newBuilder()
                .setId("id")
                .build();
        Message response = userServiceBlockingStub.deleteUser(request);
        then(userRepository).should(only()).deleteById(request.getId());
        assertNotNull(response);
        assertEquals(String.format(DELETED_SUCCESSFULLY_MESSAGE, request.getId())
                , response.getText());
    }


    @Test
    @DirtiesContext
    public void shouldSaveUser() {
        UserRequest request = UserRequest.newBuilder()
                .setName("name")
                .build();
        User user = User.builder()
                .name("name")
                .build();
        User savedUser = User.builder()
                .id(UUID.randomUUID().toString())
                .name("name")
                .build();
        given(userRepository.save(user)).willReturn(savedUser);
        UserResponse response = userServiceBlockingStub.saveUser(request);
        then(userRepository).should(only()).save(user);
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertEquals(savedUser.getId(), response.getId());
    }


    @Test
    @DirtiesContext
    public void shouldUpdateUser() {
        UserRequest request = UserRequest.newBuilder()
                .setId("id")
                .build();
        User user = User.builder()
                .id("id")
                .name("name")
                .build();
        given(userRepository.findById(request.getId())).willReturn(java.util.Optional.ofNullable(user));
        Message response = userServiceBlockingStub.updateUser(request);
        then(userRepository).should(times(1)).findById(request.getId());
        then(userRepository).should(times(1)).save(user);
        then(userRepository).shouldHaveNoMoreInteractions();
        assertNotNull(response);
        assertEquals(String.format(UPDATED_SUCCESSFULLY_MESSAGE, request.getId())
                , response.getText());
    }

}
