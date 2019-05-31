package com.lingdonge.core.algorithm;

import com.lingdonge.core.bean.common.ModelTaskEveryHourWeight;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 轮询调度算法(Round-Robin Scheduling)
 * Created by Kyle on 16/12/13.
 */
public class WeightRoundRobin {
    /**
     * 上次选择的服务器
     */
    private int currentIndex = -1;
    /**
     * 当前调度的权值
     */
    private int currentWeight = 0;
    /**
     * 最大权重
     */
    private int maxWeight;
    /**
     * 权重的最大公约数
     */
    private int gcdWeight;
    /**
     * 服务器数
     */
    private int serverCount;

    private List<IWeightRoundRobin> servers = new ArrayList<IWeightRoundRobin>();

    public int greaterCommonDivisor(int a, int b) {
        BigInteger aBig = new BigInteger(String.valueOf(a));
        BigInteger bBig = new BigInteger(String.valueOf(b));
        return aBig.gcd(bBig).intValue();
    }

    /**
     * @param servers
     * @return
     */
    public int greatestCommonDivisor(List<IWeightRoundRobin> servers) {
        int divisor = 0;
        for (int index = 0, len = servers.size(); index < len - 1; index++) {
            if (index == 0) {
                divisor = greaterCommonDivisor(
                        servers.get(index).getWeight(), servers.get(index + 1).getWeight());
            } else {
                divisor = greaterCommonDivisor(divisor, servers.get(index).getWeight());
            }
        }
        return divisor;
    }


    /**
     * 获取最大的权值
     *
     * @param servers
     * @return
     */
    public int greatestWeight(List<IWeightRoundRobin> servers) {
        int weight = 0;
        for (IWeightRoundRobin server : servers) {
            if (weight < server.getWeight()) {
                weight = server.getWeight();
            }
        }
        return weight;
    }

    /**
     * 算法流程：
     * 假设有一组服务器 S = {S0, S1, …, Sn-1}
     * 有相应的权重，变量currentIndex表示上次选择的服务器
     * 权值currentWeight初始化为0，currentIndex初始化为-1 ，当第一次的时候返回 权值取最大的那个服务器，
     * 通过权重的不断递减 寻找 适合的服务器返回，直到轮询结束，权值返回为0
     */
    public IWeightRoundRobin getServer() {
        while (true) {
            currentIndex = (currentIndex + 1) % serverCount;
            if (currentIndex == 0) {
                currentWeight = currentWeight - gcdWeight;
                if (currentWeight <= 0) {
                    currentWeight = maxWeight;
                    if (currentWeight == 0) {
                        return null;
                    }
                }
            }
            if (servers.get(currentIndex).getWeight() >= currentWeight) {
                return servers.get(currentIndex);
            }
        }
    }

    /**
     * 初始化，添加所需要的列表进来
     */
    public void init(List<IWeightRoundRobin> listServer) {
        if (servers == null) {
            servers = new ArrayList<>();
        } else {
            servers.clear();
        }
        for (IWeightRoundRobin server : listServer) {
            servers.add(server);
        }
        maxWeight = greatestWeight(servers);
        gcdWeight = greatestCommonDivisor(servers);
        serverCount = servers.size();

    }

    public static void main(String[] args) {

        testTaskEverHour();
//        WeightRoundRobin weightRoundRobin = new WeightRoundRobin();
//
//        List<IWeightRoundRobin> listServers = new ArrayList<>();
//        listServers.add(new ModelUserAgent("192.168.1.101", 1));
//        listServers.add(new ModelUserAgent("192.168.1.102", 1));
//        listServers.add(new ModelUserAgent("192.168.1.103", 1));
//        listServers.add(new ModelUserAgent("192.168.1.104", 1));
//        listServers.add(new ModelUserAgent("192.168.1.105", 1));
//
//        weightRoundRobin.init(listServers);
//
//        for (int i = 0; i < 15; i++) {
//            ModelUserAgent server = (ModelUserAgent) weightRoundRobin.getServer();
//            System.out.println("server " + server.getUA() + " weight=" + server.getWeight());
//        }
    }

    public static void testTaskEverHour(){
        WeightRoundRobin weightRoundRobin = new WeightRoundRobin();

        List<IWeightRoundRobin> listServers = new ArrayList<>();
        listServers.add(new ModelTaskEveryHourWeight(1, 10));
        listServers.add(new ModelTaskEveryHourWeight(2, 1));
        listServers.add(new ModelTaskEveryHourWeight(3, 1));
        listServers.add(new ModelTaskEveryHourWeight(4, 1));
        listServers.add(new ModelTaskEveryHourWeight(5, 1));

        weightRoundRobin.init(listServers);

        for (int i = 0; i < 15; i++) {
            ModelTaskEveryHourWeight server = (ModelTaskEveryHourWeight) weightRoundRobin.getServer();
            System.out.println("server " + server.getHour() + " weight=" + server.getWeight());
        }
    }
}