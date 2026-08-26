mvn package -f ./

# 默认放 target/dependency 了
mvn dependency:copy-dependencies -DoutputDirectory=target/lib

java -p target/jarsigner-1.0.0.jar:target/lib -m cn.ivfzhou.java.jarsigner/cn.ivfzhou.java.jarsigner.Sign ./test.p12 ./test.jar
