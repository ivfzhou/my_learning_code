module cn.ivfzhou.java.jarsigner {
    requires maven.jarsigner;
    requires maven.shared.utils;
    // maven.shared.utils / maven.jarsigner 是自动模块，无法声明自身依赖，
    // 必须由本模块显式 requires，才能把这些模块解析进模块图：
    requires org.slf4j;              // slf4j-api（LoggerFactory 所在模块）
    requires org.slf4j.nop;          // SLF4J 的 NOP 实现，避免 “No SLF4J providers were found” 警告
    requires org.apache.commons.io;  // commons-io（JarSignerUtil 会用到）
}
