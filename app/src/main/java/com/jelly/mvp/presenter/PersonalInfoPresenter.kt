package com.jelly.mvp.presenter

import com.base.httpmvp.retrofitapi.HttpMethods.Companion.instance
import com.jelly.mvp.contact.PersonalInfoContact
import com.base.httpmvp.retrofitapi.HttpMethods
import com.base.httpmvp.retrofitapi.IApiService
import com.base.httpmvp.retrofitapi.methods.HttpResultData
import com.jelly.baselibrary.model.PersonalInfo
import com.base.httpmvp.retrofitapi.function.HttpFunctions
import com.base.httpmvp.mvpbase.ObserverResponseListener
import com.base.httpmvp.retrofitapi.methods.HttpResult
import com.jelly.baselibrary.Utils.BitmapUtil
import com.jelly.baselibrary.model.UploadBean
import com.jelly.baselibrary.model.UploadData
import com.jelly.baselibrary.token.GlobalToken
import com.jelly.mvp.model.PersonalInfoModel
import io.reactivex.Observable
import kotlinx.coroutines.*
import java.io.File

/**
 * Created by Administrator on 2017/12/6.
 */
class PersonalInfoPresenter : PersonalInfoContact.Presenter(), CoroutineScope by MainScope() {
    override fun getInfo() {
        mView!!.showProgress()
        val observable = instance!!
            .getProxy(IApiService::class.java)
            .findBuyerInfo(GlobalToken.getToken().token)
            .flatMap(HttpFunctions())
        mModel!!.subscribe<HttpResultData<PersonalInfo>>(
            observable,
            mView!!.bindLifecycle(),
            object : ObserverResponseListener<HttpResultData<PersonalInfo>> {
                override fun onSuccess(model: HttpResultData<PersonalInfo>) {
                    mView!!.findPersonalInfoSuccess(model.data)
                    mView!!.closeProgress()
                }

                override fun onFailure(msg: String) {
                    mView!!.findPersonalInfoFailed(msg)
                    mView!!.closeProgress()
                }
            })
    }

    override fun upload() {
        mView!!.showProgress()
        val uploadParm = mView!!.upParam as UploadBean
        launch {
            //当图片太大时需要压缩同时会生成一张零时图片，上传完成需要删除零时图片
            var isDelete = false
            val file = withContext(Dispatchers.IO) {
                var file = File(uploadParm.filePath)
                //图片文件length大于1000000kb
                if (file.length() > 1000000) {
                    //对图片进行压缩宽1080 高 1920 不超过10M
                    val path = BitmapUtil.getInstance().compressImage(
                        uploadParm.filePath, 1080,
                        1920, 10
                    )
                    file = File(path)
                    isDelete = true
                }
                file
            }
            if (file.exists()) {
                mModel!!.upload(
                    file,
                    uploadParm,
                    mView!!.bindLifecycle(),
                    object : ObserverResponseListener<HttpResultData<UploadData>> {
                        override fun onSuccess(model: HttpResultData<UploadData>) {
                            mView!!.uploadSuccess(model.data)
                            mView!!.closeProgress()
                            if (isDelete)
                                file.delete()
                        }

                        override fun onFailure(msg: String) {
                            mView!!.uploadFailed(msg)
                            mView!!.closeProgress()
                            if (isDelete)
                                file.delete()
                        }
                    })
            } else {
                mView!!.closeProgress()
                mView!!.uploadFailed("文件不存在")
            }
        }
    }

    override fun upPersonalInfo() {
        mView!!.showProgress()
        val observable = instance!!.getProxy(
            IApiService::class.java
        ).updateBuyerInfo(
            GlobalToken.getToken().token,
            mGson.toJson(mView!!.personalInfoParam)
        )
            .flatMap(HttpFunctions())
        mModel!!.subscribe<HttpResult>(
            observable,
            mView!!.bindLifecycle(),
            object : ObserverResponseListener<HttpResult> {
                override fun onSuccess(model: HttpResult) {
                    mView!!.personalInfoSuccess(model.msg)
                    mView!!.closeProgress()
                }

                override fun onFailure(msg: String) {
                    mView!!.personalInfoFailed(msg)
                    mView!!.closeProgress()
                }
            })
    }

    override fun start() {
        mModel = PersonalInfoModel()
    }
}