/**
 * 数据库历史数据/Seed 数据的非规范 Remix Icon 类名别名映射表。
 * 用以保证历史数据（如缺少 -line 后缀）可正确解析并在图标选择器中高亮回显。
 */
export const ICON_ALIASES = {
  'ri-image-edit': 'ri-image-edit-line',
  'ri-code-s-slash': 'ri-code-s-slash-line',
  'ri-graduation-cap': 'ri-graduation-cap-line',
  'ri-terminal-box': 'ri-terminal-box-line',
  'ri-customer-service': 'ri-customer-service-line',
  'ri-advertisement': 'ri-advertisement-line',
  'ri-translate': 'ri-translate-2',
  'ri-font-size': 'ri-font-size-2',
  'ri-tools': 'ri-tools-line',
  'ri-server': 'ri-server-line',
  'ri-newspaper': 'ri-newspaper-line',
  'ri-palette': 'ri-palette-line',
  'ri-car': 'ri-car-line',
  'ri-sun': 'ri-sun-line'
}

/**
 * 规范化图标名称：除去前后空格，匹配别名表并修复不规范后缀
 */
export function normalizeIcon(icon) {
  const trimmed = (icon || '').trim()
  return ICON_ALIASES[trimmed] || trimmed
}

/**
 * 精选 60 个精美线条图标列表，分别对应 Seed 数据库中 60 个分类语义
 */
