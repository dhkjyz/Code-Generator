1. template.json : 初始化meta元信息，并定义源文件绝对位置 
2. templateMaker1.json： 测试生成多个模版，没有文件过滤规则，模型参数可以由用户进行配置从而实现更换包名 
3. templateMaker2.json： 测试生成多个模版，有文件过滤规则，实现过滤文件名涵盖Post字样的文件，由模型参数needPost实现是否开启帖子功能 
4. templateMaker3.json： 测试生成单个文件，没有文件过滤规则，实现通过模型参数needCor控制是否开启跨域配置文件的生成。
5. templateMaker4.json： 实现对多个模型参数进行统一管理 ，先由一个开关控制是否要让用户输入多个参数实现对一个功能点的配置。 
