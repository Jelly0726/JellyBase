package com.jelly.baselibrary.Utils;

import java.util.ArrayList;
import java.util.List;

public class SurName {
    /**
     * 判断姓氏是否正确，是复姓还是单姓
     * @param name   待判断的姓名
     * @return       0 姓名不正确  其他值都是姓氏的位数
     */
    public static int onCheckName(String name){
        if(name=="" || name==null){
            return 0;
        }
        if(name.length()<2){
            return 0;
        }
        String[] surName = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫",
                "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹",
                "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章",
                "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤",
                "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛",
                "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐", "于",
                "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄",
                "和", "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝",
                "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒", "屈",
                "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路",
                "娄", "危", "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "锺", "徐", "邱", "骆",
                "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "昝", "管",
                "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应", "宗", "丁", "宣", "贲", "邓",
                "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", "程", "嵇",
                "邢", "滑", "裴", "陆", "荣", "翁", "荀", "羊", "於", "惠", "甄", "麴", "家", "封",
                "芮", "羿", "储", "靳", "汲", "邴", "糜", "松", "井", "段", "富", "巫", "乌", "焦",
                "巴", "弓", "牧", "隗", "山", "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰",
                "秋", "仲", "伊", "宫", "宁", "仇", "栾", "暴", "甘", "钭", "历", "戎", "祖", "武",
                "符", "刘", "景", "詹", "束", "龙", "叶", "幸", "司", "韶", "郜", "黎", "蓟", "溥",
                "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖", "卓", "蔺",
                "屠", "蒙", "池", "乔", "阳", "郁", "胥", "能", "苍", "双", "闻", "莘", "党", "翟",
                "谭", "贡", "劳", "逄", "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "却", "璩",
                "桑", "桂", "濮", "牛", "寿", "通", "边", "扈", "燕", "冀", "僪", "浦", "尚", "农",
                "温", "别", "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习", "宦", "艾",
                "鱼", "容", "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居", "衡", "步",
                "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄", "阙", "东", "欧", "殳",
                "沃", "利", "蔚", "越", "夔", "隆", "师", "巩", "厍", "聂", "晁", "勾", "敖", "融",
                "冷", "訾", "辛", "阚", "那", "简", "饶", "空", "曾", "毋", "沙", "乜", "养", "鞠",
                "须", "丰", "巢", "关", "蒯", "相", "查", "后", "荆", "红", "游", "竺", "权", "逮",
                "盍", "益", "桓", "公", "万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方",
                "赫连", "皇甫", "尉迟", "公羊", "澹台", "公冶", "宗政", "濮阳", "淳于", "单于", "太叔",
                "申屠", "公孙", "仲孙", "轩辕", "令狐", "钟离", "宇文", "长孙", "慕容", "司徒", "司空",
                "召", "有", "舜", "叶赫那拉", "丛", "岳", "寸", "贰", "皇", "侨", "彤", "竭", "端",
                "赫", "实", "甫", "集", "象", "翠", "狂", "辟", "典", "良", "函", "芒", "苦", "其",
                "京", "中", "夕", "之", "章佳", "那拉", "冠", "宾", "香", "果", "依尔根觉罗", "依尔觉罗",
                "萨嘛喇", "赫舍里", "额尔德特", "萨克达", "钮祜禄", "他塔喇", "喜塔腊", "讷殷富察", "叶赫那兰",
                "库雅喇", "瓜尔佳", "舒穆禄", "爱新觉罗", "索绰络", "纳喇", "乌雅", "范姜", "碧鲁", "张廖",
                "张简", "图门", "太史", "公叔", "乌孙", "完颜", "马佳", "佟佳", "富察", "费莫", "蹇", "称",
                "诺", "来", "多", "繁", "戊", "朴", "回", "毓", "鉏", "税", "荤", "靖", "绪", "愈", "硕",
                "牢", "买", "但", "巧", "枚", "撒", "泰", "秘", "亥", "绍", "以", "壬", "森", "斋", "释",
                "奕", "姒", "朋", "求", "羽", "用", "占", "真", "穰", "翦", "闾", "漆", "贵", "代", "贯",
                "旁", "崇", "栋", "告", "休", "褒", "谏", "锐", "皋", "闳", "在", "歧", "禾", "示", "是",
                "委", "钊", "频", "嬴", "呼", "大", "威", "昂", "律", "冒", "保", "系", "抄", "定", "化",
                "莱", "校", "么", "抗", "祢", "綦", "悟", "宏", "功", "庚", "务", "敏", "捷", "拱", "兆",
                "丑", "丙", "畅", "苟", "随", "类", "卯", "俟", "友", "答", "乙", "允", "甲", "留", "尾",
                "佼", "玄", "乘", "裔", "延", "植", "环", "矫", "赛", "昔", "侍", "度", "旷", "遇", "偶",
                "前", "由", "咎", "塞", "敛", "受", "泷", "袭", "衅", "叔", "圣", "御", "夫", "仆", "镇",
                "藩", "邸", "府", "掌", "首", "员", "焉", "戏", "可", "智", "尔", "凭", "悉", "进", "笃",
                "厚", "仁", "业", "肇", "资", "合", "仍", "九", "衷", "哀", "刑", "俎", "仵", "圭", "夷",
                "徭", "蛮", "汗", "孛", "乾", "帖", "罕", "洛", "淦", "洋", "邶", "郸", "郯", "邗", "邛",
                "剑", "虢", "隋", "蒿", "茆", "菅", "苌", "树", "桐", "锁", "钟", "机", "盘", "铎", "斛",
                "玉", "线", "针", "箕", "庹", "绳", "磨", "蒉", "瓮", "弭", "刀", "疏", "牵", "浑", "恽",
                "势", "世", "仝", "同", "蚁", "止", "戢", "睢", "冼", "种", "涂", "肖", "己", "泣", "潜",
                "卷", "脱", "谬", "蹉", "赧", "浮", "顿", "说", "次", "错", "念", "夙", "斯", "完", "丹",
                "表", "聊", "源", "姓", "吾", "寻", "展", "出", "不", "户", "闭", "才", "无", "书", "学",
                "愚", "本", "性", "雪", "霜", "烟", "寒", "少", "字", "桥", "板", "斐", "独", "千", "诗",
                "嘉", "扬", "善", "揭", "祈", "析", "赤", "紫", "青", "柔", "刚", "奇", "拜", "佛", "陀",
                "弥", "阿", "素", "长", "僧", "隐", "仙", "隽", "宇", "祭", "酒", "淡", "塔", "琦", "闪",
                "始", "星", "南", "天", "接", "波", "碧", "速", "禚", "腾", "潮", "镜", "似", "澄", "潭",
                "謇", "纵", "渠", "奈", "风", "春", "濯", "沐", "茂", "英", "兰", "檀", "藤", "枝", "检",
                "生", "折", "登", "驹", "骑", "貊", "虎", "肥", "鹿", "雀", "野", "禽", "飞", "节", "宜",
                "鲜", "粟", "栗", "豆", "帛", "官", "布", "衣", "藏", "宝", "钞", "银", "门", "盈", "庆",
                "喜", "及", "普", "建", "营", "巨", "望", "希", "道", "载", "声", "漫", "犁", "力", "贸",
                "勤", "革", "改", "兴", "亓", "睦", "修", "信", "闽", "北", "守", "坚", "勇", "汉", "练",
                "尉", "士", "旅", "五", "令", "将", "旗", "军", "行", "奉", "敬", "恭", "仪", "母", "堂",
                "丘", "义", "礼", "慈", "孝", "理", "伦", "卿", "问", "永", "辉", "位", "让", "尧", "依",
                "犹", "介", "承", "市", "所", "苑", "杞", "剧", "第", "零", "谌", "招", "续", "达", "忻",
                "六", "鄞", "战", "迟", "候", "宛", "励", "粘", "萨", "邝", "覃", "辜", "初", "楼", "城",
                "区", "局", "台", "原", "考", "妫", "纳", "泉", "老", "清", "德", "卑", "过", "麦", "曲",
                "竹", "百", "福", "言", "第五", "佟", "爱", "年", "笪", "谯", "哈", "墨", "连", "南宫",
                "赏", "伯", "佴", "佘", "牟", "商", "西门", "东门", "左丘", "梁丘", "琴", "后", "况", "亢",
                "缑", "帅", "微生", "羊舌", "海", "归", "呼延", "南门", "东郭", "百里", "钦", "鄢", "汝",
                "法", "闫", "楚", "晋", "谷梁", "宰父", "夹谷", "拓跋", "壤驷", "乐正", "漆雕", "公西",
                "巫马", "端木", "颛孙", "子车", "督", "仉", "司寇", "亓官", "三小", "鲜于", "锺离", "盖",
                "逯", "库", "郏", "逢", "阴", "薄", "厉", "稽", "闾丘", "公良", "段干", "开", "光", "操",
                "瑞", "眭", "泥", "运", "摩", "伟", "铁", "迮","刘傅","鄗","仉亓官","博尔济锦","哀牢","艾山",

                "岸然", "铵", "岸", "安陵", "安阳", "安国", "卬", "盎", "凹", "奥屯", "澳", "熬", "奥",
                "奧", "傲", "隞", "奥敦", "突厥阿史那", "巼", "灞", "捌", "笆", "霸", "仈", "把", "八", "摆",
                "白岳", "白马", "把利巴林", "巴邻", "般", "坂班", "办", "昄", "邦", "傍", "蒡", "抱", "雹",
                "报", "豹", "北堂", "北烈", "貝", "北冥", "悲", "北泽", "北海", "北羽", "北野", "北里",
                "北鄙", "北殷", "北宫", "北唐", "奔", "甏", "薜", "俾", "鄙", "闭珊", "碧咸", "赑", "毴",
                "比", "必", "邲", "笔", "比干", "蔽", "鼻", "弊", "扁", "标", "彪", "婊", "標", "杓", "別",
                "膑", "北门", "髌", "宾牟", "彬", "兵", "冰", "并", "炳", "秉", "博罕岱", "莽努特", "博鲁特",
                "伯父", "伯里", "波瓦", "铂", "播", "泊", "博", "伯比", "伯常", "伯牙吾台", "钵", "伯高",
                "伯昏", "博尔济吉特", "孛尔只斤", "孛儿只斤", "孛术鲁", "布兰科", "卟", "步大汗", "布仁玺黑格",
                "部", "补", "步禄孤", "步六孤", "材", "蔡林", "才旦", "菜", "采", "彩", "财", "剼", "参",
                "仓", "苍梧", "草蒺", "草", "漕", "鄵", "册", "策", "恻", "侧", "厕", "茶", "察", "插",
                "姹", "察哈尔", "茬", "岔", "侪", "虿", "产", "镡", "禅", "缠", "阐", "唱", "厂", "场",
                "敞", "长鱼", "长沙", "长兴", "鬯", "菖", "常夏", "鼂", "鈔", "朝", "超", "彻", "车非", "衬",
                "陈林", "臣", "陈观", "闵陈", "郴", "晨", "沉", "陳", "陈没", "尘", "陈梁", "成吉思汗", "橙",
                "成王", "呈", "晟", "城父", "赤小豆", "赤松", "墀", "迟尉", "赤翟", "茌", "持", "斥", "齿",
                "叱", "赤张", "匙", "蚩", "叱干", "虫", "宠", "舂", "犨", "瘳", "仇由", "侴", "臭", "厨人",
                "厨", "处", "除", "楮", "出大汗", "滁", "矗", "碝", "触", "啜", "揣", "啜剌", "町川", "舛",
                "巛", "串", "穿封", "传", "钏", "川", "歂", "昶", "闯", "床", "吹", "炊", "淳", "椿",
                "春日", "褚师", "此", "慈禧", "刺", "枞", "葱", "苁", "醋", "爨", "毳", "催", "存", "村",
                "措", "厝", "错木", "达奚", "达瓦买提", "大荔", "大食", "大孙", "打", "大季", "大叔", "耷",
                "大狐", "答禄", "妲", "大连", "达薄", "大罗布次", "達", "禇", "黛", "歹", "胆", "澹", "旦",
                "萏", "當", "荡", "当涂", "岛", "稻", "鄧", "邓李", "旳", "厍狄", "棣", "第九", "第十", "迪安",
                "荻", "氐", "低", "敌", "祗", "弟五", "底", "迪", "地", "第伍", "第八", "第二", "第六", "第七",
                "第三", "第四", "第一", "嗲", "琠", "佃", "滇", "点", "电", "店", "貂", "调", "鸟", "雕",
                "秳", "迭", "牒", "鼎", "丢", "动", "洞", "侗", "峒", "东里", "东阳", "冬", "董鄂", "苳",
                "东篱", "东条", "冻", "东鄙", "东宫", "冬日", "东野", "斗", "兜", "鬭", "陡", "豆卢", "毒",
                "堵师", "独吉", "芏", "騳", "独狐", "蠹", "独孤", "锻", "断", "瑖", "队", "兑", "堆山", "趸",
                "盾", "敦", "朵", "堕", "多兰葛", "朵豁", "剁", "蛾", "鹅", "兹", "娥", "俄", "额尔德雨",
                "额勒图特", "恩", "耳", "耳东", "儿", "二", "洱", "穣", "尔朱", "发", "阀", "伐", "发克",
                "帆", "梵", "饭", "反", "凡", "蕃", "放", "防", "邡", "鲂", "柀Ψ?", "坊", "芳", "房当",
                "菲梅尔", "菲", "费也头", "濷", "废", "非", "淝", "奜", "棐", "分", "粉", "奋", "坟", "峰",
                "丰将", "封县", "凤翔", "馮", "讽", "俸", "枫", "凤翕", "凤翥", "凤翱", "枫香", "封父", "封人",
                "冯龚", "佛缶", "服", "附", "父", "辅", "抚", "釜", "阜", "郛", "复姓", "涪", "苻", "复",
                "妇", "付", "洑", "伕", "敷", "劉付", "负", "扶芳", "菔", "茯", "芙", "福图", "鎵", "尕",
                "该", "垓", "赣", "干段", "赣娄", "绀", "魐", "橄", "澉", "岗", "钢", "纲", "罡", "港",
                "冈", "冮", "半", "高堂", "杲", "高尔尨", "镐", "睾", "糕", "高侯", "高祁", "高阳", "高上",
                "高车", "窠", "仡", "各", "歌逻", "阁", "哥", "格", "歌舒", "鸽", "閤", "蛤蟆", "舸", "鬲",
                "恪", "哥舒", "格尔尼", "艮", "根", "耕", "赓", "庚桑", "亘", "更", "纥石烈", "高濑", "龔",
                "公延", "公衍", "公保", "公沙", "公丘", "工精", "公绪", "宫堂", "公伯", "供", "共叔", "公义",
                "公文", "弓耀", "共", "工", "公肩", "公孟", "公输", "公皙", "公祖", "狗", "沟", "构", "芶",
                "笱", "缑亢", "句", "鼓", "股", "孤", "姑", "固", "骨", "顧", "谷利", "頋", "纥古鲁", "孤竹",
                "谷粱", "嫴", "崓", "荻古鲁", "骨力", "沽", "古里甲", "骨仑廑骨思", "古野", "谷会", "估", "刮",
                "瓜", "索额图", "索淖罗", "瓜尔加", "瓜田", "怪", "夬", "官上", "观", "莞", "灌", "贯丘", "毌丘",
                "犷", "丨", "鬼", "龟", "诡", "昋", "诡诸", "归海", "炅", "炔", "郐", "鬼谷", "嶲", "榖梁",
                "穀梁", "滚", "郭曩", "郭尔里", "郭尔佳", "郭尔罗", "郭尔罗特", "郭尔罗斯 郭佳", "郭络罗", "郭布勒",
                "郭传罗", "郭勒本", "郭贝尔", "郭啰噶", "郭啰罗", "郭儿刺思", "呙", "锅", "过泉", "郭王", "郭李",
                "簡", "哈什纳", "铪", "張簡", "哈尔努特", "篅", "哈尼", "海拉苏", "撖", "憨", "喊", "函冶",
                "函井", "菡", "邯郸", "灏", "皓", "豪", "号", "浩", "浩生", "浩星", "郝松", "佫", "好",
                "何乌日娜", "荷丘", "菏", "贺赖", "贺楼", "贺若", "鹤", "貉", "荷", "贺兰", "河", "黑", "纥干",
                "纥奚", "黑豆", "黑齿", "很", "横", "恒", "亨", "八旗", "韓", "鸿", "红格沁", "虹", "轰",
                "宏远", "後", "郈", "吼", "猴", "侯莫陈", "壶", "忽", "狐", "沪", "胡宇", "弧", "胡毋", "斛斯",
                "轷", "胡杨", "胡同", "胡仰", "胡雅", "籮", "戲", "呼落", "斛律", "胡母", "逼", "话", "華",
                "画", "淮", "槐", "覃怀", "欢", "还", "寰", "奂", "浣", "幻", "黃", "黄豆", "晃", "黄陆",
                "扈地干", "慧", "卉", "会", "混", "魂", "緍", "婚", "活", "火", "货", "祸", "或", "豁",
                "获", "斛瑟罗", "鹘提奚补野", "婧", "伋", "姞", "极", "吉库和", "洎", "岌", "戟", "楫", "赍",
                "齑", "济", "荠", "笈", "笄", "蕺", "蒺", "芰", "芨", "棘", "佶", "既", "即", "积", "辑",
                "脊", "饥", "伎", "乩", "疾", "悸", "墼", "玑", "霽", "蕀", "氅", "技", "激", "讥", "际",
                "几", "基", "紀", "吉胡", "继", "记", "稷", "即墨", "鸡", "忌", "寂", "丌", "羁", "荚",
                "佳尔关", "佳", "架", "加", "賈", "夹", "剪", "见", "鉴", "肩吾", "雋", "寋", "健", "尖",
                "箭", "间", "监", "肩", "荐", "减", "缄", "鑑", "奸", "姦", "絡", "江戶川", "疆", "茳", "薑",
                "酱", "奖", "降", "绛", "教", "缴", "角", "胶", "姣", "徼", "角里", "敫", "皎", "郊尹", "郊",
                "交", "蕉", "楶", "杰恩", "芥川", "戒", "姐", "结", "洁", "杰", "截", "丌官", "妗", "晋楚",
                "津", "近", "禁", "巾", "卺", "缙", "荩", "緒", "今", "維", "金城", "練", "井田", "竞", "井上",
                "京兆", "京相", "精纵", "精", "京城", "经孙", "静", "竟", "晶", "净", "璟甌", "玖", "旧", "鹫",
                "九百", "久", "韭", "臼", "阄", "桕", "舅", "就", "苣", "具封", "堀", "峻", "竣", "咖", "霭",
                "邟", "克", "蔻", "枯", "块", "繻", "纠", "九方", "氿", "帺", "沮", "苴", "举", "沮渠", "雎",
                "具", "俱", "琚", "菊", "車", "簏句", "句龙", "巨剑", "娟", "觉", "觉尔察", "君", "菌", "俊",
                "郡", "骏马", "均", "巨毋", "喀尔喀", "卡", "凯", "堪", "侃", "勘", "坎", "伉", "坑", "靠",
                "柯熙", "科", "可朱浑", "空同", "空桑", "口", "寇丘", "窟贺", "哭", "苦成", "宽", "鄺", "逵",
                "駒", "繼", "奎", "魁", "葵", "髡", "宮", "桰", "蛞", "看", "廓", "阔", "腊", "剌", "拉",
                "辣", "賴", "巴佳剌", "郲", "來", "蓝馨", "婪", "拦", "篮", "兰顿", "岚", "蓝雨", "稂", "琅琊",
                "琅", "朗", "狼", "浪", "曩", "兰帕尔", "姥", "勞", "醪", "嫪", "拉万", "乐王", "勒", "累",
                "磊", "蕾", "离", "里", "劦", "理想", "立", "狸", "里安", "荔", "俪", "俚", "李黄", "叱李",
                "溧", "丽", "隶", "绮里", "立如", "厲", "羅", "荔非", "骊", "醴丞", "栎", "炼", "連", "联",
                "楝", "莲", "綦连", "恋", "梁林", "粱", "谅", "了", "寥", "辽", "列御", "叱列", "烈", "劣",
                "列", "吝", "淋", "琳", "临丘", "廪", "伶", "伶舟", "伶州", "岭", "於陵", "酃", "苓", "陵",
                "泠", "灵", "令孤", "上令下狐", "酃丘", "林业", "刘俊", "斿", "遛", "柳林", "刘根百里", "溜",
                "流", "劉", "浏", "刘谭", "刘付", "聶", "翬", "陇", "龍", "耑", "龍陽", "漏", "露", "潞",
                "芦", "盧", "陸", "樂", "路里", "露兜", "庐", "录", "漉", "叱卢", "窦卢", "屡", "甪", "爐",
                "渌", "彔", "陆费", "甪里", "孪", "峦", "卵", "娈", "略阳", "论", "倫", "裸", "鸁", "捋",
                "鑼", "螺", "濼", "玀", "曪", "雒", "駱", "络", "落下", "洛下", "驴", "呂", "叱吕", "绿",
                "绿小豆", "率", "马帅", "馬", "马矢", "马服", "马司", "马徐", "祃", "麥", "賣", "迈", "缦",
                "满红", "曼", "满丘", "蔄", "邙", "莽", "忙", "貌", "猫", "鄮", "美", "眉", "郿", "没路真",
                "美日", "叱门", "黾门", "梦", "糸", "麋", "杩", "芈", "密", "米女", "面", "棉", "淼", "苗君",
                "妙", "庙", "繆", "秒", "蔑里乞", "蔑", "民", "皿", "黾", "旻", "閩", "缗", "名", "茗",
                "糸系", "莫折", "莫那娄", "抹燃", "莫多娄", "木村", "蘑", "摩西", "末", "麿", "魔", "磨阝",
                "默", "抹撚", "木", "目", "幕", "畝", "慕蓉", "姆", "母将", "母邱", "牧野", "那金", "迺",
                "乃", "耐", "纳兰", "南郭", "南荣", "楠", "难", "難", "南野", "男", "南里", "南鄙", "内史",
                "兒", "逆", "伲", "匿", "尼玛察哈拉", "霓", "尼", "娘", "嬲", "涅", "蘖", "孽", "臬", "舊",
                "舃", "宁尔佳", "佞", "甯", "狃", "钮轱辘", "纽", "纽咕禄", "钮钴禄", "纽咕噜", "浓", "侬",
                "農", "弄", "耨", "叱奴", "虐", "傩", "诺里埃加", "诺沃阿", "女", "奕辰区", "殴", "藕", "區",
                "欧文", "歐陽", "爬", "怕", "拍", "牌", "排", "潘葛", "泮", "盤", "厐", "胖", "耪", "龐",
                "咆", "佩吉", "沛", "喷", "湓", "盆", "鹏", "邳", "甓", "匹", "偏", "片", "骈", "飘", "嫖",
                "品", "萍", "婆", "坡", "攴", "番", "破六韩", "裒", "普陋茹", "普六茹", "普弥", "菩提", "莆",
                "蒲姑", "谱", "仆散", "仆固", "扑", "企", "期", "岂", "棋", "岐", "锜", "启", "七", "柒",
                "乞伏", "棨", "乞", "起", "妻", "欺", "齐藤", "旗木", "淇", "麒", "齐默特", "泣伏利", "歐",
                "耆", "其格", "萋", "乞连", "器", "綦毋", "莊", "莮", "钤", "钳", "千金", "钱王", "浅", "茜",
                "骞", "黔", "錢", "谦", "羌", "墙", "强梁", "樵", "喬", "乔达摩", "伽", "怯", "郄", "妾",
                "客", "茄", "且末", "且", "骎", "芹", "芩", "青阳", "倾", "苘", "琼", "酋", "球", "求伯",
                "遒", "楸", "丘目陵", "秋日", "湫", "邱张", "艽", "瑕丘", "闾邱", "丘穆陵", "葉", "去疾",
                "去", "趣马", "蘧", "屈突", "葀", "麹", "萬", "蕖", "衢", "泉上", "犬", "拳", "圈", "全黄",
                "群", "瞿昙", "阕", "燃", "绕", "热", "人", "扔", "日", "日律", "蓉", "肜", "柔然", "茹华",
                "濡", "如", "入", "软", "如花", "闰", "润", "若", "若干", "若口引", "萨蛮", "恺撒", "洒",
                "撒哈拉", "三", "散", "三台", "三川", "叁", "伞", "三闾", "蓋", "三乌", "扫", "色", "涩",
                "傻", "杀", "沙门", "煞", "莎", "擅", "鄯", "杉", "閷", "珊竹", "汕", "剡", "陕", "扇",
                "鳣", "鄯善", "上", "尚书", "商丘", "商泽", "哨", "他他拉", "沓", "她", "它", "他", "太",
                "太皇", "大澤", "坦", "坛", "湯", "烫", "汤穆", "唐山", "棠", "倘", "糖", "王吴", "涛",
                "桃", "饕餮", "特", "藤原", "题", "遆", "提", "甜", "田章", "天其", "添", "田佳", "鯈",
                "铁伐", "铁木", "蘭", "庭", "廷", "听", "同蹄", "统万", "瞳", "酮", "痛", "偷", "头", "兔",
                "突", "土", "涂山", "凃", "荼", "屠岸", "屠门", "塗", "秃发", "徒", "图", "吐伏卢", "吐谷浑",
                "凸", "吐奚", "土皋", "吐卢", "吐万", "徒萨", "团", "吐火罗", "推罗", "退", "屯", "佗",
                "其其格", "托忒克", "拓略", "拓拔", "托", "妥", "拓", "瓦", "弯", "湾", "晚", "宛丘", "皖",
                "碗", "婉", "万刘", "王孙", "王父", "王官", "王人", "王史", "王叔", "王周", "王子", "望李",
                "王李", "忘", "未", "蔚迟", "为", "威王", "薇", "厃", "威梁", "贴", "巍", "维", "慰", "惟",
                "韋", "嵬名", "稳", "聞", "汶", "佀", "溫", "叱温", "文昌", "閻", "渥", "我", "五赵", "五兆",
                "吴刘", "吴和", "吴滕", "屋引", "乌苏", "芜", "芜丘", "乌拉纳喇", "乌古伦", "乌落兰", "屋兰",
                "乌拉那拉", "乌那罗", "乌喇那拉", "武陵", "乌拉纳拉", "浯", "舞", "乌尔扎", "巫馬", "屋", "鄔",
                "五鹿", "毋丘", "乌济根觉罗", "乌济耶特", "五王", "五谷", "勿忸于", "吳", "忤", "午", "兀",
                "万忸于", "兀颜", "兀良哈", "小泉", "喜塔喇", "喜塔剌", "西伯", "舄", "夕姑", "西鄙", "西里",
                "西马洛", "西风", "西河", "醯", "葸", "西贝", "西林觉罗", "西宫", "西郭", "西方", "犀", "西闾",
                "西王", "亩心", "稀", "郤", "西", "锡", "息", "羲", "隰", "卻", "樨", "下", "霞", "夏日",
                "厦", "贤", "先", "宪", "现", "羡", "县", "苋", "姺", "弦", "闲齐", "缐", "闲", "伭", "鲜卑",
                "献", "咸丘", "洗", "霰", "瞯", "祥", "翔", "乡", "湘", "襄", "相里", "彡姐", "衛", "消",
                "小田", "西萧", "晓", "笑", "效", "小王", "霄", "校师", "嚣", "袛", "箫", "小", "袓", "肖巫",
                "効", "枭", "蛸", "筱", "歇", "鞋", "谐", "蟹", "偰", "泄", "写", "颉", "謝", "挟跌", "燮",
                "勰", "薤", "辛格", "新楚特", "忻都", "新", "欣", "新垣", "信都", "歆", "辛垣", "心", "星野",
                "行人", "星尘", "形", "形成", "杏", "荥", "邢莫", "雄", "修鱼", "岫", "飍", "徐章", "盱", "婿",
                "徐吾", "徐程", "俆", "顼", "嘘", "徐离", "胥元", "轩", "褌", "裵", "玹", "选", "儇", "谖",
                "炫", "旋", "禤", "萱", "璇", "铉", "軒轅", "穴", "踅", "鄩", "郇", "盱眙", "爱心觉罗", "牙",
                "鸭", "芽", "鸦", "崖", "亚", "雅", "山丘", "衙", "娅", "雅典娜", "琊", "上山下丘", "艳", "筵",
                "檐", "沿", "偃", "啖", "演", "阉", "顔", "淹", "衍", "延陵", "嚴", "眼", "砚", "琰", "盐",
                "炎", "彦", "兖", "郾", "宴", "雁", "焱", "顏", "岩", "楊", "漾", "央", "鞅", "阳城", "样",
                "羊角", "杨柳", "偃师", "耀", "夭", "腰", "幺", "要", "药", "铫", "饒", "喇", "咬", "姚晏",
                "叶和那拉", "爷", "也", "冶", "耶", "夜", "邺", "曳", "邪", "揶", "页", "椰", "耶律",
                "叶赫纳拉", "葉赫納拉", "刈", "伊林", "矣", "乙弗", "伊娄", "乙速孤", "一那娄", "夷丘",
                "乙旃", "颐", "扆", "怡", "弋", "伊能", "壹那蒌", "邑", "移", "已", "医", "翼", "沂", "彝",
                "艺", "异", "懿", "抑", "壹", "倚", "毅", "一", "¤", "义渠", "伊尔根觉罗", "依尔觉悟罗",
                "伊雨根觉罗", "寅", "訚", "音", "因", "淫", "樱木", "茵", "瀛", "萦", "迎", "营丘", "莹",
                "婴", "颖", "郢", "赢", "樱", "殷勤", "伊祁", "右宰", "由章", "誉", "雨", "予", "瑜",
                "郁都甄", "萸", "远", "辕轩", "垣", "钺", "雲", "郓", "鄘", "永贝里", "右公", "右师", "佑",
                "尢", "悠", "攸", "右", "蚩尤", "油", "酉", "幽", "宥", "兀有", "又", "幽泉", "由吾", "阏",
                "許", "与", "裕", "豫", "渝", "语", "禺", "鬱", "预", "余王", "郁久闾", "臾", "吁", "鬻",
                "余丘", "虞丘", "狱", "芋", "圉", "爰", "渊", "园", "圆", "院", "辕", "援", "园丁", "苑曰",
                "鸢", "龠", "粤", "月", "悦", "月野", "貟", "妘", "韵", "郧", "贠", "吾丘", "杂", "再", "甾",
                "赞", "錾", "趱", "奘", "弉", "牂牁", "唕", "皂", "灶", "早", "造", "枣", "藻", "遭", "棗",
                "则", "泽", "昃", "笮", "贼", "增", "甑", "缯", "曽", "闸", "讠", "工白", "诈", "扎", "轧",
                "札拉楚特", "札哈齐特", "寨", "宅", "债", "窄", "砦", "詹伯", "辗迟", "瞻", "詹世", "绽", "蘸",
                "旃", "張", "丈", "鄣", "张包", "獐", "颢", "瘴", "彰", "漳", "黨", "仉督", "照", "兆昆",
                "诏", "趙", "者", "浙", "哲", "柘", "折兰", "折娄", "阵", "箴", "枕", "斟灌", "真田", "鄭",
                "症", "郑金", "蒸", "政", "争", "郑丘", "征", "争登", "正", "对", "挚", "郅", "纸", "职",
                "陟", "昭", "直古鲁", "芷", "致", "脂", "制", "直", "只", "指", "志", "芝", "彘", "执",
                "秩", "知", "徵", "仲叔", "中山", "中华", "鍾", "重", "众", "鐘", "中田", "中叔", "仲长",
                "中行", "皱", "鄒", "州", "纣", "舟", "住", "邾", "渚", "翥", "猪", "烛", "譚", "蓍", "柱",
                "竹里", "竹内", "主父", "主", "筑", "朱陈", "朱文", "茱", "朱邪", "爪雨佳", "颛", "专", "顓孫",
                "妆", "壮", "庄上", "颛顼", "墜", "追命", "惇", "卓张", "桌", "术要甲", "辎", "子丰", "子革",
                "子国", "子罕", "子孔", "子宽", "子强", "子然", "子人", "子如", "子师", "子驷", "子哲", "子轩",
                "子濯", "資", "孜", "觜", "子", "子雅", "自", "梓", "宗杨", "踪", "综", "宗圣", "宗正", "驺"};

