package com.kezaihui.faq.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kezaihui.faq.config.DialogueConfig;
import com.kezaihui.faq.config.ElasticsearchConfig;
import com.kezaihui.faq.config.RetrievalConfig;
import com.kezaihui.faq.dao.FaqPairDao;
import com.kezaihui.faq.dataObject.MultiQaTreeNode;
import com.kezaihui.faq.entity.FaqPair;
import com.kezaihui.faq.service.ManagementService;
import com.kezaihui.faq.util.RestClientUtil;
import com.kezaihui.faq.vo.QuestionListVo;
import com.kezaihui.faq.vo.QuestionVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description
 */
@Service
@Slf4j
public class ManagementServiceImpl implements ManagementService {

    @Autowired
    private ElasticsearchConfig ESConfig;

    @Autowired
    private RetrievalConfig retrievalConfig;

    @Autowired
    private DialogueConfig dialogueConfig;

    @Autowired
    private FaqPairDao faqPairDao;

    @Autowired
    private RestClientUtil restClientUtil;

    private String index = "{\n" +
            "  \"settings\": {\n" +
            "    \"number_of_shards\": 1,\n" +
            "    \"analysis\": {\n" +
            "      \"filter\": {\n" +
            "        \"my_synonym_filter\": {\n" +
            "          \"type\": \"synonym\",\n" +
            "          \"updateable\": true,\n" +
            "          \"synonyms_path\": \"analysis/synonym.txt\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"analyzer\": {\n" +
            "        \"ik_synonym\": {\n" +
            "          \"tokenizer\": \"ik_smart\",\n" +
            "          \"filter\": [\n" +
            "            \"my_synonym_filter\"\n" +
            "          ]\n" +
            "        },\n" +
            "        \"ik_synonym_max\": {\n" +
            "          \"tokenizer\": \"ik_max_word\",\n" +
            "          \"filter\": [\n" +
            "            \"my_synonym_filter\"\n" +
            "          ]\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"standard_question\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_synonym_max\",\n" +
            "        \"search_analyzer\": \"ik_synonym_max\"\n" +
            "      },\n" +
            "      \"standard_answer\": {\n" +
            "        \"type\": \"text\"\n" +
            "      },\"in_use\": {\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\"type\": {\n" +
            "        \"type\": \"text\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @Override
    public int totalSynchronize(String tableIndexName) throws IOException {
        int account = 0;
        //??????????????????????????????
        List<FaqPair> faqPairList = faqPairDao.listAll();
        //es client?????????
        RestHighLevelClient client = restClientUtil.getClient(ESConfig.getHost(), ESConfig.getPort());
        //???????????????
        try {
            AcknowledgedResponse deleteIndexResponse = client.indices().delete(restClientUtil.getDeleteIndexRequest(tableIndexName), RequestOptions.DEFAULT);
            log.info("????????????{} {}", tableIndexName, deleteIndexResponse.isAcknowledged());
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            if (e.status() == RestStatus.NOT_FOUND) {
                log.error("??????{}??????????????????????????????????????????", tableIndexName);
            }
        }
        //???????????????
        CreateIndexRequest createIndexRequest = restClientUtil.getCreateIndexRequest(tableIndexName);

        //String jsonSource = readElasticsearchAPIJson(retrievalConfig.getIndex().getFaqPair(), "index");
        String jsonSource = index;
        //index setting,mappings,7.0??????????????????_doc
        createIndexRequest.source(jsonSource, XContentType.JSON);

        try {
            CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            if (!createIndexResponse.isAcknowledged()) {
                log.error("????????????{}??????", tableIndexName);
                return 0;
            }
        } catch (ElasticsearchException e) {
            e.printStackTrace();
            return 0;
        }

        log.info("????????????{}??????", tableIndexName);

        //????????????
        IndexRequest request = null;
        int size = faqPairList.size();
        for (FaqPair faqPair : faqPairList) {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("standard_question", faqPair.getStandardQuestion());
            jsonMap.put("text_value", faqPair.getTextValue());
            jsonMap.put("type", faqPair.getType());
            jsonMap.put("in_use", faqPair.getInUse());
            jsonMap.put("id", faqPair.getId());
            request = restClientUtil.getIndexRequest(tableIndexName, jsonMap);
            try {
                IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
                account++;
            } catch (ElasticsearchException e) {
                e.printStackTrace();
            }

        }
        log.info("???mysql???{}????????????{}???????????????????????????es{}?????????", tableIndexName, size, account);
        //??????client??????????????????
        client.close();

        return account;
    }

