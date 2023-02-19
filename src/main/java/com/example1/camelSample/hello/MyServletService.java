package com.example1.camelSample.hello;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job;
import org.springframework.stereotype.Service;

@Service
public class MyServletService {

    @Autowired
    private JobLauncher jobLauncher1;
    @Autowired
    private Job job;

    public void request(Long id, String name) throws JobExecutionException {
        // (10)
        JobParameters params = new JobParametersBuilder()
                                .addLong("id", id)
                                .addString("name", name)
                                .addDate("reqDate", Calendar.getInstance().getTime())
                                .toJobParameters();

        jobLauncher1.run(job, params);
    }
}
