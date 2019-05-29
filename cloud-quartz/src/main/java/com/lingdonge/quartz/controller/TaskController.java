//package com.kyle.quartz.controller;
//
//package com.zealer.cps.task.controller;
//
//import java.text.SimpleDateFormat;
//import java.token.Date;
//import java.token.HashMap;
//import java.token.Map;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//
//import com.kyle.quartz.entity.ScheduleJob;
//import QuartzTaskService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//@Controller
//@RequestMapping( "/taskController" )
//public class TaskController
//{
//    private static Logger log = LoggerFactory.getLogger( TaskController.class );
//
//    @Autowired
//    private QuartzTaskService taskService;
//
//    @RequestMapping( "/list" )
//    public String listJob( @ModelAttribute("job") ScheduleJobReq jobReq, Model bean, HttpServletRequest request )
//    {
//        PaginationBean<ScheduleJob> pb = quartzJobService.getJobsByPage( jobReq );
//        try {
//            pb.setUrl( HttpUtils.getRequestInfo( request, true ) );
//        } catch ( Exception e ) {
//            log.error( "get request url error", e );
//        }
//        bean.addAttribute( "pb", pb );
//        return("task/taskList");
//    }
//
//
//    /**
//     * 立即执行定时任务
//     * @param job 任务实体
//     * @param bean
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping( value = "/executeJob", produces = "application/json;charset=utf-8" )
//    public ResponseEntity<Map<String, Object> > executeJob( ScheduleJob job, Model bean )
//    {
//        jobMethod.runJobNow( job );
//        return(new ResponseEntity<Map<String, Object> > ( new HashMap<String, Object>(), HttpStatus.OK ) );
//    }
//
//
//    /**
//     * 跳转到添加定时任务的页面
//     * @param bean
//     *            储存结果的实体
//     */
//    @RequestMapping( value = "/addJob", method = RequestMethod.GET )
//    public String addForm( Model bean )
//    {
//        bean.addAttribute( "job", new ScheduleJob() );
//        return("task/addJob");
//    }
//
//
//    /**
//     * 添加定时任务记录
//     * @param job 任务实体
//     */
//    @RequestMapping( value = "/addJob", method = RequestMethod.POST )
//    public String addUser( @ModelAttribute("job") ScheduleJob job, RedirectAttributes ra, Model bean,
//                           HttpServletRequest request )
//    {
//        SimpleDateFormat format = new SimpleDateFormat( AppConstant.DATE_FORMAT_YYYYMMDDHHMMSS );
//        job.setCreateTime( format.format( new Date() ) );
//        quartzJobService.inserJob( job );
//        ra.addFlashAttribute( "actionResult", new SuccessActionResult() );
//        return("redirect:/taskController/list.do");
//    }
//
//
//    /**
//     * 初始化修改表单
//     * @param jobId
//     * @return 跳转地址
//     */
//    @RequestMapping( value = "/updateJob", method = RequestMethod.GET )
//    public String updateForm( @RequestParam("id") Integer jobId, Model bean,
//                              HttpServletRequest request )
//    {
//        ScheduleJob job = quartzJobService.getScheduleJobById( jobId );
//        bean.addAttribute( "job", job );
//        return("task/updateJob");
//    }
//
//
//    /**
//     * 修改定时任务记录信息
//     * @param job 待修改的操作员实体
//     * @param bean 封装处理结果的实体
//     * @param request 请求对象
//     * @return 跳转地址
//     */
//    @RequestMapping( value = "/updateJob", method = RequestMethod.POST )
//    public String updateJob( @ModelAttribute ScheduleJob job, RedirectAttributes ra, Model bean,
//                             HttpServletRequest request )
//    {
//        SimpleDateFormat format = new SimpleDateFormat( AppConstant.DATE_FORMAT_YYYYMMDDHHMMSS );
//        job.setUpdateTime( format.format( new Date() ) );
//        quartzJobService.updateJob( job );
//        ra.addFlashAttribute( "actionResult", new SuccessActionResult() );
//        return("redirect:/taskController/list.do");
//    }
//
//
//    /**
//     * 删除一条定时任务记录信息
//     * @return
//     */
//    @RequestMapping( value = "/deleteJob" )
//    public String deleteJob( @RequestParam("id") int jobId, RedirectAttributes ra )
//    {
//        quartzJobService.deleteJob( jobId );
//        ra.addFlashAttribute( "actionResult", new SuccessActionResult() );
//        return("redirect:/taskController/list.do");
//    }
//
//    /**
//     * 校验执行任务的表达式是否正确
//     * @param expression
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping( value = "/checkExp", produces = "application/json;charset=utf-8" )
//    public ResponseEntity<Map<String, Object> > checkExpression( String expression )
//    {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put( AppConstant.SYSTEM_JSON_CODE, AppConstant.SYSTEM_JSON_ERROR );
//        if ( jobMethod.checkCron( expression ) )
//        {
//            map.put( AppConstant.SYSTEM_JSON_CODE, AppConstant.SYSTEM_JSON_SUCCESS );
//        }
//        return(new ResponseEntity<Map<String, Object> > ( map, HttpStatus.OK ) );
//    }
//
//
//    /**
//     * 某个定时任务下的所有执行记录信息列表
//     * @param jobReq
//     * @return
//     */
//    @RequestMapping( "/itemJob" )
//    public String executeJobList( @ModelAttribute("job") ScheduleJobReq jobReq, int jobId,
//                                  Model bean, HttpServletRequest request )
//    {
//        PaginationBean<ScheduleJobItem> pb = quartzJobService.getJobItemsByPage( jobId, jobReq );
//        try {
//            pb.setUrl( HttpUtils.getRequestInfo( request, true ) );
//        } catch ( Exception e ) {
//            log.error( "get request url error", e );
//        }
//        bean.addAttribute( "pb", pb );
//        bean.addAttribute( "jobId", jobId );
//        return("task/taskItemList");
//    }
//}