package com.lamu.controller;

import com.github.pagehelper.PageInfo;
import com.lamu.entity.Production;
import com.lamu.exception.FileUploadException;
import com.lamu.exception.PreConditionException;
import com.lamu.model.ProductionKindsModel;
import com.lamu.model.ProductionModel;
import com.lamu.model.ProductionPicModel;
import com.lamu.model.ProductionWithPicModel;
import com.lamu.service.LamuService;
import com.lamu.util.FileUtil;
import com.lamu.util.ReplyJson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by songliang on 2016/1/12.
 *
 * @author songliang
 */
@RestController
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


    @RequestMapping(value = "add", method = RequestMethod.POST)
    public void upload(MultipartHttpServletRequest request, HttpServletResponse response,
                       Production production,
                       MultipartFile recFile,
                       MultipartFile shortFile1,
                       MultipartFile shortFile2,
                       MultipartFile shortFile3,
                       MultipartFile shortFile4,
                       MultipartFile infoFile1,
                       MultipartFile infoFile2,
                       MultipartFile infoFile3,
                       MultipartFile infoFile4) {
        try {
            UUID uuid = UUID.randomUUID();
            ProductionModel model = new ProductionModel();
            BeanUtils.copyProperties(production, model);
            model.setUuid(String.valueOf(uuid.hashCode()));
            lamuService.insertProductionPic(copyFile(request, recFile, String.valueOf(uuid.hashCode()), 0));
            lamuService.insertProductionPic(copyFile(request, shortFile1, String.valueOf(uuid.hashCode()), 1));
            lamuService.insertProductionPic(copyFile(request, shortFile2, String.valueOf(uuid.hashCode()), 1));
            lamuService.insertProductionPic(copyFile(request, shortFile3, String.valueOf(uuid.hashCode()), 1));
            lamuService.insertProductionPic(copyFile(request, shortFile4, String.valueOf(uuid.hashCode()), 1));
            lamuService.insertProductionPic(copyFile(request, infoFile1, String.valueOf(uuid.hashCode()), 2));
            lamuService.insertProductionPic(copyFile(request, infoFile2, String.valueOf(uuid.hashCode()), 2));
            lamuService.insertProductionPic(copyFile(request, infoFile3, String.valueOf(uuid.hashCode()), 2));
            lamuService.insertProductionPic(copyFile(request, infoFile4, String.valueOf(uuid.hashCode()), 2));
            lamuService.insertProduction(model);
        } catch (Exception e) {
            throw new FileUploadException();
        }


    }

    private ProductionPicModel copyFile(HttpServletRequest request, MultipartFile file, String productionId, Integer picType) {
        try {
            if (file != null) {
                String fileType = FileUtil.judgeFileType(file.getInputStream());
                String realPath = request.getSession().getServletContext().getRealPath("/upload/lamu");
                File galleryDir = new File(realPath);
                if (!galleryDir.exists()) {
                    galleryDir.mkdirs();
                }
                String lamuName = System.currentTimeMillis() + "-" + new Random().nextInt(9999);
                File f = new File(galleryDir, lamuName + "." + fileType);
                file.transferTo(f);
                ProductionPicModel productionPic = new ProductionPicModel();
                productionPic.setUuid(String.valueOf(UUID.randomUUID().hashCode()));
                productionPic.setProductionId(productionId);
                productionPic.setPicAddr("/upload/lamu/" + lamuName + "." + fileType);
                productionPic.setPicType(picType);
                productionPic.setCreateTime(new Date());
                return productionPic;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询所有产品
     *
     * @param response
     * @param request
     * @param condition
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public PageInfo<ProductionModel> getAllLamusByCondition(@RequestParam(required = false, name = "name") String name,
                                                            @RequestParam(name = "date", required = false) Date date,
                                                            Integer curPage, Integer pageSize) {
        return lamuService.getAllLamusByCondition(name, date, curPage, pageSize);
    }

    @RequestMapping(value = "view", method = RequestMethod.GET)
    public ProductionModel select(String id) {
        return lamuService.select(id);

    }


    @RequestMapping(value = "viewPic", method = RequestMethod.GET)
    public List<ProductionPicModel> selectPic(String id) {

        List<ProductionPicModel> productionPics = lamuService.selectPic(id);
        return productionPics;

    }

    /**
     * 更新辣木基本信息
     *
     * @param request
     * @param response
     * @param id       需要被更新的辣木ID
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public void update(String id, ProductionModel lamu) {
        lamu.setUuid(id);
        Integer update = lamuService.update(lamu);
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

    @RequestMapping(value = "updatePic", method = RequestMethod.POST)
    public void updatePic(HttpServletRequest request, String uuid, String sort, MultipartFile file) {
        ReplyJson replyJson = new ReplyJson();
        try {
            String fileType = FileUtil.judgeFileType(file.getInputStream());
            String realPath = request.getSession().getServletContext().getRealPath("/upload/lamu");
            File galleryDir = new File(realPath);
            if (!galleryDir.exists()) {
                galleryDir.mkdirs();
            }
            String lamuName = System.currentTimeMillis() + "-" + new Random().nextInt(9999);
            File f = new File(galleryDir, lamuName + "." + fileType);
            file.transferTo(f);
            ProductionPicModel model = new ProductionPicModel();
            model.setProductionId(uuid);
            model.setPicAddr("/upload/lamu/" + model.getUuid() + "/" + file.getOriginalFilename());
            lamuService.updatePic(model);
        } catch (IOException e) {
            throw new FileUploadException();
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
    @RequestMapping(value = "addRecommand", method = RequestMethod.POST)
    public void addRecommand(String id) {
        lamuService.addRecommand(id);
    }

    /**
     * 轮播图取消推荐专区
     *
     * @param request
     * @param response
     * @param id       轮播图的ID
     * @return
     */
    @RequestMapping(value = "removeRecommand", method = RequestMethod.POST)
    public void removeRecommand(String id) {
        lamuService.removeRecommand(id);
    }

    /**
     * 删除产品
     *
     * @param request
     * @param response
     * @param id
     * @return
     */

    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public void delete(String id) {

        lamuService.delete(id);

    }

    /**
     * 前台获取八个推荐辣木产品
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "listEight", method = RequestMethod.GET)
    public PageInfo<ProductionWithPicModel> getFrontLamuEight(HttpServletRequest request, HttpServletResponse response) {

        PageInfo<ProductionWithPicModel> lamuEight = lamuService.getFrontLamuEight();
        return lamuEight;
    }

    /**
     * 获取产品的种类：note:种类有辣木子，辣木含片，辣木茶等
     *
     * @param request
     * @param response
     * @return 产品种类JSON 字符串
     */
    @RequestMapping(value = "kinds", method = RequestMethod.GET)
    public List<ProductionKindsModel> getKinds(HttpServletRequest request, HttpServletResponse response) {

        List<ProductionKindsModel> kinds = lamuService.getKinds();
        return kinds;
    }

    /**
     * 分页获取产品列表
     *
     * @param response
     * @param request
     * @param condition 分页条件。note 里面可能包含了当前页，每页显示的条数，group by 参数
     * @return
     */

    @RequestMapping(value = "page", method = RequestMethod.GET)
    public PageInfo<ProductionModel> getProductionByPage(String name, Date date, Integer curPage, Integer pageSize) {
        PageInfo<ProductionModel> allLamusByCondition = lamuService.getAllLamusByCondition(name, date, curPage, pageSize);
        return allLamusByCondition;
    }

    @RequestMapping(value = "test")
    public Object test() {
        throw new PreConditionException("sorry,请测试！");
    }

}
