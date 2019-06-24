package com.lingdonge.http.webmagic.webmagic.scheduler;


import com.lingdonge.http.webmagic.Request;
import com.lingdonge.http.webmagic.Task;
import com.lingdonge.http.webmagic.scheduler.DuplicateRemovedScheduler;
import org.junit.Test;

/**
 * @author code4crafter@gmail.com
 * Date: 17/3/11
 * Time: 上午11:26
 */
//@RunWith(MockitoJUnitRunner.class)
public class DuplicateRemovedSchedulerTest {

    private DuplicateRemovedScheduler duplicateRemovedScheduler = new DuplicateRemovedScheduler() {
        @Override
        public Request poll(Task task) {
            return null;
        }
    };

    @Test
    public void test_no_duplicate_removed_for_post_request() throws Exception {
//        DuplicateRemover duplicateRemover = Mockito.mock(DuplicateRemover.class);
//        duplicateRemovedScheduler.setDuplicateRemover(duplicateRemover);
//        Request request = new Request("https://www.google.com/");
//        request.setMethod(HttpConstant.Method.POST);
//        duplicateRemovedScheduler.push(request, null);
//        verify(duplicateRemover,times(0)).isDuplicate(any(Request.class),any(Task.class));
    }

    @Test
    public void test_duplicate_removed_for_get_request() throws Exception {
//        DuplicateRemover duplicateRemover = Mockito.mock(DuplicateRemover.class);
//        duplicateRemovedScheduler.setDuplicateRemover(duplicateRemover);
//        Request request = new Request("https://www.google.com/");
//        request.setMethod(HttpConstant.Method.GET);
//        duplicateRemovedScheduler.push(request, null);
//        verify(duplicateRemover,times(1)).isDuplicate(any(Request.class),any(Task.class));
    }
}
