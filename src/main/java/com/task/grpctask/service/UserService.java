package com.task.grpctask.service;

import com.task.grpctask.*;
import com.task.grpctask.entity.User;
import com.task.grpctask.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getAllUsers(EmptyBody emptyBody,
                            StreamObserver<MultipleUsersResponse> responseObserver) {

       List<User> users = userRepository.findAll();

       List<UserResponse> userResponses = users
               .stream()
               .map(user -> UserResponse
                       .newBuilder()
                       .setId(user.getId())
                       .setName(user.getName())
                       .build())
               .collect(Collectors.toList());

       MultipleUsersResponse multipleUsersResponse = MultipleUsersResponse
               .newBuilder()
               .addAllResponses(userResponses)
               .build();

       responseObserver.onNext(multipleUsersResponse);

        responseObserver.onCompleted();
    }



    @Override
    public void deleteUser(UserRequest request,
                           StreamObserver<Message> responseObserver) {

        userRepository.deleteById(request.getId());

        Message message = Message.newBuilder()
                .setText("The user with id " + request.getId() + " was successfully deleted")
                .build();


        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void saveUser(UserRequest request,
                         StreamObserver<UserResponse> responseObserver) {

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name(request.getName())
                .build();

        userRepository.save(user);

        UserResponse userResponse = UserResponse
                .newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .build();


        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(UserRequest request,
                        StreamObserver<UserResponse> responseObserver) {

        User user = userRepository.findById(request.getId()).orElseThrow(IllegalArgumentException::new);

        UserResponse userResponse = UserResponse
                .newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .build();

        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
