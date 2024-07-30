package com.cypherfund.campaign.user;

import com.cypherfund.campaign.user.services.paymentProcess.IPaymentProcess;
import com.cypherfund.campaign.user.utils.Enumerations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@EnableFeignClients
@SpringBootApplication
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}
	@Bean
	Map<Enumerations.PaymentMethod, IPaymentProcess> buildPaymentProcesses(List<IPaymentProcess> paymentProcesses) {
		return paymentProcesses.stream().collect(
				java.util.stream.Collectors.toMap(
						IPaymentProcess::getPaymentMethod,
						java.util.function.Function.identity()
				)
		);
	}

}
