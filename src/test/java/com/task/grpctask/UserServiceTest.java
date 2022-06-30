package com.task.grpctask;

import com.task.grpctask.configuration.UserServiceIntegrationTestConfiguration;
import com.task.grpctask.entity.User;
import com.task.grpctask.repository.UserRepository;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;

@SpringBootTest(properties = {
        "grpc.server.inProcessName=test", // Enable inProcess server
        "grpc.server.port=-1", // Disable external server
        "grpc.client.inProcess.address=in-process:test" // Configure the client to connect to the inProcess server
})
@SpringJUnitConfig(classes = { UserServiceIntegrationTestConfiguration.class })
// Spring doesn't start without a config (might be empty)
@DirtiesContext // Ensures that the grpc-server is properly shutdown after each test
// Avoids "port already in use" during tests
public class UserServiceTest {

    @GrpcClient("inProcess")
    private UserServiceGrpc.UserServiceBlockingStub userServiceBlockingStub;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DirtiesContext
    public void testSayHello() {
        UserRequest request = UserRequest.newBuilder()
                .setId("id")
                .build();
        User user = User.builder()
                .id("id")
                .name("name")
                .build();
        given(userRepository.findById("id")).willReturn(java.util.Optional.ofNullable(user));
        UserResponse response = userServiceBlockingStub.getUser(request);
        assertNotNull(response);
        assertEquals("id", response.getId());
    }

}
