package com.example.data

enum class NodeCategory(val label: String, val colorHex: Long) {
    CS_BASICS("计算机基础", 0xFF00D9FF),
    FRONTEND("前端技术栈", 0xFF00F5A0),
    BACKEND("后端技术栈", 0xFF8F00FF),
    DATABASE("数据库技术", 0xFFFFB800),
    DEVOPS("DevOps与云原生", 0xFFFF6B6B),
    ADVANCED("计算机进阶", 0xFFFFD700)
}

data class HoneycombNode(
    val id: String,
    val title: String,
    val category: NodeCategory,
    val r: Int,  // Hexagonal grid coordinates
    val q: Int,
    val state: String = "LOCKED", // LOCKED, AVAILABLE, ACTIVE, COMPLETED, MASTERED
    val description: String = "",
    val details: List<String> = emptyList()
)

data class ProjectItem(
    val code: String,
    val title: String,
    val subtitle: String,
    val levelName: String,
    val levelNum: Int,
    val techStack: List<String>,
    val hours: Double,
    val difficulty: Int, // 1-5 stars
    val description: String,
    val milestones: List<Milestone>
)

data class Milestone(
    val order: Int,
    val title: String,
    val description: String,
    val testCriteria: String,
    val starterCode: String
)

object CuratedData {
    val categories = NodeCategory.values().toList()

