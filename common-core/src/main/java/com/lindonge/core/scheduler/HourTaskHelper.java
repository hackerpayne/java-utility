package com.lindonge.core.scheduler;

import com.google.common.collect.Maps;
import com.lindonge.core.model.ModelTaskEveryHour;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.ConcurrentMap;

/**
 * 每小时指定任务数量的设置
 * Created by kyle on 17/5/7.
 */
public class HourTaskHelper {

    private static final Logger logger = LoggerFactory.getLogger(HourTaskHelper.class);
    // 限制最多数量
    private int maxCountLimit;

    // 每小时任务列表
    private ConcurrentMap<Integer, ModelTaskEveryHour> mapHour = Maps.newConcurrentMap();

    public int getMaxCountLimit() {
        return maxCountLimit;
    }

    public void setMaxCountLimit(int maxCountLimit) {
        this.maxCountLimit = maxCountLimit;
    }

    /**
     * 获取小时表的Map
     *
     * @return
     */
    public ConcurrentMap<Integer, ModelTaskEveryHour> getMapHour() {
        return mapHour;
    }

    public void setMapHour(ConcurrentMap<Integer, ModelTaskEveryHour> mapHour) {
        this.mapHour = mapHour;
    }

    /**
     * 添加一个小时任务数到队列里面
     *
     * @param hourJob
     */
    public void addHourJob(ModelTaskEveryHour hourJob) {
        if (hourJob != null)
            mapHour.put(hourJob.getHour(), hourJob);
    }

    /**
     * 获取当前小时数，一天24小时计
     *
     * @return
     */
    public Integer getCurrentHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 根据当前时间，计算任务表里面，现在这个小时，需要多少个任务
     *
     * @return
     */
    public Integer getHourJobCount() {
        return getHourJobCount(getCurrentHour());
    }

    /**
     * 根据小时时间，计算本小时，需要执行的任务数量，会根据min和max之间取随机数得到
     * 本小时应该执行的任务数量，
     * 可以用：Calendar cal = Calendar.getInstance();
     * Integer currentHour = cal.get(Calendar.HOUR_OF_DAY);
     * 得到当前小时
     *
     * @param currentHour
     * @return
     */
    public Integer getHourJobCount(Integer currentHour) {
        ModelTaskEveryHour currentJob = mapHour.get(currentHour);

        if (currentJob != null && currentJob.getMin() < currentJob.getMax()) {
            Integer currentHourJobNum = RandomUtils.nextInt(currentJob.getMin(), currentJob.getMax());

            currentJob.setWaitDone(currentHourJobNum);//存回去一份到Map内
            mapHour.put(currentHour, currentJob);
            return currentHourJobNum;

        }

        return -1;

    }


}
