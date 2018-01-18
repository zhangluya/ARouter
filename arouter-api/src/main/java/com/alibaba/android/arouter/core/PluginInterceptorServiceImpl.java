package com.alibaba.android.arouter.core;

import android.content.Context;

import com.alibaba.android.arouter.exception.HandlerException;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.utils.MapUtils;

import java.util.List;
import java.util.Map;

import static com.alibaba.android.arouter.launcher.ARouter.logger;
import static com.alibaba.android.arouter.utils.Consts.TAG;

/**
 * Created by zhangluya on 2018/1/18.
 */
@Route(path = "/arouter/service/pluginInterceptor")
public class PluginInterceptorServiceImpl extends InterceptorServiceImpl {

    private Context mContext;

    @Override
    public void init(Context context) {
        super.init(context);
        mContext = context;
    }

    public void installPluginInterceptors() {
        if (MapUtils.isNotEmpty(Warehouse.interceptorsIndex)) {
            for (Map.Entry<Integer, Class<? extends IInterceptor>> entry : Warehouse.interceptorsIndex.entrySet()) {
                Class<? extends IInterceptor> interceptorClass = entry.getValue();
                if (alikeClass(interceptorClass)) return;
                try {
                    IInterceptor iInterceptor = interceptorClass.getConstructor().newInstance();
                    iInterceptor.init(mContext);
                    Warehouse.interceptors.add(iInterceptor);
                } catch (Exception ex) {
                    throw new HandlerException(TAG + "ARouter init interceptor error! name = [" + interceptorClass.getName() + "], reason = [" + ex.getMessage() + "]");
                }
            }

            logger.info(TAG, "ARouter plugin interceptors init over.");


        }
    }

    private boolean alikeClass(Class<? extends IInterceptor> clazz) {
        List<IInterceptor> interceptors = Warehouse.interceptors;
        for (IInterceptor iInterceptor : interceptors) {
            if (iInterceptor.getClass().getSimpleName().equals(clazz.getSimpleName())) {
                return true;
            }
        }

        return false;
    }
}
