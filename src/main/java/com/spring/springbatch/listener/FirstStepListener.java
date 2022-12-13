package com.spring.springbatch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("Before Step: " + stepExecution.getStepName());
        System.out.println("Before Step: " + stepExecution.getJobExecution().getExecutionContext());
        System.out.println("Before Step: " + stepExecution.getExecutionContext());
        stepExecution.getExecutionContext().put("step", "val");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        System.out.println("After Step: " + stepExecution.getStepName());
        System.out.println("After Step: " + stepExecution.getJobExecution().getExecutionContext());
        System.out.println("After Step: " + stepExecution.getExecutionContext());
        return null;
    }
}
