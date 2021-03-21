package net.ssehub.jacat.worker.analysis.queue;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class SpringAsyncConfiguration implements AsyncConfigurer {

    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(16);
        threadPoolTaskExecutor.setMaxPoolSize(16);
        threadPoolTaskExecutor.setThreadGroupName("Analysis");
        threadPoolTaskExecutor.setThreadNamePrefix("AnalysisExecutor");
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
