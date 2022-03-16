package com.ps2site.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class MybatisConfig implements EnvironmentAware, ApplicationContextAware {
    private Environment environment;
    private ApplicationContext applicationContext;

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage("com.ps2site.dao");
        return mapperScannerConfigurer;
    }


    @Bean(name = "dataSource")
    public DataSource dataSource() throws IOException {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        String requiredProperty = environment.getRequiredProperty("spring.datasource.url");
      /*  String dbName = requiredProperty.split(":")[2];
        String absolutePath = applicationContext.getResource("classpath:" + dbName).getFile().getAbsolutePath();
        String newUrl = String.join(":", requiredProperty.split(":")[0],
                requiredProperty.split(":")[1], absolutePath);
        System.out.println("datasource url:" + newUrl);*/
        dataSource.setUrl(requiredProperty);
        return dataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        return sqlSessionFactoryBean.getObject();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
