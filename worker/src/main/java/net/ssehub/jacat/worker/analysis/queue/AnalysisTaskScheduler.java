package net.ssehub.jacat.worker.analysis.queue;

import net.ssehub.jacat.api.addon.task.Task;
import net.ssehub.jacat.api.analysis.IAnalysisScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.*;

@Component
@EnableScheduling
public class AnalysisTaskScheduler implements IAnalysisScheduler {

    public final int queueCapacity;

    private Queue<Task> queue;

    public AnalysisTaskScheduler(@Value("${analysis.max-tasks:250}")
                                         int queueCapacity) {
        if (queueCapacity <= 0) { // -1 => unendlich
            queueCapacity = 1000000000; // 1.000.000.000
        }

        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.queueCapacity = queueCapacity;
    }

    public boolean isScheduled(Task task) {
        return this.queue.contains(task);
    }

    public boolean trySchedule(Task task) {
        if (task.getId() == null) {
            return false;
        }

        if (this.isScheduled(task)) {
            return false;
        }

        return this.queue.offer(task);
    }

    public Task getNext() {
        return this.queue.poll();
    }

    public boolean canSchedule() {
        return this.queue.size() < queueCapacity;
    }

}
