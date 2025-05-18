package com.lames.standard.common

class Constants {

    object Params {
        const val ACTION = "operateAction"
        const val ARG1 = "arg1"
        const val ARG2 = "arg2"
        const val ARG3 = "arg3"
        const val ARG4 = "arg4"
        const val ARG5 = "arg5"
        const val ARG6 = "arg6"
    }

    object Project {
        const val EMPTY_STR = ""
    }

    object Oss {
        const val URL_PREFIX = "https://colofoo-xintai.oss-cn-heyuan.aliyuncs.com/"
        const val ENDPOINT = "https://oss-cn-heyuan.aliyuncs.com"
        const val BUCKET = "colofoo-xintai"
    }

    object Network {
        //操作成功
        const val SUCCESS = 200

        //系统内部错误
        const val TOKEN_EXPIRED = 401
    }

    object Folder {
        const val IMAGE = "Image"
        const val VIDEO = "Video"
        const val AUDIO = "Audio"
        const val DOWNLOAD = "Download"
        const val FILE_CACHE = "Cache"
        const val RECEIVE_FILE = "Received"
    }

    object Questionnaire {
        const val QUESTIONNAIRE01: Long = 1      //CVD检测问卷
        const val QUESTIONNAIRE02: Long = 2      //血脂问卷
        const val QUESTIONNAIRE03: Long = 3      //血糖问卷
        const val QUESTIONNAIRE04: Long = 4      //BMI问卷
        const val QUESTIONNAIRE05: Long = 5      //血压问卷
        const val QUESTIONNAIRE06: Long = 6      //基础初筛睡眠障碍问卷
        const val QUESTIONNAIRE07: Long = 7      //ESS嗜睡量表
        const val QUESTIONNAIRE08: Long = 8      //运动问卷
        const val QUESTIONNAIRE09: Long = 9      //PHQ-9 抑郁症筛查问卷
        const val QUESTIONNAIRE10: Long = 10     //幸福指数问卷
        const val QUESTIONNAIRE11: Long = 11     //scale焦虑量表
        const val QUESTIONNAIRE12: Long = 12     //每日问卷13：每周问卷
        const val QUESTIONNAIRE14: Long = 14     //每月问卷
        const val QUESTIONNAIRE15: Long = 15     //健康分析问卷
        const val QUESTIONNAIRE16: Long = 16     //活力耐力评估EHRA问卷
        const val QUESTIONNAIRE17: Long = 17     //慢性疾病问卷
        const val QUESTIONNAIRE18: Long = 18     //房颤风险评估C2HEST问卷
        const val QUESTIONNAIRE19: Long = 19     //生活方式改善评估问卷
        const val QUESTIONNAIRE20: Long = 20     //饮食量表
        const val QUESTIONNAIRE21: Long = 21     //饮食量表（高级版）
        const val QUESTIONNAIRE22: Long = 22     //底部弹出问题补充问卷
        const val QUESTIONNAIRE23: Long = 23     //运动评估问卷
        const val QUESTIONNAIRE24: Long = 24     //运动目标问卷
        const val QUESTIONNAIRE25: Long = 25     //运动感受问卷
        const val QUESTIONNAIRE26: Long = 26     //房颤风险预警反馈问卷
        const val QUESTIONNAIRE27: Long = 27     //基本信息问卷
        const val QUESTIONNAIRE35: Long = 35     //查体问卷

        /**
         * 平台那些问卷是有问卷结果的 id
         * 现在似乎大部分都会有结果，不再区分
         */
        fun isThisQuestionnaireHasResult(pid: Long): Boolean =
            true || pid == 2L || pid == 3L || pid == 4L || pid == 6L || pid == 7L ||
                    pid == 9L || pid == 10L || pid == 11L || pid == 16L || pid == 18L || pid == 20L || pid == 23L || pid == 25L || pid == 35L
    }


    object SportIntensityType {
        const val VERY_LOW = "VERY_LOW_GRADE"
        const val LOW = "LOW_GRADE"
        const val MID = "MED_GRADE"
        const val HIGH = "HIGH_GRADE"
        const val VERY_HIGH = "VERY_HIGH_GRADE"
    }

    object RequestMeasureDataType {
        const val BP = "bp"
        const val BS = "bs"
        const val SLEEP = "sleep"
        const val SLEEP_APNEA = "sleep_apnea"
        const val SLEEP_STANDARD = "sleep_standard"
        const val SPO2H = "spo2"
        const val RESPIRATORY = "respiratory"
        const val SLEEP_HYPOXIA = "sleep_hypoxia"
        const val CARDIAC_LOAD = "cardiac_load"
        const val SLEEP_GESTURE = "sleep_gesture"
        const val HEART_RATE = "heart_rate"
        const val HEART_PPG = "heart_ppg"
        const val HEART_HRV = "heart_hrv"
        const val HEART_ECG = "heart_ecg"
        const val TW = "tw"
        const val SPORT = "sport"
        const val SPORT_SINGLE = "yz_sport_single"
        const val WEIGHT = "weight"
        const val BMI = "bmi"
        const val BF = "bf"
        const val VO2 = "vo2"
        const val STRESS = "stress"
    }
}