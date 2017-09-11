#!/bin/bash
echo 'generate start-------------->>>>>'

java  -classpath src/mybatis-generator/mysql-connector-java-5.1.30.jar:src/mybatis-generator/mybatis-generator-core-1.3.6-SNAPSHOT.jar org.mybatis.generator.api.ShellRunner -configfile src/mybatis-generator/generatorConfig.xml -overwrite

echo " "
echo "========================finish=========================="
