package com.test.finalproject;

import com.test.finalproject.entity.Task;
import com.test.finalproject.entity.TaskDetail;
import com.test.finalproject.entity.User;
import com.test.finalproject.enums.AccountStatus;
import com.test.finalproject.enums.ProgressStatus;
import com.test.finalproject.repository.TaskDetailRepository;
import com.test.finalproject.repository.TaskRepository;
import com.test.finalproject.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class FinalProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinalProjectApplication.class, args);
	}

	@Bean
	CommandLineRunner run (UserRepository userRepository , PasswordEncoder
								   passwordEncoder, TaskRepository taskRepository,
						   TaskDetailRepository taskDetailRepository
	)
	{return args ->
	{
		userRepository.save(
				User.builder()
						.id(1)
						.username("tuanvo123")
						.email("test@test.com")
						.password(passwordEncoder.encode("12345678"))
						.firstName("Vo")
						.lastName("Tuan")
						.status(AccountStatus.ACTIVE)
						.build());

		taskRepository.save(
				Task.builder()
						.id(1)
						.user(User.builder().id(1).build())
						.name("Feature Manager User")
						.status(ProgressStatus.TODO)
						.build()
		);

		taskDetailRepository.save(
				TaskDetail.builder()
						.id(1)
						.name("Feature Login")
						.task(Task.builder().id(1).build())
						.build()
		);
	};
	}

}
