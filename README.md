# 电子作业系统的设计和开发

### 使用技术栈

> 并不会创建微服务的集群(没有资源)

1. **Spring Cloud Alibaba** 微服务框架
    1. Nacos 服务注册和配置管理
2. **Spring Cloud** 微服务框架
    1. Gateway 后端网关
    2. OpenFeign 微服务通信
3. **Spring Boot** 服务框架
3. **Nginx** 网络服务器进行 Nacos 的负载均衡
4. **Redis** 缓存
5. **ElasticSearch** 搜索
6. **RabbitMQ** 消息队列 异步通信
6. **Docker/Docker-Compose** 部署测试

### 设计模型

[数据库设计模型](https://dbdiagram.io/d/62beb8df69be0b672c7e89f2)<br>![image-20220702180253994](image/image-20220702180253994.png)
系统框架<br>
![系统框架](image/HomeworkSystem.png)

顶级用例图<br>![顶层图](image/HomeworkSystemTop.png)



+ 数据库设计工具: [dbdiagram](https://dbdiagram.io/home)
+ 系统架构设计工具: [diagrams.net](https://github.com/jgraph/drawio-desktop)

#### 细分

##### 数据库连接层

> 仅实现了向 SQLConnection 传输数据, 但还可以添加其他功能

1. User
2. Homework
3. Course

##### 数据操作层

> 验证数据的完整性, 以及是否可以插入 DB

4. Creator
5. Respondent
6. CourseManager

##### 数据输入层

> 仅实现了将传入数据传输到下游服务, 但还可以添加其他功能

7. Teacher
8. Student
9. Admin

##### 总入口

> 将从 Gateway 输入进来的数据向下游服务分类别传输, 并向 ElasticSearch 传输数据提高数据查询的效率

10. Register
##### 工具服务
+ SQLConnection 数据库链接
+ ElasticSearch 搜索查询
+ FileStore 文件存储和访问

#### SQLConnection

+ 利用 MyBatis 链接 MySQL 数据库

+ 使用 Redis 作为 数据缓存

+ 保证数据库内的数据一致性

  > 访问接口(未从 Gateway 暴露, 且未配置 Cors):
  >
  > ```
  > // Course
  > /course-sql
  > 	// 查询全部课程
  > 	/courses-get
  > 	method: GET
  > 	返回数据:
  > 	List<CourseSTDTO>
  > 		{
  >             'course':{
  >                 'id': int,
  >                 'name': string,
  >                 'teacher_num': int,
  >                 'student_num': int,
  >                 'create_time': date
  >             },
  >             'teachers':[
  >                 {
  >                     'id': int,
  >                     'name': string,
  >                     'password_hash': string,
  >                     'head_image': string,
  >                     'introduction': string,
  >                     'email': string,
  >                     'create_time': date
  >                 },
  >                 ...
  >             ],
  >             'students':[
  >                 {
  >                 	'id': int,
  >                     'name': string,
  >                     'password_hash': string,
  >                     'head_image': string,
  >                     'introduction': string,
  >                     'email': string,
  >                     'create_time': date
  >                 },
  > 			   ...
  >             ]
  >         }
  > 	// 获取指定ID课程
  > 	/course-get
  > 	method: GET
  > 	请求参数: id <int>
  > 	返回数据:
  > 	CourseSTDTO
  > 	// 获取指定名称课程
  > 	/courses-get-name
  > 	method: GET
  > 	请求参数: name <string>
  > 	返回数据:
  > 	List<CourseSTDTO>
  > 	// 获取指定老师的教授课程
  > 	/courses-get-teacher
  > 	method: GET
  > 	请求参数: tid <int>
  > 	返回数据:
  > 	List<CourseSTDTO>
  > 	// 获取指定学生的选修课程
  > 	/courses-get-student
  > 	method: GET
  > 	请求参数: sid <int>
  > 	返回数据:
  > 	List<CourseSTDTO>
  > 	// 创建课程
  > 	/create-course
  > 	method: POST
  > 	请求体: 
  > 	{
  >         'course':{
  >             'id': int,
  >             'name': string,
  >             'teacher_num': int,
  >             'student_num': int,
  >             'create_time': date
  >         },
  >         'tid': int
  >     }
  > 	返回数据: CourseOpBO
  > 	{
  >         'isSuccess': Boolean,
  >         'map': Map<String, Object>
  >     }
  > 	// 学生选修
  > 	/course-add-student
  > 	method: POST
  > 	请求体:
  > 	{
  >         'sid': int,
  >         'cid': int
  >     }
  > 	返回数据: CourseOpBO
  > 	// 老师授课
  > 	/course-add-teacher
  > 	method: POST
  > 	请求体:
  > 	{
  >         'tid': int,
  >         'cid': int
  >     }
  > 	返回数据: CourseOpBO
  > 	// 学生退课
  > 	/course-drop-student
  > 	method: POST
  > 	请求体:
  > 	{
  >         'sid': int,
  >         'cid': int
  >     }
  > 	返回数据: CourseOpBO
  > 	// 老师结束授课
  > 	/course-drop-teacher
  > 	method: POST
  > 	请求体:
  > 	{
  >         'tid': int,
  >         'cid': int
  >     }
  > 	返回数据: CourseOpBO
  > 	// 课程更新
  > 	/course-update-name
  > 	method: POST
  > 	请求体: 
  > 	{
  >         'course':{
  >             'id': int,
  >             'name': string,
  >             'teacher_num': int,
  >             'student_num': int,
  >             'create_time': date
  >         },
  >         'tid': int
  >     }
  > 	返回数据: CourseOpBO
  > 	// 课程删除
  > 	/course-delete
  > 	method: POST
  > 	请求体:
  > 	{
  >         'tid': int,
  >         'cid': int
  >     }
  > 	返回数据: CourseOpBO
  > ```
  >
  > ```
  > // Homework
  > /homework-sql
  > 	// 获取全部问题
  > 	/questions-get
  > 	method: GET
  > 	返回数据: QuestionResultDTO
  > 	{
  >         'question':{
  >             'id': int,
  >             'title': string,
  >             'extension': string,
  >             'score': int,
  >             'isFile': boolean,
  >             'answer': string,
  >             'comment': string,
  >             'create_time': date
  >         },
  >         'questionType': string,
  >         'teacher': {
  >             'id': int,
  >             'name': string,
  >             'password_hash': string,
  >             'head_image': string,
  >             'introduction': string,
  >             'email': string,
  >             'create_time': date
  >         },
  >         'course': {
  >             cid: cname
  >         },
  >         'results': {
  >             sid: rid,
  >             ...
  >         }
  >     }
  > 	// 获取指定ID问题
  > 	/question-get
  >     method: GET
  >     请求参数: qid int
  >     返回数据: QuestionResultDTO
  > 	// 获取指定老师的问题
  > 	/questions-get-tid
  >     method: GET
  >     请求参数: tid int
  >     返回数据: QuestionResultDTO
  > 	// 根据问题类型获取问题
  > 	/question-get-type
  > 	method: GET
  >     请求参数: type int
  >     返回数据: QuestionResultDTO
  > 	// 根据课程获取问题
  > 	/question-get-course
  >     method: GET
  >     请求参数: cid int
  >     返回数据: QuestionResultDTO
  >     // 创建问题
  >     /question-create
  >     method: POST
  >    	请求体:
  >     {
  >         'tid': int,
  >         'id': int,
  >         // create: cid; update: qid
  >     	// type: create 0; delete 0/typeID
  >        	'question': {
  >         	'id': int,
  >             'title': string,
  >             'extension': string,
  >             'score': int,
  >             'isFile': boolean,
  >             'answer': string,
  >             'comment': string,
  >             'create_time': date
  >     	},
  > 		'type': string
  >     }
  > 	返回数据: HomeworkopBO
  > 	{
  >         'isSuccess': boolean,
  >         'isQuestion': boolean,
  >         'info': Map<String, Object>
  >     }
  > 	// 更新问题
  > 	/question-update
  > 	method: POST
  >    	请求体:
  >     {
  >         'tid': int,
  >         'id': int,
  >         // create: cid; update: qid
  >     	// type: create 0; delete 0/typeID
  >        	'question': {
  >         	'id': int,
  >             'title': string,
  >             'extension': string,
  >             'score': int,
  >             'isFile': boolean,
  >             'answer': string,
  >             'comment': string,
  >             'create_time': date
  >     	},
  > 		'type': string
  >     }
  > 	返回数据: HomeworkopBO
  > 	// 问题删除
  > 	/question-delete
  > 	method: POST
  >    	请求体:
  >     {
  >         'tid': int,
  >         'qid': int
  >     }
  > 	返回数据: HomeworkopBO
  > 	// 老师批改
  > 	/result-correct
  > 	method: POST
  > 	请求体:
  > 	{
  >         'uid': int,
  >         'cid': int,
  >         'qid': int,
  >         'result': {
  >             'id': int,
  >             'content': string,
  >             'isFile': boolean,
  >             'isCheck': boolean,
  >             'score': int,
  >             'comment': string,
  >             'create_time': date
  >         }
  >     }
  > 	返回数据: HomeworkopBO
  > 	// 获取类型
  > 	/question-types-get
  > 	method: GET
  > 	返回数据: List<String>
  > 	// 新建类型
  > 	/question-type-add
  > 	method: POST
  > 	请求体:
  > 	{
  >         'tid': int,
  >         'id': int,
  >         // create: cid; update: qid
  >     	// type: create 0; delete 0/typeID
  >        	'question': {
  >         	'id': int,
  >             'title': string,
  >             'extension': string,
  >             'score': int,
  >             'isFile': boolean,
  >             'answer': string,
  >             'comment': string,
  >             'create_time': date
  >     	},
  > 		'type': string
  >     }
  > 	返回数据: boolean
  > 	// 删除类型
  > 	/question-type-delete
  > 	method: POST
  > 	请求体:
  > 	{
  >         'tid': int,
  >         'id': int,
  >         // create: cid; update: qid
  >     	// type: create 0; delete 0/typeID
  >        	'question': {
  >         	'id': int,
  >             'title': string,
  >             'extension': string,
  >             'score': int,
  >             'isFile': boolean,
  >             'answer': string,
  >             'comment': string,
  >             'create_time': date
  >     	},
  > 		'type': string
  >     }
  > 	返回数据: boolean
  > 	// 全部回答获取
  > 	/results-get
  > 	method: GET
  > 	返回数据: ResultQuestionDTO
  > 	{
  >         'result': {
  >             'id': int,
  >             'content': string,
  >             'isFile': boolean,
  >             'isCheck': boolean,
  >             'score': int,
  >             'comment': string,
  >             'create_time': date
  >         },
  >         'student': {
  >             'id': int,
  >             'name': string,
  >             'password_hash': string,
  >             'head_image': string,
  >             'introduction': string,
  >             'email': string,
  >             'create_time': date
  >         },
  >         'teacher': {
  >             tid: tname
  >         },
  >         'question': {
  >             qid: qname
  >         }
  >     }
  > 	// 获取指定ID的回答
  > 	/result-get
  > 	method: GET
  > 	请求参数: rid <int>
  > 	返回数据: ResultQuestionDTO
  > 	// 获取指定学生的回答
  > 	/result-get-student
  > 	method: GET
  > 	请求参数: sid <int>
  > 	返回数据: ResultQuestionDTO
  > 	// 获取指定课程的回答
  > 	/result-get-course
  > 	method: GET
  > 	请求参数: cid <int>
  > 	返回数据: ResultQuestionDTO
  > 	// 新建回答
  > 	/result-create
  > 	method: POST
  > 	请求体: 
  > 	{
  >         'uid': int,
  >         'cid': int,
  >         'qid': int,
  >         'result': {
  >             'id': int,
  >             'content': string,
  >             'isFile': boolean,
  >             'isCheck': boolean,
  >             'score': int,
  >             'comment': string,
  >             'create_time': date
  >         }
  >     }
  > 	返回数据: HomeworkOpBO
  > 	// 更新回答
  > 	/result-update
  > 	method: POST
  > 	请求体: 
  > 	{
  >         'uid': int,
  >         'cid': int,
  >         'qid': int,
  >         'result': {
  >             'id': int,
  >             'content': string,
  >             'isFile': boolean,
  >             'isCheck': boolean,
  >             'score': int,
  >             'comment': string,
  >             'create_time': date
  >         }
  >     }
  > 	返回数据: HomeworkOpBO
  > 	// 删除回答
  > 	/result-delete
  > 	method: POST
  > 	请求体: 
  > 	{{
  >         'uid': int,
  >         'cid': int,
  >         'qid': int,
  >         'result': {
  >             'id': int,
  >             'content': string,
  >             'isFile': boolean,
  >             'isCheck': boolean,
  >             'score': int,
  >             'comment': string,
  >             'create_time': date
  >         }
  >     }}
  > 	返回数据: HomeworkOpBO
  > ```
  >
  > ```
  > // User
  > /user-sql
  > 	// 用户注册
  > 	/user-register
  > 	method: POST
  > 	请求体:
  > 	{
  >         'user': {
  >             'id': int,
  >             'name': string,
  >             'password_hash': string,
  >             'head_image': string,
  >             'introduction': string,
  >             'email': string,
  >             'create_time': date
  >         },
  >         'roles': [
  >             string, ...
  >         ]
  >     }
  >     返回数据: UserOpBO
  >     {
  >     	'isSuccess': boolean,
  >          'info': Map<String, Object>
  >     }
  > 	// 用户登录
  > 	/user-login
  > 	method: GET
  > 	请求参数: email <string>
  > 	返回数据: UserRoleDTO
  > 	{
  >         'user': {
  >             'id': int,
  >             'name': string,
  >             'password_hash': string,
  >             'head_image': string,
  >             'introduction': string,
  >             'email': string,
  >             'create_time': date
  >         },
  >         'roles': [
  >             string, ...
  >         ]
  >     }
  >     // 获取全部用户
  >     /users-get
  >     method: GET
  >     返回数据: UserRoleDTO
  >     // 通过 ID 获取用户
  >     /user-get-id
  >     method: GET
  >     请求参数: id <int>
  >     返回数据: UserRoleDTO
  >     // 用户注销
  >     /user-delete-id
  >    	method: POST
  >     请求体:
  >     {
  >     	'id': int
  >     }
  > 	返回数据: UserOpBO
  > 	// 用户注销(通过 Email)
  > 	/user-delete
  > 	method: POST
  > 	请求体:
  >     {
  >     	'email': string
  >     }
  > 	返回数据: UserOpBO
  > 	// 用户更新
  > 	/user-update
  > 	method: POST
  > 	请求体: UserRoleDTO
  > 	返回参数: UserOpBO
  > 	// 用户数据更新
  > 	/user-info-update
  > 	method: POST
  > 	请求体:
  > 	{
  >         'id': int,
  >         'name': string,
  >         'password_hash': string,
  >         'head_image': string,
  >         'introduction': string,
  >         'email': string,
  >         'create_time': date
  >     }
  > 	返回数据: UserOpBO
  > 	// 用户角色更新
  > 	/user-role-update
  > 	method: POST
  > 	请求体: URoleDTO
  > 	{
  >         'uid': int,
  >         'roles': [string]
  >     }
  > 	返回数据: UserOpBO
  > ```

#### ElasticSearch

+ 分词搜索

+ 优化数据访问效率

  > 数据内容作为文档存储在 ES 中, 数据的 ID 作为 文档 ID 存储
  >
  > > 即搜索文档内容, 寻找匹配的内容, 再通过返回的文档 ID 从 DB 中取出完整的数据

> 访问接口(未从 Gateway 暴露, 且未配置 Cors):
>
> ```
> // CUD
> /es-data
> // 返回数据全部都是
> {
> 	'isSuccess': Boolean,
> 	'map': Map<String, Object>
> }
> 	// 目录创建
> 	/index-create
> 	method: POST
> 	请求体: 
> 	{
> 		'option': 1,
> 		'index': ${index-name}
> 	}
> 	// 文档创建
> 	/doc-create
> 	method: POST
> 	请求体: 
> 	{
> 		'option': 2,
> 		'index': ${index-name},
> 		'size': ${doc-id},
> 		'objects': [
> 			${doc}
> 		] // $('objects').length = 1
> 	}
> 	// 批量文档创建
> 	/docs-create
> 	method: POST
> 	请求体: 
> 	{
> 		'option': 3,
> 		'index': ${index-name},
> 		'objects': [
> 			${doc1},
> 			${doc2},
> 			...
> 		] // $('objects').length > 1
> 	}
> 	// 目录删除
> 	/index-delete
> 	method: POST,
> 	请求体: 
> 	{
> 		'option': 4,
> 		'index': ${index-name}
> 	}
> 	// 文档删除
> 	/doc-delete
> 	method: POST
> 	请求体: 
> 	{
> 		'option': 5,
> 		'index': ${index-name},
> 		'size': ${doc-id}
> 	}
> 	// 文档更新
> 	/doc-update
> 	method: POST
> 	请求体: 
> 	{
> 		'option': 6,
> 		'index': ${index-name},
> 		'size': ${doc-id},
> 		'objects': [
> 			${doc}
> 		] // $('objects').length = 1
> 	}
> // Search
> /es-search
> // 原本有请求体参数, 但是 GET 方法带有请求体在很多框架上不适用,因此删去
> 	// 全匹配
> 	/match-all
> 	method: GET
> 	返回数据: List<Integer>
> 	// 全搜索
> 	/search-all
> 	method: GET
> 	返回数据: List<Integer>
> 	// term-search
> 	/term-search
>     method: GET
> 	返回数据: List<Integer>
> 	// wildCard-search
> 	/wildCard-search
>     method: GET
> 	返回数据: List<Integer>
> 	// match-search
> 	/match-search
>     method: GET
> 	返回数据: List<Integer>
> ```

#### FileStore

+ 文件/图片存储

  > 访问接口(未配置 Cors):
  >
  > ```
  > // 上传
  > /upload
  > 	// 单张图片
  > 	/image
  > 	method: POST
  > 	请求参数: 
  > 		image <Mutipart>
  > 		uid <Integer>
  > 	返回数据: 文件存储位置
  > 	// 单个文件
  > 	/file
  > 	method: POST
  > 	请求参数: 
  > 		file <Mutipart>
  > 		uid <Integer>
  > 	返回数据: 文件存储位置
  > 	// 多张图片
  > 	/images
  > 	method: POST
  > 	请求参数: 
  > 		images <Mutipart[]>
  > 		uid <Integer>
  > 	返回数据: 文件存储位置
  > 	// 多个文件
  > 	/files
  > 	method: POST
  > 	请求参数: 
  > 		files <Mutipart[]>
  > 		uid <Integer>
  > 	返回数据: 文件存储位置
  > // 下载
  > /download
  > 	// 文件/图片下载
  > 	/download
  > 	method: GET
  > 	请求参数: name <string>
  > 	// 图片显示
  > 	/show
  > 	method: GET
  > 	请求参数: image-name <string>
  > ```

#### User&Homework&Course +<br> Creator&Respondent&CourseManager +<br> Teacher&Student&Admin

+ 将三大主要模块分为三个服务, 减轻服务器压力
+ 将数据输入, 数据处理, 数据入库进行划分, 减轻服务器压力

> + 访问接口(未从 Gateway 暴露, 且未实现 Cors 设置):
>
>   + User
>
>     +  注册
>       `/user-sql/user-register(SQLConnection) ->`
>       ` /user/user-register(User) ->` 
>       ` /api/register(Register[UserIOService -> LoginController])`
>     + 登录
>       `/user-sql/user-login(SQLConnection) ->`
>       ` /user/user-login(User) ->` 
>        ` /api/login(Register[UserIOService -> LoginController])`
>     + 全部获取
>       `/user-sql/users-get(SQLConnection) ->`
>       ` /user/users-get(User) ->` 
>       ` /search/user/all(Register[UserSearchService -> FindService -> SearchController])`
>     + 通过 ID 获取
>       `/user-sql/user-get-id(SQLConnection) ->`
>       ` /user/user-get-id(User) ->` 
>       ` /search/user/get(Register[UserSearchService -> FindService -> SearchController])`
>     + 通过 ID 删除
>       `/user-sql/user-delete-id(SQLConnection) ->`
>       ` /user/user-delete-id(User) ->` 
>       ` 无(Register 只接入但未使用[UserIOService])`
>     + 通过 Email 删除/注销
>       `/user-sql/user-delete(SQLConnection) ->`
>       ` /user/user-delete ->` 
>       ` /api/logoff/id & /api/logoff/email (Register<id -> email> [UserIOService -> LoginController])`
>     + 用户更新
>       `/user-sql/user-update(SQLConnection) ->`
>       ` /user/user-upda te(User) ->` 
>       ` /user/update/all(Register [UserIOService -> UserInfoController])`
>     + 用户信息更新
>       `/user-sql/user-info-update(SQLConnection) ->`
>       ` /user/user-info-update(User) ->` 
>       ` /user/update/info(Register [UserIOService -> UserInfoController])`
>     + 用户角色更新
>       `/user-sql/user-role-update(SQLConnection) ->`
>       ` /user/user-role-update(User) ->` 
>       ` /user/update/role(Register [UserIOService -> UserInfoController])`
>
>   + Homework(Result)
>
>     + 回答全部获取
>       `/homework-sql/results-get(SQLConnection) ->`
>       ` /result/results-get(Homework) ->` 
>       ` /query/result/all(Respondent[ResultSelectService -> ResultFindController]) ->` 
>       ` /search/result/all(Register[FindService -> SearchController])`
>
>     + 获取指定 ID 回答
>       `/homework-sql/result-get(SQLConnection) ->`
>       ` /result/result-get(Homework) ->` 
>       ` /query/result/id(Respondent[ResultSelectService -> ResultFindController]) ->` 
>       ` /search/result/get(Register[FindService -> SearchController])`
>
>     + 获取指定 Course 回答
>       `/homework-sql/result-get-course(SQLConnection) ->`
>       ` /result/result-get-course(Homework) ->` 
>       ` /query/result/course(Respondent[ResultSelectService -> ResultFindController]) ->` 
>       ` 无(Register 只接入未使用[FindService])`
>
>     + 获取指定学生的回答
>
>       `/homework-sql/result-get-student(SQLConnection)->`
>       ` /result/result-get-student(Homework) ->` ` /query/result/student(Respondent[ResultSelectService -> ResultFindController]) ->` 
>       ` 无(Register 只接入未使用[FindService])`
>
>     + 新建回答
>
>       `/homework-sql/result-create(SQLConnection) -> `
>       `/result/result-create(Homework) ->`
>       ` /respondent/new(Respondent[ResultIOService -> ResultHandleController]) ->` 
>       ` /s/c/result(Student[CUDController]) ->` 
>       ` /operation/student/c/result(Register[StudentController])`
>
>     + 回答更新
>
>       `/homework-sql/result-update(SQLConnection) -> `
>       `/result/result-update(Homework) ->`
>       ` /respondent/update(Respondent[ResultIOService -> ResultHandleController]) ->` 
>       ` /s/u/result(Student[CUDController]) ->` 
>       ` /operation/student/u/result(Register[StudentController])`
>
>     + 回答删除
>
>       `/homework-sql/result-delete(SQLConnection) -> `
>       `/result/result-delete(Homework) ->`
>       ` /respondent/delete(Respondent[ResultIOService -> ResultHandleController]) ->` 
>       ` /s/d/result(Student[CUDController]) ->` 
>       ` /operation/student/d/result(Register[StudentController])`
>
>     + 批改问题
>
>       `/homework-sql/result-correct(SQLConnection) ->`
>       ` /question/result-correct(Homework) ->` 
>       ` /create/correct(Creator[QuestionIOService -> QuestionHandleController]) ->` 
>       ` /t/ao/correct/question(Teacher[AddOpController]) ->` 
>       `/operation/teacher/ao/correct/question(Register[TeacherController])`
>
>   + Homework(Question)
>
>     + 问题全部获取
>
>       `/homework-sql/questions-get(SQLConnection) ->`
>       ` /question/questions-get(Homework) ->` 
>       ` /query/question/all(Creator[QuestionSelectService -> QuestionFindController]) ->` 
>       ` /search/question/all(Register[FindService -> SearchController])`
>
>     + 获取指定 ID 问题
>
>       `/homework-sql/question-get(SQLConnection) ->`
>       ` /question/question-get(Homework) ->` 
>       ` /query/question/id(Creator[QuestionSelectService -> QuestionFindController]) ->` 
>       ` /search/question/get(Register[FindService -> SearchController])`
>
>     + 获取指定老师的问题
>
>       `/homework-sql/question-get-tid(SQLConnection) ->`
>       ` /question/question-get-tid(Homework) ->` 
>       ` /query/question/teacher(Creator[QuestionSelectService -> QuestionFindController]) ->` 
>       ` 无(Register 只接入未使用[FindService])`
>
>     + 获取指定类型的问题
>
>       `/homework-sql/question-get-type(SQLConnection) ->`
>       ` /question/question-get-type(Homework) ->` 
>       ` /query/question/type(Creator[QuestionSelectService -> QuestionFindController]) ->` 
>       ` 无(Register 只接入未使用[FindService])`
>
>     + 获取指定 Course 的问题
>
>       `/homework-sql/question-get-course(SQLConnection) ->`
>       ` /question/question-get-course(Homework) ->` 
>       ` /query/question/course(Creator[QuestionSelectService -> QuestionFindController]) ->` 
>       ` 无(Register 只接入未使用[FindService])`
>
>     + 新建问题
>
>       `/homework-sql/question-create(SQLConnection) ->`
>       ` /question/question-create(Homework) ->` 
>       ` /create/new(Creator[QuestionIOService -> QuestionHandleController]) ->` 
>       ` /t/c/question(Teacher[CUDController]) ->` 
>       ` /operation/teacher/c/question(Register[TeacherController])`
>
>     + 问题更新
>
>       `/homework-sql/question-update(SQLConnection) ->`
>       ` /question/question-update(Homework) ->` 
>       ` /create/update(Creator[QuestionIOService -> QuestionHandleController]) ->` 
>       ` /t/u/question(Teacher[CUDController]) ->` 
>       ` /operation/teacher/u/question(Register[TeacherController])`
>
>     + 问题删除
>
>       `/homework-sql/question-delete(SQLConnection) ->`
>       ` /question/question-delete(Homework) ->` 
>       ` /create/delete(Creator[QuestionIOService -> QuestionHandleController]) ->` 
>       ` /t/d/question(Teacher[CUDController]) ->` 
>       ` /operation/teacher/d/question(Register[TeacherController])`
>
>     + 类型全部获取
>
>       `/homework-sql/question-types-get(SQLConnection) ->`
>       ` /question/question-types-get(Homework) ->` 
>       ` /query/question/all-type(Creator[QuestionSelectService -> QuestionFindController]) ->` 
>       ` /search/type/all(Register [FindService -> SearchController])`
>
>     + 添加问题类型
>
>       `/homework-sql/question-type-add(SQLConnection) -> /question/question-type-add(Homework) -> /create/new/type(Creator[QuestionIOService -> QuestionHadnleController]) -> /t/c/type(Teacher[CUDController]) -> /operation/teacher/c/type(Register [TeacherController])`
>
>     + 删除问题类型
>
>       `/homework-sql/question-type-delete(SQLConnection) -> /question/question-type-delete(Homework) -> /create/delete/type(Creator[QuestionIOService -> QuestionHadnleController]) -> /t/d/type(Teacher[CUDController]) -> /operation/teacher/d/type(Register [TeacherController])`
>
>   + Course
>
>     + 课程全部获取
>
>       `/course-sql/courses-get(SQLConnection) ->`
>       ` /course/courses-get(Course) ->` 
>       ` /query/course/all(CourseManager[CourseSelectService -> CourseFindController]) ->` 
>       ` /search/course/all(Register[FindService -> SearchController])`
>
>     + 获取指定ID课程
>
>       `/course-sql/course-get(SQLConnection) ->`
>       ` /course/course-get(Course) ->` 
>       ` /query/course/id(CourseManager[CourseSelectService -> CourseFindController]) ->` 
>       ` /search/course/get(Register[FindService -> SearchController])`
>
>     + 获取指定名称的课程
>
>       `/course-sql/courses-get-name(SQLConnection) ->`
>       ` /course/courses-get-name(Course) ->` 
>       ` /query/course/name(CourseManager[CourseSelectService -> CourseFindController]) ->` 
>       ` 无(Register[FindService])`
>
>     + 获取指定老师教授的课程
>
>       `/course-sql/courses-get-teacher(SQLConnection) ->`
>       ` /course/courses-get-teacher(Course) -> ` 
>       `/query/course/teacher(CourseManager[CourseSelectService -> CourseFindController]) ->`
>       ` 无(Register[FindService])`
>
>     + 获取指定学生选修的课程
>
>       `/course-sql/courses-get-student(SQLConnection) ->`
>       ` /course/courses-get-student(Course) ->` 
>       ` /query/course/student(CourseManager[CourseSelectService -> CourseFindController]) ->` 
>       ` 无(Register[FindService])`
>
>     + 新建课程
>
>       `/course-sql/create-course(SQLConnection) ->`
>       ` /course/create-course(Course) ->` 
>       ` /course-handle/teacher/new(CourseManager[CourseTeacherService -> CourseHandleController]) ->` 
>       ` /t/c/course(Teacher[CUDController]) ->` 
>       ` /operation/teacher/c/course(Register[TeacherController])`
>
>     + 课程名更新
>
>       `/course-sql/course-update-name(SQLConnection) ->`
>       ` /course/course-update-name(Course) ->` 
>       ` /course-handle/teacher/update(CourseManager[CourseTeacherService -> CourseHandleController]) ->` 
>       ` /t/u/course(Teacher[CUDController]) ->` 
>       ` /operation/teacher/u/course(Register[TeacherController])`
>
>     + 学生选修
>
>       `/course-sql/course-add-student(SQLConnection) ->`
>       ` /course/course-add-student(Course)->` 
>       ` /course-handle/student/add(CourseManager[CourseStudentService -> CourseHandleController]) ->` 
>       ` /s/ao/add/course(Student[AddOpController]) ->` 
>       ` /operation/student/ao/add/course(Register[StudentController])`
>
>     + 老师加入
>
>       `/course-sql/course-add-teacher(SQLConnection) ->`
>       ` /course/course-add-teacher(Course) ->` 
>       ` /course-handle/teacher/add(CourseManager[CourseTeacherService -> CourseHandleController]) ->` 
>       ` /t/ao/add/course(Teacher[AddOpController]) ->` 
>       ` /operation/teacher/ao/add/course(Register[TeacherController])`
>
>     + 学生退课
>
>       `/course-sql/course-drop-student(SQLConnection) ->`
>       ` /course/course-drop-student(Course)->` 
>       ` /course-handle/student/drop(CourseManager[CourseStudentService -> CourseHandleController]) ->` 
>       ` /s/ao/drop/course(Student[AddOpController]) ->` 
>       ` /operation/student/ao/drop/course(Register[StudentController])`
>
>     + 老师退出
>
>       `/course-sql/course-drop-teacher(SQLConnection) ->`
>       ` /course/course-drop-teacher(Course) ->` 
>       ` /course-handle/teacher/drop(CourseManager[CourseTeacherService -> CourseHandleController]) ->` 
>       ` /t/ao/drop/course(Teacher[AddOpController]) ->` 
>       ` /operation/teacher/ao/drop/course(Register[TeacherController])`
>
>     + 课程删除
>
>       `/course-sql/course-delete(SQLConnection) ->`
>       ` /course/course-delete(Course) ->` 
>       ` /course-handle/teacher/delete(CourseManager[CourseTeacherService -> CourseHandleController]) ->` 
>       ` /t/d/course(Teacher[CUDController]) ->` 
>       ` /operation/teacher/d/course(Register[TeacherController])`
>
> + Student & Teacher & Admin 将 Register 传入的 CourseInDTO/QuestionInDTO/ResultInDTO 转换成下游服务需要的实体类型
>
> + Creator & Respondent & CourseManager 将 S.T.A. 服务传来的实体内的数据根据操作进行检验, 以防存入数据出错

#### Register

+ 总入口, 将数据分门别类的传输到对应的服务上

+ 将数据存入 ES 中, 提高搜索查询的效率

+ 使用 JWT 实现访问鉴权

  > + 访问接口(未配置 Cors):
  >
  > ```
  > // LoginController
  > /api
  > 	// 登录
  > 	/login
  > 	method: POST
  > 	请求体: 
  > 	{
  > 		'account': string, // email
  > 		'password': string 
  > 	}
  > 	返回数据: UserVO
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'isAdmin': boolean,
  > 		'isTeacher': boolean,
  > 		'isStudent': boolean,
  > 		'uid': int,
  > 		'object': string
  > 	} // code != 200 时, isAdmin & isTeacher & isStudent & uid 无效
  > 	// 注册
  > 	/register
  > 	method: POST
  > 	请求体: 
  > 	{
  > 		'name': string,
  > 		'password': string,
  > 		'email': string
  > 	}
  > 	返回数据: UserVO
  > 	// 注销(通过id)
  > 	/logoff/id
  > 	method: POST
  > 	请求参数: uid <int>
  > 	返回数据: UserVO
  > 	// 注销(通过email)
  > 	/logoff/email
  > 	method: POST
  >     请求参数: email <string>
  > 	返回数据: UserVO
  > ```
  >
  > ```
  > // UserInfoController
  > /user
  > 	// 用户更新
  > 	/update/all
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'uid': int,
  > 		'user': {
  > 			'id': int,
  > 			'name': string,
  > 			'password_hash': string,
  > 			'head_image': string,
  > 			'introduction': string,
  > 			'email': string,
  > 			'create_time': date // 可省略
  > 		},
  > 		'roles': [string]
  > 	}
  > 	返回数据: UserVO
  > 	// 用户数据更新
  > 	/update/info
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'uid': int,
  > 		'user': {
  > 			'id': int,
  > 			'name': string,
  > 			'password_hash': string,
  > 			'head_image': string,
  > 			'introduction': string,
  > 			'email': string,
  > 			'create_time': date // 可省略
  > 		}
  > 	}
  > 	返回数据: UserVO
  > 	// 用户角色更新
  > 	/update/role
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'uid': int,
  > 		'roles': [string] 
  > 	}
  > 	返回数据: UserVO
  > ```
  >
  > ```
  > // SearchController
  > /search
  > 	// 用户
  > 		// 全部
  > 		/user/all
  > 		method: GET
  > 		请求参数:
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'user': user,
  > 				'roles': [role]
  > 			]
  > 		}
  > 		// 查询
  > 		/user/find
  > 		method: GET
  > 		请求参数:
  > 			value <string>
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'user': user,
  > 				'roles': [role]
  > 			]
  > 		}
  > 		// 指定ID
  > 		/user/get
  > 		method: GET
  > 		请求参数:
  > 			uid <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'user': user,
  > 				'roles': [role]
  > 			] // 单个元素的数组
  > 		}
  > 	// 课程
  > 		// 全部
  > 		/course/all
  > 		method: GET
  > 		请求参数:
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'course': course,
  > 				'teachers': [user],
  > 				'students': [user]
  > 			]
  > 		}
  > 		// 查询
  > 		/course/find
  > 		method: GET
  > 		请求参数:
  > 			value <string>
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'course': course,
  > 				'teachers': [user],
  > 				'students': [user]
  > 			]
  > 		}
  > 		// 指定ID
  > 		/course/get
  > 		method: GET
  > 		请求参数:
  > 			cid <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'course': course,
  > 				'teachers': [user],
  > 				'students': [user]
  > 			]
  > 		}
  > 	// 问题
  > 		// 全部
  > 		/question/all
  > 		method: GET
  > 		请求参数:
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'question': question,
  > 				'questionType': string,
  > 				'teacher': user,
  > 				'course': {
  > 					cid: cname
  > 				},
  > 				'results': {
  > 					sid: rid,
  > 					...
  > 				}
  > 			]
  > 		}
  > 		// 查询
  > 		/question/find
  > 		method: GET
  > 		请求参数:
  > 			value <string>
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'question': question,
  > 				'questionType': string,
  > 				'teacher': user,
  > 				'course': {
  > 					cid: cname
  > 				},
  > 				'results': {
  > 					sid: rid,
  > 					...
  > 				}
  > 			]
  > 		}
  > 		// 指定ID
  > 		/question/get
  > 		method: GET
  > 		请求参数:
  > 			qid <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'question': question,
  > 				'questionType': string,
  > 				'teacher': user,
  > 				'course': {
  > 					cid: cname
  > 				},
  > 				'results': {
  > 					sid: rid,
  > 					...
  > 				}
  > 			]
  > 		}
  > 	// 回答
  > 		// 全部
  > 		/result/all
  > 		method: GET
  > 		请求参数:
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'result': result,
  > 				'student': user,
  > 				'teacher': {
  > 					tid: tname
  > 				},
  > 				'question': {
  > 					qid: qtitle
  > 				}
  > 			]
  > 		}
  > 		// 查询
  > 		/result/find
  > 		method: GET
  > 		请求参数:
  > 			value <string>
  > 			from <int>
  > 			size <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'result': result,
  > 				'student': user,
  > 				'teacher': {
  > 					tid: tname
  > 				},
  > 				'question': {
  > 					qid: qtitle
  > 				}
  > 			]
  > 		}
  > 		// 指定ID
  > 		/result/get
  > 		method: GET
  > 		请求参数:
  > 			rid <int>
  > 		返回数据:
  > 		{
  > 			'code': int,
  > 			'message': string,
  > 			'objects': [
  > 				'result': result,
  > 				'student': user,
  > 				'teacher': {
  > 					tid: tname
  > 				},
  > 				'question': {
  > 					qid: qtitle
  > 				}
  > 			]
  > 		}
  > ```
  >
  > ```
  > // StudentController
  > /operation/student
  > 	// 新建回答
  > 	/c/result
  > 	method: POST
  > 	请求体:
  > 	{
  >         'qid': int,
  >         'cid': int,
  >         'sid': int,
  >         'result': {
  >             'content': string,
  >             'isFile': boolean
  >         }
  >     }
  > 	返回数据: StudentVO
  > 	{
  > 		'code': int,
  > 		'msg': rid, // code = 200
  > 		'info': rid 值 // code = 200
  > 	}
  > 	// 更新回答
  > 	/u/result
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'qid': int,
  > 		'cid': int,
  > 		'sid': int,
  > 		'result': {
  > 			'id': int,
  > 			'content': string,
  > 			'isFile': boolean
  > 		}
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': 更新后的 result 信息 // code = 200
  > 	}
  > 	// 删除回答
  > 	/d/result
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'rid': int,
  > 		'sid': int
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': 删除前的 result 信息 // code = 200
  > 	}
  > 	// 选修课程
  > 	/ao/add/course
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'cid': int,
  > 		'sid': int
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': string
  > 	}
  > 	// 退课
  > 	/ao/add/course
  > 	method: POST
  > 		请求体:
  > 	{
  > 		'cid': int,
  > 		'sid': int
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': string
  > 	}
  > ```
  >
  > ```
  > // TeacherController
  > /operation/teacher
  > 	// 新建课程
  > 	/c/course
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int,
  > 		'course': {
  > 			'name': string
  > 		}
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': cid, // code = 200
  > 		'info': cid 值 // code = 200
  > 	}
  > 	// 更新课程
  > 	/u/course
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int, 
  > 		'course': {
  > 			'id': int,
  > 			'name': string
  > 		}
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string, 
  > 		'info': 更新后的 course 数据 // code = 200
  > 	}
  > 	// 删除课程
  > 	/d/course
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int, 
  > 		'cid': int
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string, 
  > 		'info': 删除前的 course 数据 // code = 200
  > 	}
  > 	// 老师加入课程
  > 	/ao/add/course
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int, 
  > 		'cid': int
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string, 
  > 		'info': string
  > 	}
  > 	// 老师退出课程
  > 	/ao/drop/course
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int, 
  > 		'cid': int
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string, 
  > 		'info': string
  > 	}
  > 	// 新建问题
  > 	/c/question
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int,
  > 		'cid': int,
  > 		'question': {
  > 			'title': string,
  > 			'extension': string,
  > 			'score': int,
  > 			'isFile': boolean,
  > 			'answer': string,
  > 			'comment': string
  > 		},
  > 		'type': string
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': qid, // code = 200
  > 		'info': qid 值 // code = 200
  > 	}
  > 	// 更新问题
  > 	/u/question
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int,
  > 		'qid': int,
  > 		'question': {
  > 			'id': int,
  > 			'title': string,
  > 			'extension': string,
  > 			'score': int,
  > 			'isFile': boolean,
  > 			'answer': string,
  > 			'comment': string
  > 		},
  > 		'type': string
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': 更新后的 question 数据 // code = 200
  > 	}
  > 	// 删除问题
  > 	/d/question
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int,
  > 		'qid': int
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': 删除后的 question 数据 // code = 200
  > 	}
  > 	// 新建问题类型
  > 	/c/type
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int,
  > 		'typeid': int, // 与 type 二选一
  > 		'type': string
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': string
  > 	}
  > 	// 删除问题类型
  > 	/d/type
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'tid': int,
  > 		'typeid': int, // 与 type 二选一
  > 		'type': string
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': string
  > 	}
  > 	// 批改回答
  > 	/ao/correct/question
  > 	method: POST
  > 	请求体:
  > 	{
  > 		'qid': int,
  > 		'cid': int,
  > 		'tid': int,
  > 		'correct': {
  > 			'id': int,
  > 			'content': string,
  > 			'isFile': boolean,
  > 			'isCheck': true,
  > 			'score': int,
  > 			'comment': string
  > 		}
  > 	}
  > 	返回数据:
  > 	{
  > 		'code': int,
  > 		'msg': string,
  > 		'info': string
  > 	}
  > ```
  >
  > + AdminController
  >   `/operation/admin`
  >
  >   > admin 直接与 Teacher & Student 操作对应
  >
  > ```
  > // UploadController
  > /upload
  > 	// 单个文件
  > 	/file
  > 	method: POST
  > 	请求参数:
  > 		file <Multipart>
  > 		uid <int>
  > 	返回:
  > 	{
  > 		'code': int,
  > 		'message': string,
  > 		'path': string
  > 	}
  > 	// 多个文件
  > 	/files
  > 	method: POST
  > 	请求参数:
  > 		files <Multipart[]>
  > 		uid <int>
  > 	返回:
  > 	{
  > 		'code': int,
  > 		'message': string,
  > 		'path': string
  > 	}
  > 	// 单张图片
  > 	/image
  > 	method: POST
  > 	请求参数:
  > 		image <Multipart>
  > 		uid <int>
  > 	返回:
  > 	{
  > 		'code': int,
  > 		'message': string,
  > 		'path': string
  > 	}
  > 	// 多张图片
  > 	/images
  > 	method: POST
  > 	请求参数:
  > 		images <Multipart[]>
  > 		uid <int>
  > 	返回:
  > 	{
  > 		'code': int,
  > 		'message': string,
  > 		'path': string
  > 	}
  > // DownloadController
  > /download
  > 	// 下载
  > 	/download/{name}
  > 	method: GET
  > 	// 显示
  > 	/show/image/{image-name}
  > 	method: GET
  > ```

#### Gateway

+ 统一所有微服务入口 (前端访问后端)

+ 实现路由转发, 以及请求过程中的负载均衡

+ 过滤处理

  > 访问接口:
  >
  > ```
  > /entry/** register 服务(总入口)接口
  > /store/** fileStore 服务(文件存储)接口
  > ```
