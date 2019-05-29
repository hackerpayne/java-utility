package com.lindonge.core.file;

import com.google.common.base.Joiner;
import com.lindonge.core.file.csv.CsvHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class CsvHelperTest {

    private static final Logger logger = LoggerFactory.getLogger(CsvHelperTest.class);

//    @Test
//    public void testReadLine() throws Exception {
//        CsvHelper csv = new CsvHelper(FileUtil.getFile(Utils.CurrentDir, "logs", "39健康网外链.csv").getAbsolutePath());
//
//        String line;
//        while (true) {
//            line = csv.readLine();
//
//            logger.info(line);
//
//            if (StringUtils.isEmpty(line))
//                break;
//
//        }
//
//
//    }

    @Test
    public void testFromCSVLinetoArray() {

        ArrayList arrayList = CsvHelper.fromCSVLinetoArray("URL,Title,Anchor Text,Page Authority,Domain Authority,Number of Links,Number of Domains Linking to Domain,Followable,301,Origin,Target URL");

        System.out.println(Joiner.on("===").join(arrayList));

        ArrayList arrayList2 = CsvHelper.fromCSVLinetoArray("http://www.qingdaonews.com/gb/node/mspd.htm,美食频道,39健康网,37,86,1,32199,Yes,No,External,http://www.39.net/\n");

        System.out.println(Joiner.on("===").join(arrayList2));


    }

}