package com.task.grpctask.service;

import com.task.grpctask.*;
import com.task.grpctask.entity.User;
import com.task.grpctask.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
@Getter
public class UserService extends UserServiceGrpc.UserServiceImplBase {

    public static final String UPDATED_SUCCESSFULLY_MESSAGE = "The user with id %s was successfully updated";
    public static final String DELETED_SUCCESSFULLY_MESSAGE = "The user with id %s was successfully deleted";
    public static final String USER_NOT_FOUND_MESSAGE = "User with given id ws not found";

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
    public void updateUser(UserRequest request,
                           StreamObserver<Message> responseObserver) {

        User user = userRepository.findById(request.getId()).orElseThrow(
                () -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        user.setName(request.getName());

        userRepository.save(user);

        Message message = Message.newBuilder()
                .setText(String.format(UPDATED_SUCCESSFULLY_MESSAGE, request.getId()))
                .build();

        responseObserver.onNext(message);

        responseObserver.onCompleted();
    }


    @Override
    public void deleteUser(UserRequest request,
                           StreamObserver<Message> responseObserver) {

        userRepository.deleteById(request.getId());

        Message message = Message.newBuilder()
                .setText(String.format(DELETED_SUCCESSFULLY_MESSAGE, request.getId()))
                .build();


        responseObserver.onNext(message);
        responseObserver.onCompleted();
    }

    @Override
    public void saveUser(UserRequest request,
                         StreamObserver<UserResponse> responseObserver) {

        User user = User.builder()
                .name(request.getName())
                .build();

        user = userRepository.save(user);

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

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        UserResponse userResponse = UserResponse
                .newBuilder()
                .setId(user.getId())
                .setName(user.getName())
                .build();

        responseObserver.onNext(userResponse);
        responseObserver.onCompleted();
    }
}
