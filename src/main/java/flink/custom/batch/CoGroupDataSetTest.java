package flink.custom.batch;

import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;

public class CoGroupDataSetTest {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        //注意：可启用这行代码看区别
        //env.setParallelism(1);

        DataSet<Tuple2<Long, String>> source1 = env.fromElements(
                Tuple2.of(1L, "xiaoming"),
                Tuple2.of(2L, "xiaowang"));

        DataSet<Tuple2<Long, String>> source2 = env.fromElements(
                Tuple2.of(2L, "xiaoli"),
                Tuple2.of(1L, "shinelon"),
                Tuple2.of(2L, "xiaohong"),
                Tuple2.of(3L, "hhhhhh"));


        source2.sortPartition(0, Order.ASCENDING).print();
        //(1,shinelon)
        //(2,xiaoli)
        //(2,xiaohong)
        //(3,hhhhhh)
        System.out.println("------");

        //先按第一个字段排升序，再按第二个字段排升序，= order by c1,c2 ;默认asc;
        source2.sortPartition(0, Order.ASCENDING).sortPartition(1, Order.ASCENDING).print();
        //(1,shinelon)
        //(2,xiaohong)
        //(2,xiaoli)
        //(3,hhhhhh)

        System.out.println("------");

        //取前2个元素
        source2.first(2).print();
        //(2,xiaoli)
        //(1,shinelon)

        System.out.println("------");

        source2.groupBy(1).sortGroup(1, Order.ASCENDING).first(2).print();
        //(3,hhhhhh)
        //(1,shinelon)
        //(2,xiaohong)
        //(2,xiaoli)

        System.out.println("------");

        source2.groupBy(0).sortGroup(0, Order.ASCENDING).first(2).print();
        //(1,shinelon)
        //(2,xiaoli)
        //(2,xiaohong)
        //(3,hhhhhh)

        System.out.println("------");

        source2.groupBy(0).sortGroup(0, Order.ASCENDING).first(1).print();
        //默认12个并行度
        //(3,hhhhhh)
        //(1,shinelon)
        //(2,xiaoli)

        //1个并行度
        //(1,shinelon)
        //(2,xiaoli)
        //(3,hhhhhh)

        System.out.println("------");

        //按第一个字段分组，每个组内按第二个字段升序排序，每个组取都第一条记录
        source2.groupBy(0).sortGroup(1, Order.ASCENDING).first(1).print();
        //默认12个并行度（线程），局部有序，相同key的元素放在同一个线程下运行。
        //(3,hhhhhh)
        //(1,shinelon)
        //(2,xiaohong)

        //1个并行度，全局有序，
        //(1,shinelon)
        //(2,xiaohong)
        //(3,hhhhhh)

    }

}