package cn.summer.homework.cache;

import cn.summer.homework.utils.ApplicationContextUtils;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author VHBin
 * @date 2022/7/3-11:44
 */
public class RedisCache implements Cache {
    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);
    private final String id;

    // id 由使用该 cache 的 dao 决定
    public RedisCache(String id) {
        this.id = id;
    }

    // 获得 Redis 客户端
    @SuppressWarnings("unchecked")
    private RedisTemplate<String, Object> getRedisTemplate() {
        return (RedisTemplate<String, Object>) ApplicationContextUtils
                .getBean("redisTemplate");
    }

    // hash map 内的 key 以 MD5 的形式存储
    private String getMD5Key(String s) {
        return DigestUtils
                .md5DigestAsHex(
                        s.getBytes(StandardCharsets.UTF_8));
    }

    // 获得 MyBatis 自定义的 id
    @Override
    public String getId() {
        return id;
    }

    // o hash 内的 key, o1 hash 内的 value; 以 id 作为 hash 的 key
    @Override
    public void putObject(Object o, Object o1) {
        RedisTemplate<String, Object> template = getRedisTemplate();
        // 存值
        template.opsForHash()
                .put(id, getMD5Key(o.toString()), o1);
        // 设置超时时间
        // 这样设置将可能导致所有的缓存同一时间全部过期, 从而引发缓存雪崩或缓存穿透
        template.expire(id, 30, TimeUnit.MINUTES);

    }

    @Override
    public Object getObject(Object o) {
        return getRedisTemplate()
                .opsForHash()
                .get(id, getMD5Key(o.toString()));
    }

    // 没有实现
    @Override
    public Object removeObject(Object o) {
        logger.info("Redis Cache Remove");
        return null;
    }

    @Override
    public void clear() {
        logger.info("Redis Cache Clear");
        getRedisTemplate().delete(id);
    }

    @Override
    public int getSize() {
        return getRedisTemplate()
                .opsForHash()
                .size(id)
                .intValue();
    }
}