    @Override
    public int updateMultiTree() {
        String path = dialogueConfig.getMultiTurnQa().getPath();
        //??????????????????????????????
        File dir = new File(dialogueConfig.getMultiTurnQa().getPath());
        if (!dir.exists()) {
            log.error("????????????????????????{}?????????", path);
            return 0;
        }
        if (!dir.isDirectory()) {
            log.error("????????????????????????{}??????????????????", path);
            return 0;
        }
        String[] files = dir.list();
        if (files == null) {
            log.error("{}????????????????????????", path);
            return 0;
        }
        //????????????????????????????????????redis???

        //??????question???qaId???????????????
        HashMap<String, Integer> question2id = new HashMap<>();

        int NumsOfTreeNode = files.length;
        int accout = 0;
        for (String file : files) {
            String filePath = dir + "/" + file;
            //???json???????????????java??????
            MultiQaTreeNode node = readFileToObject(filePath);
            if (node == null) {
                log.error("?????????????????????{}???????????????", filePath);
                continue;
            }
            int qaId = node.getQaId();
            question2id.put(node.getQuestion(), qaId);
            accout++;
        }

        //???????????????????????????????????????
        log.info("?????????{}?????????????????????redis???", accout);
        return NumsOfTreeNode;
    }

    @Override
    public Page<FaqPair> page(QuestionListVo questionListVo) {
        Page<FaqPair> page = new Page<>(questionListVo.getPage(), questionListVo.getSize());
        LambdaQueryWrapper<FaqPair> queryWrapper = new LambdaQueryWrapper<>();
        Optional.ofNullable(questionListVo.getStandardQuestion()).filter(StringUtils::hasText)
                .ifPresent(value -> queryWrapper.like(FaqPair::getStandardQuestion, value));
        Optional.ofNullable(questionListVo.getCreatorId())
                .ifPresent(value -> queryWrapper.eq(FaqPair::getCreatorId, value));
        return faqPairDao.selectPage(page, queryWrapper);
    }

    @Override
    public void update(QuestionVo questionVo, Integer qaId) {
        FaqPair faqPair = new FaqPair();
        BeanUtils.copyProperties(questionVo, faqPair);
        faqPair.setId(qaId);
        faqPair.setUpdatedAt(LocalDateTime.now());
        String tableIndexName = "faq_pair";

        faqPairDao.updateById(faqPair);
        try {
            totalSynchronize(tableIndexName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFaqPair(FaqPair faqPair) {
        checkAddParams(faqPair);
        faqPairDao.insert(faqPair);
    }

    @Override
    public void deleteFaqPair(Integer id) {
        faqPairDao.deleteById(id);
    }

    private void checkAddParams(FaqPair faqPair) {

    }

    /**
     * ??????????????????esAPI???json??????
     *
     * @param indexName ?????????
     * @param APIType   ??????
     * @return jsonString
     */
    public String readElasticsearchAPIJson(String indexName, String APIType) {
        String jsonFile;
        switch (APIType) {
            case "index":
                jsonFile = String.format("%s/index_API/PUT-%s.json", retrievalConfig.getElasticsearchAPIPath(), indexName);
                break;
            case "document":
                jsonFile = String.format("%s/document_API/PUT-%s.json", retrievalConfig.getElasticsearchAPIPath(), indexName);
                break;
            case "search":
                jsonFile = String.format("%s/search_API/GET-%s.json", retrievalConfig.getElasticsearchAPIPath(), indexName);
                break;
            default:
                jsonFile = null;
        }
        if (jsonFile == null) {
            log.error("????????????{} API?????????", APIType);
            return null;
        }

        String jsonData = "";
        //?????????????????????json??????
        try {
            jsonData = FileUtils.readFileToString(new File(jsonFile), String.valueOf(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("??????esAPIJson??????{}??????", jsonFile);
            return null;
        }

        return jsonData;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @return MultiQaTreeNode
     */
    public MultiQaTreeNode readFileToObject(String file) {
        String jsonData;
        //?????????????????????json??????
        try {
            jsonData = FileUtils.readFileToString(new File(file), String.valueOf(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //?????????MultiQaTreeNode?????????????????????
        return JSONObject.parseObject(jsonData, MultiQaTreeNode.class);
    }

    @Override
    public List<FaqPair> topList() {
        List<FaqPair> result = new ArrayList<>();
        List<FaqPair> topList = faqPairDao.getTopList();
        if (!CollectionUtils.isEmpty(topList)) {
            result = topList;
        }
        return result;
    }

    @Override
    public void addCount(Integer qaId) {
        faqPairDao.addCount(qaId);
    }
}
