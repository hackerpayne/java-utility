package com.lingdonge.push.bean;

import com.lingdonge.core.bean.base.BaseEntity;
import com.lingdonge.push.enums.PushSubmitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * push消息结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PushSendResult extends BaseEntity {

    /**
     * 平台推送一次的批次
     */
    private String messageId;

    /**
     * 平台推送一次的requestId(阿里有返回)
     */
    private String requestId;

    /**
     * 本地短信提交状态
     */
    private PushSubmitStatus submitStatus;

    /**
     * 本地提交结果
     */
    private String submitResult;

    /**
     * 本地提交时间
     */
    private LocalDateTime submitTime;

}
