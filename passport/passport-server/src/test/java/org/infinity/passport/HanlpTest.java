package org.infinity.passport;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

@RunWith(SpringJUnit4ClassRunner.class)
public class HanlpTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HanlpTest.class);

    @Test
    public void segment() {

        String[] testCase = new String[] { "刘德华多大的" };
        Segment segment = HanLP.newSegment().enableNameRecognize(true);

        // 初始的HanlpData位于resources目录下，需要手动将Hanlp模型文件拷贝到user.home下的HanlpData目录下
        String path = System.getProperty("user.home") + File.separatorChar + "HanlpData" + File.separatorChar;

        String[] pathArray = { "CustomDictionary.txt", "现代汉语补充词库.txt", "全国地名大全.txt ns", "人名词典.txt", "机构名词典.txt",
                "上海地名.txt ns", "nrf.txt nrf" };
        String prePath = path;
        for (int i = 0; i < pathArray.length; ++i) {
            if (pathArray[i].startsWith(" ")) {
                pathArray[i] = prePath + pathArray[i].trim();
            } else {
                pathArray[i] = path + pathArray[i];
                int lastSplash = pathArray[i].lastIndexOf('/');
                if (lastSplash != -1) {
                    prePath = pathArray[i].substring(0, lastSplash + 1);
                }
            }
        }

        HanLP.Config.CustomDictionaryPath = pathArray;
        for (String s : HanLP.Config.CustomDictionaryPath) {
            LOGGER.debug("Initialized custom dictionary {} successfully", s);
        }

        for (String sentence : testCase) {
            List<Term> termList = segment.seg(sentence);
            LOGGER.debug("Testing hanlp custom dictionary successfully: {}", termList);

        }
        LOGGER.debug("Initialized hanlp custom dictionary successfully");

    }
}
