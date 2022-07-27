package com.vpn.ui.private

import androidx.collection.ArrayMap
import com.vpn.url.UrlRepository
import com.zdu.kt.KtHttpClient
import kotlinx.coroutines.flow.Flow

object SystemRepository {
    fun requestSystemData(): Flow<SystemBean> {
        val params = ArrayMap<String, Any>()
        params.put("name","test")
        return KtHttpClient.post().params(params).url(UrlRepository.System_DATA).launchFlow(SystemBean::class.java)
    }
}