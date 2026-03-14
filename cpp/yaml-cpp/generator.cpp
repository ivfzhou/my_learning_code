#include <exception>
#include <iostream>
#include <map>
#include <string>
#include <vector>

#include <yaml-cpp/yaml.h>

#include "generator.hpp"

namespace gitee::com::ivfzhou::cpp::yaml_cpp {

    // 构建 yaml 数据。
    void example_6() {
        std::cout << "[yaml-cpp] 开始 - 构建 yaml 数据" << std::endl;
        try {
            YAML::Node node;
            node["stringValue"] = "a";
            node["integerValue"] = 1;
            node["floatValue"] = 1.1;
            node["sequenceValue"].push_back(1);
            node["sequenceValue"].push_back(1.1);
            node["sequenceValue"].push_back("a");
            node["mapValue"]["a"] = "a";
            node["mapValue"][1] = 1;
            node["mapValue"][1.1] = 1.1;

            node["alias"] = node["mapValue"]["a"];
            node["alias"] = "b"; // 别名 node 和原 node 互相影响。

            node["self"] = node;

            node[node["alias"]] = node["self"]; // 使用 node 作为下标。

            std::cout << node << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 构建 yaml 数据" << std::endl << std::endl;
    }

    // 节点类型从序列变成映射。
    void example_7() {
        std::cout << "[yaml-cpp] 开始 - 节点类型从序列变成映射" << std::endl;
        try {
            YAML::Node node;
            node[0] = "a";
            node[1] = "c";
            node[2] = "c";
            std::cout << node << std::endl;

            node["key"] = "value"; // 转成映射类型。
            std::cout << node << std::endl;

            node.remove("key");
            std::cout << node << std::endl; // 仍然是映射类型。

            node[5] = "d"; // 转成映射类型。
            std::cout << node << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 节点类型从序列变成映射" << std::endl << std::endl;
    }

    // 使用 YAML::Emitter 构建 yaml 数据。
    void example_10() {
        std::cout << "[yaml-cpp] 开始 - 使用 YAML::Emitter 构建 yaml 数据" << std::endl;
        try {
            YAML::Emitter emitter;

            emitter << YAML::BeginMap;
            emitter << YAML::Key << 1 << YAML::Value << "a";
            emitter << YAML::Key << "b";
            emitter << YAML::Value;
            emitter << YAML::BeginSeq;
            emitter << 1;
            emitter << "a";
            emitter << YAML::BeginMap;
            emitter << YAML::Key << "key" << YAML::Value << "value";
            emitter << YAML::Key << "key2" << YAML::Value << "value2";
            emitter << YAML::EndMap;
            emitter << YAML::EndSeq;
            emitter << YAML::EndMap;

            emitter << YAML::Comment("输入裸字符串");
            emitter << YAML::Literal << "A\n B\n  C";

            std::cout << std::boolalpha << emitter.good() << std::endl;
            std::cout << emitter.c_str() << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 使用 YAML::Emitter 构建 yaml 数据" << std::endl << std::endl;
    }

    // YAML::Emitter 构建 yaml 数据时使用引用。
    void example_11() {
        std::cout << "[yaml-cpp] 开始 - YAML::Emitter 构建 yaml 数据时使用引用" << std::endl;
        try {
            YAML::Emitter emitter;
            emitter << YAML::BeginMap;
            emitter << YAML::Key << "key" << YAML::Anchor("anchor") << YAML::Value << "value";
            emitter << YAML::EndMap;
            emitter << YAML::BeginMap;
            emitter << YAML::Alias("anchor");
            emitter << YAML::Key << "a" << YAML::Value << "b";
            emitter << YAML::EndMap;

            std::cout << emitter.c_str() << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - YAML::Emitter 构建 yaml 数据时使用引用" << std::endl << std::endl;
    }

    // YAML::Emitter 结合 STL 使用。
    void example_12() {
        std::cout << "[yaml-cpp] 开始 - YAML::Emitter 结合 STL 使用" << std::endl;
        try {
            YAML::Emitter emitter;

            const std::vector vector{1, 2, 3};
            emitter << YAML::BeginSeq;
            emitter << vector;
            emitter << YAML::EndSeq;

            const std::list list{4, 5, 6};
            emitter << YAML::BeginSeq;
            emitter << list;
            emitter << YAML::EndSeq;


            std::map<std::string, std::string> map;
            map["key"] = "value";
            map["key2"] = "value2";
            emitter << YAML::BeginMap;
            emitter << map;
            emitter << YAML::EndMap;

            std::cout << emitter.c_str() << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - YAML::Emitter 结合 STL 使用" << std::endl << std::endl;
    }

    // 控制 YAML::Emitter 输出格式。
    void example_13() {
        std::cout << "[yaml-cpp] 开始 - 控制 YAML::Emitter 输出格式" << std::endl;
        try {
            YAML::Emitter emitter;

            emitter << YAML::Flow;
            emitter << YAML::BeginSeq;
            emitter << 1 << 2 << 3;
            emitter << YAML::EndSeq;

            emitter << YAML::Flow;
            emitter << YAML::BeginMap;
            emitter << YAML::Key << "key" << YAML::Value << "value";
            emitter << YAML::EndMap;

            // 设置整个 emitter 的输出格式。
            emitter.SetOutputCharset(YAML::EscapeNonAscii);
            emitter.SetIndent(8);
            emitter.SetMapFormat(YAML::Block);

            emitter << YAML::BeginMap;
            emitter << YAML::Key << "你" << YAML::Value << "好";
            emitter << YAML::Key << "c" << YAML::Value << "d";
            emitter << YAML::EndMap;

            std::cout << emitter.c_str() << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 控制 YAML::Emitter 输出格式" << std::endl << std::endl;
    }

    // 将 YAML::Node 发送给 YAML::Emitter。
    void example_14() {
        std::cout << "[yaml-cpp] 开始 - 将 YAML::Node 发送给 YAML::Emitter" << std::endl;
        try {
            YAML::Emitter emitter;
            YAML::Node node;
            node["key"] = "value";
            node["seq"] = std::vector{1, 2, 3, 4};
            emitter << node;
            std::cout << emitter.c_str() << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 将 YAML::Node 发送给 YAML::Emitter" << std::endl << std::endl;
    }
}
