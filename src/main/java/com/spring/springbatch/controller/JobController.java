package com.spring.springbatch.controller;

import com.spring.springbatch.request.JobParamRequest;
import com.spring.springbatch.service.AsyncJobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private AsyncJobService asyncJobService;

    @Autowired
    private JobOperator jobOperator;

    @GetMapping("/start/{jobname}")
    public String startJob(
            @PathVariable String jobname,
            @RequestBody List<JobParamRequest> paramRequests
            ) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        asyncJobService.asyncJob(jobname, paramRequests);

        return "Job Started...";
    }

    @GetMapping("/stop/{executionID}")
    public String stopJob(@PathVariable Long executionID) throws NoSuchJobExecutionException, JobExecutionNotRunningException {

        jobOperator.stop(executionID);
        return "Job ended...";
    }


}
