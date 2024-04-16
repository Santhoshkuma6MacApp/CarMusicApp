package com.sk.carmusicapp.repo

import androidx.lifecycle.MutableLiveData
import com.sk.carmusicapp.api.ApiInterface
import com.sk.carmusicapp.api.RetrofitApi
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import com.sk.carmusicapp.api.Response
import com.sk.carmusicapp.model.MyMusic

class MusicRepo {
//    private val context by lazy { MusicClass.getContext }

    private val serverApi by lazy { RetrofitApi.retrofitInstance().create(ApiInterface::class.java) }
    val mutableLiveData=MutableLiveData<Response<MyMusic>>()
    private fun getErrorBodyMessage(responseBody: ResponseBody): String {
        val errorJson = JSONObject(responseBody.string())
        return errorJson.getString("message")
    }

    suspend fun questionList(eminem:String) {
//        if (NetworkUtils.isInternetAvailable(context = context)) {
            mutableLiveData.value = Response.Loading(showLoader = true)
            try {
                val result = serverApi.getData(eminem)
                if (result.body() != null) {
                    mutableLiveData.value = Response.Success(data = result.body())
                    mutableLiveData.value = Response.Loading(showLoader = false)
                } else {
                    mutableLiveData.value =
                        Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    mutableLiveData.value = Response.Error(errorMessage = result.message())
                    try {
                        mutableLiveData.value =
                            Response.Error(errorMessage = getErrorBodyMessage(responseBody = result.errorBody()!!))
                    } catch (e: JSONException) {
                        mutableLiveData.value = Response.Error(errorMessage = result.message())
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                mutableLiveData.value = Response.Error(errorMessage = e.message.toString())
                mutableLiveData.value = Response.Loading(showLoader = false)
            }
        }

//        else {
//            mutableLiveData.value = Response.Error(errorMessage = "No Internet Connection")
//        }
//    }


}