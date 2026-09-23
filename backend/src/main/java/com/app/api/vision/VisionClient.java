package com.app.api.vision;
import org.springframework.stereotype.Component;


@Component
public interface VisionClient {

    /**
     * What the model needs to know about the job, so it can judge "is this done?" rather than
     * just describing pictures. Built from task_invoice_table.title / instructions and the task
     * type's description. Any field may be null or blank.
     */
    record TaskContext(String title, String instructions, String taskType) { }

    /**
     * Describe the resident's reference ("before") photo: labels, a one-sentence insight, and how
     * confident the model is in that description. taskLooksComplete is not used.
     */
    VisionResult analyzeBaseline(TaskContext task, byte[] referenceImage);

    /**
     * Compare the reference ("before") photo with the helper's completion ("after") photo and
     * judge whether the task looks done.
     */
    VisionResult compare(TaskContext task, byte[] referenceImage, byte[] completionImage);
}
