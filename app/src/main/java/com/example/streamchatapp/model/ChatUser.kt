package com.example.streamchatapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatUser(
    val username: String
) : Parcelable