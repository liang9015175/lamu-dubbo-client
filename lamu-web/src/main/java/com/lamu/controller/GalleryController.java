package com.lamu.controller;

import com.github.pagehelper.PageInfo;
import com.lamu.exception.FileUploadException;
import com.lamu.exception.NotFoundException;
import com.lamu.exception.PreConditionException;
import com.lamu.exception.RecommandGtFourException;
import com.lamu.model.GalleryModel;
import com.lamu.service.GalleryService;
import com.lamu.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
@RestController
public class GalleryController {
    private static final Integer PAGE_SIZE = 20;
    @Autowired
    private GalleryService galleryService;

    @RequestMapping(value = "list")
    public PageInfo<GalleryModel> getAllGalleryPics(Integer currentPage) {
        if (currentPage == null) {
            throw new PreConditionException();
        }
        PageInfo<GalleryModel> gallerysByPage = galleryService.getGallerysByPage(currentPage);
        return gallerysByPage;
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public void uploadGalleryPic(HttpServletRequest request, HttpServletResponse response, MultipartFile file) throws IOException {
        if (file == null) {
            throw new PreConditionException("没有文件");
        }

        String type = FileUtil.judgeFileType(file.getInputStream());
        if (type == null) {
            throw new PreConditionException();
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
            gallery.setName(file.getOriginalFilename());
            gallery.setPath("/upload/gallery/" + galleryName + "." + type);
            gallery.setCreateTime(new Date());
            gallery.setRecommand(0);
            Integer integer = galleryService.insertGallery(gallery);
            if (integer != 1) {
                throw new FileUploadException();
            }
        }

    }

    @RequestMapping(value = "view")
    public GalleryModel getGalleryById(Long id) {
        GalleryModel gallery = galleryService.getGalleryById(id);
        if (gallery == null) {
            throw new NotFoundException();
        }
        return gallery;
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
    @RequestMapping(value = "addRecommand", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public void addRecommand(HttpServletRequest request, HttpServletResponse response, Long id) {
        Long integer = galleryService.countRecommand();
        if (integer >= 4) {
            throw new RecommandGtFourException();
        }
        Integer update = galleryService.addRecommand(id);
        if (update != 1) {
            throw new RuntimeException();
        }
    }

    /**
     * 轮播图取消推荐专区
     *
     * @param id       轮播图的ID
     * @return
     */
    @RequestMapping(value = "removeRecommand", method = RequestMethod.POST)
    public void removeRecommand(Long id) {
        galleryService.removeRecommand(id);
    }

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public void delete(Long id) {
        galleryService.delete(id);
    }

    @RequestMapping(value = "frontList", method = RequestMethod.GET)
    public List<GalleryModel> getFrontGallery() {
        List<GalleryModel> frontGallery = galleryService.getFrontGallery();
        return frontGallery;

    }
}
