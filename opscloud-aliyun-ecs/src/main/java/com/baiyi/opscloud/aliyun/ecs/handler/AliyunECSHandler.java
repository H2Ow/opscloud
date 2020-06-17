package com.baiyi.opscloud.aliyun.ecs.handler;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.*;
import com.aliyuncs.exceptions.ClientException;
import com.baiyi.opscloud.aliyun.ecs.base.BaseAliyunECS;
import com.baiyi.opscloud.common.util.JSONUtils;
import com.baiyi.opscloud.domain.BusinessWrapper;
import com.baiyi.opscloud.domain.ErrorEnum;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * @Author baiyi
 * @Date 2020/1/14 10:09 上午
 * @Version 1.0
 */
@Component
public class AliyunECSHandler extends BaseAliyunECS {


    @Resource
    private AliyunInstanceHandler aliyunInstanceHandler;

    public List<DescribeInstanceAutoRenewAttributeResponse.InstanceRenewAttribute> getInstanceRenewAttribute(String regionId, DescribeInstanceAutoRenewAttributeRequest describe) {
        IAcsClient client = acqAcsClient(regionId);
        try {
            DescribeInstanceAutoRenewAttributeResponse response
                    = client.getAcsResponse(describe);
            return response.getInstanceRenewAttributes();
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }

    public DescribeDisksResponse getDisksResponse(String regionId, DescribeDisksRequest request) {
        IAcsClient client;
        client = acqAcsClient(regionId);
        try {
            return client.getAcsResponse(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public DescribeInstancesResponse.Instance getInstance(String regionId, String instanceId) {
        DescribeInstancesRequest describe = new DescribeInstancesRequest();
        Collection<String> instanceIds = Lists.newArrayList();
        instanceIds.add(instanceId);
        describe.setInstanceIds(JSONUtils.writeValueAsString(instanceIds));
        try {
            DescribeInstancesResponse response = aliyunInstanceHandler.getInstancesResponse(regionId, describe);
            return response.getInstances().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    public List<DescribeInstancesResponse.Instance> getInstanceList(String regionId) {
        List<DescribeInstancesResponse.Instance> instanceList = Lists.newArrayList();
        try {
            DescribeInstancesRequest describe = new DescribeInstancesRequest();
            describe.setPageSize(QUERY_PAGE_SIZE);
            int size = QUERY_PAGE_SIZE;
            int pageNumber = 1;
            // 循环取值
            while (QUERY_PAGE_SIZE <= size) {
                describe.setPageNumber(pageNumber);
                DescribeInstancesResponse response = aliyunInstanceHandler.getInstancesResponse(regionId, describe);
                instanceList.addAll(response.getInstances());
                size = response.getTotalCount();
                pageNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instanceList;
    }
//    public List<DescribeInstancesResponse.Instance> getInstanceList(String regionId) {
//        List<DescribeInstancesResponse.Instance> instanceList = Lists.newArrayList();
//        DescribeInstancesRequest describe = new DescribeInstancesRequest();
//        describe.setPageSize(QUERY_PAGE_SIZE);
//        DescribeInstancesResponse response = getInstancesResponse(regionId, describe);
//        instanceList.addAll(response.getInstances());
//        //cacheInstanceRenewAttribute(regionId, response);
//        // 获取总数
//        int totalCount = response.getTotalCount();
//        // 循环次数
//        int cnt = (totalCount + QUERY_PAGE_SIZE - 1) / QUERY_PAGE_SIZE;
//        for (int i = 1; i < cnt; i++) {
//            describe.setPageNumber(i + 1);
//            response = getInstancesResponse(regionId, describe);
//            instanceList.addAll(response.getInstances());
//            //cacheInstanceRenewAttribute(regionId, response);
//        }
//        return instanceList;
//    }

    public BusinessWrapper<Boolean> start(String regionId, String instanceId) {
        try {
            StartInstanceRequest describe = new StartInstanceRequest();
            describe.setInstanceId(instanceId);
            StartInstanceResponse response = startInstanceResponse(regionId, describe);
            if (response != null && !StringUtils.isEmpty(response.getRequestId()))
                return BusinessWrapper.SUCCESS;
        } catch (Exception ignored) {
        }
        return new BusinessWrapper(ErrorEnum.CLOUD_SERVER_POWER_MGMT_FAILED);
    }

    public BusinessWrapper<Boolean> stop(String regionId, String instanceId) {
        try {
            StopInstanceRequest describe = new StopInstanceRequest();
            describe.setInstanceId(instanceId);
            StopInstanceResponse response = stopInstanceResponse(regionId, describe);
            if (response != null && !StringUtils.isEmpty(response.getRequestId()))
                return BusinessWrapper.SUCCESS;
        } catch (Exception ignored) {
        }
        return new BusinessWrapper(ErrorEnum.CLOUD_SERVER_POWER_MGMT_FAILED);
    }

    private StopInstanceResponse stopInstanceResponse(String regionId, StopInstanceRequest describe) {
        IAcsClient client = acqAcsClient(regionId);
        try {
            return client.getAcsResponse(describe);
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }

    private StartInstanceResponse startInstanceResponse(String regionId, StartInstanceRequest describe) {
        IAcsClient client = acqAcsClient(regionId);
        try {
            return client.getAcsResponse(describe);
        } catch (ClientException e) {
            e.printStackTrace();
            return null;
        }
    }

}