        List<String> l1=new ArrayList<>();
        for (String na : surName){
            if (name.contains(na)){
                l1.add(na);
            }
        }
        if (l1.size()==1){
            return l1.get(0).length();
        }else {
            for(int i=name.length();i>=1;i--){
                for (String na : l1){
                    String ss=name.substring(0, i);
                    if (ss.equals(na)){
                        return na.length();
                    }
                }
            }
        }
        return 0;

    }

    /**
     * 替换姓名为*
     * @param name   原姓名
     * @return       替换后的姓名
     */
    public static String replaceName(String name){
        int i= onCheckName(name);
        if (i==0){
            return name;
        }
        if (name.trim().length()-i==1){
            return name.substring(0,i)+"*";
        }else {
            String nn=name.substring(0,i);
            for (int j = 0;j<name.trim().length()-i-1;j++){
                nn=nn+"*";
            }
            nn=nn+name.substring(name.trim().length()-1,name.trim().length());
            return nn;
        }
    }

    /**
     * 替换姓名并指定替换内容
     * @param name      原姓名
     * @param str       替换的字符
     * @return          替换后的姓名
     */
    public static String replaceName(String name,String str){
        int i= onCheckName(name);
        if (i==0){
            return name;
        }
        if (name.trim().length()-i==1){
            return name.substring(0,i)+str;
        }else {
            String nn=name.substring(0,i);
            for (int j = 0;j<name.trim().length()-i-1;j++){
                nn=nn+str;
            }
            nn=nn+name.substring(name.trim().length()-1,name.trim().length());
            return nn;
        }
    }

    /**
     * 指定替换内容并保留姓名末尾几位数替换姓名
     * @param name   原姓名
     * @param str    替换字符
     * @param num    名字末尾保留字数
     * @return       替换后的姓名
     */
    public static String replaceName(String name,String str,int num){
        int i= onCheckName(name);
        if (i==0){
            return name;
        }
        if (num>=name.trim().length()-i){
            return name;
        }
        if (num<=0){
            return name.substring(0,i)+str;
        }
        if (name.trim().length()-i==1){
            return name.substring(0,i)+str;
        }else {
            String nn=name.substring(0,i);
            for (int j = 0;j<name.trim().length()-i-num;j++){
                nn=nn+str;
            }
            nn=nn+name.substring(name.trim().length()-num,name.trim().length());
            return nn;
        }
    }
    public static void main(String[] args) {
        String name = "札拉楚特山岑";//柀Ψ?   ¤
        System.out.println(onCheckName(name));
        System.out.println(replaceName(name));
        System.out.println(replaceName(name,"%"));
        System.out.println(replaceName(name,"师傅",0));
    }
}