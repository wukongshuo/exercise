package com.xxw.base.dao;

import java.util.UUID;

/**
 * @author 唐少峰
 * @title ID生成工具类
 * @date 2015年11月9日 上午9:02:16
 * @description 生成各种需要分片的数据ID，如：userId，格式为：CN_000001_USER_后缀，CN为数据中心，000001为编码，USER为类型（最长为8位），后缀为UUID。整个id最长为51位
 */
public class IDUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String generateId(String nation, String city, String type) {
        StringBuilder builder = new StringBuilder();
        builder.append(nation);
        builder.append("_");
        builder.append(city);
        builder.append("_");
        builder.append(type);
        builder.append("_");
        String postfix = UUID.randomUUID().toString().replace("-", "");
        builder.append(postfix);
        return builder.toString();
    }

    public static String getTypeFromId(String id) {
        String[] info = id.split("_");
        if (info.length != 4) {
            return null;
        }
        return info[2];
    }

    /**
     * 根据服务id生成对应的id
     * @param serverId
     * @param type
     * @return
     */
    public static String generateId(String serverId, String type) {
        StringBuilder builder = new StringBuilder();
        builder.append(serverId);
        builder.append("_");
        builder.append(type);
        builder.append("_");
        String postfix = UUID.randomUUID().toString().replace("-", "");
        builder.append(postfix);
        return builder.toString();
    }

    public static String generateServerId() {
        StringBuilder builder = new StringBuilder();
        builder.append("CN");
//        builder.append("_");
//        builder.append("sz");
        builder.append("_");
        int rand = (int) (Math.random() * 900000) + 100000;
        String postfix = String.valueOf(rand);
        builder.append(postfix);
        return builder.toString();
    }

    public static String getServerIdFromUserId(String innerId) {
        String[] info = innerId.split("_");
        if (info.length != 4) {
            return null;
        }
        return info[0] + "_" + info[1];
    }

    public static String getUserIdPostfix(String userId) {
        int index = userId.lastIndexOf("_");
        return userId.substring(index + 1);
    }

    public static boolean isUserIdRight(String userId, String serverId) {
        if (userId.length() != 47) {
            return false;
        }
        return userId.contains(serverId + "_USER");
    }

    public static void main(String[] args) {
//        System.out.println(IDUtil.generateId("CN_sz0001", IdType.USERIDTYPE));
        System.out.println(generateServerId());
        System.out.println(getServerIdFromUserId("CN_sz0001_USER_83838383838383838"));
        System.out.println(getUserIdPostfix("CN_sz0001_USER_83838383838383838"));
    }
}
