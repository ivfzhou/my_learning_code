mvn package -f ./

mvn dependency:copy-dependencies -DoutputDirectoy=target/lib

java -p target/jarsigner-1.0.0.jar:target/lib -m cn.ivfzhou.java.jarsigner/cn.ivfzhou.java.jarsigner.Sign ./test.p12 ./test.jar
