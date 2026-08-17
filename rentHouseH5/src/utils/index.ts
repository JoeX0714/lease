/**
 * 递归树结构，修改树结构的属性的key
 * @param data 树结构数据
 * @param key 要修改的key
 * @param value 要修改的key的值
 * @param childrenName 子节点的key名称
 * @param joinPropName 要拼接的key的名称
 */
export function recursionTree(
  data: any[],
  key: string,
  value: string,
  childrenName = "children",
  joinPropName?: string
) {
  data.forEach(item => {
    if (item[value]) {
      item[key] = item[value];
      delete item[value];
    }
    if (joinPropName) {
      item[key] = item[key] + "|" + item[joinPropName];
    }
    if (item[childrenName]) {
      recursionTree(item[childrenName], key, value, childrenName, joinPropName);
    }
  });
}
