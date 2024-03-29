import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.mango.maker.template.TemplateMake;
import com.mango.maker.template.model.TemplateMakeConfig;
import org.junit.Test;

public class TemplateMakeTest {
    @Test
    public void test2() {
        String resourcePath = "examples/springboot-init/";
        String jsonStr = ResourceUtil.readUtf8Str(resourcePath+ "templateMaker.json");
        TemplateMakeConfig templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);
    }

    @Test
    public void test3() {

        String resourcePath = "examples/springboot-init";
        String jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker.json");
        TemplateMakeConfig templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker1.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker2.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker3.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker4.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker5.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker6.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker7.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);

        jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker7.json");
        templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);



    }
    @Test
    public void test4(){
        String resourcePath = "examples/springboot-init";
        String jsonStr = ResourceUtil.readUtf8Str(resourcePath + "/templateMaker5.json");
        TemplateMakeConfig templateMakeConfig = JSONUtil.toBean(jsonStr, TemplateMakeConfig.class);
        TemplateMake.makeTemplate(templateMakeConfig);
    }




}
