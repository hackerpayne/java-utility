package com.lingdonge.quartz.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("schedule_job")
public class ScheduleJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long jobId;

    private String jobName;

    private String springId;

    private String methodName;

    private String jobGroup;

    private String cronExpression;

    private Integer jobStatus;

    private String desc;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    public static final String ID = "id";

    public static final String JOB_ID = "job_id";

    public static final String JOB_NAME = "job_name";

    public static final String SPRING_ID = "spring_id";

    public static final String METHOD_NAME = "method_name";

    public static final String JOB_GROUP = "job_group";

    public static final String CRON_EXPRESSION = "cron_expression";

    public static final String JOB_STATUS = "job_status";

    public static final String DESC = "desc";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_TIME = "update_time";

}
