package test.andre.batch;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.jsr.JsrJobParametersConverter;
import org.springframework.batch.core.jsr.launch.JsrJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * See: https://stackoverflow.com/questions/47801149/spring-batch-not-seeing-h2-datasource-bean-in-spring-boot-application
 */
@Configuration
@EnableBatchProcessing(modular = true)
public class Jsr352Configuration {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Environment env;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    public JsrJobOperator jsrJobOperator(final PlatformTransactionManager transactionManager) throws Exception {
        final JsrJobOperator jobOperator = new JsrJobOperator(jobExplorer, jobRepository, jsrJobParametersConverter(), transactionManager);
        jobOperator.setTaskExecutor(taskExecutor());
        jobOperator.setApplicationContext(applicationContext);
        return jobOperator;
    }

    private JsrJobParametersConverter jsrJobParametersConverter() throws Exception {
        JsrJobParametersConverter jsrJobParametersConverter = new JsrJobParametersConverter(dataSource);
        jsrJobParametersConverter.afterPropertiesSet();
        return jsrJobParametersConverter;
    }

    private TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(env.getProperty("batch.core.pool.size", Integer.class, 5));
        taskExecutor.setQueueCapacity(env.getProperty("batch.queue.capacity", Integer.class, Integer.MAX_VALUE));
        taskExecutor.setMaxPoolSize(env.getProperty("batch.max.pool.size", Integer.class, Integer.MAX_VALUE));
        taskExecutor.afterPropertiesSet();
        return taskExecutor;
    }
}