package com.baiyi.opscloud.ansible.handler;

import com.baiyi.opscloud.ansible.builder.*;
import com.baiyi.opscloud.ansible.config.AnsibleConfig;
import com.baiyi.opscloud.common.base.ServerTaskStatus;
import com.baiyi.opscloud.common.base.ServerTaskStopType;
import com.baiyi.opscloud.domain.vo.ansible.playbook.PlaybookVars;
import com.baiyi.opscloud.common.util.PlaybookUtils;
import com.baiyi.opscloud.domain.generator.OcServerTask;
import com.baiyi.opscloud.domain.generator.OcServerTaskMember;
import com.baiyi.opscloud.domain.generator.opscloud.OcServer;
import com.baiyi.opscloud.domain.param.server.ServerTaskExecutorParam;
import com.baiyi.opscloud.service.server.OcServerService;
import com.baiyi.opscloud.service.server.OcServerTaskMemberService;
import com.baiyi.opscloud.service.server.OcServerTaskService;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.exec.CommandLine;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.baiyi.opscloud.common.base.Global.ASYNC_POOL_TASK_EXECUTOR;

/**
 * @Author baiyi
 * @Date 2020/4/6 4:34 下午
 * @Version 1.0
 */
@Component
public class AnsibleTaskHandler {

    public static final String ANSIBLE_MODULE_SHELL = "shell";

    public static final String ANSIBLE_DEFAULT_BECOME_USER = "root";

    // 任务并发数
    public static final int TASK_CONCURRENT = 10;

    @Resource
    private AnsibleConfig ansibleConfig;

    @Resource
    private AnsibleExecutorHandler ansibleExecutorHandler;

    @Resource
    private OcServerTaskMemberService ocServerTaskMemberService;

    @Resource
    private OcServerTaskService ocServerTaskService;

//    @Resource
//    private SchedulerManager schedulerManager;

    @Resource
    private OcServerService ocServerService;

