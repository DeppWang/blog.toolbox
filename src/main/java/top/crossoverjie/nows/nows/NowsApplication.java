package top.crossoverjie.nows.nows;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.crossoverjie.nows.nows.config.AppConfig;
import top.crossoverjie.nows.nows.constants.BaseConstants;
import top.crossoverjie.nows.nows.filter.AbstractFilterProcess;
import top.crossoverjie.nows.nows.filter.FixPicFilterProcessManager;
import top.crossoverjie.nows.nows.filter.TotalSumFilterProcessManager;
import top.crossoverjie.nows.nows.scan.ScannerFile;
import top.crossoverjie.nows.nows.service.ResultService;
import top.crossoverjie.nows.nows.service.impl.fixpic.PicResultServiceImpl;
import top.crossoverjie.nows.nows.service.impl.totalsum.TotalSumResultServiceImpl;
import top.crossoverjie.nows.nows.thread.ScanTask;
import top.crossoverjie.nows.nows.util.SpringBeanFactory;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;




/**
 *
 ## 备份模式3：只下载到本地
 ## 替换模式2：备份+替换
 1. 遍历目录下所有文章，
 2. 遍历查找当前文章的每一行，看是否有图片链接，有就返回
 3. 如果链接为图床链接，过滤掉
 4. 如果链接在本地已经存在，跳过下载
 5. 但还是上传到图床，因为上传到图床的链接都被过滤掉了
 6. 上传成功，原地址与新地址映射
 7. 替换当前文章的所有图片链接
 * 替换模式只有1、2、3
 */
@SpringBootApplication
public class NowsApplication implements CommandLineRunner {


    private static Logger logger = LoggerFactory.getLogger(NowsApplication.class);

    private AbstractFilterProcess filterProcessManager;

    private ResultService resultService;

    /**
     * 要处理的文件数量
     */
    private int fileCount = 0;

    @Autowired
    private AppConfig config;

    @Autowired
    private ScannerFile scannerFile;

    @Autowired
    private ExecutorService executorService;


    public static void main(String[] args) {
        SpringApplication.run(NowsApplication.class, args);

    }

    @Override
    public void run(String... strings) throws Exception {

        if (strings.length == 1) {
            fileCount = Integer.parseInt(strings[0]);
        }

        if (config.getAppModel().equals(BaseConstants.TOTAL_WORDS)) {
            filterProcessManager = SpringBeanFactory.getBean(TotalSumFilterProcessManager.class);
            resultService = SpringBeanFactory.getBean(TotalSumResultServiceImpl.class);
            ((TotalSumResultServiceImpl) resultService).setCurrentTime();

        } else {
            filterProcessManager = SpringBeanFactory.getBean(FixPicFilterProcessManager.class);//执行过滤
            resultService = SpringBeanFactory.getBean(PicResultServiceImpl.class);
            fileCount = 100;
            ((PicResultServiceImpl) resultService).setCurrentTime();
        }

        Set<ScannerFile.FileInfo> allFile = scannerFile.getAllFile(strings[0]);
//        Set<ScannerFile.FileInfo> allFile = scannerFile.getAllFile("E://GitHub//Blog//source//_posts");
//        Set<ScannerFile.FileInfo> allFile = scannerFile.getAllFile("/Users/yanjie/GitHub/deppwang.github.io/source/_posts");
        logger.info("allFile size=[{}]", allFile.size());
        if (fileCount > allFile.size()) {
            fileCount = allFile.size();
        }

        int flag = 0;
        for (ScannerFile.FileInfo msg : allFile) {
            executorService.execute(new ScanTask(msg.getFilePath(), filterProcessManager));
            flag++;
            if (flag == fileCount) {
                break;
            }
        }

        executorService.shutdown();
        while (!executorService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
            //logger.info("worker running");
        }

        resultService.end();


    }
}
