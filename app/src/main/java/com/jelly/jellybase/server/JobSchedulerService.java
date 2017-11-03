package com.jelly.jellybase.server;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;

import com.base.appservicelive.toolsUtil.CommonStaticUtil;


/**
 * Created by BYPC006 on 2016/10/27.
 */
@TargetApi(21)
public class JobSchedulerService extends JobService {
	@Override
	public boolean onStartJob(JobParameters params) {
		CommonStaticUtil.startService(getApplicationContext());
		return false;
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		return false;
	}
}
