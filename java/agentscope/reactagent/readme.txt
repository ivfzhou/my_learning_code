mvn package -f ./

mvn dependency:copy-dependencies -DoutputDirectory=target/lib

set DASHSCOPE_API_KEY=your key

java -p target/agentscope-reactagent-1.0.0.jar:target/lib -m cn.ivfzhou.java.agentscope.reactagent.Sample
java -cp "target/agentscope-reactagent-1.0.0.jar;target/lib/*" cn.ivfzhou.java.agentscope.reactagent.Sample
