package com.cdk.standardms.common.utils;

import xc.crabapple.core.log.LogUtil;

/**
 * xuechong 2025-01-18
 * 系统定时器任务调度工具类
 */
public final class AppScheduleUtil {

    private static boolean p_inited = false;

    /**
     * xuechong 2025-01-18
     */
    public static final void init() {
        if (p_inited) {
            return;
        }
        p_inited = true;
        LogUtil.print("AppScheduleUtil_init_1", "系统定时器初始化完成");
        //
        // 时间点执行
        // TimerClockUtil.clockByPoints(AppScheduleUtil.class, "aaa", "****-**-** 01:30:00");
        //
        // 时间延迟循环执行
        // TimerClockUtil.clockByDelay(AppScheduleUtil.class, "bbb", 60000L);
    }

}