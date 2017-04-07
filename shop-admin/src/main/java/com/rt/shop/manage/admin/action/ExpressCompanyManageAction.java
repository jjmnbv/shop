 package com.rt.shop.manage.admin.action;
 
 import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.plugins.Page;
import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.rt.shop.common.annotation.SecurityMapping;
import com.rt.shop.common.tools.CommUtil;
import com.rt.shop.entity.Address;
import com.rt.shop.entity.ExpressCompany;
import com.rt.shop.entity.OrderForm;
import com.rt.shop.entity.SysConfig;
import com.rt.shop.entity.query.ExpressCompanyQueryObject;
import com.rt.shop.manage.util.WebForm;
import com.rt.shop.mv.JModelAndView;
import com.rt.shop.service.IExpressCompanyService;
import com.rt.shop.service.IOrderFormService;
import com.rt.shop.service.ISysConfigService;
import com.rt.shop.service.IUserConfigService;
import com.rt.shop.util.CommWebUtil;
 
 @Controller
 public class ExpressCompanyManageAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IExpressCompanyService expresscompanyService;
 
   @Autowired
   private IOrderFormService orderFormService;
 
   @SecurityMapping(display = false, rsequence = 0, title="快递设置", value="/admin/set_kuaidi.htm*", rtype="admin", rname="快递设置", rcode="admin_set_kuaidi", rgroup="设置")
   @RequestMapping({"/admin/set_kuaidi.htm"})
   public ModelAndView set_kuaidi(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView("admin/blue/set_kuaidi.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="保存快递设置", value="/admin/set_kuaidi_save.htm*", rtype="admin", rname="快递设置", rcode="admin_set_kuaidi", rgroup="设置")
   @RequestMapping({"/admin/set_kuaidi_save.htm"})
   public ModelAndView set_kuaidi_save(HttpServletRequest request, HttpServletResponse response, String id, String kuaidi_id)
   {
     SysConfig obj = this.configService.getSysConfig();
     WebForm wf = new WebForm();
     SysConfig config = null;
     if (id.equals("")) {
       config = (SysConfig)wf.toPo(request, SysConfig.class);
       config.setAddTime(new Date());
     } else {
       config = (SysConfig)wf.toPo(request, obj);
     }
     config.setKuaidi_id(kuaidi_id);
     if (id.equals(""))
       this.configService.insertSelective(config);
     else {
       this.configService.updateSelectiveById(config);
     }
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("op_title", "快递设置成功");
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/set_kuaidi.htm");
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="快递公司列表", value="/admin/express_company_list.htm*", rtype="admin", rname="快递公司", rcode="admin_express_company", rgroup="设置")
   @RequestMapping({"/admin/express_company_list.htm"})
   public ModelAndView express_company_list(HttpServletRequest request, HttpServletResponse response, String currentPage, String orderBy, String orderType)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/express_company_list.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     String url = this.configService.getSysConfig().getAddress();
     if ((url == null) || (url.equals(""))) {
       url = CommUtil.getURL(request);
     }
     String params = "";
     ExpressCompanyQueryObject qo = new ExpressCompanyQueryObject(
       currentPage, mv, "company_sequence", "asc");
     Page pList = this.expresscompanyService.selectPage(new Page<ExpressCompany>(Integer.valueOf(CommUtil.null2Int(currentPage)), 12), null);
    
     CommWebUtil.saveIPageList2ModelAndView(url + 
       "/admin/expresscompany_list.htm", "", params, pList, mv);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="快递公司添加", value="/admin/express_company_add.htm*", rtype="admin", rname="快递公司", rcode="admin_express_company", rgroup="设置")
   @RequestMapping({"/admin/express_company_add.htm"})
   public ModelAndView express_company_add(HttpServletRequest request, HttpServletResponse response)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/express_company_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="快递公司编辑", value="/admin/express_company_edit.htm*", rtype="admin", rname="快递公司", rcode="admin_express_company", rgroup="设置")
   @RequestMapping({"/admin/express_company_edit.htm"})
   public ModelAndView express_company_edit(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "admin/blue/express_company_add.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     if ((id != null) && (!id.equals(""))) {
       ExpressCompany expresscompany = this.expresscompanyService
         .selectById(Long.valueOf(Long.parseLong(id)));
       mv.addObject("obj", expresscompany);
       mv.addObject("currentPage", currentPage);
       mv.addObject("edit", Boolean.valueOf(true));
     }
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="快递公司保存", value="/admin/express_company_save.htm*", rtype="admin", rname="快递公司", rcode="admin_express_company", rgroup="设置")
   @RequestMapping({"/admin/express_company_save.htm"})
   public ModelAndView express_company_save(HttpServletRequest request, HttpServletResponse response, String id, String currentPage)
   {
     WebForm wf = new WebForm();
     ExpressCompany expresscompany = null;
     if (id.equals("")) {
       expresscompany = (ExpressCompany)wf.toPo(request, ExpressCompany.class);
       expresscompany.setAddTime(new Date());
     } else {
       ExpressCompany obj = this.expresscompanyService.selectById(
         Long.valueOf(Long.parseLong(id)));
       expresscompany = (ExpressCompany)wf.toPo(request, obj);
     }
     if (id.equals(""))
       this.expresscompanyService.insertSelective(expresscompany);
     else
       this.expresscompanyService.updateSelectiveById(expresscompany);
     ModelAndView mv = new JModelAndView("admin/blue/success.html", 
       this.configService.getSysConfig(), this.userConfigService
       .getUserConfig(), 0, request, response);
     mv.addObject("list_url", CommUtil.getURL(request) + 
       "/admin/express_company_list.htm");
     mv.addObject("op_title", "保存快递公司成功");
     mv.addObject("add_url", CommUtil.getURL(request) + 
       "/admin/express_company_add.htm?currentPage=" + currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="快递公司删除", value="/admin/express_company_del.htm*", rtype="admin", rname="快递公司", rcode="admin_express_company", rgroup="设置")
   @RequestMapping({"/admin/express_company_del.htm"})
   public String express_company_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String currentPage)
   {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         ExpressCompany ec = this.expresscompanyService.selectById(
           Long.valueOf(Long.parseLong(id)));
         for (OrderForm of : ec.getOfs()) {
           of.setEc_id(null);
           this.orderFormService.updateSelectiveById(of);
         }
         this.expresscompanyService.deleteById(Long.valueOf(Long.parseLong(id)));
       }
     }
     return "redirect:express_company_list.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="快递公司Ajax更新数据", value="/admin/express_company_ajax.htm*", rtype="admin", rname="快递公司", rcode="admin_express_company", rgroup="设置")
   @RequestMapping({"/admin/express_company_ajax.htm"})
   public void express_company_ajax(HttpServletRequest request, HttpServletResponse response, String id, String fieldName, String value) throws ClassNotFoundException {
     ExpressCompany obj = this.expresscompanyService.selectById(
       Long.valueOf(Long.parseLong(id)));
     Field[] fields = ExpressCompany.class.getDeclaredFields();
     BeanWrapper wrapper = new BeanWrapper(obj);
     Object val = null;
     for (Field field : fields)
     {
       if (field.getName().equals(fieldName)) {
         Class clz = Class.forName("java.lang.String");
         if (field.getType().getName().equals("int")) {
           clz = Class.forName("java.lang.Integer");
         }
         if (field.getType().getName().equals("boolean")) {
           clz = Class.forName("java.lang.Boolean");
         }
         if (!value.equals(""))
           val = BeanUtils.convertType(value, clz);
         else {
           val = Boolean.valueOf(
             !CommUtil.null2Boolean(wrapper
             .getPropertyValue(fieldName)));
         }
         wrapper.setPropertyValue(fieldName, val);
       }
     }
     this.expresscompanyService.updateSelectiveById(obj);
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(val.toString());
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 
   @RequestMapping({"/admin/express_company_mark.htm"})
   public void express_company_mark(HttpServletRequest request, HttpServletResponse response, String company_mark, String id)
   {
    
     String sql="where company_mark='"+company_mark.trim()+"' and id!="+CommUtil.null2Long(id);
     List ecs = this.expresscompanyService.selectList(sql,null);

     boolean ret = true;
     if (ecs.size() > 0) {
       ret = false;
     }
     response.setContentType("text/plain");
     response.setHeader("Cache-Control", "no-cache");
     response.setCharacterEncoding("UTF-8");
     try
     {
       PrintWriter writer = response.getWriter();
       writer.print(ret);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
 }


 
 
 