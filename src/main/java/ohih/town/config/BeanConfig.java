package ohih.town.config;

import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class BeanConfig {
    @Bean(name = "verificationMail")
    public PropertiesFactoryBean verificationMail() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        ClassPathResource classPathResource = new ClassPathResource("/mail/verification-mail.properties");
        propertiesFactoryBean.setLocation(classPathResource);
        return propertiesFactoryBean;
    }

    @Bean(name = "filePaths")
    public PropertiesFactoryBean filePaths() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        ClassPathResource classPathResource = new ClassPathResource("/config/file-paths.properties");
        propertiesFactoryBean.setLocation(classPathResource);
        return propertiesFactoryBean;
    }
}
