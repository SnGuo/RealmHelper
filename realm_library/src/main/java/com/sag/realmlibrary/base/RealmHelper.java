package com.sag.realmlibrary.base;

import com.sag.foundationlibrary.base.file.FileStamp;
import com.sag.foundationlibrary.base.model.ModelStamp;
import com.sag.foundationlibrary.base.util.ContextUtil;
import com.sag.foundationlibrary.base.util.GsonUtil;
import com.sag.foundationlibrary.base.view.ResponseStamp;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by SAG on 2016/10/31 0031.
 */

public class RealmHelper implements FileStamp {

    private Realm mRealm;

    public RealmHelper() {
        mRealm = Realm.getInstance(new RealmConfiguration.Builder(ContextUtil.getContext()).schemaVersion(2).build());
    }


    @Override
    public void read(ModelStamp target) {

        Observable<RealmBean> observable = mRealm.where(RealmBean.class)
                .equalTo(RealmBean.KEY, target.getMethod())
                .findFirstAsync()//异步查询方法，取查询到的第一个值
                .asObservable();

        Subscription subscription = observable.filter(bean -> bean.isLoaded())
                .subscribe(new Action1<RealmBean>() {

                    private int i = 0;

                    @Override
                    public void call(RealmBean bean) {
                        if (bean.isValid()) {
                            target.callBack(ResponseStamp.Target_CACHE_VALID, GsonUtil.getGson().fromJson(bean.message, target.getClass()));
                        } else {
                            target.callBack(ResponseStamp.Target_Cache_Null, target);
                        }
                        if ((i++) != 0) {
                            target.unSubscribeMonitor();
                        } else {
                            target.transmit();
                        }
                    }

                });

        target.receiveMonitorSubscription(subscription);

    }

    public void write(final String method, final String message) {
        mRealm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(new RealmBean(method, message)));
    }

}
