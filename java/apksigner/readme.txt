mvn package -f ./

mvn dependency:copy-dependencies -DoutputDirectory=target/lib

java -p target/apksigner-1.0.0.jar:target/lib/ -m cn.ivfzhou.java.apksigner/cn.ivfzhou.java.apksigner.GenerateCertificate
