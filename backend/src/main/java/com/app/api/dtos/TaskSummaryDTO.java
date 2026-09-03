package com.app.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TaskSummaryDTO {
    private int taskId;
    private String title;
    private String instructionsSnippet;
    private String status;
    private Integer taskTypeId;
    private Integer helperId;
    private Integer dependentId;
}
