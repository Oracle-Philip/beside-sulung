package com.rummy.sulung.viewmodel

import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.InverseMethod
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TermsViewModel2 : ObservableViewModel() {

    var _isAllAgree = MutableLiveData(false)
    val isAllAgree : LiveData<Boolean>
    get() = _isAllAgree

    var termsOfserviceData : Boolean = false
    @Bindable
    fun getTermsOfservice() : Boolean{
        return termsOfserviceData
    }
    fun setTermsOfservice(value : Boolean){
        if(termsOfserviceData != value) {
            termsOfserviceData = value
            termsallAgreeData = termsOfpersonalData && termsOfserviceData
            _isAllAgree.value = termsallAgreeData
        }
        notifyPropertyChanged(BR.termsOfservice)
        notifyPropertyChanged(BR.termsallAgree)
        }

    var termsOfpersonalData : Boolean = false
    @Bindable
    fun getTermsOfpersonal() : Boolean {
        return termsOfpersonalData
    }
    fun setTermsOfpersonal(value : Boolean){
        if(termsOfpersonalData != value) {
            termsOfpersonalData = value
            termsallAgreeData = termsOfpersonalData && termsOfserviceData
            _isAllAgree.value = termsallAgreeData
        }
        notifyPropertyChanged(BR.termsOfpersonal)
        notifyPropertyChanged(BR.termsallAgree)
    }

    var termsallAgreeData: Boolean = false
    @Bindable
    fun getTermsallAgree(): Boolean {
        return termsallAgreeData
    }
    fun setTermsallAgree(value : Boolean){
        if(termsallAgreeData != value) {
            termsallAgreeData = value
            termsOfpersonalData = value
            termsOfserviceData = value
            _isAllAgree.value = value
        }
        notifyPropertyChanged(BR.termsallAgree)
        notifyPropertyChanged(BR.termsOfpersonal)
        notifyPropertyChanged(BR.termsOfservice)
    }

    fun isAllAgreeTrue() : LiveData<Boolean> = isAllAgree
}