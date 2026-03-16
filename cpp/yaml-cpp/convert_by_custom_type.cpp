#include <exception>
#include <iostream>
#include <map>
#include <ostream>
#include <string>
#include <vector>

#include "convert_by_custom_type.hpp"
#include "forward_reference.hpp"

namespace gitee::com::ivfzhou::cpp::yaml_cpp {
    // 自定义数据类与 yaml 节点的互相转换。
    void example_9() {
        std::cout << "[yaml-cpp] 开始 - 自定义数据类与 yaml 节点的互相转换" << std::endl;
        try {
            custom_type data;
            data.intValue = 1;
            data.floatValue = 1.1;
            data.stringValue = "a";
            data.vectorValue = {1, 2, 3};
            data.mapValue["key"] = "value";

            YAML::Node node;
            node = data;
            std::cout << node << std::endl;

            const auto& data2 = node.as<custom_type>();
        } catch (const std::exception& e) {
            LOG("发生错误: ", e.what());
        }

        std::cout << "[yaml-cpp] 结束 - 自定义数据类与 yaml 节点的互相转换" << std::endl << std::endl;
    }
}

namespace YAML {
    Node convert<custom_type>::encode(const custom_type& data) {
        Node node;
        node["intValue"] = data.intValue;
        node["floatValue"] = data.floatValue;
        node["stringValue"] = data.stringValue;
        node["vectorValue"] = data.vectorValue;
        node["mapValue"] = data.mapValue;
        return node;
    }

    bool convert<custom_type>::decode(const Node& node, custom_type& data) {
        data = {};
        data.intValue = node["intValue"].as<int>();
        data.floatValue = node["floatValue"].as<float>();
        data.stringValue = node["stringValue"].as<std::string>();
        data.vectorValue = node["vectorValue"].as<std::vector<int>>();
        data.mapValue = node["mapValue"].as<std::map<std::string, std::string>>();
        return true;
    }
}
