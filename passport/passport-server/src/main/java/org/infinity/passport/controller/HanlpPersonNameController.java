package org.infinity.passport.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.HanlpPersonName;
import org.infinity.passport.repository.HanlpPersonNameRepository;
import org.infinity.passport.utils.MongoDbIOAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "Hanlp人名")
public class HanlpPersonNameController {

    @Autowired
    private HanlpPersonNameRepository hanlpPersonNameRepository;

    @Autowired
    private MongoDbIOAdapter          mongoDbIOAdapter;

    @ApiOperation(value = "导入参数值")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功导入") })
    @RequestMapping(value = "/api/hanlp-person-name/import", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> importData() throws IOException {
        File file = new File(
                System.getProperty("user.home") + File.separatorChar + "HanlpData" + File.separatorChar + "人名词典.txt");
        List<String> lines = IOUtils.readLines(new FileInputStream(file), StandardCharsets.UTF_8);
        int i = 1;
        List<HanlpPersonName> list = new ArrayList<HanlpPersonName>();
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                String[] splitResults = line.split(" ");

                HanlpPersonName entity = new HanlpPersonName();
                entity.setName(splitResults[0]);
                entity.setPos(splitResults[1]);
                entity.setFrequency(Integer.parseInt(splitResults[2]));
                list.add(entity);
            }
            if (i % (lines.size() / 10000) == 0) {
                hanlpPersonNameRepository.save(list);
                list = new ArrayList<>();
            }
            i++;
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation("分词")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "成功获取") })
    @RequestMapping(value = "/api/hanlp-person-name/segment", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(Authority.DEVELOPER)
    @Timed
    public ResponseEntity<List<Term>> segment(
            @ApiParam(value = "inputText", required = true, defaultValue = "刘德华多大的") @RequestParam(value = "inputText", required = false) String inputText) {
        Segment segment = HanLP.newSegment().enableNameRecognize(true);
        String[] dictionaries = { HanlpPersonName.class.getName() };
        HanLP.Config.CustomDictionaryPath = dictionaries;
        HanLP.Config.IOAdapter = mongoDbIOAdapter;
        List<Term> termList = segment.seg(inputText);
        return new ResponseEntity<>(termList, HttpStatus.OK);
    }
}
