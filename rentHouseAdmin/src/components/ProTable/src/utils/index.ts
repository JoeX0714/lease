/**
 * @description 根据枚举列表查询当需要的数据（如果指定了 label 和 value 的 key值，会自动识别格式化）
 * @param {String} callValue 当前单元格值
 * @param {Array} enumData 字典列表
 * @param {Array} fieldNames 指定 label && value 的 key 值
 * @param {String} type 过滤类型（目前只有 tag）
 * @return string
 */
export function filterEnum(
  callValue: any,
  enumData: any[] | undefined,
  fieldNames?: { label: string; value: string },
  type?: string,
): string {
  const value = fieldNames?.value ?? 'value'
  const label = fieldNames?.label ?? 'label'
  let filterData: { [key: string]: any } = {}
  if (Array.isArray(enumData))
    filterData = enumData.find((item: any) => item[value] === callValue)
  if (type == 'tag') return filterData?.tagType ? filterData.tagType : ''
  return filterData ? filterData[label] : '--'
}

/**
 * @description 处理无数据情况
 * @param {String} callValue 需要处理的值
 * @return string
 */
export function formatValue(callValue: any) {
  // 如果当前值为数组,使用 / 拼接（根据需求自定义）
  if (Array.isArray(callValue))
    return callValue.length ? callValue.join(' / ') : '--'
  return callValue ?? '--'
}

/**
 * @description 处理 prop 为多级嵌套的情况(列如: prop:user.name)
 * @param {Object} row 当前行数据
 * @param {String} prop 当前 prop
 * @return any
 */
export function handleRowAccordingToProp(
  row: { [key: string]: any },
  prop: string,
) {
  if (!prop.includes('.')) return row[prop] ?? '--'
  prop.split('.').forEach((item) => (row = row[item] ?? '--'))
  return row
}
