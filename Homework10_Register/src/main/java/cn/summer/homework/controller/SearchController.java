package cn.summer.homework.controller;

import cn.summer.homework.DTO.*;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.SearchCourseVO;
import cn.summer.homework.VO.SearchQuestionVO;
import cn.summer.homework.VO.SearchResultVO;
import cn.summer.homework.VO.SearchUserVO;
import cn.summer.homework.feignClient.ESReadClient;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import cn.summer.homework.service.FindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author VHBin
 * @date 2022/8/7-16:52
 */

@RestController
@RequestMapping("search")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);
    @Resource
    private FindService find;
    @Resource
    private ESReadClient es;
    @Resource
    private ElasticSearchDirectExchangeService mq;

    private List<Integer> esFindAll(String index, SearchDTO s) {
        ElasticSearchDTO es = new ElasticSearchDTO();
        es.setOption(7);
        es.setIndex(index);
        if (s != null) {
            es.setFrom(s.getFrom());
            es.setSize(s.getSize());
        }
        return this.es.searchAll(es);
    }

    private List<Integer> esFind(String index, SearchDTO s) {
        ElasticSearchDTO es = new ElasticSearchDTO();
        es.setOption(7);
        es.setIndex(index);
        if (s == null || s.getValue() == null || s.getValue().equals("")
                || s.getSize() == 0) {
            return Collections.singletonList(-2);
        }
        es.setValue(s.getValue());
        es.setFrom(s.getFrom());
        es.setSize(s.getSize());
        return this.es.matchSearch(es);
    }

    private Map<String, Integer> getPage(SearchDTO s) {
        HashMap<String, Integer> res
                = new HashMap<>(2, 1f);
        if (s == null || s.getSize() == 0) {
            res.put("from", 0);
            res.put("size", 10);
        } else {
            res.put("from", s.getFrom());
            res.put("size", s.getSize());
        }
        return res;
    }

    private boolean isException(List<Integer> list) {
        if (list.get(0) == -1) {
            logger.error("ES 查询异常");
            return true;
        } else if (list.get(0) == -2) {
            logger.error("传入数据无效");
            return true;
        }
        return false;
    }

    /**
     * 获取全部用户信息
     *
     * @param s 可为空, 也可以设定 from & size
     * @return SearchUserVO 状态码, 消息, 用户信息
     */
    @GetMapping("/user/all")
    public SearchUserVO allUsers(@RequestBody SearchDTO s) {
        List<Integer> uidList = esFindAll(IndexUtil.USER, s);
        if (isException(uidList)) {
            return new SearchUserVO(500, "User-Search:Error", null);
        }
        List<UserRoleDTO> users;
        if (uidList.size() == 0) {
            Map<String, Integer> page = getPage(s);
            int from, size;
            from = page.get("from");
            size = page.get("size");
            users = find.users();
            if (users.size() > 0) {
                mq.save(users);
                users = users.subList(
                        from,
                        from + size);
            }
        } else {
            users = new ArrayList<>();
            try {
                for (Integer uid : uidList) {
                    users.add(find.user(uid));
                }
            } catch (IOException ex) {
                logger.error("SQL User 查询异常", ex);
                users = null;
            }
        }
        if (users == null) {
            return new SearchUserVO(500, "User-Search:Error", null);
        } else {
            return new SearchUserVO(200, "OK", users);
        }
    }

    /**
     * 查找符合条件的用户
     *
     * @param s value 为条件(关键字), 可设定 from & size
     * @return SearchUserVO 状态码, 消息, 用户信息
     */
    @GetMapping("/user/find")
    public SearchUserVO findUser(@RequestBody SearchDTO s) {
        List<Integer> uidList = esFind(IndexUtil.USER, s);
        if (isException(uidList)) {
            return new SearchUserVO(500, "User-Search:Error", null);
        }
        List<UserRoleDTO> users = new ArrayList<>();
        try {
            if (uidList.size() == 0) {
                mq.save(find.users());
                uidList = esFind(IndexUtil.USER, s);
            }
            for (Integer uid : uidList) {
                users.add(find.user(uid));
            }
            return new SearchUserVO(200, "OK", users);
        } catch (IOException ex) {
            logger.error("SQL User 查询异常", ex);
            return new SearchUserVO(500, "User-Search:Error", null);
        }
    }

    /**
     * 获取指定用户
     *
     * @param uid 用户id
     * @return SearchUserVO 状态码, 消息, 用户信息
     */
    @GetMapping("/user/get")
    public SearchUserVO getUser(@RequestParam("uid") Integer uid) {
        try {
            UserRoleDTO user = find.user(uid);
            return new SearchUserVO(200, "OK",
                    Collections.singletonList(user));
        } catch (IOException ex) {
            logger.error("SQL User 获取异常", ex);
            return new SearchUserVO(500, "User-Search:Error", null);
        }
    }

    /**
     * 获取全部课程信息
     *
     * @param s 可为空, 也可以设定 from & size
     * @return SearchCourseVO 状态码, 消息, 用户信息
     */
    @GetMapping("/course/all")
    public SearchCourseVO allCourse(@RequestBody SearchDTO s) {
        List<Integer> cidList = esFindAll(IndexUtil.COURSE, s);
        if (isException(cidList)) {
            return new SearchCourseVO(500, "Course-Search:Error", null);
        }
        List<CourseSTDTO> courses;
        if (cidList.size() == 0) {
            Map<String, Integer> page = getPage(s);
            int from = page.get("from");
            int size = page.get("size");
            courses = find.courses();
            if (courses.size() > 0) {
                mq.save(courses);
                courses = courses.subList(
                        from,
                        from + size);
            }
        } else {
            courses = new ArrayList<>();
            try {
                for (Integer cid : cidList) {
                    courses.add(find.course(cid));
                }
            } catch (IOException io) {
                logger.error("SQL Course 查询异常", io);
                courses = null;
            }
        }
        if (courses != null) {
            return new SearchCourseVO(200, "OK", courses);
        } else {
            return new SearchCourseVO(500, "Course-Search:Error", null);
        }
    }

    /**
     * 查找符合条件的课程
     *
     * @param s value 为条件(关键字), 可设定 from & size
     * @return SearchCourseVO 状态码, 消息, 用户信息
     */
    @GetMapping("/course/find")
    public SearchCourseVO findCourse(@RequestBody SearchDTO s) {
        List<Integer> cidList = esFind(IndexUtil.COURSE, s);
        if (isException(cidList)) {
            return new SearchCourseVO(500, "Course-Search:Error", null);
        }
        List<CourseSTDTO> courses = new ArrayList<>();
        try {
            if (cidList.size() == 0) {
                mq.save(find.courses());
                cidList = esFind(IndexUtil.COURSE, s);
            }
            for (Integer cid : cidList) {
                courses.add(find.course(cid));
            }
            return new SearchCourseVO(200, "OK", courses);
        } catch (IOException io) {
            logger.error("SQL Course 查询异常", io);
            return new SearchCourseVO(500, "Course-Search:Error", null);
        }
    }

    /**
     * 获取指定课程
     *
     * @param cid 课程id
     * @return SearchCourseVO 状态码, 消息, 用户信息
     */
    @GetMapping("/course/get")
    public SearchCourseVO getCourse(@RequestParam("cid") Integer cid) {
        try {
            CourseSTDTO course = find.course(cid);
            return new SearchCourseVO(200, "OK",
                    Collections.singletonList(course));
        } catch (IOException ex) {
            logger.error("SQL Course 获取异常", ex);
            return new SearchCourseVO(500, "Course-Search:Error", null);
        }
    }

    /**
     * 获取全部问题信息
     *
     * @param s 可为空, 也可以设定 from & size
     * @return SearchQuestionVO 状态码, 消息, 用户信息
     */
    @GetMapping("/question/all")
    public SearchQuestionVO allQuestion(@RequestBody SearchDTO s) {
        List<Integer> qidList = esFindAll(IndexUtil.QUESTION, s);
        if (isException(qidList)) {
            return new SearchQuestionVO(500, "Question-Search:Error", null);
        }
        List<QuestionResultDTO> questions;
        if (qidList.size() == 0) {
            Map<String, Integer> page = getPage(s);
            int from = page.get("from");
            int size = page.get("size");
            questions = find.questions();
            if (questions.size() > 0) {
                mq.save(questions);
                questions = questions.subList(
                        from,
                        from + size);
            }
        } else {
            questions = new ArrayList<>();
            try {
                for (Integer qid : qidList) {
                    questions.add(find.question(qid));
                }
            } catch (IOException ex) {
                logger.error("SQL Question 查询异常", ex);
                questions = null;
            }
        }
        if (questions != null) {
            return new SearchQuestionVO(200, "OK", questions);
        } else {
            return new SearchQuestionVO(500, "Question-Search:Error", null);
        }
    }

    /**
     * 查找符合条件的问题
     *
     * @param s value 为条件(关键字), 可设定 from & size
     * @return SearchQuestionVO 状态码, 消息, 用户信息
     */
    @GetMapping("/question/find")
    public SearchQuestionVO findQuestion(@RequestBody SearchDTO s) {
        List<Integer> qidList = esFind(IndexUtil.QUESTION, s);
        if (isException(qidList)) {
            return new SearchQuestionVO(500, "Question-Search:Error", null);
        }
        List<QuestionResultDTO> questions = new ArrayList<>();
        try {
            if (qidList.size() == 0) {
                mq.save(find.questions());
                qidList = esFind(IndexUtil.QUESTION, s);
            }
            for (Integer qid : qidList) {
                questions.add(find.question(qid));
            }
            return new SearchQuestionVO(200, "OK", questions);
        } catch (IOException ex) {
            logger.error("SQL Question 查询异常", ex);
            return new SearchQuestionVO(500, "Question-Search:Error", null);
        }
    }

    /**
     * 获取指定问题
     *
     * @param qid 课程id
     * @return SearchQuestionVO 状态码, 消息, 用户信息
     */
    @GetMapping("/question/get")
    public SearchQuestionVO getQuestion(@RequestParam("qid") Integer qid) {
        try {
            QuestionResultDTO question = find.question(qid);
            return new SearchQuestionVO(200, "OK",
                    Collections.singletonList(question));
        } catch (IOException ex) {
            logger.error("SQL Question 获取异常", ex);
            return new SearchQuestionVO(500, "Question-Search:Error", null);
        }
    }

    /**
     * 获取全部回答信息
     *
     * @param s 可为空, 也可以设定 from & size
     * @return SearchResultVO 状态码, 消息, 用户信息
     */
    @GetMapping("/result/all")
    public SearchResultVO allResult(@RequestBody SearchDTO s) {
        List<Integer> ridList = esFindAll(IndexUtil.RESULT, s);
        if (isException(ridList)) {
            return new SearchResultVO(500, "Result-Search:Error", null);
        }
        List<ResultQuestionDTO> results;
        if (ridList.size() == 0) {
            Map<String, Integer> page = getPage(s);
            int from = page.get("from");
            int size = page.get("size");
            results = find.results();
            if (results.size() > 0) {
                mq.save(results);
                results = results.subList(
                        from,
                        from + size);
            }
        } else {
            results = new ArrayList<>();
            try {
                for (Integer rid : ridList) {
                    results.add(find.result(rid));
                }
            } catch (IOException ex) {
                logger.error("SQL Result 查询异常", ex);
                results = null;
            }
        }
        if (results != null) {
            return new SearchResultVO(200, "OK", results);
        } else {
            return new SearchResultVO(500, "Result-Search:Error", null);
        }
    }

    /**
     * 查找符合条件的回答
     *
     * @param s value 为条件(关键字), 可设定 from & size
     * @return SearchResultVO 状态码, 消息, 用户信息
     */
    @GetMapping("/result/find")
    public SearchResultVO findResult(@RequestBody SearchDTO s) {
        List<Integer> ridList = esFind(IndexUtil.RESULT, s);
        if (isException(ridList)) {
            return new SearchResultVO(500, "Result-Search:Error", null);
        }
        List<ResultQuestionDTO> results = new ArrayList<>();
        try {
            if (ridList.size() == 0) {
                mq.save(find.results());
                ridList = esFind(IndexUtil.RESULT, s);
            }
            for (Integer rid : ridList) {
                results.add(find.result(rid));
            }
            return new SearchResultVO(200, "OK", results);
        } catch (IOException ex) {
            logger.error("SQL Result 查询异常", ex);
            return new SearchResultVO(500, "Result-Search:Error", null);
        }
    }

    /**
     * 获取指定回答
     *
     * @param rid 课程id
     * @return SearchResultVO 状态码, 消息, 用户信息
     */
    @GetMapping("/result/get")
    public SearchResultVO getResult(@RequestParam("rid") Integer rid) {
        try {
            ResultQuestionDTO result = find.result(rid);
            return new SearchResultVO(200, "OK",
                    Collections.singletonList(result));
        } catch (IOException ex) {
            logger.error("SQL Result 获取异常", ex);
            return new SearchResultVO(500, "Result-Search:Error", null);
        }
    }
}
