package com.base.httpmvp.presenter;

import com.base.httpmvp.mode.business.Business;
import com.base.httpmvp.mode.business.IBusiness;

/**
 * Created by Administrator on 2017/11/8.
 *  * 说明：Presenter接口
 */

public interface IBasePresenter{
    public IBusiness mIBusiness = new Business();
}
