//package org.infinity.passport.schedule;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
//import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;
//import org.infinity.passport.spi.service.UserSessionService;
//
//@Component
//public class RemoveOldPersistentSessionJob extends AbstractSimpleElasticJob {
//
//    @Autowired
//    private UserSessionService userSessionService;
//
//    @Override
//    public void process(JobExecutionMultipleShardingContext shardingContext) {
//        userSessionService.removeOldPersistentSesssions();
//    }
//}
