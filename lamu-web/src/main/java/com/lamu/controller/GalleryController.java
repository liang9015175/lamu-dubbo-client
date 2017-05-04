package com.lamu.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.lamu.entity.Gallery;
import com.lamu.model.GalleryModel;
import com.lamu.service.GalleryService;
import com.lamu.util.FileUtil;
import com.lamu.util.Page;
import com.lamu.util.ReplyJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by songliang on 2016/1/6.
 *
 * @author songliang
 */
@RequestMapping("gallery")
@Controller
public class GalleryController {
    @Autowired
    private GalleryService galleryService;

    @ResponseBody
    @RequestMapping(value = "list", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String getAllGalleryPics(HttpServletRequest request, HttpServletResponse response, String currentPage) {
        ReplyJson replyJson = new ReplyJson();
        Page<Gallery> page = new Page<Gallery>();
        Integer curPage = 1;
        if (!StringUtils.isEmpty(currentPage)) {
            curPage = Integer.valueOf(currentPage);
        } else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("参数错误！");
            return JSON.toJSONString(replyJson);
        }
        PageInfo<GalleryModel> gallerysByPage = galleryService.getGallerysByPage(curPage);
        replyJson.setReplyCode(1);
        replyJson.setReplyInfo(JSON.toJSONString(gallerysByPage));
        return JSON.toJSONString(replyJson);
    }

    @ResponseBody
    @RequestMapping(value = "upload", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String uploadGalleryPic(HttpServletRequest request, HttpServletResponse response, MultipartFile file, String token) throws IOException {
        ReplyJson replyJson = new ReplyJson();
        if (file == null) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("您没有上传文件！");
            return JSON.toJSONString(replyJson);
        }
        if (token == null || token.equals("")) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("不可以重复上传文件！");
            return JSON.toJSONString(replyJson);
        }
        Object sessionToken = (String) request.getSession().getAttribute("token");
        if (sessionToken == null) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("不可以重复上传文件");
            return JSON.toJSONString(replyJson);
        } else if (!token.equals(sessionToken)) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("不可以重复上传文件！");
            return JSON.toJSONString(replyJson);
        } else {
            String type = FileUtil.judgeFileType(file.getInputStream());
            if (type == null) {
                replyJson.setReplyCode(0);
                replyJson.setReplyInfo("文件类型错误！");
                return JSON.toJSONString(replyJson);
            } else {
                String realPath = request.getSession().getServletContext().getRealPath("/upload/gallery");
                File galleryDir = new File(realPath);
                if (!galleryDir.exists()) {
                    galleryDir.mkdirs();
                }
                String galleryName = System.currentTimeMillis() + "-" + new Random().nextInt(9999);
                File f = new File(galleryDir, galleryName + "." + type);
                file.transferTo(f);
                GalleryModel gallery = new GalleryModel();
                gallery.setUuid(UUID.randomUUID().toString());
                gallery.setName(file.getOriginalFilename());
                gallery.setPath("/upload/gallery/" + galleryName + "." + type);
                gallery.setDate(new Date());
                gallery.setRecommand(0);
                Integer integer = galleryService.insertGallery(gallery);
                if (integer == 1) {
                    request.getSession().removeAttribute("token");
                    replyJson.setReplyCode(1);
                    replyJson.setReplyInfo("上传成功!");
                    return JSON.toJSONString(replyJson);
                } else {
                    replyJson.setReplyCode(0);
                    replyJson.setReplyInfo("上传失败!");
                    return JSON.toJSONString(replyJson);
                }
            }
        }

    }

    @ResponseBody
    @RequestMapping(value = "view", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String getGalleryById(HttpServletRequest request, HttpServletResponse response, String id) {
        ReplyJson replyJson = new ReplyJson();
        if (StringUtils.isEmpty(id)) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("参数错误！");
            return JSON.toJSONString(replyJson);
        } else {
            GalleryModel gallery = galleryService.getGalleryById(id);
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo(JSON.toJSONString(gallery));
        }
        return JSON.toJSONString(replyJson);
    }

    /**
     * 生成token 令牌，防止表单的重复提交
     *
     * @param request
     * @param response
     * @return 经过64位加密的token令牌
     */
    @ResponseBody
    @RequestMapping(value = "token", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String generateToken(HttpServletRequest request, HttpServletResponse response) {
        String token = System.currentTimeMillis() + new Random().nextInt(9999) + "";
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] md5 = md.digest(token.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            String token64 = encoder.encode(md5);
            request.getSession().setAttribute("token", token64);
            return token64;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);

        }
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
        Integer integer = galleryService.countRecommand();
        if (integer >= 4) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("被推荐的轮播图已经大于4个，不允许再次添加推荐！");
            return JSON.toJSONString(replyJson);
        }
        Integer update = galleryService.addRecommand(id);
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
        Integer update = galleryService.removeRecommand(id);
        if (update == 1) {
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("取消推荐成功！");
        } else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("取消推荐失败！");
        }
        return JSON.toJSONString(replyJson);
    }

    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String delete(HttpServletResponse response, HttpServletRequest request, String id) {
        ReplyJson replyJson = new ReplyJson();
        Integer delete = galleryService.delete(id);
        if (delete == 1) {
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo("删除成功！");
        } else {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("删除失败！");
        }
        return JSON.toJSONString(replyJson);
    }

    @ResponseBody
    @RequestMapping(value = "frontList", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public String getFrontGallery(HttpServletRequest request, HttpServletResponse response) {
        ReplyJson replyJson = new ReplyJson();
        List<GalleryModel> frontGallery = galleryService.getFrontGallery();
        if (frontGallery == null || frontGallery.isEmpty()) {
            replyJson.setReplyCode(0);
            replyJson.setReplyInfo("没有任何数据！");
        } else {
            replyJson.setReplyCode(1);
            replyJson.setReplyInfo(JSON.toJSONString(frontGallery));
        }
        return JSON.toJSONString(replyJson);
    }
}
