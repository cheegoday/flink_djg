import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {



    public static void main(String[] args) throws InterruptedException {
        Object obj = new Object();
        ReferenceQueue queue = new ReferenceQueue();
        PhantomReference reference = new PhantomReference(obj, queue);
        System.out.println("queue:" + queue.poll());
        obj = null;
        System.gc();
        // JVM将虚引用塞入pending队列需要一个时间，不是立马就塞进去
        Thread.sleep(2000);
        System.out.println("-----after gc-----------");
        System.out.println("queue:" + queue.poll());

    }
}
