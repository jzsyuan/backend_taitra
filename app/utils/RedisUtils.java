package utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.io.IOException;
import java.util.Vector;

public class RedisUtils {
    private static Vector<JedisPool> poolList = null;
    private final static String KEY_PREFIX = "";

    static {
        if (poolList == null) {
            String redisHostConfig = "localhost:6379";
            //String redisHostConfig = (String) Utils.getApplicationConfig("redis.host");
            Logger.info("Redis pool list initializing:" + redisHostConfig);
            String[] redisHosts = redisHostConfig.split(",");
            poolList = new Vector<>();
            for (String host : redisHosts) {
                String[] hostPort = host.split(":");
                JedisPool pool;
                JedisPoolConfig config = new JedisPoolConfig();
                config.setMinIdle(5);
                if (hostPort.length > 1) {
                    pool = new JedisPool(config, hostPort[0], Integer.parseInt(hostPort[1]));
                    Logger.info("Redis poll initialized:" + hostPort[0] + " " + hostPort[1]);
                } else {
                    pool = new JedisPool(config, hostPort[0]);
                    Logger.info("Redis poll initialized:" + hostPort[0]);
                }
                poolList.add(pool);
            }
            Logger.info("Redis poll initialized with pool size:" + poolList.size());
        }
    }

    public static JedisPool getPool() {
        return poolList.firstElement();
    }

    public static Vector<JedisPool> getPoolList() {
        return poolList;
    }

    protected static <T> Object readFromJsonString(String jsonString, Class<T> objectClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonString, objectClass);
    }

    public static String get(String key, String defaultValue) {
        String content = null;
        try (Jedis jedis = RedisUtils.getPool().getResource()) {
            final String redisKey = KEY_PREFIX + key;
            content = jedis.get(redisKey);
            if (content != null) {
                return content;
            }
        }
        return defaultValue;
    }

    private static void saveToRedis(String key, String value) {
        saveToRedis(key, value, 0);
    }


    private static void saveToRedis(String key, String value, int expiredSeconds) {
        Vector<JedisPool> redisPools = RedisUtils.getPoolList();
        for (JedisPool pool : redisPools) {
            try (Jedis jedis = pool.getResource()) {
                String redisKey = KEY_PREFIX + key;
                String response = null;
                if (expiredSeconds > 0) {
                    response = jedis.set(redisKey, value, SetParams.setParams().ex(expiredSeconds));
                } else {
                    response = jedis.set(redisKey, value);
                }
                Logger.trace("save to redis: " + redisKey + "\n" + response);
            }
        }
    }

    public static void savaToRedis(String key, String value) {
        Vector<JedisPool> redisPools = RedisUtils.getPoolList();
        for (JedisPool pool : redisPools) {
            try (Jedis jedis = pool.getResource()) {
                long response = jedis.lpush(key, value);
            }
        }
    }

    public static boolean save(String key, String value, int expiredSeconds) {
        saveToRedis(key, value, expiredSeconds);
        return true;
    }

    public static boolean save(String key, String value) {
        saveToRedis(key, value);
        return true;
    }

    public static <T> T get(String key, Class<T> objectClass) {
        String content = null;
        try (Jedis jedis = RedisUtils.getPool().getResource()) {
            final String redisKey = KEY_PREFIX + key;
            content = jedis.get(redisKey);
            if (content == null) {
                return null;
            }
            return (T) readFromJsonString(content, objectClass);
        } catch (IOException e) {
            Logger.error("redis find key error:" + key + "\n" + content, e);
        }
        return null;
    }

    public static String get(String key) {
        String content = null;
        try (Jedis jedis = RedisUtils.getPool().getResource()) {
            final String redisKey = KEY_PREFIX + key;
            return jedis.get(redisKey);
        } catch (Exception e) {
            Logger.error("redis find key error:" + key + "\n" + content, e);
        }
        return null;
    }

    public static boolean save(String key, Object object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String content = mapper.writeValueAsString(object);
            saveToRedis(key, content);
        } catch (JsonProcessingException e) {
            Logger.error("Session save error", e);
            return false;
        }
        return true;
    }

    public static void delete(String key) {
        Vector<JedisPool> redisPools = RedisUtils.getPoolList();
        for (JedisPool pool : redisPools) {
            try (Jedis jedis = pool.getResource()) {
                String redisKey = KEY_PREFIX + key;
                jedis.del(redisKey);
                Logger.trace("delete key from redis: " + redisKey);
            }
        }
    }
}