    val initialNodes = listOf(
        // Layer 0: Core CS (408)
        HoneycombNode("cs1", "数据结构与算法", NodeCategory.CS_BASICS, 0, 0, "AVAILABLE", "涵盖线性表、栈、二叉树、红黑树、图及排序算法。", listOf("线性表与链表", "树与图论", "排序与查找", "并查集与红黑树")),
        HoneycombNode("cs2", "计算机组成原理", NodeCategory.CS_BASICS, 0, 1, "LOCKED", "微指令代码、数据的原补码浮点数表示与运算、CPU数据通路与指令集架构设计。", listOf("浮点数与原补码表示", "Cache与虚拟存储器", "CPU指令周期", "DMA与中断机制")),
        HoneycombNode("cs3", "操作系统内核", NodeCategory.CS_BASICS, 1, -1, "LOCKED", "核心讲授进程调度、虚拟存储置换、文件分区读取与死锁避免算法。", listOf("进程控制与经典线程同步", "虚拟页式存储与置换算法", "磁盘寻道与FAT管理", "系统接口与微守护态")),
        HoneycombNode("cs4", "计算机网络体系", NodeCategory.CS_BASICS, -1, 0, "LOCKED", "七层/五层网络分流，TCP三次握手四次挥手、拥塞避免机制与ARP/ICMP协议实操。", listOf("TCP握手挥手协议细节", "滑动窗口与拥塞控制", "IP寻址与子网划分规则", "WebSocket双工长连接机制")),
        HoneycombNode("cs5", "编译原理与语法分析", NodeCategory.CS_BASICS, -2, 0, "LOCKED", "解析词法分析、LL(1)与LR(1)语法分析、AST抽象语法树生成及JIT优化原理。", listOf("词法分析与有限自动机", "AST抽象语法树构建", "递归下降与LL/LR分析", "JIT即时编译与运行期优化")),
        HoneycombNode("cs6", "GOF设计模式体系", NodeCategory.CS_BASICS, 2, -2, "LOCKED", "深入面向对象设计原则、经典创建型/结构型/行为型模式与Spring AOP解耦实践。", listOf("SOLID面向对象设计规范", "观察者与发布订阅核心", "代理模式与硬核动态代理", "装饰器与策略模式融合")),

        // Layer 1: Front and Back Ends
        HoneycombNode("fe1", "HTML5 & CSS3布局", NodeCategory.FRONTEND, 0, -1, "AVAILABLE", "Flexbox、Grid网格体系、CSS 3D变换动画及流体液态自适应样式设计。", listOf("语义化结构规范", "Flexy & Grids弹性布局", "CSS3 3D翻转粒子特效", "Web 5A级无障碍支持")),
        HoneycombNode("fe2", "Modern JS & TS", NodeCategory.FRONTEND, 1, -2, "LOCKED", "ES6+、事件轮询异步、原型链闭包、高级类型体操及泛型工程实战。", listOf("原型闭包机制与作用域", "Promise/Async及事件循环", "高级泛型与类型守卫", "类型体操高难度体操")),
        HoneycombNode("fe3", "React & Hooks", NodeCategory.FRONTEND, -1, -1, "LOCKED", "JSX虚拟DOM机制，自定义Hooks提炼与Context跨层全局状态协调。", listOf("Fiber与虚拟DOM对比协调", "自定义Hooks函数解耦", "Context API配合Reducer", "Next.js SSR同构服务端渲染")),
        HoneycombNode("fe4", "Vue3 响应式底层", NodeCategory.FRONTEND, 0, -2, "LOCKED", "系统解密Vue3 Reactive与Ref代理劫持原理、精妙的双端Diff算法及Pinia全局共享流。", listOf("Proxy对象拦截与响应性", "Computed与Watch调度原理解析", "双端指针与现代VNode对比", "Pinia轻量状态树内部模型")),
        HoneycombNode("fe5", "Webpack & Vite构建", NodeCategory.FRONTEND, -2, -1, "LOCKED", "极速Vite构建工具基于浏览器原生ESM启动流、Webpack事件打包、TreeShaking及代码级HMR热加载实现方式。", listOf("Vite零包冷启动机制", "Webpack生命周期与Plugin开发", "TreeShaking摇树性能剪枝", "HMR热加载长连接通知")),

        // Backend
        HoneycombNode("be1", "SpringBoot微服务", NodeCategory.BACKEND, 1, 0, "AVAILABLE", "Spring IOC控制反转，拦截过滤及网关治理、微服务Feign声明式消费调用。", listOf("IOC容器与生命周期", "AOP切面拦截日志", "JPA事务与并发乐观锁", "Feign熔断与限流")),
        HoneycombNode("be2", "Go & Gin服务", NodeCategory.BACKEND, 2, -1, "LOCKED", "Go并发通道Channel，主进程携程调度机制，高效自建RESTful Web网关路由。", listOf("切片底层扩容与Map并发", "Go携程Goroutine调度", "Channel多路复用Select", "Gin拦截器与优雅退出")),
        HoneycombNode("be3", "Rust语言系统", NodeCategory.BACKEND, 2, 0, "LOCKED", "所有权系统(Ownership)，借用(Borrowing)生命周期与Tokio异步高并发运行时。", listOf("所有权生命周期作用域", "Safe/Unsafe模式转换", "Tokio多线程异步任务", "FFI与C/C++原生混合开发")),
        HoneycombNode("be4", "SpringCloud微服务平台", NodeCategory.BACKEND, 3, -1, "LOCKED", "Nacos注册中心治理、Sentinel分布式高频自适应限流与Sentinel弹性保护流、Seata分布式事务管理。", listOf("Nacos注册寻址与拉取机制", "Sentinel配置动态加载与阻断", "Gateway网关安全认证路由", "Seata AT两阶段提交方案")),
        HoneycombNode("be5", "Python FastAPI生态", NodeCategory.BACKEND, 3, 0, "LOCKED", "ASGI异步网关、Pydantic强类型规范、自包含依赖注入（DI）及高并发异步SQL/ORM结合实操。", listOf("ASGI异步单线程高效吞吐", "Pydantic数据模式高级强规整", "FastAPI弹性依赖解耦注入", "Motor与TortoiseAsync连接")),

        // Database
        HoneycombNode("db1", "MySQL索引事务", NodeCategory.DATABASE, -1, 1, "AVAILABLE", "B+树叶结点索引存储，ACID事务一致性级别、MVCC高并发多版本读取。", listOf("B+树索引树高度分析", "隔离状态与死锁避免", "MVCC多版本读与UndoLog", "Sharding分库分表策略")),
        HoneycombNode("db2", "Redis内存哨兵", NodeCategory.DATABASE, -1, 2, "LOCKED", "五大核心数据模型，AOF持久化，Sentinel高可用集群及分布式原子锁防刷。", listOf("底层跳表极其编码解析", "缓存穿透雪崩及双写保障", "Lua脚本高并发原子控制", "Sentinel哨兵与RedisCluster")),
        HoneycombNode("db3", "Elasticsearch检索", NodeCategory.DATABASE, -3, 1, "LOCKED", "系统拆解高性能全文检索底层、倒排索引与词条召回模型、集群Shard分片与高可用副本。", listOf("词法倒排与TF-IDF相关性计算", "分形检索与中文分词器IK集成", "分布式读写原理及Master机制", "ELK大容量日志系统综合调优")),
        HoneycombNode("db4", "MongoDB & NoSQL", NodeCategory.DATABASE, -3, 2, "LOCKED", "高可扩展BSON文档数据库、高并发分片集群模式、ClickHouse大容量列存储底盘特性及OLAP极速大数据汇总层。", listOf("MongoDB高平片与复制集机制", "列式数据库ClickHouse存储结构", "OLAP复杂分析聚合算子", "混合数据库双写同步策略")),

        // DevOps & Systems
        HoneycombNode("do1", "Docker容器化", NodeCategory.DEVOPS, -2, 1, "LOCKED", "Dockerfile多阶段解耦构建，Volume文件映射，自定义组网容器通信管理。", listOf("容器命名空间隔离原理", "多阶段多点高效构建", "Volume容器读写隔离", "Compose一键多服务编排")),
        HoneycombNode("do2", "Kubernetes编排", NodeCategory.DEVOPS, -2, 2, "LOCKED", "Pod、Deployment自愈，安全加密证书及滚动灰度升级管控系统。", listOf("Pod多节点自愈调配", "Ingress负载服务发现", "Helm模板镜像统一发布", "HPA动态监控水平伸缩")),
        HoneycombNode("do3", "CI/CD 自动化集成", NodeCategory.DEVOPS, -4, 1, "LOCKED", "构建一站式GitHub Actions自动化拉流、Jenkins声明式流水线及企业代码级SonarQube自动化检查和漏洞预排阻拦。", listOf("GitHub Runner极速测试机配给", "Jenkinsfile声明式语法实操", "SonarQube检测标准与安全闸门", "构建推送至公共DockerRegistry")),
        HoneycombNode("do4", "Prometheus分布式监控", NodeCategory.DEVOPS, -4, 2, "LOCKED", "时间序列PromQL、各种环境系统Exporter、Grafana可视化大屏面板及高容灾警报通知体系。", listOf("时间序列指标与PromQL拉流", "Exporter采集主进程指标", "Grafana优雅大屏配对与共享", "报警路由Alertmanager接入")),

        // Advanced
        HoneycombNode("adv1", "大厂百万并发核心架构", NodeCategory.ADVANCED, 1, 1, "LOCKED", "探论大厂生产环境、多机群分布式锁精妙设计、自适应瞬发限流及高可用防死锁架构。", listOf("Redisson分布式锁底层设计", "令牌桶与漏桶限流速率拦截", "服务熔断Degrade自恢复逻辑", "高并发长连接（Websocket）优化")),
        HoneycombNode("adv2", "大语言模型RAG应用开发", NodeCategory.ADVANCED, -1, 3, "LOCKED", "拆解AI检索增强文本技术生成（RAG）、现代嵌入式向量数据库检索、提示词动态注入及智能Agent自主决策模型。", listOf("RAG基础框架与Chunk碎片化", "Vector Embeddings相似检索", "智能模型Agent工具自主绑定", "多模态大组件提示工程流"))
    )

