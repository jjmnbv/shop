 package com.rt.shop.view.admin.sellers.action;
 
 import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.plugins.Page;
import com.rt.shop.common.annotation.SecurityMapping;
import com.rt.shop.common.tools.CommUtil;
import com.rt.shop.domain.virtual.SysMap;
import com.rt.shop.entity.Consult;
import com.rt.shop.entity.Template;
import com.rt.shop.entity.User;
import com.rt.shop.entity.query.ConsultQueryObject;
import com.rt.shop.mv.JModelAndView;
import com.rt.shop.service.IConsultService;
import com.rt.shop.service.ISysConfigService;
import com.rt.shop.service.ITemplateService;
import com.rt.shop.service.IUserConfigService;
import com.rt.shop.service.IUserService;
import com.rt.shop.tools.MsgTools;
import com.rt.shop.util.CommWebUtil;
import com.rt.shop.util.SecurityUserHolder;
 
 @Controller
 public class ConsultSellerAction
 {
 
   @Autowired
   private ISysConfigService configService;
 
   @Autowired
   private IUserConfigService userConfigService;
 
   @Autowired
   private IConsultService consultService;
 
   @Autowired
   private IUserService userService;
 
   @Autowired
   private ITemplateService templateService;
 
   @Autowired
   private MsgTools msgTools;
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询列表", value="/seller/consult.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult.htm"})
   public ModelAndView consult(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage)
   {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/consult.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv, 
       "addTime", "desc");
     if (!CommUtil.null2String(reply).equals("")) {
       qo.addQuery("obj.reply", 
         new SysMap("reply", 
         Boolean.valueOf(CommUtil.null2Boolean(reply))), "=");
     }
     User user = this.userService.selectById(
       SecurityUserHolder.getCurrentUser().getId());
     qo.addQuery("obj.goods.goods_store.id", 
       new SysMap("store_id", user
       .getStore_id()), "=");
     Page pList = this.consultService.selectPage(new Page<Consult>(Integer.valueOf(CommUtil.null2Int(currentPage)), 12), null);
     CommWebUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
     mv.addObject("reply", CommUtil.null2String(reply));
     return mv;
   }
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询回复", value="/seller/consult_reply.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult_reply.htm"})
   public ModelAndView consult_reply(HttpServletRequest request, HttpServletResponse response, String id, String currentPage) {
     ModelAndView mv = new JModelAndView(
       "user/default/usercenter/consult_reply.html", this.configService
       .getSysConfig(), 
       this.userConfigService.getUserConfig(), 0, request, response);
     Consult obj = this.consultService.selectById(CommUtil.null2Long(id));
     mv.addObject("obj", obj);
     mv.addObject("currentPage", currentPage);
     return mv;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询回复保存", value="/seller/consult_reply_save.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult_reply_save.htm"})
   public String consult_reply_save(HttpServletRequest request, HttpServletResponse response, String id, String consult_reply, String currentPage) throws Exception {
     Consult obj = this.consultService.selectById(CommUtil.null2Long(id));
     obj.setConsult_reply(consult_reply);
     obj.setReply_time(new Date());
     obj.setReply_user(SecurityUserHolder.getCurrentUser());
     obj.setReply(true);
     this.consultService.updateSelectiveById(obj);
     send_email(request, obj, "email_tobuyer_cousult_reply_notify");
     return "redirect:consult.htm?currentPage=" + currentPage;
   }
 
   @SecurityMapping(display = false, rsequence = 0, title="卖家咨询删除", value="/seller/consult_del.htm*", rtype="seller", rname="咨询管理", rcode="consult_seller", rgroup="客户服务")
   @RequestMapping({"/seller/consult_del.htm"})
   public String consult_del(HttpServletRequest request, HttpServletResponse response, String mulitId, String consult_reply, String currentPage) {
     String[] ids = mulitId.split(",");
     for (String id : ids) {
       if (!id.equals("")) {
         this.consultService.deleteById(CommUtil.null2Long(id));
       }
     }
     return "redirect:consult.htm?currentPage=" + currentPage;
   }
 
   private void send_email(HttpServletRequest request, Consult obj, String mark) throws Exception
   {
	   Template sTemplate=new Template();
	   sTemplate.setMark(mark);
     Template template = this.templateService.selectOne(sTemplate);
     if (template.getOpen()) {
       String email = obj.getConsult_email();
       String subject = template.getTitle();
       String path = request.getSession().getServletContext()
         .getRealPath("/") + 
         "/vm/";
       PrintWriter pwrite = new PrintWriter(
         new OutputStreamWriter(new FileOutputStream(path + "msg.vm", false), "UTF-8"));
       pwrite.print(template.getContent());
       pwrite.flush();
       pwrite.close();
 
       Properties p = new Properties();
       p.setProperty("file.resource.loader.path", request
         .getRealPath("/") + 
         "vm" + File.separator);
       p.setProperty("input.encoding", "UTF-8");
       p.setProperty("output.encoding", "UTF-8");
       Velocity.init(p);
       org.apache.velocity.Template blank = Velocity.getTemplate("msg.vm", 
         "UTF-8");
       VelocityContext context = new VelocityContext();
       context.put("buyer", obj.getConsult_user());
       context.put("config", this.configService.getSysConfig());
       context.put("send_time", CommUtil.formatLongDate(new Date()));
       context.put("webPath", CommUtil.getURL(request));
       context.put("goods", obj.getGoods());
       StringWriter writer = new StringWriter();
       blank.merge(context, writer);
 
       String content = writer.toString();
       this.msgTools.sendEmail(email, subject, content);
     }
   }
 }


 
 
 