export const CATEGORY_ICONS = [
  // === 常用/通用 ===
  { class: 'ri-home-line', label: '主页', category: 'common', tags: ['home', '首页', 'shouye', '主页'] },
  { class: 'ri-apps-line', label: '应用', category: 'common', tags: ['apps', '应用', 'yingyong', '软件', '工具'] },
  { class: 'ri-global-line', label: '全球', category: 'common', tags: ['global', '全球', 'quanqiu', '网络', '外网', '出海'] },
  { class: 'ri-compass-line', label: '导航', category: 'common', tags: ['compass', '导航', 'daohang', '指南', '探索'] },
  { class: 'ri-folder-line', label: '分类', category: 'common', tags: ['folder', '分类', 'fenlei', '文件夹', '目录'] },
  { class: 'ri-bookmark-line', label: '书签', category: 'common', tags: ['bookmark', '书签', 'shuqian', '收藏', '网址'] },
  { class: 'ri-star-line', label: '星标', category: 'common', tags: ['star', '星标', 'xingbiao', '推荐', '优质'] },
  { class: 'ri-hashtag', label: '标签', category: 'common', tags: ['hashtag', '标签', 'biaoqian', '井号', 'tag'] },
  { class: 'ri-notification-line', label: '通知', category: 'common', tags: ['notification', '通知', 'tongzhi', '消息', '新闻'] },
  { class: 'ri-user-line', label: '用户', category: 'common', tags: ['user', '用户', 'yonghu', '账号', '个人'] },
  { class: 'ri-settings-line', label: '设置', category: 'common', tags: ['settings', '设置', 'shezhi', '系统', '管理'] },
  { class: 'ri-database-2-line', label: '数据', category: 'common', tags: ['database', '数据', 'shuju', '数据库', '存储'] },

  // === 音视频 ===
  { class: 'ri-palette-line', label: '设计', category: 'media', tags: ['palette', '设计', 'sheji', '艺术', '调色', '素材'] },
  { class: 'ri-image-line', label: '图像', category: 'media', tags: ['image', '图像', 'tuxiang', '图片', '照片', '素材'] },
  { class: 'ri-image-edit-line', label: '生图', category: 'media', tags: ['生图', '作图', '图片生成', '图像生成'] },
  { class: 'ri-film-line', label: '视频', category: 'media', tags: ['film', '视频', 'shipin', '电影', '影音', '放映'] },
  { class: 'ri-music-2-line', label: '音频', category: 'media', tags: ['music', '音频', 'yinpin', '音乐', '伴奏'] },
  { class: 'ri-headphone-line', label: '耳机', category: 'media', tags: ['headphone', '耳机', 'erji', '听歌', '音乐平台'] },
  { class: 'ri-mic-line', label: '麦克风', category: 'media', tags: ['mic', '麦克风', 'maikefeng', '录音', '语音', '配音'] },
  { class: 'ri-camera-line', label: '摄影', category: 'media', tags: ['camera', '摄影', 'sheying', '相机', '拍照'] },
  { class: 'ri-video-line', label: '影视', category: 'media', tags: ['video', '影视', '视频', '播放器', '视频平台'] },
  { class: 'ri-clapperboard-line', label: '剪辑', category: 'media', tags: ['clapperboard', '剪辑', 'jianji', '后期', '板板'] },
  { class: 'ri-paint-brush-line', label: '画笔', category: 'media', tags: ['paint', '画笔', 'huabi', '画画', '绘画', '手绘'] },
  { class: 'ri-brush-line', label: '画刷', category: 'media', tags: ['brush', '画刷', 'huashua', '涂鸦'] },
  { class: 'ri-pantone-line', label: '配色', category: 'media', tags: ['pantone', '配色', 'peise', '色卡', '排版'] },
  { class: 'ri-quill-pen-line', label: '文案', category: 'media', tags: ['文案', '撰写', '写作', '文案工具'] },

  // === 开发技术 ===
  { class: 'ri-code-s-slash-line', label: '编程', category: 'tech', tags: ['code', '编程', 'biancheng', '代码', '开发', '编程工具', '编程助手'] },
  { class: 'ri-terminal-box-line', label: '控制台', category: 'tech', tags: ['terminal', '控制台', 'kongzhitai', '终端', 'cmd', '开发平台', '科技博客'] },
  { class: 'ri-braces-line', label: '代码块', category: 'tech', tags: ['braces', '括号', 'daima', '代码', '程序'] },
  { class: 'ri-cpu-line', label: '芯片', category: 'tech', tags: ['cpu', '芯片', 'xinpian', '处理器', '算力'] },
  { class: 'ri-brain-line', label: '智能', category: 'tech', tags: ['brain', '智能', 'zhineng', 'ai', '大模型', '训练模型', '模型', '人工智能', '算法'] },
  { class: 'ri-server-line', label: '服务器', category: 'tech', tags: ['server', '服务器', 'fuwuqi', '后台', '后端', '后端框架'] },
  { class: 'ri-cloud-line', label: '云服务', category: 'tech', tags: ['cloud', '云', 'yun', '云主机', '云服务平台'] },
  { class: 'ri-git-branch-line', label: '托管', category: 'tech', tags: ['git', '托管', 'tuoguan', '版本控制', '分支', '代码托管'] },
  { class: 'ri-terminal-line', label: '终端', category: 'tech', tags: ['terminal', '终端', 'zhongduan', '指令'] },
  { class: 'ri-command-line', label: '指令', category: 'tech', tags: ['command', '提示词', 'zhiling', '提示指令', 'Prompt'] },
  { class: 'ri-robot-line', label: '机器人', category: 'tech', tags: ['robot', '机器人', 'jiqiren', 'ai助手', '自动化'] },
  { class: 'ri-sparkles-line', label: '闪耀', category: 'tech', tags: ['sparkles', '闪耀', 'shanyao', '魔法', '模型评测', '评测'] },
  { class: 'ri-shield-check-line', label: '检测', category: 'tech', tags: ['检测', 'jiance', '安全检测', '内容检测'] },

  // === 办公效率 ===
  { class: 'ri-briefcase-line', label: '办公', category: 'office', tags: ['briefcase', '办公', 'bangong', '求职', '工作', '办公工具'] },
  { class: 'ri-flashlight-line', label: '效率', category: 'office', tags: ['flashlight', '效率', 'xiaolv', '极速', '捷径', '效率工具'] },
  { class: 'ri-table-line', label: '表格', category: 'office', tags: ['table', '表格', 'biaoge', 'excel', '电子表格'] },
  { class: 'ri-file-text-line', label: '文档', category: 'office', tags: ['file', '文档', 'wendang', 'doc', '笔记'] },
  { class: 'ri-file-list-3-line', label: '列表', category: 'office', tags: ['list', '列表', 'liebiao', '清单', 'todo'] },
  { class: 'ri-slideshow-line', label: '演示', category: 'office', tags: ['slideshow', '演示', 'yanshi', 'ppt', '幻灯片'] },
  { class: 'ri-todo-line', label: '待办', category: 'office', tags: ['todo', '待办', 'daiban', '计划', '日程'] },
  { class: 'ri-calendar-line', label: '日程', category: 'office', tags: ['calendar', '日程', 'richeng', '日历', '时间'] },
  { class: 'ri-mail-line', label: '邮件', category: 'office', tags: ['mail', '邮件', 'youjian', 'email', '邮箱'] },
  { class: 'ri-customer-service-line', label: '客服', category: 'office', tags: ['customer', '客服', 'kefu', '反馈', '客服系统'] },
  { class: 'ri-search-2-line', label: '搜索', category: 'office', tags: ['search', '搜索', 'sousuo', '查询', '寻找', '搜索引擎'] },
  { class: 'ri-translate-2', label: '翻译', category: 'office', tags: ['translate', '翻译', 'fanyi', '中英', '翻译工具'] },

  // === 生活社交 ===
  { class: 'ri-share-line', label: '分享', category: 'life', tags: ['share', '分享', 'fenxiang', '社交', '社交媒体'] },
  { class: 'ri-team-line', label: '社区', category: 'life', tags: ['team', '社区', 'shequ', '团队', '团队协作'] },
  { class: 'ri-hand-coin-line', label: '副业', category: 'life', tags: ['sidehustle', '副业', 'fuye', '赚钱', '理财', '副业工具'] },
  { class: 'ri-money-cny-box-line', label: '金融', category: 'life', tags: ['money', '金融', 'jinrong', '理财', '财务', '财经金融'] },
  { class: 'ri-wallet-3-line', label: '钱包', category: 'life', tags: ['wallet', '钱包', 'qianbao', '支付'] },
  { class: 'ri-shopping-cart-line', label: '电商', category: 'life', tags: ['shopping', '电商', 'dianshang', '购物车', '电商平台'] },
  { class: 'ri-car-line', label: '汽车', category: 'life', tags: ['car', '汽车', 'qiche', '出行', '汽车资讯'] },
  { class: 'ri-heart-pulse-line', label: '健康', category: 'life', tags: ['health', '健康', 'jiankang', '医疗', '健康医疗'] },
  { class: 'ri-gamepad-line', label: '游戏', category: 'life', tags: ['game', '游戏', 'youxi', '娱乐', '游戏手柄'] },
  { class: 'ri-run-line', label: '运动', category: 'life', tags: ['run', '运动', 'yundong', '健身', '跑', '健身运动'] },
  { class: 'ri-restaurant-line', label: '美食', category: 'life', tags: ['food', '美食', 'meishi', '菜谱', '吃饭', '美食菜谱'] },
  { class: 'ri-flight-takeoff-line', label: '旅游', category: 'life', tags: ['travel', '旅游', 'lvyou', '出行', '飞机', '旅游出行'] }
]

/**
 * 仅通过 tags 匹配标题，取最长命中词（减少「工具」「平台」等短词误判）
 */
export function matchIconByTitle(title) {
  const text = (title || '').trim()
  if (!text) return null
  let matchedClass = null
  let bestLen = 0
  for (const icon of CATEGORY_ICONS) {
    for (const tag of icon.tags) {
      if (!tag || tag.length < 2) continue
      if (text.includes(tag) && tag.length > bestLen) {
        matchedClass = icon.class
        bestLen = tag.length
      }
    }
  }
  return matchedClass
}
