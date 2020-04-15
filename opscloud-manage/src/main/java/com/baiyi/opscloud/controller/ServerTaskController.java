package com.baiyi.opscloud.controller;

import com.baiyi.opscloud.domain.BusinessWrapper;
import com.baiyi.opscloud.domain.DataTable;
import com.baiyi.opscloud.domain.HttpResult;
import com.baiyi.opscloud.domain.param.ansible.AnsiblePlaybookParam;
import com.baiyi.opscloud.domain.param.server.ServerTaskExecutorParam;
import com.baiyi.opscloud.domain.vo.ansible.OcAnsiblePlaybookVO;
import com.baiyi.opscloud.domain.vo.server.OcServerTaskVO;
import com.baiyi.opscloud.facade.ServerTaskFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author baiyi
 * @Date 2020/4/7 6:19 下午
 * @Version 1.0
 */
@RestController
@RequestMapping("/server/task")
@Api(tags = "服务器管理")
public class ServerTaskController {

    @Resource
    private ServerTaskFacade serverTaskFacade;

    @ApiOperation(value = "分页模糊查询playbook列表")
    @PostMapping(value = "/playbook/page/query", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<DataTable<OcAnsiblePlaybookVO.AnsiblePlaybook>> queryPlaybookPage(@RequestBody @Valid AnsiblePlaybookParam.PageQuery pageQuery) {
        return new HttpResult<>(serverTaskFacade.queryPlaybookPage(pageQuery));
    }

    @ApiOperation(value = "新增playbook")
    @PostMapping(value = "/playbook/add", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> addPlaybook(@RequestBody @Valid OcAnsiblePlaybookVO.AnsiblePlaybook ansiblePlaybook) {
        return new HttpResult<>(serverTaskFacade.addPlaybook(ansiblePlaybook));
    }

    @ApiOperation(value = "更新playbook")
    @PutMapping(value = "/playbook/update", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> updastePlaybook(@RequestBody @Valid OcAnsiblePlaybookVO.AnsiblePlaybook ansiblePlaybook) {
        return new HttpResult<>(serverTaskFacade.updatePlaybook(ansiblePlaybook));
    }


    @ApiOperation(value = "删除指定的playbook")
    @DeleteMapping(value = "/playbook/del", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> deleteServerById(@Valid @RequestParam int id) {
        return new HttpResult<>(serverTaskFacade.deletePlaybookById(id));
    }

    @ApiOperation(value = "批量命令")
    @PostMapping(value = "/command/executor", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<BusinessWrapper<Boolean>> executorCommand(@RequestBody @Valid ServerTaskExecutorParam.ServerTaskCommandExecutor serverTaskCommandExecutor) {
        return new HttpResult(serverTaskFacade.executorCommand(serverTaskCommandExecutor));
    }

    @ApiOperation(value = "执行playbook")
    @PostMapping(value = "/playbook/executor", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<BusinessWrapper<Boolean>> executorPlaybook(@RequestBody @Valid ServerTaskExecutorParam.ServerTaskPlaybookExecutor serverTaskPlaybookExecutor) {
        return new HttpResult(serverTaskFacade.executorPlaybook(serverTaskPlaybookExecutor));
    }

    @ApiOperation(value = "查询任务")
    @GetMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<OcServerTaskVO.ServerTask> queryServerTask(@Valid int taskId) {
        return new HttpResult<>(serverTaskFacade.queryServerTaskByTaskId(taskId));
    }

    @ApiOperation(value = "创建ansible主机配置文件")
    @GetMapping(value = "/ansible/hosts/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> createAnsibleHosts() {
        return new HttpResult<>(serverTaskFacade.createAnsibleHosts());
    }

    @ApiOperation(value = "中止任务")
    @GetMapping(value = "/abort", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> abortServerTask(@Valid int taskId) {
        return new HttpResult<>(serverTaskFacade.abortServerTask(taskId));
    }

    @ApiOperation(value = "中止子任务")
    @GetMapping(value = "/member/abort", produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpResult<Boolean> abortServerTaskMember(@Valid int memberId) {
        return new HttpResult<>(serverTaskFacade.abortServerTaskMember(memberId));
    }

}
