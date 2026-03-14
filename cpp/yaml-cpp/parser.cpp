#include <array>
#include <exception>
#include <iostream>
#include <map>
#include <string>
#include <vector>

#include <yaml-cpp/yaml.h>

#include "parser.hpp"

namespace gitee::com::ivfzhou::cpp::yaml_cpp {

    const constexpr char* YAML_FILE_PATH = "../yaml-cpp/test.yml";

    // 解析 yaml 文件。
    void example_1() {
        std::cout << "[yaml-cpp] 开始 - 解析 yaml 文件" << std::endl;
        try {
            const auto& object = YAML::LoadFile(YAML_FILE_PATH);
            if (object["lastLogin"]) {
                std::cout << "lastLogin is " << object["lastLogin"].as<std::string>() << std::endl;
            }
        } catch (const YAML::BadFile& e) {
            std::cout << "yaml 文件未找到: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 解析 yaml 文件" << std::endl << std::endl;
    }

    // 判断 yaml 节点类型。
    void example_2() {
        std::cout << "[yaml-cpp] 开始 - 判断 yaml 节点类型" << std::endl;
        try {
            const auto& object = YAML::LoadFile(YAML_FILE_PATH);
            for (auto v : {"scalarType", "mapType", "sequenceType", "nullType", "undefinedType"}) {
                switch (const auto& type = object[v].Type()) {
                case YAML::NodeType::Null:
                    std::cout << "hit null type " << v << ", " << std::boolalpha << object[v].IsNull() << std::endl;
                    break;
                case YAML::NodeType::Scalar:
                    std::cout << "hit scalar type " << v << ", " << std::boolalpha << object[v].IsScalar() << std::endl;
                    break;
                case YAML::NodeType::Sequence:
                    std::cout << "hit sequence type " << v << ", " << std::boolalpha << object[v].IsSequence()
                              << std::endl;
                    break;
                case YAML::NodeType::Map:
                    std::cout << "hit map type " << v << ", " << std::boolalpha << object[v].IsMap() << std::endl;
                    break;
                case YAML::NodeType::Undefined:
                    std::cout << "hit undefined type " << v << ", " << std::boolalpha << object[v].IsDefined()
                              << std::endl;
                    break;
                default:
                    std::cout << "unknown node type: " << type << ", " << v << ", " << std::boolalpha
                              << object[v].IsNull() << std::endl;
                    break;
                }
            }
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 判断 yaml 节点类型" << std::endl << std::endl;
    }

    // 解析 yaml 序列节点。
    void example_3() {
        std::cout << "[yaml-cpp] 开始 - 解析 yaml 序列节点" << std::endl;
        try {
            const auto& object = YAML::LoadFile(YAML_FILE_PATH);
            if (const auto& sequence = object["sequenceType2"]; sequence && sequence.IsSequence()) {
                for (std::size_t i = 0; i < sequence.size(); i++) {
                    std::cout << sequence[i].as<int>() << " ";
                }
                std::cout << std::endl;

                for (auto it = sequence.begin(); it != sequence.end(); ++it) {
                    std::cout << it->as<int>() << " ";
                }
                std::cout << std::endl;
            }

            if (const auto& sequence = object["sequenceType3"]; sequence && sequence.IsSequence()) {
                for (const auto& i : sequence) {
                    std::cout << i.as<int>() << " ";
                }
                std::cout << std::endl;
            }

            if (const auto& sequence = object["sequenceType4"]; sequence && sequence.IsSequence()) {
                std::cout << sequence[0].as<int>() << " ";
                std::cout << sequence[1].as<std::string>() << " ";
                std::cout << sequence[2].as<long double>() << " ";
                std::cout << std::endl;
            }
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 解析 yaml 序列节点" << std::endl << std::endl;
    }

    // 解析 yaml 映射节点。
    void example_4() {
        std::cout << "[yaml-cpp] 开始 - 解析 yaml 映射节点" << std::endl;
        try {
            const auto& object = YAML::LoadFile(YAML_FILE_PATH);
            if (const auto& map = object["mapType2"]; map && map.IsMap()) {
                for (auto it = map.begin(); it != map.end(); ++it) {
                    std::cout << it->first.as<std::string>() << "=" << it->second.as<std::string>() << " ";
                }
                std::cout << std::endl;
            }

            if (const auto& map = object["mapType3"]; map && map.IsMap()) {
                for (auto it = map.begin(); it != map.end(); ++it) {
                    std::cout << it->first.as<std::string>() << "=" << it->second.as<std::string>() << " ";
                }
                std::cout << std::endl;
            }
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 解析 yaml 映射节点" << std::endl << std::endl;
    }

    // 解析 yaml 字符串数据。
    void example_5() {
        std::cout << "[yaml-cpp] 开始 - 解析 yaml 字符串数据" << std::endl;
        try {
            const auto& object = YAML::Load("[1, 2, 3]");
            const auto& object2 = YAML::Load("{key: value}");
            const auto& object3 = YAML::Load("[1, 2]: a");
            const auto& object4 = YAML::Load("bad value");
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - 解析 yaml 字符串数据" << std::endl << std::endl;
    }

    // yaml 节点数据转成其他 C++ 数据类型。
    void example_8() {
        std::cout << "[yaml-cpp] 开始 - yaml 节点数据转成其他 C++ 数据类型" << std::endl;
        try {
            const auto& object = YAML::Load("[1, 2, 3]");

            const auto& vector = object.as<std::vector<int>>();
            for (const auto v : vector) {
                std::cout << v << " ";
            }
            std::cout << std::endl;

            const auto& list = object.as<std::list<int>>();
            for (const auto v : list) {
                std::cout << v << " ";
            }
            std::cout << std::endl;

            const auto& array = object.as<std::array<int, 3>>();
            for (const auto v : array) {
                std::cout << v << " ";
            }
            std::cout << std::endl;

            const auto& object2 = YAML::Load("{key: value}");
            const auto& map = object2.as<std::map<std::string, std::string>>();
            for (const auto& [key, value] : map) {
                std::cout << key << "=" << value << " ";
            }
            std::cout << std::endl;
        } catch (const std::exception& e) {
            std::cout << "发生错误: " << e.what() << std::endl;
        }

        std::cout << "[yaml-cpp] 结束 - yaml 节点数据转成其他 C++ 数据类型" << std::endl << std::endl;
    }
}
