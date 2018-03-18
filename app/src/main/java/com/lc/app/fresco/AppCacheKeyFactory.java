package com.lc.app.fresco;

import android.net.Uri;

import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheKey;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class AppCacheKeyFactory implements CacheKeyFactory {
    private Map<String, String> mUriKeyMap = new HashMap<>();

    public void putUriKeyMap(Uri sourceUri, String key) {
        String url = getCacheKeySourceUri(sourceUri).toString();
        mUriKeyMap.put(url, key);
    }

    public void removeUriKeyMap(Uri sourceUri) {
        String url = getCacheKeySourceUri(sourceUri).toString();
        mUriKeyMap.remove(url);
    }

    public CacheKey getBitmapCacheKey(ImageRequest request, Object callerContext) {
        return new BitmapMemoryCacheKey(
                getUriKeyString(request.getSourceUri()),
                request.getResizeOptions(),
                request.getRotationOptions(),
                request.getImageDecodeOptions(),
                null,
                null,
                callerContext);
    }

    public CacheKey getPostprocessedBitmapCacheKey(ImageRequest request, Object callerContext) {
        final Postprocessor postprocessor = request.getPostprocessor();
        final CacheKey postprocessorCacheKey;
        final String postprocessorName;
        if (postprocessor != null) {
            postprocessorCacheKey = postprocessor.getPostprocessorCacheKey();
            postprocessorName = postprocessor.getClass().getName();
        } else {
            postprocessorCacheKey = null;
            postprocessorName = null;
        }
        return new BitmapMemoryCacheKey(
                getUriKeyString(request.getSourceUri()),
                request.getResizeOptions(),
                request.getRotationOptions(),
                request.getImageDecodeOptions(),
                postprocessorCacheKey,
                postprocessorName,
                callerContext);
    }

    public CacheKey getEncodedCacheKey(ImageRequest request, Object callerContext) {
        return getEncodedCacheKey(request, request.getSourceUri(), callerContext);
    }

    public CacheKey getEncodedCacheKey(
            ImageRequest request,
            Uri sourceUri,
            Object callerContext) {
        return new SimpleCacheKey(getUriKeyString(sourceUri));
    }

    protected String getUriKeyString(Uri sourceUri) {
        String url = getCacheKeySourceUri(sourceUri).toString();
        if (mUriKeyMap.containsKey(url)) {
            return mUriKeyMap.get(url);
        }
        return url;
    }

    /**
     * @return a {@link Uri} that unambiguously indicates the source of the image.
     */
    protected Uri getCacheKeySourceUri(Uri sourceUri) {
        return sourceUri;
    }
}