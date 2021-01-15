package io.github.nandandesai.secureservermonitoring;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAdminServer
@SpringBootApplication
public class SecureServerMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureServerMonitoringApplication.class, args);
	}

}
