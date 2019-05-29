package com.lingdonge.auth.jwt;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;

/**
 * 创建StatelessDefaultSubjectFactory,关闭session的创建
 * 如果之后调用Subject.getSession()将抛出DisabledSessionException异常
 */
@Slf4j
public class StatelessDefaultSubjectFactory extends DefaultWebSubjectFactory {

    @Override
    public Subject createSubject(SubjectContext context) {

//        logger.info("<<<<<<<<<<<<<< Shiro 不创建Session设置 >>>>>>>>>>");

//        //不创建session
//        context.setSessionCreationEnabled(false);
//        Subject ct = super.createSubject(context);
//        ct.getSession(false);
//        return ct;

        // 当Token无状态时，才不用Session
        AuthenticationToken token = context.getAuthenticationToken();
        if ((token instanceof StatelessToken)) {
            context.setSessionCreationEnabled(false);
        }
        return super.createSubject(context);
    }
}
