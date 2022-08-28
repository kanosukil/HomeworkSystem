package cn.summer.homework.controller;

import cn.summer.homework.DTO.*;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.SearchVO;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import cn.summer.homework.service.FindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private ESCRUDClient es;
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
        if (list.size() > 0 && list.get(0) == -1) {
            logger.error("ES 查询异常");
            return true;
        } else if (list.size() > 0 && list.get(0) == -2) {
            logger.error("传入数据无效");
            return true;
        }
        return false;
    }

    /**
     * 获取全部用户信息
     *
     * @param from 分页起始
     * @param size 分页大小
     * @return 角色信息
     */
    @GetMapping("/user/all")
    public SearchVO<UserRoleDTO> allUsers(@RequestParam("from") Integer from,
                                          @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO("all", from, size);
        List<Integer> uidList = esFindAll(IndexUtil.USER, s);
        if (isException(uidList)) {
            return new SearchVO<>(500, "User-Search:Error", null);
        }
        List<UserRoleDTO> users;
        if (uidList.size() == 0) {
            logger.info("新建");
            Map<String, Integer> page = getPage(s);
            int fi, si;
            fi = page.get("from");
            si = page.get("size");
            users = find.users();
            logger.info("Users: {}", users);
            if (users.size() > 0) {
                mq.save(users);
                users = users.subList(
                        fi,
                        Math.min(fi + si, users.size()));
            }
        } else {
            logger.info("从 ES");
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
            return new SearchVO<>(500, "User-Search:Error", null);
        } else {
            return new SearchVO<>(200, "OK", users);
        }
    }

    /**
     * 查找符合条件的用户
     *
     * @param value 查询值
     * @param from  分页起始
     * @param size  分页大小
     * @return 角色信息
     */
    @GetMapping("/user/find")
    public SearchVO<UserRoleDTO> findUser(@RequestParam("value") String value,
                                          @RequestParam("from") Integer from,
                                          @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO(value, from, size);
        List<Integer> uidList = esFind(IndexUtil.USER, s);
        if (isException(uidList)) {
            return new SearchVO<>(500, "User-Search:Error", null);
        }
        List<UserRoleDTO> users = new ArrayList<>();
        try {
            if (uidList.size() == 0) {
                logger.info("新建");
                mq.save(find.users());
                uidList = esFind(IndexUtil.USER, s);
            }
            for (Integer uid : uidList) {
                users.add(find.user(uid));
            }
            logger.info("Users: {}", users);
            return new SearchVO<>(200, "OK", users);
        } catch (IOException ex) {
            logger.error("SQL User 查询异常", ex);
            return new SearchVO<>(500, "User-Search:Error", null);
        }
    }

    /**
     * 获取指定用户
     *
     * @param uid 用户id
     * @return SearchUserVO 状态码, 消息, 用户信息
     */
    @GetMapping("/user/get")
    public SearchVO<UserRoleDTO> getUser(@RequestParam("uid") Integer uid) {
        try {
            UserRoleDTO user = find.user(uid);
            logger.info("User: {}", user);
            return new SearchVO<>(200, "OK",
                    Collections.singletonList(user));
        } catch (IOException ex) {
            logger.error("SQL User 获取异常", ex);
            return new SearchVO<>(500, "User-Search:Error", null);
        }
    }

    /**
     * 获取全部课程信息
     *
     * @param from 分页起始
     * @param size 分页大小
     * @return 课程信息
     */
    @GetMapping("/course/all")
    public SearchVO<CourseSTDTO> allCourse(@RequestParam("from") Integer from,
                                           @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO("all", from, size);
        List<Integer> cidList = esFindAll(IndexUtil.COURSE, s);
        if (isException(cidList)) {
            return new SearchVO<>(500, "Course-Search:Error", null);
        }
        List<CourseSTDTO> courses;
        if (cidList.size() == 0) {
            logger.info("新建");
            Map<String, Integer> page = getPage(s);
            int fi = page.get("from");
            int si = page.get("size");
            courses = find.courses();
            logger.info("Courses: {}", courses);
            if (courses.size() > 0) {
                mq.save(courses);
                courses = courses.subList(
                        fi,
                        Math.min(fi + si, courses.size()));
            }
        } else {
            logger.info("从 ES");
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
            return new SearchVO<>(200, "OK", courses);
        } else {
            return new SearchVO<>(500, "Course-Search:Error", null);
        }
    }

    /**
     * 查找符合条件的课程
     *
     * @param value 查询之
     * @param from  分页起始
     * @param size  分页大小
     * @return 课程信息
     */
    @GetMapping("/course/find")
    public SearchVO<CourseSTDTO> findCourse(@RequestParam("value") String value,
                                            @RequestParam("from") Integer from,
                                            @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO(value, from, size);
        List<Integer> cidList = esFind(IndexUtil.COURSE, s);
        if (isException(cidList)) {
            return new SearchVO<>(500, "Course-Search:Error", null);
        }
        List<CourseSTDTO> courses = new ArrayList<>();
        try {
            if (cidList.size() == 0) {
                logger.info("新建");
                mq.save(find.courses());
                cidList = esFind(IndexUtil.COURSE, s);
            }
            for (Integer cid : cidList) {
                courses.add(find.course(cid));
            }
            logger.info("Courses: {}", courses);
            return new SearchVO<>(200, "OK", courses);
        } catch (IOException io) {
            logger.error("SQL Course 查询异常", io);
            return new SearchVO<>(500, "Course-Search:Error", null);
        }
    }

    /**
     * 获取指定课程
     *
     * @param cid 课程id
     * @return SearchCourseVO 状态码, 消息, 用户信息
     */
    @GetMapping("/course/get")
    public SearchVO<CourseSTDTO> getCourse(@RequestParam("cid") Integer cid) {
        try {
            CourseSTDTO course = find.course(cid);
            logger.info("course: {}", course);
            return new SearchVO<>(200, "OK",
                    Collections.singletonList(course));
        } catch (IOException ex) {
            logger.error("SQL Course 获取异常", ex);
            return new SearchVO<>(500, "Course-Search:Error", null);
        }
    }

    /**
     * 获取全部问题信息
     *
     * @param from 分页起始
     * @param size 分页大小
     * @return 问题信息
     */
    @GetMapping("/question/all")
    public SearchVO<QuestionResultDTO> allQuestion(@RequestParam("from") Integer from,
                                                   @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO("all", from, size);
        List<Integer> qidList = esFindAll(IndexUtil.QUESTION, s);
        if (isException(qidList)) {
            return new SearchVO<>(500, "Question-Search:Error", null);
        }
        List<QuestionResultDTO> questions;
        if (qidList.size() == 0) {
            logger.info("新建");
            Map<String, Integer> page = getPage(s);
            int fi = page.get("from");
            int si = page.get("size");
            questions = find.questions();
            logger.info("questions: {}", questions);
            if (questions.size() > 0) {
                mq.save(questions);
                questions = questions.subList(
                        fi,
                        Math.min(fi + si, questions.size()));
            }
        } else {
            logger.info("从 ES");
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
            return new SearchVO<>(200, "OK", questions);
        } else {
            return new SearchVO<>(500, "Question-Search:Error", null);
        }
    }

    /**
     * 查找符合条件的问题
     *
     * @param value 查询值
     * @param from  分页起始
     * @param size  分页大小
     * @return 问题信息
     */
    @GetMapping("/question/find")
    public SearchVO<QuestionResultDTO> findQuestion(@RequestParam("value") String value,
                                                    @RequestParam("from") Integer from,
                                                    @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO(value, from, size);
        List<Integer> qidList = esFind(IndexUtil.QUESTION, s);
        if (isException(qidList)) {
            return new SearchVO<>(500, "Question-Search:Error", null);
        }
        List<QuestionResultDTO> questions = new ArrayList<>();
        try {
            if (qidList.size() == 0) {
                logger.info("新建");
                mq.save(find.questions());
                qidList = esFind(IndexUtil.QUESTION, s);
            }
            for (Integer qid : qidList) {
                questions.add(find.question(qid));
            }
            logger.info("questions: {}", questions);
            return new SearchVO<>(200, "OK", questions);
        } catch (IOException ex) {
            logger.error("SQL Question 查询异常", ex);
            return new SearchVO<>(500, "Question-Search:Error", null);
        }
    }

    /**
     * 获取指定问题
     *
     * @param qid 课程id
     * @return SearchQuestionVO 状态码, 消息, 用户信息
     */
    @GetMapping("/question/get")
    public SearchVO<QuestionResultDTO> getQuestion(@RequestParam("qid") Integer qid) {
        try {
            QuestionResultDTO question = find.question(qid);
            logger.info("question: {}", question);
            return new SearchVO<>(200, "OK",
                    Collections.singletonList(question));
        } catch (IOException ex) {
            logger.error("SQL Question 获取异常", ex);
            return new SearchVO<>(500, "Question-Search:Error", null);
        }
    }

    /**
     * 获取全部回答信息
     *
     * @param from 分页起始
     * @param size 分页大小
     * @return 回答信息
     */
    @GetMapping("/result/all")
    public SearchVO<ResultQuestionDTO> allResult(@RequestParam("from") Integer from,
                                                 @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO("all", from, size);
        List<Integer> ridList = esFindAll(IndexUtil.RESULT, s);
        if (isException(ridList)) {
            return new SearchVO<>(500, "Result-Search:Error", null);
        }
        List<ResultQuestionDTO> results;
        if (ridList.size() == 0) {
            logger.info("新建");
            Map<String, Integer> page = getPage(s);
            int fi = page.get("from");
            int si = page.get("size");
            results = find.results();
            logger.info("results: {}", results);
            if (results.size() > 0) {
                mq.save(results);
                results = results.subList(
                        fi,
                        Math.min(fi + si, results.size()));
            }
        } else {
            logger.info("从 ES");
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
            return new SearchVO<>(200, "OK", results);
        } else {
            return new SearchVO<>(500, "Result-Search:Error", null);
        }
    }

    /**
     * 查找符合条件的回答
     *
     * @param value 查询值
     * @param from  分页起始
     * @param size  分页大小
     * @return 回答信息
     */
    @GetMapping("/result/find")
    public SearchVO<ResultQuestionDTO> findResult(@RequestParam("value") String value,
                                                  @RequestParam("from") Integer from,
                                                  @RequestParam("size") Integer size) {
        SearchDTO s = new SearchDTO(value, from, size);
        List<Integer> ridList = esFind(IndexUtil.RESULT, s);
        if (isException(ridList)) {
            return new SearchVO<>(500, "Result-Search:Error", null);
        }
        List<ResultQuestionDTO> results = new ArrayList<>();
        try {
            if (ridList.size() == 0) {
                logger.info("新建");
                mq.save(find.results());
                ridList = esFind(IndexUtil.RESULT, s);
            }
            for (Integer rid : ridList) {
                results.add(find.result(rid));
            }
            logger.info("results: {}", results);
            return new SearchVO<>(200, "OK", results);
        } catch (IOException ex) {
            logger.error("SQL Result 查询异常", ex);
            return new SearchVO<>(500, "Result-Search:Error", null);
        }
    }

    /**
     * 获取指定回答
     *
     * @param rid 课程id
     * @return SearchResultVO 状态码, 消息, 用户信息
     */
    @GetMapping("/result/get")
    public SearchVO<ResultQuestionDTO> getResult(@RequestParam("rid") Integer rid) {
        try {
            ResultQuestionDTO result = find.result(rid);
            logger.info("result: {}", result);
            return new SearchVO<>(200, "OK",
                    Collections.singletonList(result));
        } catch (IOException ex) {
            logger.error("SQL Result 获取异常", ex);
            return new SearchVO<>(500, "Result-Search:Error", null);
        }
    }

    /**
     * 获取全部问题类型
     *
     * @return SearchResultVO 状态码, 消息, 用户信息
     */
    @GetMapping("/type/all")
    public SearchVO<String> getType() {
        try {
            List<String> types = find.types();
            if (types == null) {
                throw new IOException("问题类型获取异常");
            }
            return new SearchVO<>(200, "OK", types);
        } catch (Exception ex) {
            logger.error("SQL Type 获取异常", ex);
            return new SearchVO<>(500, "Type-Search:Error", null);
        }
    }

    @GetMapping("/score/question")
    public SearchVO<String> getScoreByQID(@RequestParam("qid") Integer qid) {
        try {
            List<String> res = new ArrayList<>();
            for (ResultQuestionDTO it : find.resultBQuestion(qid)) {
                res.add(it.getStudent().getName() + ":" + it.getResult().getScore());
            }
            if (res.size() == 0) {
                throw new IOException("问题类型获取异常");
            }
            return new SearchVO<>(200, "OK", res);
        } catch (IOException io) {
            logger.error("SQL Result 获取异常", io);
            return new SearchVO<>(500, "Result-Search:Error", null);
        }
    }
}
