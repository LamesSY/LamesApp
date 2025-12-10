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
        const val URL_PREFIX = ""
        const val ENDPOINT = ""
        const val BUCKET = ""
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

}