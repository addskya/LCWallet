package com.lc.app.fresco;

import android.content.Context;
import android.util.Log;

import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class FrescoConfig {

    /**
     * init fresco lib
     *
     * @param applicationContext the application context
     */
    public static void init(Context applicationContext) {

        int MAX_MEM = 30 * ByteConstants.MB;
        final MemoryCacheParams params = new MemoryCacheParams(MAX_MEM,
                Integer.MAX_VALUE,
                MAX_MEM,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE);
        Supplier<MemoryCacheParams> supplier = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return params;
            }
        };

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(applicationContext)
                .setDownsampleEnabled(true)
                .setBitmapMemoryCacheParamsSupplier(supplier)
                .setMemoryTrimmableRegistry(NoOpMemoryTrimmableRegistry.getInstance())
                .setNetworkFetcher(new FrescoNetworkFetcher())
                .setCacheKeyFactory(new AppCacheKeyFactory())
                .build();

        NoOpMemoryTrimmableRegistry.getInstance()
                .registerMemoryTrimmable(new MemoryTrimmable() {
                    @Override
                    public void trim(MemoryTrimType trimType) {
                        final double suggestedTrimRatio = trimType.getSuggestedTrimRatio();

                        if (MemoryTrimType.OnCloseToDalvikHeapLimit.getSuggestedTrimRatio() == suggestedTrimRatio
                                || MemoryTrimType.OnSystemLowMemoryWhileAppInBackground.getSuggestedTrimRatio() == suggestedTrimRatio
                                || MemoryTrimType.OnSystemLowMemoryWhileAppInForeground.getSuggestedTrimRatio() == suggestedTrimRatio) {
                            Log.e("OrangeDebug", "clearMemoryCaches");
                            ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
                        }
                    }
                });

        Fresco.initialize(applicationContext, config);
    }

    /**
     * Clear Memory Cache
     */
    public static void clearMemoryCaches() {
        ImagePipelineFactory.getInstance().getImagePipeline().clearMemoryCaches();
        System.gc();
    }
}
