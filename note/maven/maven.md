#  一、笔记

1. 运行 phase 时，会把生命周期上层的 phase 依次运行一遍。
1. 非 compile 范围的依赖没有传递性。

# 二、配置

1. 默认路径：`$MAVEN_HOME/conf/settings.xml`、`${HOME}/.m2/settings.xml`。

1. 配置说明：

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0 https://maven.apache.org/xsd/settings-1.2.0.xsd">
    
        <!-- 指定本地仓库的路径，用于存放从远程仓库下载的依赖和插件。 -->
        <localRepository>/path/to/local/repo</localRepository>
    
        <!-- 是否以交互模式运行 Maven。 -->
        <interactiveMode>true</interactiveMode>
    
        <!-- 是否以离线模式运行 Maven。使用本地仓库中已有的依赖。 -->
        <offline>false</offline>
    
        <!-- 插件组用于简化插件坐标的书写。 -->
        <pluginGroups>
          <pluginGroup>org.apache.maven.plugins</pluginGroup>
          <pluginGroup>org.codehaus.mojo</pluginGroup>
          <pluginGroup>cn.ivfzhou.plugins</pluginGroup>
        </pluginGroups>
        
        <!-- 配置网络代理。 -->
        <proxies>
          <proxy>
            <id>my-proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>127.0.0.1</host>
            <port>7897</port>
            <username></username>
            <password></password>
            <!-- 多个用 | 分隔 -->
            <nonProxyHosts>localhost|*.ivfzhou.cn</nonProxyHosts>
          </proxy>
        </proxies>
        
        <!-- 配置访问远程仓库或部署仓库时所需的认证信息，id 需要与 pom.xml 中 distributionManagement 或 repositories 里定义的仓库 id 对应。 -->
        <servers>
          <server>
            <id>releases-repo</id>
            <username>deploy</username>
            <password>secret</password>
          </server>
        
          <server>
            <id>scp-repo</id>
            <username>scpuser</username>
            <privateKey>/path/to/private/key</privateKey>
            <passphrase>key-passphrase</passphrase>
            <!-- 部署文件时的权限。 -->
            <filePermissions>664</filePermissions>
            <!-- 创建目录时的权限。 -->
            <directoryPermissions>775</directoryPermissions>
          </server>
        </servers>
        
        <!-- 镜像用于拦截对远程仓库的请求，并将请求重定向到另一个仓库地址。 -->
        <mirrors>
          <mirror>
            <id>aliyun</id>
            <name>Aliyun Public Mirror</name>
            <url>https://maven.aliyun.com/repository/public</url>
            <!-- *：所有仓库、central：中央仓库、external:*：除本地仓库外的所有仓库、*,!repo1：所有仓库，但排除 repo1、central,repo1：只镜像 central 和 repo1 -->
            <mirrorOf>central</mirrorOf>
          </mirror>
        </mirrors>
        
        <!-- 一组可选的环境配置，可以根据不同条件激活。可以定义仓库、插件仓库、属性等。 -->
        <profiles>
          <profile>
            <!-- 唯一标识，激活时使用。 -->
            <id>dev</id>
            <!-- 定义激活条件。 -->
            <activation>
              <activeByDefault>true</activeByDefault>
              <jdk>1.8</jdk>
              <os>
                <name>Windows 10</name>
                <family>Windows</family>
                <arch>amd64</arch>
                <version>10.0</version>
              </os>
              <property>
                <name>env</name>
                <value>dev</value>
              </property>
              <file>
                <exists>${basedir}/file2.properties</exists>
                <missing>${basedir}/file1.properties</missing>
              </file>
            </activation>
            <!-- 定义键值对属性，激活后可以在 POM 中使用 ${属性名} 引用。 -->
            <properties>
              <maven.compiler.source>25</maven.compiler.source>
      		  <maven.compiler.target>25</maven.compiler.target>
            </properties>
            <!-- 定义额外的远程仓库。 -->
            <repositories>
              <repository>
                <id>internal-repo</id>
                <name></name>
                <url>http://repo.example.com/maven2</url>
                <releases>
                  <!-- 是否允许下载发布版/快照版。 -->
                  <enabled>true</enabled>
                  <!-- 更新策略。控制 Maven 多长时间检查一次远程仓库中的依赖或插件是否有更新。如 always（默认）、daily、interval:60、never -->
                  <updatePolicy>daily</updatePolicy>
                  <!-- 校验和策略。 -->
                  <checksumPolicy>warn</checksumPolicy>
                </releases>
                <snapshots>
                  <enabled>false</enabled>
                </snapshots>
                <layout>default</layout>
              </repository>
            </repositories>
            <!-- 用于查找 Maven 插件。 -->
            <pluginRepositories>
              <pluginRepository>
                <id>plugin-repo</id>
                <url>https://repo.example.com/plugins</url>
                <releases>
                  <enabled>true</enabled>
                </releases>
                <snapshots>
                  <enabled>false</enabled>
                 </snapshots>
              </pluginRepository>
            </pluginRepositories>
          </profile>
        </profiles>
        
        <!-- 手动激活指定的 profile。即使某个 profile 没有通过 activation 条件自动激活。 -->
        <activeProfiles>
          <activeProfile>dev</activeProfile>
          <activeProfile>jdk8</activeProfile>
        </activeProfiles>
    <settings>
    ```

# 三、仓库地址

1. Central：https://repo1.maven.org/maven2/
1. Aliyun：http://maven.aliyun.com/nexus/content/groups/public/  
                     https://maven.aliyun.com/repository/public/
                     https://maven.aliyun.com/repository/google
1. Spring Lib Release：https://repo.spring.io/libs-release/
1. Spring Plugins：https://repo.spring.io/plugins-release/
1. Spring Lib M：https://repo.spring.io/libs-milestone/
1. Cloudera：https://repository.cloudera.com/artifactory/cloudera-repos
1. Redhat：https://maven.repository.redhat.com/ga/
1. Jboos public：http://repository.jboss.org/nexus/content/groups/public
1. Pentaho：https://nexus.pentaho.org/content/repositories/omni/
1. Icm：http://maven.icm.edu.pl/artifactory/repo/
1. JBossEA：https://repository.jboss.org/nexus/content/repositories/ea/
1. JBoss Releases：https://repository.jboss.org/nexus/content/repositories/releases/
1. Google Maven：https://dl.google.com/dl/android/maven2

# 四、命令

1. **mvn** *选项* *groupId:artifactId:version:goals* *phases/goals*：goal 绑定一个 phase，当执行一个 phase 时，就将绑定的所有 goal 运行一遍。

    - 选项：
        - --am、--also-make：同时编译依赖的项目。
        - --amd、--also-make-dependents：同时编译依赖了这个项目的项目。
        - -N、--non-recursive：不编译子项目。
        - -B、--batch-mode：批处理模式，不交互模式。
        - -b、--builder *arg*：指定编译策略 ID。
        - -C、--strict-checksums：检查校验和。
        - -c、--lax-checksums：如果检验和不匹配发出警告。
        - -pl、--projects *arg*：编译指定的项目，以逗号分隔的相对路径或 \[*groupId*\]:*artifactId*。
        - -q、--quit：较少的日志打印，仅显示错误级别。
        - -e、--errors：打印错误日志。
        - -X、--debug：较多的日志打印。
        - -l、--log-file *arg*：打印信息输出到指定文件。
        - -h、--help：打印帮助信息。
        - -v、--version：打印版本信息。
        - -V、--show-version：打印版本信息并编译。
        - -P、--activate-profiles *args*：指定环境文件，逗号分隔。
        - -D、--define *arg*：指定参数。

    - lifecycle 的 phases，和 phase 下的默认 goals：

      - clean
          - pre-clean
          - clean：maven-clean-plugin:clean
          - post-clean

      - default
          - validate：校验项目是否正确，POM 是否完整。
          - initialize：初始化构建状态。
          - generate-sources：生成源码。
          - process-sources：处理源码，例如过滤。
          - generate-resources：生成资源文件。
          - process-resources：maven-resources-plugin:resources，复制和过滤资源到 target/classes。
          - compile：maven-compiler-plugin:compile：编译 main 源码
          - process-classes：对编译过的字节码做处理，例如字节码增强。
          - generate-test-sources：生成测试源码
          - process-test-sources：处理测试源码。
          - generate-test-resources：生成测试资源。
          - process-test-resources：maven-resources-plugin:testResources，复制测试资源到 target/test-classes。
          - test-compile：maven-compiler-plugin:testCompile，编译测试源码。
          - process-test-classes
          - test：maven-surefire-plugin:test，运行单元测试。
          - prepare-package：在打包前做一些处理，比如 OSGi manifest 生成。
          - package：maven-jar-plugin:jar（打 jar 包，普通项目）。maven-war-plugin:war（web 项目）。
          - pre-integration-test
          - integration-test：通常绑定一些集成测试插件。
          - post-integration-test
          - verify：通常执行检查任务。
          - install：maven-install-plugin:install，把构建好的包安装到本地仓库。
          - deploy：maven-deploy-plugin:deploy，把构建好的包部署到远程仓库。

      - site
          - pre-site
          - site：maven-site-plugin:site，生成站点文档。
          - post-site
          - site-deploy：maven-site-plugin:deploy，部署站点到服务器。

1. **mvn clean complie**

1. **mvn clean install -Dmaven.test.skip=true**

1. **mvn test -Dtest=*classname*＃*method***

1. **mvn help:describe -Dplugin=*groupId*:*artifactId*:*version***：打印插件帮助信息。

1. **mvn dependency:resolve dependency:resolve-sources**：下载依赖二进制包和源码包。

1. **mvn dependency:resolve-sources -Dclassifier=javadoc**：下载依赖文档包。

1. **mvn tree**：打印依赖信息。

      
