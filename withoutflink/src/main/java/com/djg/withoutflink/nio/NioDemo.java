package com.djg.withoutflink.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Map;

public class NioDemo {
    @Test
    public void client() throws IOException {
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",7498));

        // 切换成非 阻塞模式
        socketChannel.configureBlocking(false);

        FileChannel inputChannel = FileChannel.open(Paths.get("/Users/djg/Downloads/demo.jpg"), StandardOpenOption.READ);

        ByteBuffer clientBuffer = ByteBuffer.allocate(1024);

        while (inputChannel.read(clientBuffer) != -1){
            clientBuffer.flip();
            socketChannel.write(clientBuffer);
            clientBuffer.clear();
        }
        socketChannel.close();
        inputChannel.close();
    }


    @Test
    public void server() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 非阻塞
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(7498));

        FileChannel outputChannel = FileChannel.open(Paths.get("/Users/djg/Downloads/demo2.jpg"),StandardOpenOption.WRITE,StandardOpenOption.CREATE);


        // 选择器
        Selector selector = Selector.open();

        // 将通道注册到选择器上，并制定监听事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 轮巡式获得选择器里的已经准备就绪的事件
        while (selector.select() > 0 ){

            // 获取已经就绪的监听事件
            Iterator<SelectionKey> selectorIterator =  selector.selectedKeys().iterator();

            // 迭代获取
            while (selectorIterator.hasNext()){
                // 获取准备就绪的事件

                SelectionKey key = selectorIterator.next();

                SocketChannel socketChannel = null;
                // 判断是什么事件
                if (key.isAcceptable()){
                    // 或接受就绪，，则获取客户端连接
                    // 这一步，相当于完成了客户端和服务端的TCP三次握手，TCP物理链路正式建立
                    socketChannel = serverSocketChannel.accept();

                    //切换非阻塞方式
                    socketChannel.configureBlocking(false);
                    // 注册到选择器上
                    socketChannel.register(selector,SelectionKey.OP_READ);
                } else if (key.isReadable()){
                    // 获取读就绪通道
                    SocketChannel readChannel = (SocketChannel) key.channel();

                    readChannel.configureBlocking(false);
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);

                    int len = 0;
                    while ( (len = readChannel.read(readBuffer)) != -1){
                        readBuffer.flip();
                        System.out.println(new String(readBuffer.array(),0,len));
                        outputChannel.write(readBuffer);
                        readBuffer.clear();
                    }
                    readChannel.close();
                    outputChannel.close();

                }
            }

            // 取消选择键
            selectorIterator.remove();
        }
    }

}
