package net.ssehub.jacat.worker.analysis.queue;

import net.ssehub.jacat.api.addon.task.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisTaskSchedulerTest {

    public static final int QUEUE_CAPACITY = 1;
    public static final String A_FIRST_ID = "A_FIRST_ID";

    private AnalysisTaskScheduler analysisTaskScheduler;

    @BeforeEach
    void setUp() {
        analysisTaskScheduler = new AnalysisTaskScheduler(QUEUE_CAPACITY);
    }

    @Test
    void trySchedule_withCapacityOne_canOnlyScheduleOneItem() {
        boolean first = analysisTaskScheduler.trySchedule(
                new Task(A_FIRST_ID, null, null, null, null, null));
        boolean second = analysisTaskScheduler.trySchedule(
                new Task("A_SECOND_ID", null, null, null, null, null));
        boolean third = analysisTaskScheduler.trySchedule(
                new Task("A_THIRD_ID", null, null, null, null, null));

        assertTrue(first);
        assertFalse(analysisTaskScheduler.canSchedule());
        assertFalse(second);
        assertFalse(third);
    }

    @Test
    void trySchedule_withIncompleteTask_shouldReturnFalse() {
        boolean first = analysisTaskScheduler.trySchedule(
                new Task(A_FIRST_ID, null, null, null, null, null));
        boolean second = analysisTaskScheduler.trySchedule(
                new Task(A_FIRST_ID, null, null, null, null, null));

        assertTrue(first);
        assertFalse(second);
    }

    @Test
    void trySchedule_withSameTask_shouldReturnFalse() {
        assertFalse(analysisTaskScheduler.trySchedule(new Task()));
    }

    @Test
    void getNext_withOneItem_shouldReturnItem() {
        Task task = new Task(
                A_FIRST_ID, null, null,
                null, null, null);
        analysisTaskScheduler.trySchedule(task);

        assertEquals(task, analysisTaskScheduler.getNext());
    }

    @Test
    void getNext_withZeroItems_shouldReturnNull() {
        assertNull(analysisTaskScheduler.getNext());
    }

    @Test
    void getNext_withOneItemTaken_shouldReturnNull() {
        Task task = new Task(
                A_FIRST_ID, null, null,
                null, null, null);
        analysisTaskScheduler.trySchedule(task);

        analysisTaskScheduler.getNext(); // Take one

        assertNull(analysisTaskScheduler.getNext());
    }
}