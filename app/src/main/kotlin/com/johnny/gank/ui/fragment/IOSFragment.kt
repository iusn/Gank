package com.johnny.gank.ui.fragment
/*
 * Copyright (C) 2015 Johnny Shieh Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.johnny.gank.action.ActionType
import com.johnny.gank.action.IOSActionCreator
import com.johnny.gank.di.component.IOSFragmentComponent
import com.johnny.gank.stat.StatName
import com.johnny.gank.store.NormalGankStore
import com.johnny.gank.ui.activity.MainActivity
import com.johnny.gank.ui.widget.LoadMoreView
import com.johnny.rxflux.Store
import com.johnny.rxflux.StoreObserver
import kotlinx.android.synthetic.main.fragment_refresh_recycler.*
import javax.inject.Inject

/**
 * description
 *
 * @author Johnny Shieh
 * @version 1.0
 */
class IOSFragment : CategoryGankFragment(),
    StoreObserver {

    companion object {
        @JvmStatic
        fun newInstance() = IOSFragment()
    }

    var mStore: NormalGankStore? = null
        @Inject set

    var mActionCreator: IOSActionCreator? = null
        @Inject set

    private var mComponent: IOSFragmentComponent? = null

    override val statPageName = StatName.PAGE_IOS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInjector()
    }

    private fun initInjector() {
        mComponent = (activity as MainActivity).mainActivityComponent
            .iosFragmentComponent()
            .build()
        mComponent!!.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle?): View {
        val contentView = createView(inflater, container)

        mStore!!.setObserver(this)
        mStore!!.register(ActionType.GET_IOS_LIST)
        return contentView
    }

    override fun onDestroyView() {
        mStore!!.unRegister()
        super.onDestroyView()
    }

    override fun refreshList() {
        mActionCreator!!.getIOSList(1)
    }

    override fun loadMore() {
        vLoadMore!!.status = LoadMoreView.STATUS_LOADING
        mActionCreator!!.getIOSList(wrappedAdapter.curPage + 1)
    }

    override fun onChange(store: Store, actionType: String) {
        if (1 == mStore!!.page) {
            refresh_layout.isRefreshing = false
        }
        wrappedAdapter.updateData(mStore!!.page, mStore!!.gankList)
        loadingMore = false
        vLoadMore!!.status = LoadMoreView.STATUS_INIT
    }

    override fun onError(store: Store, actionType: String) {
        refresh_layout.isRefreshing = false
        loadingMore = false
        vLoadMore!!.status = LoadMoreView.STATUS_FAIL
    }
}