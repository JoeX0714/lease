export const num = 0

export function timeFix() {
  const time = new Date()
  const hour = time.getHours()
  return hour < 9
    ? '早上好'
    : hour <= 11
    ? '上午好'
    : hour <= 13
    ? '中午好'
    : hour < 20
    ? '下午好'
    : '晚上好'
}

export function welcome() {
  const arr = [
    '加油努力工作',
    '喝一杯咖啡吧',
    '休息一会儿吧',
    '准备吃什么呢',
    '要不要站起来活动一下',
  ]
  const index = Math.floor(Math.random() * arr.length)
  return arr[index]
}
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
  childrenName = 'children',
  joinPropName?: string,
) {
  data.forEach((item) => {
    if (item[value]) {
      item[key] = item[value]
      delete item[value]
    }
    if (joinPropName) {
      item[key] = item[key] + '|' + item[joinPropName]
    }
    if (item[childrenName]) {
      recursionTree(item[childrenName], key, value, childrenName, joinPropName)
    }
  })
}

/**
 * 将形如'HH:mm:ss'的时间字符串转换为秒数
 * @param timeString : 'HH:mm:ss' 格式的时间字符串
 */
export function timeStringToSeconds(timeString: string): number {
  const [hours, minutes, seconds] = timeString.split(':').map(Number)
  return (hours * 60 + minutes) * 60 + seconds
}
/**
 * 将秒数转换为形如 'HH:mm:ss' 的时间字符串
 * @param seconds 秒数
 * @returns 格式为 'HH:mm:ss' 的时间字符串
 */
export function secondsToTimeString(seconds: number): string {
  const hours = Math.floor(seconds / 3600)
    .toString()
    .padStart(2, '0')
  const minutes = Math.floor((seconds % 3600) / 60)
    .toString()
    .padStart(2, '0')
  const sec = (seconds % 60).toString().padStart(2, '0')
  return `${hours}:${minutes}:${sec}`
}