    @Async(value = ASYNC_POOL_TASK_EXECUTOR)
    public void call(OcServerTask ocServerTask, ServerTaskExecutorParam.ServerTaskPlaybookExecutor serverTaskPlaybookExecutor, String playbookPath) {
        PlaybookVars vars = PlaybookUtils.buildVars(serverTaskPlaybookExecutor.getVars());
        AnsiblePlaybookArgsBO args = AnsiblePlaybookArgsBO.builder()
                .playbook(playbookPath)
                .tags(serverTaskPlaybookExecutor.getTags())
                .becomeUser(StringUtils.isEmpty(serverTaskPlaybookExecutor.getBecomeUser()) ? ANSIBLE_DEFAULT_BECOME_USER : serverTaskPlaybookExecutor.getBecomeUser())
                .build();
        if (vars != null && vars.getVars() != null)
            args.setExtraVars(vars.getVars());

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> serverTreeHostPatternMap = new GsonBuilder().create().fromJson(ocServerTask.getServerTargetDetail(), type);
        Map<String, String> taskServerMap = getTaskServerMap(serverTreeHostPatternMap, serverTaskPlaybookExecutor.getHostPatterns());
        // 更新子任务数量
        ocServerTask.setTaskSize(taskServerMap.size());
        ocServerTaskService.updateOcServerTask(ocServerTask);
        // 创建子任务
        createTaskMember(ocServerTask, taskServerMap);
        boolean isFinalized = false;

        while (!isFinalized) {
            int executingSize = ocServerTaskMemberService.countOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.EXECUTING.getStatus(), TASK_CONCURRENT);
            // 执行队列数量
            int executeQueueSize = ocServerTaskMemberService.countOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.EXECUTE_QUEUE.getStatus(), TASK_CONCURRENT);
            int concurrentTotal = executingSize + executeQueueSize;
            // 等待队列执行
            if (isWaitingExecuteQueue(concurrentTotal))
                continue;
            // 执行
            executorPlaybook(ocServerTaskMemberService.queryOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.QUEUE.getStatus(), TASK_CONCURRENT - concurrentTotal), args);
            int finalizedSize = ocServerTaskMemberService.countOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.FINALIZED.getStatus(), ocServerTask.getTaskSize());
            // 判断任务是否结束
            if (finalizedSize == ocServerTask.getTaskSize()) {
                ocServerTask.setFinalized(1);
                //   ocServerTask.setExitValue(0);
                ocServerTaskService.updateOcServerTask(ocServerTask);
                isFinalized = true;
            }
            // 判断主任务是否结束
            ocServerTask = ocServerTaskService.queryOcServerTaskById(ocServerTask.getId());
            if (ocServerTask.getStopType() == ServerTaskStopType.SERVER_TASK_STOP.getType())
                isFinalized = true;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    @Async(value = ASYNC_POOL_TASK_EXECUTOR)
    public void call(OcServerTask ocServerTask, ServerTaskExecutorParam.ServerTaskCommandExecutor serverTaskCommandExecutor) {
        AnsibleArgsBO args = AnsibleArgsBO.builder()
                .moduleName(ANSIBLE_MODULE_SHELL)
                .moduleArguments(serverTaskCommandExecutor.getCommand())
                .becomeUser(StringUtils.isEmpty(serverTaskCommandExecutor.getBecomeUser()) ? ANSIBLE_DEFAULT_BECOME_USER : serverTaskCommandExecutor.getBecomeUser())
                .build();

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> serverTreeHostPatternMap = new GsonBuilder().create().fromJson(ocServerTask.getServerTargetDetail(), type);
        Map<String, String> taskServerMap = getTaskServerMap(serverTreeHostPatternMap, serverTaskCommandExecutor.getHostPatterns());
        // 更新子任务数量
        ocServerTask.setTaskSize(taskServerMap.size());
        ocServerTaskService.updateOcServerTask(ocServerTask);
        // 创建子任务
        createTaskMember(ocServerTask, taskServerMap);
        boolean isFinalized = false;

        while (!isFinalized) {
            int executingSize = ocServerTaskMemberService.countOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.EXECUTING.getStatus(), TASK_CONCURRENT);
            // 执行队列数量
            int executeQueueSize = ocServerTaskMemberService.countOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.EXECUTE_QUEUE.getStatus(), TASK_CONCURRENT);
            int concurrentTotal = executingSize + executeQueueSize;
            // 等待队列执行
            if (isWaitingExecuteQueue(concurrentTotal))
                continue;
            // 执行
            executorCommand(ocServerTaskMemberService.queryOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.QUEUE.getStatus(), TASK_CONCURRENT - concurrentTotal), args);

            int finalizedSize = ocServerTaskMemberService.countOcServerTaskMemberByTaskStatus(ocServerTask.getId(), ServerTaskStatus.FINALIZED.getStatus(), ocServerTask.getTaskSize());
            // 判断任务是否结束
            if (finalizedSize == ocServerTask.getTaskSize()) {
                ocServerTask.setFinalized(1);
                //   ocServerTask.setExitValue(0);
                ocServerTaskService.updateOcServerTask(ocServerTask);
                isFinalized = true;
            }
            // 判断主任务是否结束
            ocServerTask = ocServerTaskService.queryOcServerTaskById(ocServerTask.getId());
            if (ocServerTask.getStopType() == ServerTaskStopType.SERVER_TASK_STOP.getType())
                isFinalized = true;
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 判断队列是否已满，并等待
     *
     * @param concurrentTotal
     * @return
     */
    private boolean isWaitingExecuteQueue(int concurrentTotal) {
        try {
            if (concurrentTotal >= TASK_CONCURRENT) {
                TimeUnit.SECONDS.sleep(3);
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void executorPlaybook(List<OcServerTaskMember> memberList, AnsiblePlaybookArgsBO args) {
        for (OcServerTaskMember member : memberList) {
            // 执行队列状态
            member.setTaskStatus(ServerTaskStatus.EXECUTE_QUEUE.getStatus());
            ocServerTaskMemberService.updateOcServerTaskMember(member);
            args.setHosts(member.getManageIp());
            CommandLine commandLine = AnsiblePlaybookArgsBuilder.build(ansibleConfig, args);
            executorCommand(member, commandLine);
        }
    }

    private void executorCommand(List<OcServerTaskMember> memberList, AnsibleArgsBO args) {
        for (OcServerTaskMember member : memberList) {
            // 执行队列状态
            member.setTaskStatus(ServerTaskStatus.EXECUTE_QUEUE.getStatus());
            ocServerTaskMemberService.updateOcServerTaskMember(member);
            args.setPattern(member.getManageIp());
            CommandLine commandLine = AnsibleArgsBuilder.build(ansibleConfig, args);
            executorCommand(member, commandLine);
        }
    }

    /**
     * 执行命令
     * schedulerManager.registerJob(() -> {
     * args.setInventory(member.getManageIp());
     * CommandLine commandLine = AnsibleArgsBuilder.build(ansibleConfig, args);
     * ansibleExecutorHandler.executorRecorder(member, commandLine, 0L);
     * });
     *
     * @param member
     * @param commandLine
     */
    private void executorCommand(OcServerTaskMember member, CommandLine commandLine) {
        ansibleExecutorHandler.executorRecorder(member, commandLine, 0L);
    }

    private void createTaskMember(OcServerTask ocServerTask, Map<String, String> taskServerMap) {
        for (String hostPattern : taskServerMap.keySet()) {
            String manageIp = taskServerMap.get(hostPattern);
            OcServer ocServer = ocServerService.queryOcServerByIp(manageIp);
            ocServerTaskMemberService.addOcServerTaskMember(ServerTaskMemberBuilder.build(ocServerTask, hostPattern, manageIp, ocServer));
        }
    }

    private Map<String, String> getTaskServerMap(Map<String, String> serverTreeHostPatternMap, Set<String> hostPatterns) {
        Map<String, String> taskServerMap = Maps.newHashMap();
        for (String hostPattern : hostPatterns)
            if (serverTreeHostPatternMap.containsKey(hostPattern))
                taskServerMap.put(hostPattern, serverTreeHostPatternMap.get(hostPattern));
        return taskServerMap;
    }
}
