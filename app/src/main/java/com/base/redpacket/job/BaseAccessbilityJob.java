package com.base.redpacket.job;

import android.content.Context;

import com.base.redpacket.Config;
import com.base.redpacket.service.RedPacketService;


public abstract class BaseAccessbilityJob implements AccessbilityJob {

    private RedPacketService service;

    @Override
    public void onCreateJob(RedPacketService service) {
        this.service = service;
    }

    public Context getContext() {
        return service.getApplicationContext();
    }

    public Config getConfig() {
        return service.getConfig();
    }

    public RedPacketService getService() {
        return service;
    }
}
