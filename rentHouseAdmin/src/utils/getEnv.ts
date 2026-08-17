/**
 * @description 用来获取环境变量,暂时未使用
 * @description loadEnv()同样能获取
 * @param envConf env
 * @returns
 */
export function wrapperEnv(envConf: Recordable): ViteEnv {
  const ret: any = {}

  for (const envName of Object.keys(envConf)) {
    let realName = envConf[envName].replace(/\\n/g, '\n')
    realName =
      realName === 'true' ? true : realName === 'false' ? false : realName

    if (envName === 'VITE_PORT') {
      realName = Number(realName)
    }
    if (envName === 'VITE_PROXY') {
      try {
        realName = JSON.parse(realName)
      } catch (error) {
        /* empty */
        console.log(error)
      }
    }
    ret[envName] = realName
    process.env[envName] = realName
  }
  return ret
}

/**
 * @description 获取环境变量
 * @param name
 */
export function getEnvByName(name: string): string {
  const env = import.meta.env
  return env[name] || process.env[name]
}
