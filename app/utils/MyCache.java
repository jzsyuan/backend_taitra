package utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import models.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyCache {
    private static MyCache instance = null;
    //A0-Default
    private final Cache<String, List<OthUnWorldYear>> othUnWorldYearCache;
    private final Cache<String, List<OthUnTwnYear>> othUnTwnYearCache;
    private final Cache<String, List<UnNonWorldYear>> unNonWorldYearCache;
    //B0-Default
    private final Cache<String, List<TxnUnYearHscode4>> txnUnYearHscode4Cache;
    private final Cache<String, List<TxnUnYearHscode6>> txnUnYearHscode6Cache;
    private final Cache<String, List<TnxMofYearHscode4>> tnxMofYearHscode4Cache;
    private final Cache<String, List<TxnMofYearHscode6Raw>> txnMofYearHscode6RawCache;
    private final Cache<String, List<B3TableRaw>> B3TableCache;
    private final Cache<String, List<UnWorldYear>> unWorldYearCache;
    //C0-Default
    private final Cache<String, List<C3TableRaw>> C3TableCache;
    private final Cache<String, List<MofWorldYear>> mofWorldYearCache;
    private final Cache<String, List<MofNonWorldYear>> mofNonWorldYearCache;

    private MyCache() {
        othUnWorldYearCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        othUnTwnYearCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        unNonWorldYearCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        txnUnYearHscode4Cache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        txnUnYearHscode6Cache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        tnxMofYearHscode4Cache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        txnMofYearHscode6RawCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        B3TableCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        unWorldYearCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        C3TableCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        mofWorldYearCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
        mofNonWorldYearCache = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)
                .build();
    }

    public static MyCache getInstance() {
        if (instance == null) {
            instance = new MyCache();
        }
        return instance;
    }

    public void clearAllCache() {
        othUnWorldYearCache.invalidateAll();
        othUnTwnYearCache.invalidateAll();
        unNonWorldYearCache.invalidateAll();
        txnUnYearHscode4Cache.invalidateAll();
        txnUnYearHscode6Cache.invalidateAll();
        tnxMofYearHscode4Cache.invalidateAll();
        txnMofYearHscode6RawCache.invalidateAll();
        B3TableCache.invalidateAll();
        unWorldYearCache.invalidateAll();
        C3TableCache.invalidateAll();
        mofWorldYearCache.invalidateAll();
        mofNonWorldYearCache.invalidateAll();
    }

    public void clearDbCache() {
        othUnWorldYearCache.cleanUp();
        othUnTwnYearCache.cleanUp();
        unNonWorldYearCache.cleanUp();
        txnUnYearHscode4Cache.cleanUp();
        txnUnYearHscode6Cache.cleanUp();
        tnxMofYearHscode4Cache.cleanUp();
        txnMofYearHscode6RawCache.cleanUp();
        B3TableCache.cleanUp();
        unWorldYearCache.cleanUp();
        C3TableCache.cleanUp();
        mofWorldYearCache.cleanUp();
        mofNonWorldYearCache.cleanUp();
    }

    public void putOthUnWorldYearCache(String key, List<OthUnWorldYear> othUnWorldYearList) {
        othUnWorldYearCache.put(key, othUnWorldYearList);
    }

    public List<OthUnWorldYear> getOthUnWorldYearCache(String key) {
        return othUnWorldYearCache.getIfPresent(key);
    }

    public void putOthUnTwnYearCache(String key, List<OthUnTwnYear> othUnTwnYear) {
        othUnTwnYearCache.put(key, othUnTwnYear);
    }

    public List<OthUnTwnYear> getOthUnTwnYearCache(String key) {
        return othUnTwnYearCache.getIfPresent(key);
    }

    public void putUnNonWorldYearCache(String key, List<UnNonWorldYear> unNonWorldYear) {
        unNonWorldYearCache.put(key, unNonWorldYear);
    }

    public List<UnNonWorldYear> getUnNonWorldYearCache(String key) {
        return unNonWorldYearCache.getIfPresent(key);
    }

    public void putTxnUnYearHscode4Cache(String key, List<TxnUnYearHscode4> txnUnYearHscode4List) {
        txnUnYearHscode4Cache.put(key, txnUnYearHscode4List);
    }

    public List<TxnUnYearHscode4> getTxnUnYearHscode4Cache(String key) {
        return txnUnYearHscode4Cache.getIfPresent(key);
    }

    public void putTxnUnYearHscode6Cache(String key, List<TxnUnYearHscode6> txnUnYearHscode6List) {
        txnUnYearHscode6Cache.put(key, txnUnYearHscode6List);
    }

    public List<TxnUnYearHscode6> getTxnUnYearHscode6Cache(String key) {
        return txnUnYearHscode6Cache.getIfPresent(key);
    }

    public void putTnxMofYearHscode4Cache(String key, List<TnxMofYearHscode4> txnMofYearHscode4List) {
        tnxMofYearHscode4Cache.put(key, txnMofYearHscode4List);
    }

    public List<TnxMofYearHscode4> getTnxMofYearHscode4Cache(String key) {
        return tnxMofYearHscode4Cache.getIfPresent(key);
    }

    public void putTxnMofYearHscode6RawCache(String key, List<TxnMofYearHscode6Raw> txnMofYearHscode6RawList) {
        txnMofYearHscode6RawCache.put(key, txnMofYearHscode6RawList);
    }

    public List<TxnMofYearHscode6Raw> getTxnMofYearHscode6RawCache(String key) {
        return txnMofYearHscode6RawCache.getIfPresent(key);
    }

    public void putB3TableCache(String key, List<B3TableRaw> B3TableRawList) {
        B3TableCache.put(key, B3TableRawList);
    }

    public List<B3TableRaw> getB3TableCache(String key) {
        return B3TableCache.getIfPresent(key);
    }

    public void putUnWorldYearCache(String key, List<UnWorldYear> unWorldYearList) {
        unWorldYearCache.put(key, unWorldYearList);
    }

    public List<UnWorldYear> getUnWorldYearCache(String key) {
        return unWorldYearCache.getIfPresent(key);
    }

    public void putC3TableCache(String key, List<C3TableRaw> C3TableRawList) {
        C3TableCache.put(key, C3TableRawList);
    }

    public List<C3TableRaw> getC3TableCache(String key) {
        return C3TableCache.getIfPresent(key);
    }

    public void putMofWorldYearCache(String key, List<MofWorldYear> mofWorldYearList) {
        mofWorldYearCache.put(key, mofWorldYearList);
    }

    public List<MofWorldYear> getMofWorldYearCache(String key) {
        return mofWorldYearCache.getIfPresent(key);
    }

    public void putMofNonWorldYearCache(String key, List<MofNonWorldYear> mofNonWorldYearList) {
        mofNonWorldYearCache.put(key, mofNonWorldYearList);
    }

    public List<MofNonWorldYear> getMofNonWorldYearCache(String key) {
        return mofNonWorldYearCache.getIfPresent(key);
    }
}
