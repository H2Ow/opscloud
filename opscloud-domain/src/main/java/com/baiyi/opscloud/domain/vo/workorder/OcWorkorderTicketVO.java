package com.baiyi.opscloud.domain.vo.workorder;

import com.baiyi.opscloud.domain.vo.user.OcUserVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author baiyi
 * @Date 2020/4/28 6:42 下午
 * @Version 1.0
 */
public class OcWorkorderTicketVO {

    @Data
    @NoArgsConstructor
    @ApiModel
    public static class Ticket {

        /**
         * 工单条目
         **/
        private List<OcWorkorderTicketEntryVO.Entry> ticketEntries;

        /**
         * 审批步骤
         */
        private ApprovalStepsVO.ApprovalDetail approvalDetail;

        /**
         * 工单
         */
        private OcWorkorderVO.Workorder workorder;

        /**
         * 工单申请时间
         */
        private String ago;

        /**
         * 发起人
         */
        private OcUserVO.User user;

        /**
         * 审批中
         */
        private Boolean isInApproval;

        private Integer id;
        private Integer workorderId;
        private Integer userId;
        private String username;
        private Integer flowId;
        private String comment;
        private String ticketPhase;
        private Integer ticketStatus;
        private Date startTime;
        private Date endTime;
        private Date createTime;
        private Date updateTime;
        private String userDetail;
    }

    @Data
    @NoArgsConstructor
    @ApiModel
    public static class TicketApproval {
        private Integer stepActive;
        private List<ApprovalStep> approvalSteps;
    }

    @Data
    @NoArgsConstructor
    @ApiModel
    public static class ApprovalStep {
        private String title;
        private String description;
    }
}
