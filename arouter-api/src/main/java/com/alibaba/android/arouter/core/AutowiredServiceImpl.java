package com.alibaba.android.arouter.core;

import android.content.Context;
import android.util.LruCache;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.service.AutowiredService;
import com.alibaba.android.arouter.facade.template.ISyringe;
import com.qihoo360.replugin.RePlugin;
import com.qihoo360.replugin.model.PluginInfo;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.android.arouter.utils.Consts.SUFFIX_AUTOWIRED;

/**
 * Autowired service impl.
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/2/28 下午6:08
 */
@Route(path = "/arouter/service/autowired")
public class AutowiredServiceImpl implements AutowiredService {
    private LruCache<String, ISyringe> classCache;
    private List<String> blackList;

    @Override
    public void init(Context context) {
        classCache = new LruCache<>(66);
        blackList = new ArrayList<>();
    }

    @Override
    public void autowire(Object instance) {
        String className = instance.getClass().getName();
        try {
            if (!blackList.contains(className)) {
                ISyringe autowiredHelper = classCache.get(className);
                if (null == autowiredHelper) {  // No cache.
                    ClassLoader loader = getClassLoader(className);
                    Class<?> clazz = loader.loadClass(instance.getClass().getName() + SUFFIX_AUTOWIRED);
                    autowiredHelper = (ISyringe) clazz.getConstructor().newInstance();
                }
                autowiredHelper.inject(instance);
                classCache.put(className, autowiredHelper);
            }
        } catch (Exception ex) {
            blackList.add(className);    // This instance need not autowired.
        }
    }

    private ClassLoader getClassLoader(String className) {
        ClassLoader loader = getClass().getClassLoader();
        List<PluginInfo> pluginInfoList = RePlugin.getPluginInfoList();
        for (PluginInfo info : pluginInfoList) {
            if (className.contains(info.getPackageName())) {
                loader = RePlugin.fetchClassLoader(info.getName());
                break;
            }
        }

        return loader;
    }
}
