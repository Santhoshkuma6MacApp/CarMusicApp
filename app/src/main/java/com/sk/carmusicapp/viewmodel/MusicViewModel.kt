package com.sk.carmusicapp.viewmodel

import androidx.lifecycle.ViewModel
import com.sk.carmusicapp.repo.MusicRepo

class MusicViewModel: ViewModel() {
    private val musicRepo by lazy { MusicRepo() }
    val mutableLiveDataResponse by lazy { musicRepo.mutableLiveData }
    suspend fun getMusicList(eminem:String) {
        musicRepo.questionList(eminem)
    }

}