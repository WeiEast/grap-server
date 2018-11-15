 1. CarInfoController  
    * /car_info,/grap/car_info/collect + 
        * /collect  车辆信息采集接口

 2. DemoController  
    * /grap/h5/demo/fund/user_info
    * /grap/h5/demo/fund/bill_record/list
    * /grap/h5/demo/fund/loan_info/list
    * /grap/h5/demo/fund/loan_repay_record/list

 3. DiplomaController
    * /diploma, /h5/diploma, /grap/h5/diploma, /grap/diploma +
        * /config 学信网获取配置
        * /login/init 学信网登陆初始化
        * /login/submit 学信网登陆
        * /register/init 学信网注册初始化
        * /register/refresh/picCode 学信网注册刷新图片验证码
        * /register/validate/picCode/send/smsCode 学信网注册提交
        * /third/party/refercence 学信网获取合作方属性值引用

 4. EcommerceController
    * /ecommerce, /grap/ecommerce +
        * /rawtypes    电商-发信息到mq 登陆成功之后调用

 5. EcommerceH5Controller
    * /h5/ecommerce, /grap/h5/ecommerce +
        * /refresh/qr_code 电商H5获取二维码
        * /qr_code/status  电商H5轮询获取二维码状态

 6. EmailController
    * /email, /grap/email + 
        * /start
        * /acquisition

 7. EmailH5Controller
    * /h5/email, /grap/h5/email +
        * /login/submit, /qq/login/submit  登陆（异步）
        * /refresh/qr_code, /qq/refresh/qr_code 刷新二微码（异步）
        * /qr_code/status, /qq/qr_code/status  查询二维码状态
        * /qq/exmail/login/submit    登陆(异步)
        * /qq/exmail/login/init   登陆初始化

        * /163/login/submit   登陆(异步)
        * /163/refresh/qr_code  刷新二维码异步
        * /163/qr_code/status  查询二维码状态

        * /126/login/submit  登陆(异步)
        * /126/refresh/qr_code 刷新二维码（异步）
        * /126/qr_code/status  查询二维码状态
        * /sina/login/init 登陆初始化
        * /sina/login/submit 登陆
        * /sina/refresh/picCode 刷新图片验证码

        * /support/province_proxy  是否支持当前IP的省份代理
        * /process/status       轮询处理状态接口

 8. FeedbackController
    * /grap/feedback, /grap/h5/feedback +
        * /save  意见反馈

 9. GrapDataController
    * /grap/{bizType}/data

 10. MoxieWebHookController
        * /grap/moxie/webhook +   
            * /notifications 回调接口， moxie通过此endpoint通知账单更新和任务状态更新

 11. OperatorController  
        * /operator, /h5/operator, /grap/h5/operator,/grap/operator +
            * /start 创建任务
            * /config/groups  获取商户配置与运营商分组信息
            * /config/prelogin 获取运营商登陆配置信息
            * /mobile/attribution   根据输入号码查找该号码的归属地
            * /loginpage/prepare   准备登陆(登陆初始化)
            * /loginpage/pic/captcha  刷新图片验证码
            * /loginpage/sms/captcha 刷新短信验证码

            * /loginpage/submit  登陆
        
 12. QuestionnaireController  
        * /grap, /grap/h5 + 
            * /{bizType}/questionnaire/get   查询问券  
            * /{bizType}/questionnaire/save   保存问券结果

 13. SpecialController  
        * /qrscan/checking

 14. StartController  
        * /, /grap, /h5, /grap/h5 +
            * /start   创建任务接口

 15. TaskController  
        * /task, /h5/task, grap/h5/task, /grap/task +
            * /config  获取配置，电商在用
            * /tips   获取滚动消息提示或其他类型消息提示
            * /agreement
            * /next_directive  轮询任务执行指令
            * /verification/code  发送验证码
            * /cancel    取消爬树任务
            * /bury/point/log   埋点记录

 16. TongdunController  
        * /loan/special/ss  +
            * /query 同盾信息采集
            * /query/detail 同盾详细信息采集








