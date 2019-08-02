package top.crossoverjie.nows.nows.service.impl.fixpic;

import top.crossoverjie.nows.nows.filter.FilterProcess;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Function: 图片过滤器
 *
 * @author crossoverJie
 * Date: 2019-05-05 23:13
 * @since JDK 1.8
 */
public class PicFilterProcess implements FilterProcess {

    // pattern source: https://stackoverflow.com/questions/3809401/what-is-a-good-regular-expression-to-match-a-url
    // 如果pattern直接是这样 "https?://.+\.(jpg|gif|png)"，那么将匹配出下面这种类似的 url
    // https://pan.baidu.com/s/1PB-9w-OnzWW1CA7Jr4NJzA，提取密码：jw8b](https://upload-images.jianshu.io/upload_images/5863464-6233be38f590fc58.png
    private String pattern = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)\\.(jpg|gif|png)";

    @Override
    public String process(String msg) {

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(msg);
        while (m.find()) {
            return m.group();
        }

        return null;
    }
}
