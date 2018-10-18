package test.andre.batch;

import java.util.Properties;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.jsr.launch.JsrJobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BatchApplication {
	private static boolean isRunning = true;
	@Autowired
	private JsrJobOperator jp;
	private static final Logger log = LoggerFactory.getLogger(BatchApplication.class);

	@Autowired
	ApplicationArguments applicationarguments;

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
 
		String[] original_arguments = applicationarguments.getSourceArgs();
		if (original_arguments.length != 0) {
			long jobId = jp.start(original_arguments[0], new Properties());
			JobExecution je = jp.getJobExecution(jobId);
			BatchStatus status = je.getBatchStatus();
			while (true) {
				if (status == BatchStatus.STOPPED || status == BatchStatus.COMPLETED || status == BatchStatus.FAILED) {
					isRunning = false;
					log.info("job completed");	
					break;
				}
				Thread.sleep(1000);
				je = jp.getJobExecution(jobId);
				status = je.getBatchStatus();
			}
		}
		};
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(BatchApplication.class, args);
		if (!isRunning) {
			log.info("Terminating job");
			SpringApplication.exit(ctx, () -> 0);
			System.exit(0);
		}
	}
}
