package com.qimok.sharding.aop;

import com.qimok.sharding.aop.annotation.MultiDataSourceTransactional;
import javafx.util.Pair;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.util.Stack;
import static com.qimok.sharding.aop.MultiDataSourceTransactionAspect.RollbackOrCommit.COMMIT;
import static com.qimok.sharding.aop.MultiDataSourceTransactionAspect.RollbackOrCommit.ROLLBACK;

/**
 * 多数据源事务管理切面
 *
 * @author qimok
 * @since 2020-08-20
 */
@Aspect
@Component
public class MultiDataSourceTransactionAspect {

    enum RollbackOrCommit {
        COMMIT, ROLLBACK
    }

    /**
     * 线程本地变量
     */
    private static final ThreadLocal<Stack<Pair<PlatformTransactionManager, TransactionStatus>>> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 用于获取事务管理器
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 事务声明
     */
    private DefaultTransactionDefinition dtd = new DefaultTransactionDefinition();

    {
        // 非只读模式
        dtd.setReadOnly(false);
        // 事务隔离级别：采用数据库的
        dtd.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
        // 事务传播行为
        dtd.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    }

    /**
     * 切面
     */
    @Pointcut("@annotation(com.qimok.sharding.aop.annotation.MultiDataSourceTransactional)")
    public void pointcut() {
    }

    /**
     * 声明事务
     *
     * @param transactional 注解
     */
    @Before("pointcut() && @annotation(transactional)")
    public void before(MultiDataSourceTransactional transactional) {
        // 根据设置的事务的名称顺序声明，并放到 ThreadLocal 里
        String[] transactionManagerNames = transactional.transactionManagers();
        Stack<Pair<PlatformTransactionManager, TransactionStatus>> pairStack = new Stack<>();
        for (String transactionManagerName : transactionManagerNames) {
            PlatformTransactionManager transactionManager = applicationContext
                    .getBean(transactionManagerName, PlatformTransactionManager.class);
            TransactionStatus transactionStatus = transactionManager.getTransaction(dtd);
            pairStack.push(new Pair(transactionManager, transactionStatus));
        }
        THREAD_LOCAL.set(pairStack);
    }

    /**
     * 提交事务
     */
    @AfterReturning("pointcut()")
    public void afterReturning() {
        rollbackOrCommitTransaction(COMMIT);
    }

    /**
     * 回滚事务
     */
    @AfterThrowing(value = "pointcut()")
    public void afterThrowing() {
        rollbackOrCommitTransaction(ROLLBACK);
    }

    private void rollbackOrCommitTransaction(RollbackOrCommit rollbackOrCommit) {
        Stack<Pair<PlatformTransactionManager, TransactionStatus>> pairStack = THREAD_LOCAL.get();
        while (!pairStack.empty()) {
            // 栈顶弹出（后进先出）
            Pair<PlatformTransactionManager, TransactionStatus> pair = pairStack.pop();
            if (rollbackOrCommit.equals(COMMIT)) {
                pair.getKey().commit(pair.getValue());
            } else {
                pair.getKey().rollback(pair.getValue());
            }
        }
        THREAD_LOCAL.remove();
    }

}