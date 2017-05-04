package com.lamu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.lamu.entity.Lamu;
import com.lamu.entity.Production;
import com.lamu.model.ProductionKindsModel;
import com.lamu.model.ProductionModel;
import com.lamu.model.ProductionPicModel;
import com.lamu.service.LamuService;
import com.lamu.util.ReplyJson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by songliang on 2016/1/12.
 *
 * @author songliang
 */
@Controller
@RequestMapping("lamu")
public class LamuController {
    private static List<Integer> types = new ArrayList<Integer>();
    @Value("${recommand_type}")
    private String recommandType;
    @Value("${short_type}")
    private String shortType;
    @Value("${info_type}")
    private String infoType;
    @Autowired
    private LamuService lamuService;

    @ResponseBody
    @RequestMapping(value = "add", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String upload(MultipartHttpServletRequest request, HttpServletResponse response, Production production, MultipartFile file) {
        ReplyJson replyJson = new ReplyJson();
        List<MultipartFile> files = request.getFiles("file");
        if (files == null || files.isEmpty() || files.size() < 9) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("您上传的图片信息有误！");
            return JSON.toJSONString(replyJson);
        } else {
            UUID uuid = UUID.randomUUID();
            production.setUuid(uuid.toString());
            Integer one = Integer.valueOf(recommandType);
            //插入辣木基本信息
            types.add(0, Integer.valueOf(recommandType));
            for (int i = 1; i <= 4; i++) {
                types.add(i, Integer.valueOf(shortType));
            }
            for (int i = 5; i <= 8; i++) {
                types.add(i, Integer.valueOf(infoType));
            }
          /*  Integer insert = lamuService.insertProduction(request,production,files,types);
            if(insert==1){
                replyJson.setReplyCode(1);
                replyJson.setReplyInfo("插入成功！");
            }*/
        }
        return JSON.toJSONString(replyJson);
    }

    /**
     * 查询所有产品
     *
     * @param response
     * @param request
     * @param condition
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String getAllLamusByCondition(HttpServletResponse response, HttpServletRequest request, String condition) {
        ReplyJson replyJson = new ReplyJson();
        JSONObject conditionMap = JSON.parseObject(condition);
        if (!conditionMap.containsKey("curPage")) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("传递参数有误！");
            return JSON.toJSONString(replyJson);
        }
        if (!conditionMap.containsKey("pageSize")) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("传递参数有误！");
            return JSON.toJSONString(replyJson);
        }
        JSONObject clone = (JSONObject) conditionMap.clone();
        Set<Map.Entry<String, Object>> entries = conditionMap.entrySet();
        //去除空条件
        for (Map.Entry<String, Object> entry : entries) {
            if (entry.getValue() == null || entry.getValue().equals("")) {
                clone.remove(entry.getKey());
            }
        }
        PageInfo<ProductionModel> byCondition = lamuService.getAllLamusByCondition(clone);
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo(JSON.toJSONString(byCondition));
        return JSON.toJSONString(replyJson);
    }

    @ResponseBody
    @RequestMapping(value = "view", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String select(HttpServletRequest request, HttpServletResponse response, String id) {
        ReplyJson replyJson = new ReplyJson();
        ProductionModel select = lamuService.select(id);
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo(JSON.toJSONString(select));
        return JSON.toJSONString(replyJson);
    }

    @ResponseBody
    @RequestMapping(value = "viewPic", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String selectPic(HttpServletRequest request, HttpServletResponse response, String id) {
        ReplyJson replyJson = new ReplyJson();
        List<ProductionPicModel> productionPics = lamuService.selectPic(id);
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo(JSON.toJSONString(productionPics));
        return JSON.toJSONString(replyJson);

    }

    /**
     * 更新辣木基本信息
     *
     * @param request
     * @param response
     * @param id       需要被更新的辣木ID
     */
    @ResponseBody
    @RequestMapping(value = "update", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String update(HttpServletRequest request, HttpServletResponse response, String id, ProductionModel lamu) {
        ReplyJson replyJson = new ReplyJson();
        Map<String, String[]> parameterMap = request.getParameterMap();
        lamu.setUuid(id);
        Integer update = lamuService.update(lamu);
        if (update == 1) {
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("更新成功！");
        } else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("更新失败！");
        }
        return JSON.toJSONString(replyJson);


    }

