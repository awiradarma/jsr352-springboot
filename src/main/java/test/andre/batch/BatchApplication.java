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
			Properties properties = new Properties();
            for(int i=1; i<original_arguments.length; i++)  {
                int equalIndex = original_arguments[i].indexOf('=');
                String name = original_arguments[i].substring(0, equalIndex);
				String value = original_arguments[i].substring(equalIndex+1);
				System.out.println(name + " : " + value);
                properties.put(name, value);
            }
			long jobId = jp.start(original_arguments[0], properties);
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
		System.out.println("In main..");
		ConfigurableApplicationContext ctx = SpringApplication.run(BatchApplication.class, args);
		if (!isRunning) {
			log.info("Terminating job");
			SpringApplication.exit(ctx, () -> 0);
			System.exit(0);
		}
	}
}
