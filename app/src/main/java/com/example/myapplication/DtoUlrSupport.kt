package com.example.myapplication
import com.google.gson.annotations.SerializedName
data class DtoUlrSupport (

    @SerializedName("url"  ) var url  : String? = null,
    @SerializedName("text" ) var text : String? = null

)