    /**
     * 更新图片
     *
     * @param request
     * @param response
     * @param uuid     辣木的ID
     * @param sort     图片的序号
     * @param file     图片信息
     */
    @ResponseBody
    @RequestMapping(value = "updatePic", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String updatePic(HttpServletRequest request, HttpServletResponse response, String uuid, String sort, MultipartFile file) {
        ReplyJson replyJson = new ReplyJson();
      /*  Integer update = lamuService.updatePic(request, uuid, sort, file);
        if(update==1){
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("更新成功！");
        }else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("更新失败！");
        }*/
        return JSON.toJSONString(replyJson);
    }

    /**
     * 轮播图加入推荐专区
     *
     * @param request
     * @param response
     * @param id       轮播图的ID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "addRecommand", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String addRecommand(HttpServletRequest request, HttpServletResponse response, String id) {
        ReplyJson replyJson = new ReplyJson();
        Integer integer = lamuService.countRecommand();
        if (integer >= 8) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("被推荐的辣木已经大于8个，不允许再次添加推荐！");
            return JSON.toJSONString(replyJson);
        }
        Integer update = lamuService.addRecommand(id);
        if (update == 1) {
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("加入推荐成功！");
        } else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("加入推荐失败！");
        }
        return JSON.toJSONString(replyJson);
    }

    /**
     * 轮播图取消推荐专区
     *
     * @param request
     * @param response
     * @param id       轮播图的ID
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "removeRecommand", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String removeRecommand(HttpServletRequest request, HttpServletResponse response, String id) {
        ReplyJson replyJson = new ReplyJson();
        Integer update = lamuService.removeRecommand(id);
        if (update == 1) {
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("取消推荐成功！");
        } else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("取消推荐失败！");
        }
        return JSON.toJSONString(replyJson);
    }

    /**
     * 删除产品
     *
     * @param request
     * @param response
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String delete(HttpServletRequest request, HttpServletResponse response, String id) {
        ReplyJson replyJson = new ReplyJson();
        Integer delete = lamuService.delete(id);
        if (delete == 1) {
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("删除成功！");
        } else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("删除失败！");
        }
        return JSON.toJSONString(replyJson);
    }

    /**
     * 前台获取八个推荐辣木产品
     *
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "listEight", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String getFrontLamuEight(HttpServletRequest request, HttpServletResponse response) {
        ReplyJson replyJson = new ReplyJson();
        Map<ProductionModel, ProductionPicModel> lamuEight = lamuService.getFrontLamuEight();
        Set<Map.Entry<ProductionModel, ProductionPicModel>> entries = lamuEight.entrySet();
        List<Lamu> lamus = new ArrayList<Lamu>();
        for (Map.Entry<ProductionModel, ProductionPicModel> entry : entries) {
            Lamu lamu = new Lamu();
            BeanUtils.copyProperties(entry.getKey(), lamu);
            lamu.setPicAddr(entry.getValue().getPicAddr());
            lamus.add(lamu);
        }
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo(JSON.toJSONString(lamus));
        return JSON.toJSONString(replyJson);
    }

    /**
     * 获取产品的种类：note:种类有辣木子，辣木含片，辣木茶等
     *
     * @param request
     * @param response
     * @return 产品种类JSON 字符串
     */
    @ResponseBody
    @RequestMapping(value = "kinds", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String getKinds(HttpServletRequest request, HttpServletResponse response) {
        ReplyJson replyJson = new ReplyJson();
        List<ProductionKindsModel> kinds = lamuService.getKinds();
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo(JSON.toJSONString(kinds));
        return JSON.toJSONString(replyJson);
    }

    /**
     * 分页获取产品列表
     *
     * @param response
     * @param request
     * @param condition 分页条件。note 里面可能包含了当前页，每页显示的条数，group by 参数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String getProductionByPage(HttpServletResponse response, HttpServletRequest request, String condition) {
        ReplyJson replyJson = new ReplyJson();
        JSONObject jsonObject = JSONObject.parseObject(condition);
        PageInfo<ProductionModel> allLamusByCondition = lamuService.getAllLamusByCondition(jsonObject);
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo(JSON.toJSONString(allLamusByCondition));
        return JSON.toJSONString(replyJson);
    }

   /* *//**
     * 获取基本信息中的
     * @param request
     * @param response
     * @param id
     * @return
     *//*
    @ResponseBody
    @RequestMapping(value = "infoPic",method = RequestMethod.GET,produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.TEXT_PLAIN_VALUE})
    public String getInfoPic(HttpServletRequest request,HttpServletResponse response,String id){

    }*/
}
