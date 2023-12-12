package com.hanzaitu.admin.web.controller.system;


import com.hanzaitu.admin.system.domain.AiDrawQuadrat;
import com.hanzaitu.admin.system.domain.AiDrawQuadratCheck;
import com.hanzaitu.admin.system.domain.AiDrawQuadratLabel;
import com.hanzaitu.admin.system.domain.dto.AiDrawQuadratDto;
import com.hanzaitu.admin.system.domain.dto.DrawCheckDto;
import com.hanzaitu.admin.system.service.IAiDrawQuadratCheckService;
import com.hanzaitu.admin.system.service.IAiDrawQuadratLabelService;
import com.hanzaitu.admin.system.service.IAiDrawQuadratService;
import com.hanzaitu.admin.system.service.ISysDrawService;
import com.hanzaitu.admin.web.controller.common.BaseController;
import com.hanzaitu.common.core.domain.AjaxResult;
import com.hanzaitu.common.service.NotifyMsgCommonService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("draw")
public class SysDrawController extends BaseController {


    @Autowired
    private ISysDrawService sysDrawService;
    @Autowired
    private IAiDrawQuadratService aiDrawQuadratService;
    @Autowired
    private IAiDrawQuadratCheckService aiDrawQuadratCheckService;
    @Autowired
    private IAiDrawQuadratLabelService aiDrawQuadratLabelService;

    @GetMapping("/check")
    public AjaxResult checkDraw(@Validated @RequestBody DrawCheckDto drawCheckDto) {
        return sysDrawService.checkDraw(drawCheckDto);
    }


    //已经上架的图列表接口
    @ApiOperation(value = "获取已经上架的图列表", httpMethod = "POST")
    @PostMapping("/getTaskList")
    public AjaxResult getTaskList(Integer labelId){
        aiDrawQuadratService.selectList(labelId);
        return AjaxResult.success();
    }

    //所有图列表接口（已审核上架，未审核上架）
    @ApiOperation(value = "获取所有图的列表（包括还未审核上架的）", httpMethod = "POST")
    @PostMapping("/getAllList")
    public AjaxResult getAllList(Integer labelId){
        List<AiDrawQuadrat> quadrats = aiDrawQuadratService.selectList(labelId);
        List<AiDrawQuadratDto> dtoList = new ArrayList<>();
        for (AiDrawQuadrat quadrat : quadrats) {
            AiDrawQuadratDto dto = new AiDrawQuadratDto();
            dto.setUserId(quadrat.getUserId());
            dto.setTaskId(quadrat.getTaskId());
            dto.setImgUrl(quadrat.getImgUrl());
            dto.setPrompt(quadrat.getPrompt());
            dto.setCheckStatus("1");//1为审核通过
            dtoList.add(dto);
        }
        List<AiDrawQuadratCheck> checks = aiDrawQuadratCheckService.selectNotPassList();
        for (AiDrawQuadratCheck check : checks) {
            AiDrawQuadratDto dto = new AiDrawQuadratDto();
            dto.setUserId(check.getUserId());
            dto.setTaskId(check.getTaskId());
        }
        return AjaxResult.success(dtoList);
    }

    /**
     *
     * @param check   需要进行上下架操作的对象
     * @param status  上下架参数，on为上架，off为下架
     * @return
     */
    @ApiOperation(value = "上下架", httpMethod = "PUT")
    @PutMapping("/Shelf")
    public AjaxResult shelf(@RequestBody AiDrawQuadratCheck check,@RequestParam String status){
        aiDrawQuadratCheckService.shelf(check,status);
        return AjaxResult.success();
    }

    //获取分类列表
    @ApiOperation(value = "获取图分类列表", httpMethod = "GET")
    @GetMapping("/labelList")
    public AjaxResult getlabelList(){
        return AjaxResult.success(aiDrawQuadratLabelService.selectList());
    }

    //删除分类
    @ApiOperation(value = "删除分类", httpMethod = "DELETE")
    @DeleteMapping("/deleteLabel")
    public AjaxResult deleteLabel(Long id){
        aiDrawQuadratLabelService.deleteOne(id);
        return AjaxResult.success();
    }

    //修改分类
    @ApiOperation(value = "修改分类", httpMethod = "POST")
    @PostMapping("/updateLabel")
    public AjaxResult updateLabel(@RequestBody AiDrawQuadratLabel label){
        aiDrawQuadratLabelService.updateLabel(label);
        return AjaxResult.success();
    }

    //新增分类
    @ApiOperation(value = "新增分类", httpMethod = "POST")
    @PostMapping("/addLabel")
    public AjaxResult addLabel(@RequestBody AiDrawQuadratLabel label){
        aiDrawQuadratLabelService.addLabel(label);
        return AjaxResult.success();
    }

}

