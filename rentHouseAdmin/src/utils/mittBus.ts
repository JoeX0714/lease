/*
 * @Author: 朽木白
 * @Date: 2023-03-14 16:32:06
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-27 17:04:19
 * @Description: 全局事件监听
 * @docs: https://www.npmjs.com/package/mitt
 */

import mitt from 'mitt'

const mittBus = mitt()

export default mittBus
