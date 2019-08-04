package top.crossoverjie.nows.nows.util;

import okhttp3.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import top.crossoverjie.nows.nows.filter.AbstractFilterProcess;
import top.crossoverjie.nows.nows.filter.FixPicFilterProcessManager;
import top.crossoverjie.nows.nows.thread.ScanTask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class DownloadPicTest {

    @Test
    public void download() throws Exception {

        String path = "/Users/chenjie/Documents/blog-img/006tNc79ly1g36zsmm4yvj30yg0h8n27.jpg";

        DownloadUploadPic.download("http://ww1.sinaimg.cn/large/006tNc79ly1g36zsmm4yvj30yg0h8n27.jpg", path);

        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), new File(path));
        OkHttpClient httpClient = new OkHttpClient().newBuilder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("smfile", "i", fileBody)

                .build();


        Request request = new Request.Builder()
                .url("https://sm.ms/api/upload")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36")
                .post(requestBody)
                .build();

        Response response = httpClient.newCall(request).execute();
        System.out.println(response.body().string());
    }


    @Test
    public void download2() throws Exception {
        //        String path = "/Users/yanjie/GitHub/deppwang.github.io/images2/20Extreme9Box_575px.jpg";
//        String path = "/Users/yanjie/GitHub/deppwang.github.io/images2/b4c1da1ea127ae27.jpg";
        String path = "E:/GitHub/Blog/images2/fayuan_logo.png";
//        DownloadUploadPic.download("http://mmbiz.qpic.cn/mmbiz_png/qdzZBE73hWsbhfAng9ibqfcbjrqgyRWqAhJichVhow2eIVjZwNwzdsmksrFicRibsluTYFPXreCVnxy4QUEkAGFQtw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1", path);
        DownloadUploadPic.download("http://upload-images.jianshu.io/upload_images/5863464-b4c1da1ea127ae27.png", path);
//        DownloadUploadPic.download("http://10.1.234.99:8406/trial/images/login/fayuan_logo.png", path);
    }

    @Test
    public void str() {
        String path = "https://i.loli.net/2019/05/05/5ccef1ffd774f.jpg";

        int index = path.lastIndexOf("/");
        path = path.substring(index + 1);
        System.out.println(path);
    }

    @Test
    public void str2() {
        String path = "/Users/chenjie/Documents/Hexo/source/_posts/distributed/distributed-discovery-zk.md";

        int index = path.lastIndexOf(System.getProperty("file.separator"));
        path = path.substring(index + 1);
        System.out.println(path);
    }

    @Autowired
    private ExecutorService executorService;

    private AbstractFilterProcess filterProcessManager;

    @Test
    public void downPic() {
        filterProcessManager = SpringBeanFactory.getBean(FixPicFilterProcessManager.class);
        executorService.execute(new ScanTask("/Users/yanjie/GitHub/deppwang.github.io/source/_posts/20170611-Hexo搭建博客系列：（一）Hexo安装与添加NexT主题.md", filterProcessManager));
    }

    @Test
    public void uploadPic() throws Exception {
        String path = "/Users/yanjie/GitHub/amyyanjie.github.io/images/20171122-binary-tree-traversal-note---2405011-5f5b0b136713f744.jpg";
        String s = DownloadUploadPic.upload(path, 0);
        System.out.println(s);
    }

}