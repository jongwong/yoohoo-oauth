package cn.jongwong.server.common;

import cn.ipokerface.snowflake.SnowflakeIdGenerator;

public class SnowflakeIdUtils {

    // 机器ID（workerId）和数据中心ID（dataCenterId）可根据实际情况修改
    private static final long WORKER_ID = 1L;  // 机器ID
    private static final long DATA_CENTER_ID = 1L;  // 数据中心ID
    private static final long CUSTOM_EPOCH = 1609459200000L; // 自定义时间戳起点（如2021年1月1日00:00:00）

    private static SnowflakeIdGenerator snowflakeIdGenerator;

    static {
        // 初始化 SnowflakeIdGenerator
        snowflakeIdGenerator = new SnowflakeIdGenerator(WORKER_ID, DATA_CENTER_ID, CUSTOM_EPOCH);
    }

    /**
     * 获取一个唯一的ID
     *
     * @return 唯一ID
     */
    public static long generateId() {
        return snowflakeIdGenerator.nextId();
    }

    /**
     * 获取多个唯一的ID
     *
     * @param count 需要生成的ID个数
     * @return 唯一ID数组
     */
    public static long[] generateIds(int count) {
        long[] ids = new long[count];
        for (int i = 0; i < count; i++) {
            ids[i] = snowflakeIdGenerator.nextId();
        }
        return ids;
    }

    /**
     * 获取唯一ID的字符串形式
     *
     * @return 唯一ID字符串
     */
    public static String generateIdString() {
        return String.valueOf(snowflakeIdGenerator.nextId());
    }

    /**
     * 获取多个唯一ID的字符串形式
     *
     * @param count 需要生成的ID个数
     * @return 唯一ID字符串数组
     */
    public static String[] generateIdsString(int count) {
        String[] ids = new String[count];
        for (int i = 0; i < count; i++) {
            ids[i] = String.valueOf(snowflakeIdGenerator.nextId());
        }
        return ids;
    }
}