    val curatedProjects = listOf(
        ProjectItem(
            code = "P01",
            title = "个人数字名片",
            subtitle = "前端布局入门第一步",
            levelName = "🟢 等级一：萌芽期",
            levelNum = 1,
            techStack = listOf("HTML5", "CSS3", "Flexbox"),
            hours = 2.0,
            difficulty = 1,
            description = "创建一个精美的个人数字名片网页，展示个人基本信息、技能标签、社交链接，支持深色/浅色主题一键转化，建立自适应布局直觉。",
            milestones = listOf(
                Milestone(1, "语义化结构", "使用 header, main, section, footer 搭建语义清晰的个人名片基础框架，避免过度使用无标签div。", "名片可访问标签语义评分达标", "<!-- 编写你的语义 HTML 名片 -->\n<main class=\"card-container\">\n  <header>\n    <h1>全栈探险家</h1>\n  </header>\n</main>"),
                Milestone(2, "Flex 居中排版", "编写弹性样式表使名片在任何尺寸移动屏幕下完美水平居中，并为社交徽章云加上间距排版。", "CSS variables 正常调用并且在深浅主题中保持对比度", ":root {\n  --color-bg: #FAFAF9;\n  --color-text: #1C1917;\n}\n.card-container {\n  display: flex;\n  justify-content: center;\n}"),
                Milestone(3, "浮动 3D 过渡", "增加 CSS Transform 旋转特效与 hover 时的半透明发光毛玻璃圆角阴影扩散效果。", "触碰元素时 hover border glow 特效流畅运行", ".card-container:hover {\n  transform: translateY(-5px);\n  box-shadow: 0 10px 25px rgba(0, 245, 160, 0.2);\n}")
            )
        ),
        ProjectItem(
            code = "P02",
            title = "动态时钟仪表盘",
            subtitle = "数学角度与指针旋转动画",
            levelName = "🟢 等级一：萌芽期",
            levelNum = 1,
            techStack = listOf("JavaScript", "CSS 3D", "DOM", "math"),
            hours = 4.0,
            difficulty = 2,
            description = "模拟精美的物理仪表盘表盘，计算时分秒针对应得旋转角度。利用 JS 定时器进行每秒平滑角度修正，防抖防漂移。",
            milestones = listOf(
                Milestone(1, "画表盘刻度", "生成 60 个细密刻度线，其中每五个刻度自动增粗作为整点标示。运用 transform-origin 重置旋转圆心。", "刻度呈360度分布均匀且圆心对齐", "function renderTicks() {\n  // 动态创建刻度线\n}"),
                Milestone(2, "计算时分秒偏转角", "高精度计算秒针、分针、时针的偏转角度，并运用 CSS 变量在每一帧重刷。解决 59s 到 0s 指针倒卷漂移的痛点。", "秒针旋转角度 = seconds * 6 并且带小数位修正", "const hrAngle = (hours * 30) + (minutes * 0.5);\nconst minAngle = (minutes * 6) + (seconds * 0.1);"),
                Milestone(3, "添加定时器与自校准", "编写自校准 setInterval 引擎，每次 tick 检查本地高精准毫秒时间，自动消除定时器执行卡段漂移延迟。", "时间同步刷新不间断", "function tick() {\n  // 编写自校准时间循环\n}")
            )
        ),
        ProjectItem(
            code = "P13",
            title = "JWT用户认证后端",
            subtitle = "密码哈希加盐与Token分发",
            levelName = "🔵 等级二：生长期",
            levelNum = 2,
            techStack = listOf("Node.js", "Express", "JWT", "bcrypt"),
            hours = 6.0,
            difficulty = 3,
            description = "从零实现一个安全完整的用户注册、登录密码加盐哈希存储，分发 access / refresh token 双令牌机制的无状态用户系统。",
            milestones = listOf(
                Milestone(1, "密码 Bcrypt 哈希", "对输入明文密码进行 10 轮加盐哈希处理，防止数据库泄露导致彩虹表反查篡改。", "数据库不保存任何明文明文字符", "const salt = await bcrypt.genSalt(10);\nconst hash = await bcrypt.hash(password, salt);"),
                Milestone(2, "OAuth Token 设计", "签发 access_token (20分钟有效期) 及 refresh_token (7天有效期)，并将密钥保存到 .env 配置变量。", "签发的JWT包含用户UID和权限等级", "const token = jwt.sign({ uid: user.id }, process.env.JWT_SECRET, { expiresIn: '20m' });"),
                Milestone(3, "中间件权限阻击器", "编写统一保护请求授权的 express 中间件，提取 header 中的 Bearer Token 并进行严格过期比对。", "未带 Token 的请求自动报 401 Unauthorized", "function authMiddleware(req, res, next) {\n  // 解析 authorization header\n}")
            )
        ),
        ProjectItem(
            code = "P21",
            title = "全栈融合博客系统",
            subtitle = "前后端接口全链路会师",
            levelName = "🟡 等级三：成长期",
            levelNum = 3,
            techStack = listOf("React", "Express", "RESTful API", "CORS"),
            hours = 8.0,
            difficulty = 3,
            description = "合并前端 React 客户端和后端 Node-Express 验证服务，通过 Axios 拦截器注入 Auth Token，打通前后端完整生命周期数据流。",
            milestones = listOf(
                Milestone(1, "跨域资源共享配对", "解决 localhost 浏览器异端口跨域报错(CORS Error)，在后端白名单精准释放指定源以及 credentials 通行许可。", "OPTIONS 预检请求绿灯通行", "app.use(cors({\n  origin: 'http://localhost:5173',\n  credentials: true\n}));"),
                Milestone(2, "Axios 双流拦截器", "编写前端 HTTP 拦截网络层。请求时自动捎上 Token，在 Token 过期时利用 Refresh Token 自动静默重试更新。", "静默令牌更新完成，无阻碍后续请求", "axios.interceptors.response.use(response => response,\n  async error => {\n    // 如果 401 触发自动静默更新\n  }\n);"),
                Milestone(3, "Markdown 异步解析渲染", "获取后端富文章文本，引入异步按需加载包进行前端 Markdown 文档的高保真转义渲染，并加入 Prism 代码高亮。", "网页文章支持流式载入和代码美化", "import { parseSync } from './markdown';\nconst html = parseSync(content);")
            )
        ),
        ProjectItem(
            code = "P28",
            title = "高并发秒杀系统",
            subtitle = "Lua脚本与RabbitMQ异步降压",
            levelName = "🟡 等级三：成长期",
            levelNum = 3,
            techStack = listOf("Redis", "RabbitMQ", "SpringBoot", "Lua"),
            hours = 12.0,
            difficulty = 4,
            description = "打造承受每秒上万级超高突发压力的商品限时秒杀，编写防超卖、队列降载、Redis-Lua 共享内存原子控制服务。",
            milestones = listOf(
                Milestone(1, "Redis-Lua 原子扣减", "将判断是否有剩余库存以及对用户进行去重的多步判断逻辑编写成一段 Lua 脚本，避免传统数据库分流锁表崩溃。", "秒杀扣费完全由 Redis 原子拦截", "-- Lua 脚本\nlocal key = KEYS[1]\nlocal user = KEYS[2]\nlocal limit = tonumber(ARGV[1])\n-- 判断去重及扣除库存"),
                Milestone(2, "秒杀通道削峰分流", "秒杀成功的抢购意愿，发送到 AMQP 协议的 RabbitMQ 消息队列。后端多机轮询消费，进行异步入库下单。", "高并发流量化为匀速库写入", "rabbitTemplate.convertAndSend(\"seckillExchange\", \"seckillKey\", orderMessage);"),
                Milestone(3, "接口防暴力点击速率限制", "基于 Redis 滑动窗口核心拦截高频恶意 IP 流量，限制单账号每分钟访问频次，并引入验证码随机验证防刷。", "爆刷脚本自动封印 429 Too Many Requests", "public boolean checkLimit(String ip) {\n  // Redis-Zset 记录访问窗口时间线\n}")
            )
        ),
        ProjectItem(
            code = "P43",
            title = "分布式KV极速数据库",
            subtitle = "Raft共识强一致与LSM存储",
            levelName = "🔴 等级五：精通期",
            levelNum = 5,
            techStack = listOf("Rust", "gRPC", "Raft", "LSM-Tree"),
            hours = 16.0,
            difficulty = 5,
            description = "使用 Rust 实现多节点强一致存储。基于 Raft 共识协议，包含 Leader 选举、日志持久度复制、物理磁盘 LSM 引擎。",
            milestones = listOf(
                Milestone(1, "Raft 领导者选举机", "用 Rust 原生并发线程对 Raft 进行周期心跳建模，当节点由于失联超时切换为 Candidate 并拉票重构网络。", "新一届 Leader 平滑选举并建立全网络权威统治", "fn start_election(&mut self) {\n  self.state = State::Candidate;\n  self.request_vote();\n}"),
                Milestone(2, "LSM-Tree 闪存落盘", "构建内存 MemTable 并使用 SkipList 跳表维护有序，检测超过 64MB 时冻结为 Immutable 刷新为磁盘 SSTable 文件。", "磁盘写入极其平顺且全排序检索支持", "pub struct LsmEngine {\n  memtable: SkipList,\n  sstable_levels: Vec<SSTable>\n}"),
                Milestone(3, "gRPC 集群分流路由", "利用 Protocol Buffers 绑定客户端请求。当访问到 Follower 节点时，自动通过 Raft 通道转发给 Leader 写入。", "集群强一致读写返回成功", "service KVStore {\n  rpc Put(PutRequest) returns (PutResponse);\n}")
            )
        ),
        ProjectItem(
            code = "P32",
            title = "AI Agent 智能知识库",
            subtitle = "基于RAG检索与LLM自适应问答",
            levelName = "🟠 等级四：卓越期",
            levelNum = 4,
            techStack = listOf("FastAPI", "Gemini", "ChromaDB", "Python"),
            hours = 10.0,
            difficulty = 4,
            description = "打造企业级RAG检索增强知识库。分段读取本地PDF讲义并向量化保存，利用LangChain/Gemini大模型实现高效关联上下文问答。",
            milestones = listOf(
                Milestone(1, "文本Chunk切割分段", "设计Overlap重叠区间的分离器，保留上下文段落语境并生成384维Embedding特征向量。", "分段关联率与检索精确度达标", "def chunk_document(text, size=500, overlap=50):\n    # 编写文本按重叠度切割逻辑"),
                Milestone(2, "向量检索库存储", "配置 ChromaDB 内存向量数据库，建立正向索引并根据 cosine 相似度执行最高相似度 TOP-K 检索查找。", "向量相似度查询准确率高达90%以上", "db.add_documents(chunks)\nresults = db.similarity_search(query, k=3)"),
                Milestone(3, "Prompt 提纯与模型合成", "将检索的内容动态组装为高精准上下文提示词，传递给 Gemini 进行高度保真的事实推理生成，规避模型幻觉。", "AI 问答实现无幻觉、证据链完全可考证", "prompt = f\"基于以下内容回答：{context}\\n问题：{query}\"\nresponse = gemini.generate(prompt)")
            )
        ),
        ProjectItem(
            code = "P38",
            title = "高性能WebSocket聊天室",
            subtitle = "分段管道与多房高并发通信",
            levelName = "🟠 等级四：卓越期",
            levelNum = 4,
            techStack = listOf("Go", "WebSocket", "Redis PubSub", "React"),
            hours = 14.0,
            difficulty = 4,
            description = "使用 Go-Gorilla 模块与 Redis 发布订阅模式，自研支持多聊天分组、自动重连与断线历史消息缓存的企业级高吞吐即时通讯系统。",
            milestones = listOf(
                Milestone(1, "Goroutine管道分集", "为每一个客户端连接订阅独立的读/写管道，解耦心跳检测、断开机制与主进管道高频广播交互。", "协程并发数平稳无泄漏", "type Client struct {\n    hub *Hub\n    conn *websocket.Conn\n    send chan []byte\n}"),
                Milestone(2, "Redis多房间共享广播", "利用 Redis Channel 发布订阅机制无缝打通多集群环境下的跨进程实时消息分发以及数据同步。", "集群跨节点消息延迟小于50ms", "rdb.Publish(ctx, \"chat_room_\"+roomID, messageBytes)"),
                Milestone(3, "离线消息按需补发", "当连接短暂断开重连时，根据递增序列号极速从 Redis-SortedSet 中提取丢失的未读消息进行主动补发同步。", "历史断线未读重连接补发成功", "messages, _ := rdb.ZRangeByScore(ctx, \"room_\"+roomID, &redis.ZRangeBy{ Min: lastSeq, Max: \"+inf\" }).Result()")
            )
        )
    )
}
