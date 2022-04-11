package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class DtoUrlInfo (

    @SerializedName("page"        ) var page       : Int?            = null,
    @SerializedName("per_page"    ) var perPage    : Int?            = null,
    @SerializedName("total"       ) var total      : Int?            = null,
    @SerializedName("total_pages" ) var totalPages : Int?            = null,
    @SerializedName("data"        ) var data       : ArrayList<DtoUrlData> = arrayListOf(),
    @SerializedName("support"     ) var support    : DtoUlrSupport?        = DtoUlrSupport()

)