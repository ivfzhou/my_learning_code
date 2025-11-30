-- 创建员工表
CREATE TABLE employees
(
    id         INT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    position   VARCHAR(100),
    manager_id INT          NULL,
    salary     DECIMAL(10, 2),
    FOREIGN KEY (manager_id) REFERENCES employees (id)
);

-- 插入示例数据
INSERT INTO employees
VALUES (1, '张总', 'CEO', NULL, 100000),
       (2, '李经理', '技术总监', 1, 80000),
       (3, '王经理', '市场总监', 1, 75000),
       (4, '赵主管', '开发主管', 2, 60000),
       (5, '钱主管', '测试主管', 2, 58000),
       (6, '孙专员', '高级开发', 4, 50000),
       (7, '周专员', '开发', 4, 45000),
       (8, '吴专员', '测试', 5, 42000),
       (9, '郑专员', '市场策划', 3, 48000),
       (10, '冯专员', '市场推广', 3, 46000);

-- 查找某个员工的所有下属（向下递归）
WITH RECURSIVE subordinate_tree AS (
    -- 锚成员：选择起点员工（李经理，ID=2）
    SELECT id, name, position, manager_id, salary, 1 AS level
    FROM employees
    WHERE id = 2

    UNION ALL

    -- 递归成员：查找所有直接和间接下属
    SELECT e.id, e.name, e.position, e.manager_id, e.salary, st.level + 1
    FROM employees e
             JOIN subordinate_tree st ON e.manager_id = st.id)
SELECT id,
       name,
       position,
       CONCAT(REPEAT('    ', level - 1), '└─ ', name) AS hierarchy,
       salary,
       level                                          AS management_level
FROM subordinate_tree
ORDER BY level, id;

-- 查找某个员工的所有上级（向上递归）
WITH RECURSIVE manager_tree AS (
    -- 锚成员：选择起点员工（孙专员，ID=6）
    SELECT id, name, position, manager_id, salary, 1 AS level
    FROM employees
    WHERE id = 6

    UNION ALL

    -- 递归成员：查找所有直接和间接上级
    SELECT e.id, e.name, e.position, e.manager_id, e.salary, mt.level + 1
    FROM employees e
             JOIN manager_tree mt ON e.id = mt.manager_id)
SELECT id,
       name,
       position,
       CONCAT(name, ' ← ', IFNULL((SELECT name FROM employees WHERE id = manager_id), '顶层')) AS reporting_line,
       salary,
       level - 1                                                                               AS levels_above_start
FROM manager_tree
ORDER BY level DESC;

-- 计算组织结构的完整树形图
WITH RECURSIVE org_chart AS (
    -- 锚成员：选择所有顶级管理者（没有上级的员工）
    SELECT id, name, position, manager_id, 0 AS level, CAST(name AS CHAR(1000)) AS path
    FROM employees
    WHERE manager_id IS NULL

    UNION ALL

    -- 递归成员：查找每个节点的下属
    SELECT e.id,
           e.name,
           e.position,
           e.manager_id,
           oc.level + 1,
           CONCAT(oc.path, ' > ', e.name) AS path
    FROM employees e
             JOIN org_chart oc ON e.manager_id = oc.id)
SELECT id,
       CONCAT(REPEAT('    ', level), name) AS tree_display,
       position,
       level                               AS depth,
       path
FROM org_chart
ORDER BY path;