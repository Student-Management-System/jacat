package net.ssehub.jacat.worker.analysis.queue;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan("net.ssehub.jacat.worker")
public class ScheduledConfig {}
