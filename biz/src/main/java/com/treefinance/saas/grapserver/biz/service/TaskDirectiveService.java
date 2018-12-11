package com.treefinance.saas.grapserver.biz.service;

import com.treefinance.saas.grapserver.biz.domain.Directive;
import com.treefinance.saas.grapserver.context.component.AbstractService;
import com.treefinance.saas.grapserver.manager.DirectiveManager;
import com.treefinance.saas.grapserver.manager.domain.DirectiveBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * @author luoyihua on 2017/4/26.
 */
@Service
public class TaskDirectiveService extends AbstractService {

    @Autowired
    private DirectiveManager directiveManager;

    public Directive queryNextDirective(@Nonnull Long taskId) {
        DirectiveBO directive = directiveManager.queryNextDirective(taskId);

        return convert(directive, Directive.class);
    }

    /**
     * 删除指令 数据库中的指令是只插入的,所以这里的删除指插入waiting指令
     */
    public void deleteNextDirective(Long taskId) {
        directiveManager.deleteDirective(taskId);
    }

    public void deleteNextDirective(Long taskId, String directive) {
        directiveManager.deleteDirective(taskId, directive);
    }

}
