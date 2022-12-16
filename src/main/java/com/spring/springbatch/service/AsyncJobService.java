package com.spring.springbatch.service;

import com.spring.springbatch.request.JobParamRequest;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("ALL")
public class AsyncJobService {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("firstJob")
    private Job firsJob;

    @Autowired
    @Qualifier("secondJob")
    private Job secondJob;

    @Async
    public void asyncJob(String jobname, List<JobParamRequest> jobParamRequests) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Map<String, JobParameter> param = new HashMap<>();
        param.put("currTime", new JobParameter(System.currentTimeMillis()));

        jobParamRequests.forEach(jobParamRequest -> {
            param.put(jobParamRequest.getParamKey(), new JobParameter(jobParamRequest.getParamValue()));
        });

        JobParameters jobParameters = new JobParameters(param);

        try {
            JobExecution jobExecution = null;
            if (jobname.equals("firstJob")){
                jobExecution = jobLauncher.run(firsJob, jobParameters);
            } else if (jobname.equals("secondJob")) {
                jobExecution = jobLauncher.run(secondJob, jobParameters);
            }
            System.out.println("Job execution ID = " + jobExecution.getJobId());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}
