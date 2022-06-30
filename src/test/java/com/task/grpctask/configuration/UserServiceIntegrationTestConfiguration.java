package com.task.grpctask.configuration;

import com.task.grpctask.UserServiceGrpc;
import com.task.grpctask.repository.UserRepository;
import com.task.grpctask.service.UserService;
import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ImportAutoConfiguration({
        GrpcServerAutoConfiguration.class, // Create required server beans
        GrpcServerFactoryAutoConfiguration.class, // Select server implementation
        GrpcClientAutoConfiguration.class}) // Support @GrpcClient annotation
public class UserServiceIntegrationTestConfiguration {

    @Bean
    UserRepository userRepository() {
        return mock(UserRepository.class); // mock(OtherDependency.class);
    }

    @Bean
    UserService userService() {
        return new UserService(userRepository());
    }

}